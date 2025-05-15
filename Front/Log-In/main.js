document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('loginForm');

  form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;
    // El campo email lo puedes enviar o ignorar según tu API
    const email    = document.getElementById('email').value.trim();

    try {
      const url = 'http://localhost:8080/TestControllerPostgre/api/login';

const resp = await fetch(url, {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ username, password })
});


      if (resp.ok) {
        // Login ok: recibes { id, username, email }
        const user = await resp.json();
        console.log('Autenticado:', user);
        // Rediriges al index
        window.location.href = '/Redwood Grill/index.html';
      } else if (resp.status === 401) {
        const err = await resp.json();
        alert(err.error || 'Credenciales inválidas');
      } else {
        alert('Error inesperado: ' + resp.status);
      }
    } catch (err) {
      console.error(err);
      alert('No se pudo conectar con el servidor.');
    }
  });
});
