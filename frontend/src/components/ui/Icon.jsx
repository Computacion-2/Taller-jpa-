// Iconos SVG de trazo, propios del proyecto (sin librerias externas).
const PATHS = {
  home: 'M3 11l9-8 9 8M5 10v10h5v-6h4v6h5V10',
  wallet: 'M3 7h15a3 3 0 013 3v8a2 2 0 01-2 2H5a2 2 0 01-2-2V7zm0 0V6a2 2 0 012-2h11M16 13h2',
  swap: 'M7 4L3 8l4 4M3 8h14M17 20l4-4-4-4M21 16H7',
  repeat: 'M17 2l4 4-4 4M3 11V9a3 3 0 013-3h15M7 22l-4-4 4-4M21 13v2a3 3 0 01-3 3H3',
  tag: 'M3 12V4a1 1 0 011-1h8l9 9-9 9-9-9zM8 8h.01',
  pie: 'M21 12A9 9 0 1112 3v9h9zM15 3.5A9 9 0 0120.5 9H15V3.5z',
  debt: 'M4 6h16v12H4zM4 10h16M8 15h3',
  piggy: 'M5 11a7 6 0 0113-2h2v4l-2 1v3h-3v-2H9v2H6v-3a6 6 0 01-1-3zM15 10h.01M2 9a2 2 0 002 2',
  mail: 'M3 6h18v12H3zM3 7l9 6 9-6',
  users: 'M16 20v-2a4 4 0 00-4-4H6a4 4 0 00-4 4v2M9 10a4 4 0 100-8 4 4 0 000 8zM22 20v-2a4 4 0 00-3-3.9M16 2.1a4 4 0 010 7.8',
  shield: 'M12 3l8 3v6c0 5-3.5 8-8 9-4.5-1-8-4-8-9V6l8-3zM9 12l2 2 4-4',
  key: 'M15 7a4 4 0 11-3.9 5H3v4h3v-2h2v2h3.1A4 4 0 0115 7zM16 11h.01',
  link: 'M10 14a4 4 0 005.7 0l3-3a4 4 0 00-5.7-5.7l-1 1M14 10a4 4 0 00-5.7 0l-3 3a4 4 0 005.7 5.7l1-1',
  coin: 'M12 21a9 9 0 100-18 9 9 0 000 18zM15 9.5c-.5-1-1.6-1.5-3-1.5-1.7 0-3 .9-3 2s1.3 1.7 3 2 3 .9 3 2-1.3 2-3 2c-1.4 0-2.5-.5-3-1.5M12 6v2M12 16v2',
  layers: 'M12 3l9 5-9 5-9-5 9-5zM3 13l9 5 9-5M3 17l9 5 9-5',
  user: 'M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2M12 11a4 4 0 100-8 4 4 0 000 8z',
  logout: 'M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4M16 17l5-5-5-5M21 12H9',
  plus: 'M12 5v14M5 12h14',
  edit: 'M4 20h4L19 9l-4-4L4 16v4zM14 6l4 4',
  trash: 'M4 7h16M10 11v6M14 11v6M6 7l1 13h10l1-13M9 7V4h6v3',
  eye: 'M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12zM12 15a3 3 0 100-6 3 3 0 000 6z',
  eyeOff: 'M3 3l18 18M10.6 5.1A10 10 0 0112 5c6.5 0 10 7 10 7a17 17 0 01-3.1 4M6.6 6.6C3.9 8.4 2 12 2 12s3.5 7 10 7a9.7 9.7 0 005.4-1.6M9.9 9.9a3 3 0 004.2 4.2',
  search: 'M11 19a8 8 0 100-16 8 8 0 000 16zM21 21l-4.3-4.3',
  menu: 'M4 6h16M4 12h16M4 18h16',
  x: 'M18 6L6 18M6 6l12 12',
  check: 'M20 6L9 17l-5-5',
  alert: 'M12 9v4M12 17h.01M10.3 3.9L1.8 18a2 2 0 001.7 3h17a2 2 0 001.7-3L13.7 3.9a2 2 0 00-3.4 0z',
  info: 'M12 22a10 10 0 100-20 10 10 0 000 20zM12 16v-4M12 8h.01',
  arrowLeft: 'M19 12H5M12 19l-7-7 7-7',
  chevronLeft: 'M15 18l-6-6 6-6',
  chevronRight: 'M9 18l6-6-6-6',
  refresh: 'M21 12a9 9 0 11-3-6.7L21 8M21 3v5h-5',
  lock: 'M5 11h14v10H5zM8 11V7a4 4 0 018 0v4',
  trendUp: 'M3 17l6-6 4 4 8-8M14 7h7v7',
  trendDown: 'M3 7l6 6 4-4 8 8M14 17h7v-7',
  calendar: 'M3 5h18v16H3zM16 3v4M8 3v4M3 10h18',
  toggle: 'M7 7h10a5 5 0 010 10H7A5 5 0 017 7zM7 15a3 3 0 100-6 3 3 0 000 6z',
};

/** <Icon name="wallet" /> — hereda el color del texto (currentColor). */
export default function Icon({ name, size = 20, className = '', title }) {
  const path = PATHS[name];
  if (!path) {
    return null;
  }
  return (
    <svg
      className={`icon ${className}`}
      width={size}
      height={size}
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.8"
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden={title ? undefined : true}
      role={title ? 'img' : undefined}
    >
      {title && <title>{title}</title>}
      <path d={path} />
    </svg>
  );
}
