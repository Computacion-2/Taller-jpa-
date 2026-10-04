-- Catalogos y roles para primer arranque; preserva valores ya existentes.
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
