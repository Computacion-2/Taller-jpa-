# Entrega Backend

## Alcance

El despliegue queda fuera de esta entrega por ahora. Se entregan el codigo, los parches, las pruebas y las instrucciones de ejecucion local.

## Video de demostracion (4 a 6 minutos)

1. Mostrar MVC: login, usuarios/roles/permisos y logout.
2. Abrir Swagger, mostrar login JWT y autorizar; explicar iat, nbf, exp y roles sin mostrar secretos productivos.
3. Ejecutar toda la coleccion en Postman Runner y mostrar el resumen de resultados.
4. Mostrar un movimiento y el cambio de saldo, un abono y el saldo pendiente, y la aceptacion de una invitacion.
5. Mostrar 401 sin token, 403 para administracion con usuario normal y 404 al consultar una cuenta ajena.
6. Mostrar `mvnw clean verify` y los resultados.

Los integrantes deben grabar el video y agregar su enlace a la entrega. Este archivo es el guion; no es un video ya grabado.

## Referencias y limites de validacion

Validacion local del 8 de noviembre de 2026, despues de corregir Swagger: 105 pruebas, 0 fallos, 0 errores y 0 omitidas, con el POM temporal descrito abajo. La coleccion completo 146 solicitudes y 153 comprobaciones usando MockMvc. El empaquetado se completo con un nombre temporal diferente porque Windows mantenia abierto el WAR de la aplicacion en ejecucion. Se cumplieron los umbrales JaCoCo existentes de los cuatro servicios del taller anterior; ese umbral no representa la cobertura de todo el backend.

La integracion local parte del commit publicado `80a6f7cb75cad53d009ef96e596004a2ccdae69f`. Se conservaron sus cambios y los cuatro commits de integracion en `Taller-jpa-entrega`, con sus fechas y autores. La correccion posterior agrega respuestas y esquemas explicitos a las 99 operaciones REST, identifica correctamente las rutas publicas y comprueba la cobertura de Swagger contra los metodos registrados por Spring. La prueba adicional del companero se conserva.

El POM de entrega usa Spring Boot 3.5.11, MapStruct 1.5.5.Final, JJWT 0.12.6 y springdoc 2.8.9. La red de preparacion no pudo descargar springdoc 2.8.9 ni Jackson YAML 2.19.4. Las verificaciones locales utilizaron un POM temporal, excluido del parche, con springdoc 2.3.0 y Jackson YAML 2.15.4, manteniendo el resto de versiones. Por ello debe repetirse `mvnw clean verify` con internet antes de entregar el WAR definitivo.

La aplicacion demo iniciada por el usuario se comprobo desde el navegador: login MVC, usuarios, roles y permisos; Swagger permitio obtener un JWT y consultar `/api/me` con respuesta 200. La sesion MVC por si sola obtuvo 401 en la API, como corresponde. Esta instancia corresponde al WAR anterior a la correccion de Swagger; debe reiniciarse con el WAR reconstruido para visualizar los cambios. La coleccion completa se verifico con MockMvc; su ejecucion completa por HTTP real sigue pendiente.

Para completar las comprobaciones pendientes en Windows, ejecutar `VERIFICAR-ANTES-DEL-PUSH.cmd`. Usa el POM definitivo, inicia y detiene una instancia demo local, comprueba MVC y Swagger, ejecuta Postman por HTTP y verifica que GitHub no tenga commits pendientes de integrar. No hace push. Solo genera `target/validacion-final.json` cuando todas las comprobaciones terminan correctamente.
