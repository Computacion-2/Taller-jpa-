import { ADMIN_ACCESS, FINANCE_ACCESS } from '../auth/roles';

/**
 * Menu lateral. Cada seccion declara que autoridades la pueden ver (allow);
 * las mismas restricciones se aplican en las rutas con <ProtectedRoute allow={...}>.
 */
export const NAV_SECTIONS = [
  {
    title: 'General',
    items: [
      { to: '/', label: 'Inicio', icon: 'home', end: true },
      { to: '/perfil', label: 'Mi perfil', icon: 'user' },
    ],
  },
  {
    title: 'Finanzas',
    allow: FINANCE_ACCESS,
    items: [
      { to: '/cuentas', label: 'Cuentas', icon: 'wallet' },
      { to: '/movimientos', label: 'Movimientos', icon: 'swap' },
      { to: '/recurrentes', label: 'Recurrentes', icon: 'repeat' },
      { to: '/categorias', label: 'Categorías', icon: 'tag' },
      { to: '/presupuestos', label: 'Presupuestos', icon: 'pie' },
      { to: '/deudas', label: 'Deudas', icon: 'debt' },
      { to: '/planes', label: 'Planes de ahorro', icon: 'piggy' },
      { to: '/invitaciones', label: 'Invitaciones', icon: 'mail' },
    ],
  },
  {
    title: 'Administración',
    allow: ADMIN_ACCESS,
    items: [
      { to: '/admin/usuarios', label: 'Usuarios', icon: 'users' },
      { to: '/admin/roles', label: 'Roles', icon: 'shield' },
      { to: '/admin/permisos', label: 'Permisos', icon: 'key' },
      { to: '/admin/asignaciones', label: 'Asignaciones', icon: 'link' },
      { to: '/admin/monedas', label: 'Monedas', icon: 'coin' },
      { to: '/admin/tipos-cuenta', label: 'Tipos de cuenta', icon: 'layers' },
    ],
  },
];
