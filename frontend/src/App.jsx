// Pantalla temporal mientras se integran los servicios, las rutas y las paginas.
export default function App() {
  return (
    <main style={{ display: 'grid', placeItems: 'center', minHeight: '100vh', padding: 'var(--space-6)' }}>
      <div style={{ textAlign: 'center' }}>
        <h1 style={{ color: 'var(--color-primary)' }}>Finanzas personales</h1>
        <p className="muted">SPA en React para la API REST de Spring Boot.</p>
      </div>
    </main>
  );
}
