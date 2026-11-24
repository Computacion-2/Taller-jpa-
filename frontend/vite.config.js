import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';

// base debe coincidir con el nombre de la app en webapps/ de Tomcat (finanzas-front -> /finanzas-front/).
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '');
  return {
    base: env.VITE_BASE_PATH || '/',
    plugins: [react()],
    server: { port: 5173, strictPort: true },
    preview: { port: 4173, strictPort: true },
    build: { outDir: 'dist', emptyOutDir: true, sourcemap: false },
  };
});
