# Finanzas personales - Taller 4

Backend REST con Spring Security, JWT, DTOs y MapStruct. El modulo MVC/Thymeleaf conserva sus vistas, sesiones y formularios con CSRF.

## Ejecutar

Java 17 o 21. Maven Wrapper incluido. La primera compilacion requiere internet.

```powershell
.\mvnw.cmd clean verify
java -jar target/finanzas-personales-1.0.0.war
```

En Linux/macOS: `sh mvnw clean verify` y el mismo comando Java.

En Windows, `VERIFICAR-ANTES-DEL-PUSH.cmd` ejecuta la compilacion definitiva, comprueba MVC/Swagger y la coleccion Postman mediante HTTP real, y consulta el estado de GitHub. Requiere Java, Git, Node.js 20 o posterior e internet. No hace push. Si todo pasa, guarda `target/validacion-final.json` y muestra `VALIDACION COMPLETA`.

| Acceso local | URL |
|---|---|
| MVC | http://localhost:8080/login |
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| Login REST | POST http://localhost:8080/api/auth/login |

Perfil predeterminado: `demo`, con H2 en memoria. Los cambios se pierden al detener la aplicacion.

| Cuenta demo | Password | Rol |
|---|---|---|
| admin@u.icesi.edu.co | Demo2026! | ADMINISTRADOR |
| estudiante@u.icesi.edu.co | Demo2026! | USUARIO |

El WAR anterior en `artifacts/` pertenece al taller MVC. Para este taller se utiliza el WAR reconstruido en `target/`.

## Autenticacion

Enviar `POST /api/auth/login` con JSON:

```json
{"correo":"estudiante@u.icesi.edu.co","password":"Demo2026!"}
```

La respuesta contiene `accessToken`, `tokenType`, `issuedAt`, `expiresAt`, `correo` y `roles`. En las siguientes solicitudes: `Authorization: Bearer <accessToken>`. En Swagger, pegar solo el token en **Authorize**.

El JWT contiene `sub`, `iss`, `iat`, `nbf`, `exp`, roles y autoridades; se firma con HS256 y dura 3600 segundos por defecto. Cada solicitud vuelve a consultar el usuario y sus autoridades: desactivar la cuenta invalida su acceso y revocar un permiso se aplica inmediatamente. Cambiar la contrasena no revoca automaticamente los JWT existentes; vencen al expirar. La API no crea sesiones ni admite HTTP Basic. Una sesion MVC no autentica la API y un JWT no inicia sesion en MVC.

Registro publico: `POST /api/auth/registro`. La ruta anterior `/api/registro` sigue disponible. Solo se asigna USUARIO; el JSON no admite roles ni passwordHash. Perfil y cambio de contrasena: `GET /api/me` y `PUT /api/me/password`.

## Recursos

Todas las filas incluyen GET listado, GET por clave, POST, PUT por clave y DELETE por clave. POST devuelve 201, PUT/GET 200 y DELETE 204. Las creaciones principales incluyen Location. Swagger documenta los cuerpos y tipos de todos los metodos.

| Recurso | Ruta | Clave |
|---|---|---|
| Usuario | /api/admin/usuarios | id |
| Rol | /api/admin/roles | id |
| Permiso | /api/admin/permisos | id |
| UsuarioRol | /api/admin/usuarios-roles | usuarioId/rolId |
| RolPermiso | /api/admin/roles-permisos | rolId/permisoId |
| Moneda | /api/monedas | id |
| TipoCuenta | /api/tipos-cuenta | id |
| Categoria | /api/categorias | id |
| Cuenta | /api/cuentas | id |
| Movimiento | /api/movimientos | id |
| MovimientoRecurrente | /api/movimientos-recurrentes | id |
| Presupuesto | /api/presupuestos | id |
| Deuda | /api/deudas | id |
| AbonoDeuda | /api/abonos-deuda | id |
| PlanAhorro | /api/planes-ahorro | id |
| InvitacionPlan | /api/invitaciones-plan | id |
| AportePlan | /api/aportes-plan | id |
| ParticipacionPlan | /api/participaciones-plan | planId/usuarioId |

Ejemplo de clave compuesta: `/api/participaciones-plan/1/2`. En PUT, las claves del JSON deben coincidir con la ruta. Los identificadores simples y fechas de creacion son generados por el servidor.

