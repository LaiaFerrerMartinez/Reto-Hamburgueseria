document.addEventListener('DOMContentLoaded', () => {
  // 1) Referencias al DOM
  const container   = document.querySelector('.cards-container');
  const lowBtn      = document.getElementById('low-price');
  const highBtn     = document.getElementById('high-price');
  const searchInput = document.querySelector('.buscador input');

  // 2) Array donde guardamos los productos “healthy”
  let productos = [];

  // 3) Fetch genérico al backend, con sort opcional
  async function fetchProducts(sort) {
    const url = new URL('http://localhost:8080/TestControllerPostgre/api/products');
    url.searchParams.set('type', 'healthy');
    if (sort) url.searchParams.set('sort', sort);
    const resp = await fetch(url);
    if (!resp.ok) throw new Error(`HTTP ${resp.status}`);
    return resp.json();
  }

  // 4) Renderiza un array de productos en el contenedor, incluyendo overlay
  function renderProducts(arr) {
    container.innerHTML = '';
    arr.forEach(prod => {
      container.insertAdjacentHTML('beforeend', `
        <div class="card">
          <div class="card-content">
            <img src="${prod.img}" alt="${prod.nombre}">
            <div class="texto-card">
              <div class="titulo-card"><p>${prod.nombre}</p></div>
              <div class="footer-card">
                <p>${prod.precioFormateado}$</p>
                <button onclick="addToCart(${prod.id})">I WANT IT!</button>
              </div>
            </div>
          </div>
          <div class="overlay">
            <p>${prod.descripcion || prod.desc || prod.descripcion_producto}</p>
          </div>
        </div>
      `);
    });
  }

  // 5) Carga inicial SIN ordenar
  async function initialLoad() {
    try {
      productos = await fetchProducts(null);
      renderProducts(productos);
    } catch (err) {
      console.error('Error cargando healthy:', err);
      container.innerHTML = '<p>Error cargando productos.</p>';
    }
  }

  // 6) Recarga CON orden al hacer click
  async function reloadWithSort(sort) {
    try {
      productos = await fetchProducts(sort);
      applyFilterAndRender();
    } catch (err) {
      console.error('Error al ordenar healthy:', err);
    }
  }

  // 7) Filtrado por búsqueda + render
  function applyFilterAndRender() {
    const term = searchInput.value.trim().toLowerCase();
    const filtered = productos.filter(p => {
      const name = p.nombre.toLowerCase();
      const desc = (p.descripcion || p.desc || p.descripcion_producto || '').toLowerCase();
      return name.includes(term) || desc.includes(term);
    });
    renderProducts(filtered);
  }

  // 8) Enlazamos eventos
  lowBtn.addEventListener('click',  () => reloadWithSort('asc'));
  highBtn.addEventListener('click', () => reloadWithSort('desc'));
  searchInput.addEventListener('input', applyFilterAndRender);

  // 9) Ejecutamos la carga inicial
  initialLoad();
});
