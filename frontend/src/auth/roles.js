// Autoridades que emite el backend en el claim "authorities" del JWT.
export const ROLE_ADMIN = 'ROLE_ADMINISTRADOR';
export const PERM_FINANZAS = 'GESTIONAR_FINANZAS';

/** Quien puede usar los modulos financieros (misma regla que WebSecurityConfig). */
export const FINANCE_ACCESS = [ROLE_ADMIN, PERM_FINANZAS];
/** Quien puede usar los modulos administrativos (/api/admin/**). */
export const ADMIN_ACCESS = [ROLE_ADMIN];

/** Nombre legible de un rol a partir de su autoridad ROLE_X. */
export function roleLabel(authority) {
  const name = authority.replace(/^ROLE_/, '').toLowerCase();
  return name.charAt(0).toUpperCase() + name.slice(1);
}
