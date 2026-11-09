# Finanzas personales

Aplicación con interfaz web para administrar usuarios, roles y permisos, y una API REST para gestionar cuentas, movimientos, presupuestos, deudas y planes de ahorro.

## Ejecutar la aplicación

Requisitos: **JDK 17 o 21**, con `java` disponible en la terminal, e internet para la primera descarga de dependencias. El proyecto incluye Maven Wrapper.

1. Abrir una terminal en la carpeta que contiene `pom.xml`.
2. En **PowerShell o CMD**, compilar, ejecutar las pruebas y generar el archivo WAR:

   ```powershell
   .\mvnw.cmd clean verify
   ```

   Esperar el mensaje `BUILD SUCCESS`. En **Git Bash, Linux o macOS**, usar `sh mvnw clean verify`.

3. Iniciar la aplicación con el WAR generado:

   ```shell
   java -jar target/finanzas-personales-1.0.0.war
   ```

4. Mantener esa terminal abierta y entrar a [http://localhost:8080/login](http://localhost:8080/login).

La base H2 y los datos demo se cargan automáticamente; no hay que instalar una base de datos. Detener con `Ctrl+C` antes de volver a compilar. Al reiniciar se restauran los datos demo y se pierden los cambios realizados.

## Credenciales y funciones por rol

Las cuentas demo sirven para la web y para el login de la API:

| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | admin@u.icesi.edu.co | Demo2026! |
| Usuario | estudiante@u.icesi.edu.co | Demo2026! |

- **Administrador:** gestiona usuarios, roles, permisos y sus asignaciones desde la web o la API. También administra los catálogos y los registros financieros de los usuarios mediante la API.
- **Usuario:** accede a la web sin los módulos administrativos. Mediante la API consulta su perfil, cambia su contraseña y gestiona sus cuentas, movimientos, categorías personales, presupuestos, deudas, abonos y planes de ahorro. El acceso a planes compartidos depende de su participación y de si es propietario.

## Probar la interfaz web

1. Entrar como **administrador** y abrir **Usuarios → Crear usuario**. Completar los datos, asignar un rol y guardar; comprobar que aparezca en el listado.
2. Abrir **Editar**, cambiar un dato y guardar. Desde **Roles**, crear un rol y asignarle permisos.
3. Cerrar sesión con el botón **Salir** e ingresar como **usuario**. Al intentar abrir [http://localhost:8080/usuarios](http://localhost:8080/usuarios), debe mostrarse acceso denegado.

## Probar la API con Swagger

Con la aplicación iniciada, abrir [Swagger UI](http://localhost:8080/swagger-ui/index.html). La sesión de la web y la autenticación JWT de la API son independientes.

1. Desplegar `POST /api/auth/login`, pulsar **Try it out** y reemplazar el cuerpo por:

   ```json
   {
     "correo": "estudiante@u.icesi.edu.co",
     "password": "Demo2026!"
   }
   ```

2. Pulsar **Execute**. Debe responder `200` con un `accessToken`.
3. Copiar solo el valor de `accessToken`, sin comillas. Pulsar **Authorize**, pegarlo sin escribir `Bearer`, confirmar y cerrar el cuadro.
4. Ejecutar cada operación con **Try it out → Execute**:

   | Operación | Resultado esperado con el usuario demo |
   |---|---|
   | `GET /api/me` | `200`: perfil del estudiante. |
   | `GET /api/cuentas` | `200`: sus cuentas financieras. |
   | `GET /api/admin/usuarios` | `403`: operación reservada al administrador. |

5. Repetir el login con las credenciales del administrador. En **Authorize**, pulsar **Logout** y autorizar el nuevo token. `GET /api/admin/usuarios` debe responder `200`.
6. Pulsar **Authorize → Logout** y repetir `GET /api/me`: debe responder `401` sin token.

Para probar un CRUD, ejecutar `POST /api/cuentas` con los campos que muestra Swagger. Copiar el `id` devuelto y usarlo en `GET`, `PUT` y `DELETE /api/cuentas/{id}`. Deben responder `201`, `200`, `200` y `204`, respectivamente.

## Probar toda la API con Postman

Usar la aplicación recién iniciada con los datos demo:

1. En Postman, pulsar **Import** y seleccionar:

   - `postman/Finanzas.postman_collection.json`
   - `postman/Local.postman_environment.json`
2. Seleccionar el entorno **Finanzas local demo**. Verificar que `baseUrl` sea `http://localhost:8080`; las credenciales ya están configuradas.
3. Abrir la colección, pulsar **Run collection** y ejecutar todas las solicitudes en su orden original, con **1 iteración**.
4. Comprobar que todas las pruebas pasen. La colección obtiene tokens e identificadores automáticamente y prueba CRUD, permisos y errores. Algunas respuestas `4xx` son intencionales y deben pasar su validación.

También se puede ejecutar la colección por HTTP desde una **segunda terminal**, con **Node.js 20 o posterior** y la aplicación encendida:

```shell
node scripts/verificar-api.mjs
```

Resultado esperado: `OK: 146 solicitudes, 153 verificaciones.` Informe: `target/postman-report.json`. Si falla o se interrumpe, reiniciar la aplicación antes de repetirla.

## Pruebas automáticas del proyecto

Ejecutar desde la carpeta del proyecto; estas pruebas usan su propia base de datos y no requieren iniciar la aplicación:

| Qué probar | Comando en PowerShell o CMD |
|---|---|
| Todas las pruebas | `.\mvnw.cmd test` |
| CRUD REST, JWT y Swagger | `.\mvnw.cmd "-Dtest=RestIntegrationTest" test` |
| Interfaz web y permisos | `.\mvnw.cmd "-Dtest=MvcIntegrationTest" test` |

En Git Bash, Linux o macOS, reemplazar `.\mvnw.cmd` por `sh mvnw`. Los informes quedan en `target/surefire-reports/`.

Con Node.js, la suite completa también ejecuta la colección mediante MockMvc. Sin Node.js, esa prueba se omite: ejecutar la colección en Postman. `clean verify` ejecuta toda la suite, comprueba la cobertura configurada y genera el WAR.
