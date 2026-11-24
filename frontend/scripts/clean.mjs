// Borra dist/ y target/ antes de compilar. Se hace a mano porque en Windows, con rutas que
// contienen emojis, Vite no logra vaciar dist/ y quedan archivos de compilaciones anteriores.
import { existsSync, readdirSync, rmdirSync, unlinkSync } from 'node:fs';
import { join, resolve } from 'node:path';

function removeDir(dir) {
  if (!existsSync(dir)) {
    return;
  }
  for (const entry of readdirSync(dir, { withFileTypes: true })) {
    const path = join(dir, entry.name);
    if (entry.isDirectory()) {
      removeDir(path);
    } else {
      unlinkSync(path);
    }
  }
  rmdirSync(dir);
}

const root = resolve(import.meta.dirname, '..');
removeDir(join(root, 'dist'));
removeDir(join(root, 'target'));
