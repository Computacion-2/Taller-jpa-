$ErrorActionPreference = 'Stop'
$repoPath = Split-Path -Parent $PSScriptRoot
$server = $null
$client = $null
$handler = $null
$listener = $null
Push-Location $repoPath

function Check-Git {
    param([string[]]$GitArgs)
    $output = & git @GitArgs
    if ($LASTEXITCODE -ne 0) { throw "Git fallo: $($GitArgs -join ' ')" }
    return $output
}

function Request-App {
    param([string]$Method, [string]$Route, [int]$Expected, [string]$Form)
    $message = [System.Net.Http.HttpRequestMessage]::new([System.Net.Http.HttpMethod]::new($Method), "$baseUrl$Route")
    $response = $null
    try {
        if ($Form) { $message.Content = [System.Net.Http.StringContent]::new($Form, [Text.Encoding]::UTF8, 'application/x-www-form-urlencoded') }
        $response = $client.SendAsync($message).GetAwaiter().GetResult()
        if ([int]$response.StatusCode -ne $Expected) { throw "$Method $Route devolvio $([int]$response.StatusCode); se esperaba $Expected" }
        return $response.Content.ReadAsStringAsync().GetAwaiter().GetResult()
    } finally {
        if ($response) { $response.Dispose() }
        $message.Dispose()
    }
}

function Get-Csrf {
    param([string]$Html)
    foreach ($inputTag in [regex]::Matches($Html, '<input\b[^>]*>')) {
        if ($inputTag.Value -match 'name="_csrf"' -and $inputTag.Value -match 'value="([^"]+)"') {
            return [uri]::EscapeDataString([System.Net.WebUtility]::HtmlDecode($Matches[1]))
        }
    }
    throw 'El formulario MVC no contiene CSRF'
}

