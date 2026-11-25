/** Decodifica el payload de un JWT (sin verificar la firma: eso lo hace el backend). */
export function decodeToken(token) {
  try {
    const payload = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
    const padded = payload.padEnd(Math.ceil(payload.length / 4) * 4, '=');
    const bytes = Uint8Array.from(window.atob(padded), (char) => char.charCodeAt(0));
    return JSON.parse(new TextDecoder().decode(bytes));
  } catch {
    return null;
  }
}

/** Milisegundos que le quedan al token; 0 si ya vencio o es invalido. */
export function remainingTime(claims) {
  if (!claims?.exp) {
    return 0;
  }
  return Math.max(0, claims.exp * 1000 - Date.now());
}
