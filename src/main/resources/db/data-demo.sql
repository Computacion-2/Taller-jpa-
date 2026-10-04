INSERT INTO moneda (moneda_id,codigo,nombre,simbolo) SELECT 1,'COP','Peso colombiano','$' WHERE NOT EXISTS (SELECT 1 FROM moneda WHERE moneda_id = 1);
INSERT INTO moneda (moneda_id,codigo,nombre,simbolo) SELECT 2,'USD','Dolar estadounidense','US$' WHERE NOT EXISTS (SELECT 1 FROM moneda WHERE moneda_id = 2);
INSERT INTO permiso (permiso_id,nombre,descripcion,estado) SELECT 1,'ADMINISTRAR_CUENTAS','Permiso base','ACTIVO' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE permiso_id = 1);
INSERT INTO permiso (permiso_id,nombre,descripcion,estado) SELECT 2,'GESTIONAR_FINANZAS','Permiso base','ACTIVO' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE permiso_id = 2);
INSERT INTO rol (rol_id,nombre,descripcion,estado) SELECT 1,'ADMINISTRADOR','Rol base','ACTIVO' WHERE NOT EXISTS (SELECT 1 FROM rol WHERE rol_id = 1);
INSERT INTO rol_permiso (rol_id,permiso_id,fecha_asignacion) SELECT 1,1,CURRENT_DATE WHERE NOT EXISTS (SELECT 1 FROM rol_permiso WHERE rol_id=1);
INSERT INTO rol (rol_id,nombre,descripcion,estado) SELECT 2,'USUARIO','Rol base','ACTIVO' WHERE NOT EXISTS (SELECT 1 FROM rol WHERE rol_id = 2);
INSERT INTO rol_permiso (rol_id,permiso_id,fecha_asignacion) SELECT 2,2,CURRENT_DATE WHERE NOT EXISTS (SELECT 1 FROM rol_permiso WHERE rol_id=2);
INSERT INTO tipo_cuenta (tipo_cuenta_id,nombre,estado) SELECT 1,'Efectivo','ACTIVO' WHERE NOT EXISTS (SELECT 1 FROM tipo_cuenta WHERE tipo_cuenta_id = 1);
INSERT INTO tipo_cuenta (tipo_cuenta_id,nombre,estado) SELECT 2,'Cuenta bancaria','ACTIVO' WHERE NOT EXISTS (SELECT 1 FROM tipo_cuenta WHERE tipo_cuenta_id = 2);
INSERT INTO tipo_cuenta (tipo_cuenta_id,nombre,estado) SELECT 3,'Billetera digital','ACTIVO' WHERE NOT EXISTS (SELECT 1 FROM tipo_cuenta WHERE tipo_cuenta_id = 3);
-- Datos ficticios. Ambas cuentas usan Demo2026! exclusivamente en el perfil demo.
INSERT INTO usuario (usuario_id,nombre_completo,correo,password_hash,moneda_id,estado,fecha_registro)
VALUES (1,'Administrador demo','admin@u.icesi.edu.co','$2a$10$tzqy6YKsP5Q1mW2EULoBbeNrRVpXcAcaqYZbuiiC2bk28YOv/SSR6',1,'ACTIVO',CURRENT_DATE),
(2,'Estudiante demo','estudiante@u.icesi.edu.co','$2a$10$tzqy6YKsP5Q1mW2EULoBbeNrRVpXcAcaqYZbuiiC2bk28YOv/SSR6',1,'ACTIVO',CURRENT_DATE);
INSERT INTO usuario_rol VALUES (1,1,CURRENT_DATE),(2,2,CURRENT_DATE);
INSERT INTO cuenta VALUES (1,2,1,'Efectivo estudiante',500000,450000,'ACTIVA',CURRENT_DATE,NULL);
INSERT INTO categoria VALUES (1,NULL,'Alimentacion','GASTO','Categoria compartida','ACTIVO',CURRENT_DATE),
(2,NULL,'Mensualidad','INGRESO','Categoria compartida','ACTIVO',CURRENT_DATE),
(3,2,'Transporte personal','GASTO','Categoria del estudiante','ACTIVO',CURRENT_DATE);
INSERT INTO movimiento VALUES (1,1,1,'GASTO',50000,CURRENT_DATE,'Mercado',NULL,CURRENT_DATE);
INSERT INTO movimiento_recurrente VALUES (1,2,1,3,'GASTO',20000,'SEMANAL',CURRENT_DATE,DATEADD('DAY',90,CURRENT_DATE),DATEADD('DAY',7,CURRENT_DATE),'ACTIVO','Transporte semanal');
INSERT INTO presupuesto VALUES (1,2,1,YEAR(CURRENT_DATE),MONTH(CURRENT_DATE),300000,CURRENT_DATE);
INSERT INTO deuda VALUES (1,2,NULL,'Companero de clase','POR_PAGAR',100000,CURRENT_DATE,DATEADD('DAY',15,CURRENT_DATE),'Prestamo', 'PENDIENTE',80000);
INSERT INTO abono_deuda VALUES (1,1,20000,CURRENT_DATE,'Primer abono');
INSERT INTO plan_ahorro VALUES (1,2,'Viaje de fin de semestre','Ahorro compartido',2000000,DATEADD('MONTH',6,CURRENT_DATE),'COMPARTIDO','ACTIVO',CURRENT_DATE);
INSERT INTO invitacion_plan VALUES (1,1,1,2,'PENDIENTE',CURRENT_DATE,NULL);
INSERT INTO participacion_plan VALUES (1,2,1000000,CURRENT_DATE,'ACTIVO');
INSERT INTO aporte_plan VALUES (1,1,2,1,50000,CURRENT_DATE,'ACTIVO',CURRENT_DATE);