try {
    Get-Command git, node, java -ErrorAction Stop | Out-Null
    $nodeVersion = & node --version
    if ($LASTEXITCODE -ne 0 -or [int]($nodeVersion.TrimStart('v').Split('.')[0]) -lt 20) { throw 'Se requiere Node.js 20 o posterior' }
    if (Check-Git @('status', '--porcelain')) { throw 'Guarda los cambios del repositorio antes de verificar' }
    $testedCommit = Check-Git @('rev-parse', 'HEAD')
    Check-Git @('diff', '--check', 'origin/main..HEAD')

    Write-Host '1/4 Compilando y ejecutando pruebas con pom.xml...' -ForegroundColor Cyan
    $maven = Get-Command mvn.cmd -ErrorAction SilentlyContinue
    if ($maven) { & $maven.Source -B -f pom.xml clean verify }
    else { & .\mvnw.cmd -B -f pom.xml clean verify }
    if ($LASTEXITCODE -ne 0) { throw 'La compilacion definitiva fallo' }

    $totals = @{ tests = 0; failures = 0; errors = 0; skipped = 0 }
    $reports = @(Get-ChildItem target/surefire-reports/TEST-*.xml)
    foreach ($report in $reports) {
        [xml]$suite = Get-Content -Raw $report.FullName
        foreach ($key in @('tests', 'failures', 'errors', 'skipped')) { $totals[$key] += [int]$suite.testsuite.GetAttribute($key) }
    }
    if ($totals.tests -lt 105 -or $totals.failures -or $totals.errors -or $totals.skipped) { throw 'Faltan pruebas o hay fallos, errores o pruebas omitidas' }

    Write-Host '2/4 Iniciando la aplicacion para comprobar HTTP real...' -ForegroundColor Cyan
    $listener = [System.Net.Sockets.TcpListener]::new([System.Net.IPAddress]::Loopback, 0)
    $listener.Start()
    $port = $listener.LocalEndpoint.Port
    $listener.Stop()
    $listener = $null
    $baseUrl = "http://127.0.0.1:$port"
    $war = (Resolve-Path target/finanzas-personales-1.0.0.war).Path
    $server = Start-Process -FilePath (Get-Command java).Source -ArgumentList @('-jar', ('"' + $war + '"'), "--server.port=$port", '--server.address=127.0.0.1', '--spring.profiles.active=demo') -WorkingDirectory $repoPath -WindowStyle Hidden -PassThru -RedirectStandardOutput "$repoPath\target\validacion-http.log" -RedirectStandardError "$repoPath\target\validacion-http-error.log"
    Add-Type -AssemblyName System.Net.Http
    $handler = [System.Net.Http.HttpClientHandler]::new()
    $handler.AllowAutoRedirect = $false
    $handler.CookieContainer = [System.Net.CookieContainer]::new()
    $client = [System.Net.Http.HttpClient]::new($handler)
    $client.Timeout = [TimeSpan]::FromSeconds(5)
    $deadline = [DateTime]::UtcNow.AddSeconds(90)
    $loginHtml = $null
    while ([DateTime]::UtcNow -lt $deadline) {
        if ($server.HasExited) { throw 'La aplicacion se detuvo; revisa target/validacion-http.log y validacion-http-error.log' }
        try { $loginHtml = Request-App GET /login 200; break } catch { Start-Sleep -Milliseconds 300 }
    }
    if (-not $loginHtml) { throw 'La aplicacion no respondio en 90 segundos' }

    $loginCsrf = Get-Csrf $loginHtml
    Request-App POST /login 403 'username=admin%40u.icesi.edu.co&password=Demo2026%21' | Out-Null
    Request-App POST /login 302 "username=admin%40u.icesi.edu.co&password=Demo2026%21&_csrf=$loginCsrf" | Out-Null
    $homeHtml = Request-App GET / 200
    foreach ($route in @('/usuarios', '/roles', '/permisos', '/css/app.css', '/swagger-ui/index.html', '/swagger-ui/swagger-ui-bundle.js')) { Request-App GET $route 200 | Out-Null }
    Request-App GET /api/me 401 | Out-Null
    $openApi = (Request-App GET /v3/api-docs 200) | ConvertFrom-Json
    if ($openApi.components.securitySchemes.bearerAuth.scheme -ne 'bearer' -or $openApi.components.securitySchemes.bearerAuth.type -ne 'http') { throw 'Swagger no publica el esquema JWT esperado' }
    $logoutCsrf = Get-Csrf $homeHtml
    Request-App POST /logout 403 'sin=csrf' | Out-Null
    Request-App POST /logout 302 "_csrf=$logoutCsrf" | Out-Null
    Request-App GET /usuarios 302 | Out-Null

    Write-Host '3/4 Ejecutando la coleccion Postman mediante HTTP...' -ForegroundColor Cyan
    & node scripts/verificar-api.mjs postman/Local.postman_environment.json $baseUrl
    if ($LASTEXITCODE -ne 0) { throw 'La coleccion fallo mediante HTTP real' }
    $postman = Get-Content -Raw target/postman-report.json | ConvertFrom-Json
    if (@($postman.results).Count -ne 146 -or @($postman.checks).Count -ne 153 -or @($postman.results | Where-Object { -not $_.passed }).Count -or @($postman.checks | Where-Object { -not $_.passed }).Count) { throw 'El reporte Postman esta incompleto' }

    Write-Host '4/4 Comprobando el estado de GitHub...' -ForegroundColor Cyan
    Check-Git @('fetch', 'origin')
    Check-Git @('merge-base', '--is-ancestor', 'origin/main', 'HEAD')
    if ((Check-Git @('rev-parse', 'HEAD')) -ne $testedCommit -or (Check-Git @('status', '--porcelain'))) { throw 'El codigo cambio durante la verificacion' }
    $pending = Check-Git @('rev-list', '--count', 'origin/main..HEAD')
    Write-Host "Commits locales pendientes de publicar: $pending"
    @{
        commit = $testedCommit
        originMain = (Check-Git @('rev-parse', 'origin/main'))
        verifiedAt = [DateTimeOffset]::Now.ToString('o')
        build = 'pom.xml: clean verify'
        junit = $totals
        mvcHttp = 'passed'
        swaggerHttp = 'passed'
        postmanHttpRequests = @($postman.results).Count
        postmanHttpChecks = @($postman.checks).Count
        pendingCommits = [int]$pending
        pushPerformed = $false
    } | ConvertTo-Json -Depth 5 | Set-Content -Encoding UTF8 target/validacion-final.json
    Write-Host 'VALIDACION COMPLETA. No se hizo push. Reporte: target/validacion-final.json' -ForegroundColor Green
} catch {
    Write-Host "VALIDACION PENDIENTE: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
} finally {
    if ($client) { $client.Dispose() }
    if ($handler) { $handler.Dispose() }
    if ($listener) { $listener.Stop() }
    if ($server -and -not $server.HasExited) { $server.Kill(); $server.WaitForExit(5000) | Out-Null }
    Pop-Location
}
