document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('signup-form');

  form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const email    = document.getElementById('email').value.trim();
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;

    try {
      const resp = await fetch('http://localhost:8080/TestControllerPostgre/api/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, username, password })
      });

      if (resp.status === 201) {
        const user = await resp.json();
        console.log('Registrado:', user);
        // Redirige al login tras registro exitoso
        window.location.href = '/Redwood Grill/index.html';
      } else if (resp.status === 409) {
        const err = await resp.json();
        alert(err.error || 'El correo ya está registrado');
      } else {
        const err = await resp.json();
        alert(err.error || 'Error de registro');
      }
    } catch (err) {
      console.error(err);
      alert('No se pudo conectar con el servidor.');
    }
  });
});