## Autorizacion y reglas

- ADMINISTRADOR gestiona usuarios, roles, permisos y catalogos. Se conserva el ultimo administrador activo, al menos un rol por usuario y un permiso por rol.
- La operacion financiera requiere ADMINISTRADOR o GESTIONAR_FINANZAS. Los usuarios consultan y modifican sus datos. Un identificador ajeno responde 404 y una escritura no autorizada 403.
- Monedas, tipos de cuenta y categorias globales son consultables; solo el administrador modifica los catalogos globales.
- Los movimientos ajustan el saldo de la cuenta al crearse, editarse o eliminarse, dentro de una transaccion con bloqueo. Una categoria debe corresponder al tipo de movimiento y ser global o del mismo usuario.
- El saldo pendiente de una deuda se calcula a partir de su monto y abonos. No se acepta un abono mayor al saldo. Al saldarse cambia el estado a SALDADA; eliminar un abono revierte el saldo.
- Solo el propietario administra un plan y sus participantes. El invitado acepta o rechaza su invitacion; aceptar crea su participacion. No se invita a planes individuales ni se cambia el destinatario al responder.
- Un aporte requiere pertenecer al plan y usar una cuenta propia. Los aportes representan ahorro reservado; no generan otro movimiento ni descuentan dos veces el saldo. Las recurrencias se almacenan como programaciones; este taller no ejecuta un planificador automatico.
- Las relaciones existentes impiden eliminaciones destructivas y devuelven 409. Las entradas invalidas responden 400; credenciales o tokens invalidos, 401. Los errores REST tienen formato ProblemDetail y no incluyen trazas ni SQL.

## Pruebas

`clean verify` ejecuta pruebas de servicios, MVC y REST, ademas del control JaCoCo existente para los servicios del taller anterior. Las pruebas nuevas cubren las 18 entidades, JWT, permisos, datos ajenos, saldos, asociaciones y Swagger. Los resultados actuales se generan en `target/surefire-reports/` y `target/site/jacoco/`.

En Postman:

1. Importar `postman/Finanzas.postman_collection.json` y `postman/Local.postman_environment.json`.
2. Seleccionar **Finanzas local demo** y ajustar `baseUrl` si cambia el puerto o el contexto Tomcat.
3. Abrir **Runner**, seleccionar toda la coleccion y ejecutar en orden.

La coleccion obtiene tokens e identificadores, prueba los CRUD y errores, y elimina los datos creados. Sus cuentas demo no deben usarse en produccion. Si una ejecucion se interrumpe, reiniciar el perfil demo antes de repetirla. No cambiar el orden.

Tambien puede ejecutarse la misma secuencia y sus scripts con Node.js 20 o posterior, sin instalar paquetes:

```bash
node scripts/verificar-api.mjs
```

Para otro puerto: `node scripts/verificar-api.mjs postman/Local.postman_environment.json http://localhost:18084`. El reporte sin tokens se guarda en `target/postman-report.json`. Este ejecutor soporta los scripts de esta coleccion; no pretende reemplazar todas las funciones de Postman.

Si Node.js esta disponible, Maven tambien ejecuta la coleccion y sus scripts mediante MockMvc, sin abrir puertos. Sin Node se omite esa prueba; ejecutar entonces la coleccion desde Postman. Esto complementa la comprobacion HTTP local. El despliegue no forma parte del alcance actual.

## Estructura

`controller/` conserva MVC; `api/` contiene REST; `api/dto/` y `api/mapper/` separan los contratos y sus conversiones MapStruct. `api/service/` contiene transacciones, propiedad de recursos y reglas financieras. `service/impl/` conserva la administracion existente compartida con MVC. `security/` contiene UserDetailsService, JwtService, SecurityFilter y las dos SecurityFilterChain ordenadas.

Se tomaron como referencia la separacion MVC/REST y las cadenas de seguridad de [262WebApp6](https://github.com/Domiciano/262WebApp6) y las notas REST/JWT/Postman de [Compunet2-252](https://github.com/Domiciano/Compunet2-252). Se consultaron las copias locales disponibles; no se pudo actualizar GitHub durante esta preparacion.

Guia de demostracion y validacion: [docs/ENTREGA_BACKEND.md](docs/ENTREGA_BACKEND.md).
