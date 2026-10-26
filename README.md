# miga. · Finanzas personales — Taller MVC

Aplicación de Computación en Internet II, Universidad Icesi. Este taller continúa el [mismo repositorio de JPA](https://github.com/Computacion-2/Taller-jpa-): conserva el modelo financiero, los repositorios, los servicios y la API, y agrega páginas con Spring MVC y Thymeleaf para administrar usuarios, roles y permisos.

## Ejecutar localmente

Requisitos: JDK 17 o 21 y acceso a Maven Central durante la primera compilación. Verificado con JDK 21. El proyecto incluye Maven Wrapper.

Desde la raíz del repositorio, en PowerShell:

```powershell
.\mvnw.cmd clean verify
java -jar target/finanzas-personales-1.0.0.war
```

En Linux o macOS:

```bash
sh mvnw clean verify
java -jar target/finanzas-personales-1.0.0.war
```

Abrir [http://localhost:8080](http://localhost:8080). Para usar otro puerto, agregar `--server.port=18080` al comando de Java. Detener la aplicación con Ctrl+C antes de reconstruir el WAR en Windows.

Si el comando `java` de Windows apunta a otra instalación, usar el ejecutable del JDK configurado para Maven: `& "$env:JAVA_HOME\bin\java.exe" -jar target/finanzas-personales-1.0.0.war`.

El perfil predeterminado `demo` crea una base H2 en memoria. Sus cambios se pierden al detener la aplicación.

| Cuenta de demostración | Contraseña | Rol |
|---|---|---|
| `admin@u.icesi.edu.co` | `Demo2026!` | ADMINISTRADOR |
| `estudiante@u.icesi.edu.co` | `Demo2026!` | USUARIO |

Estas cuentas son ficticias y corresponden al perfil `demo`, preparado para revisar el taller.

## Compilar los estilos

Las vistas usan **Tailwind CSS 4.3.3**, compilado localmente con su CLI oficial. El CSS generado se incluye en el repositorio y en el WAR; ejecutar Maven y la aplicación requiere únicamente Java, sin Node ni una conexión a un CDN.

Para modificar los estilos o las clases de las plantillas, instalar Node.js y ejecutar desde la raíz:

```powershell
npm ci --cache .npm-cache
npm run build:css
```

Durante el diseño se puede usar `npm run watch:css`, que recompila al guardar cambios. La fuente es `src/main/frontend/app.css` y el archivo generado es `src/main/resources/static/css/app.css`. Después de terminar, ejecutar `npm run build:css` para guardar la versión minificada y luego reconstruir el WAR.

## Usar la aplicación

| Ruta | Función |
|---|---|
| `/` | Inicio y navegación según el acceso de la cuenta |
| `/login` | Iniciar sesión con correo y contraseña |
| `/registro` | Registrar una cuenta con correo `@u.icesi.edu.co` y rol USUARIO |
| `/usuarios` | Consultar, crear, editar y eliminar usuarios; asignar o retirar roles desde la edición |
| `/roles` | Consultar, crear, editar y eliminar roles y seleccionar sus permisos |
| `/permisos` | Consultar, crear, editar y eliminar permisos |

Las páginas administrativas requieren el rol ADMINISTRADOR o el permiso activo del módulo: `USUARIOS_GESTIONAR`, `ROLES_GESTIONAR` o `PERMISOS_GESTIONAR`. La autorización se verifica en el servidor. Ocultar un enlace en la navegación no sustituye ese control.

Los formularios muestran los errores de validación junto a los datos y conservan los valores permitidos al corregirlos. Una operación completada redirige a una página de consulta mediante el patrón POST/Redirect/GET (PRG), para que actualizar el navegador no repita la escritura. Los formularios de cambios y la salida de sesión incluyen protección CSRF.

## Arquitectura y pruebas

```text
src/main/java/edu/co/icesi/finanzas/
  entity/          Modelo JPA, asociaciones y claves compuestas
  repo/            Repositorios Spring Data JPA
  service/         Interfaces de servicios
  service/impl/    Reglas de negocio y transacciones
  dto/             Datos de entrada y salida; formularios en dto/form
  controller/      Controladores MVC que devuelven vistas y redirecciones
  api/             Controladores REST del taller JPA
  security/        UserDetailsService y cadenas de seguridad web/API
  config/          Inicialización de producción
  exception/       Manejo separado de errores MVC y REST
src/main/resources/
  templates/       Páginas Thymeleaf
  static/          Estilos de la aplicación
  db/              Esquema SQL y datos iniciales
src/test/java/     Pruebas de servicios e integración
```

Los controladores MVC usan los servicios existentes. Las operaciones de negocio y la persistencia continúan en la capa de servicios; las vistas no acceden directamente a los repositorios. Las contraseñas se almacenan con BCrypt y no se muestran en listas o formularios de edición.

La distribución `controller` / `api` / `security` sigue el ejemplo docente [262WebApp6](https://github.com/Domiciano/262WebApp6).

`clean verify` ejecuta las pruebas y la verificación de JaCoCo. El proyecto configura un umbral propio del 100 % de líneas y ramas de las implementaciones de los servicios; el enunciado pide pruebas para todos los servicios. Revisar los resultados de la ejecución actual en `target/surefire-reports` y el reporte de cobertura en `target/site/jacoco/index.html`.

Para ejecutar solamente las pruebas, usar `.\mvnw.cmd test` en PowerShell o `sh mvnw test` en Bash. Las pruebas cubren los cuatro servicios y los flujos MVC de autenticación, formularios, CSRF y autorización.

Para comprobar manualmente la aplicación con el perfil `demo`:

1. Entrar como administrador y abrir usuarios, roles y permisos.
2. Crear un permiso de prueba y asociarlo a un nuevo rol.
3. Crear o editar un usuario para asignarle ese rol; retirar o sustituirlo desde la edición.
4. Probar campos inválidos o duplicados y comprobar los mensajes del formulario.
5. Eliminar los datos de prueba después de retirar sus asociaciones; comprobar que los roles base están protegidos.
6. Cerrar sesión y registrar una cuenta institucional. Verificar que no recibe privilegios administrativos.
7. Entrar como estudiante e intentar acceder directamente a `/usuarios`, `/roles` y `/permisos`; debe denegarse el acceso.
8. Ejecutar las pruebas de integración para comprobar, además, el rechazo de operaciones sin CSRF y la actualización de permisos en sesiones abiertas.

## WAR de entrega

Según las aclaraciones del profesor, la entrega en pareja está autorizada y el binario se incluye en la carpeta `artifacts/` del mismo repositorio de Classroom. No es obligatorio desplegarlo en IAsLab.

El [WAR incluido](artifacts/finanzas-personales-1.0.0.war) puede ejecutarse directamente desde la raíz con Java 17 o posterior:

```bash
java -jar artifacts/finanzas-personales-1.0.0.war
```

Abrir [http://localhost:8080/login](http://localhost:8080/login) y usar las cuentas demo indicadas arriba. Para reconstruir y actualizar el archivo en PowerShell:

```powershell
.\mvnw.cmd clean verify
if ($LASTEXITCODE -ne 0) { throw 'La compilación o las pruebas fallaron.' }
New-Item -ItemType Directory -Path artifacts -Force | Out-Null
Copy-Item -LiteralPath 'target/finanzas-personales-1.0.0.war' -Destination 'artifacts/finanzas-personales-1.0.0.war' -Force
```

En Bash:

```bash
sh mvnw clean verify || exit 1
mkdir -p artifacts
cp target/finanzas-personales-1.0.0.war artifacts/finanzas-personales-1.0.0.war
```

Detener primero cualquier ejecución que mantenga abierto el archivo que se va a reemplazar. Entregar el `.war` ejecutable; `target/`, `*.war.original`, dependencias descargadas y archivos locales de trabajo no forman parte de la entrega.
