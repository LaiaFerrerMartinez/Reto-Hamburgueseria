document.addEventListener('DOMContentLoaded', () => {
  // 1) Referencias a DOM
  const container = document.querySelector('.cards-container');
  const lowBtn    = document.getElementById('low-price');
  const highBtn   = document.getElementById('high-price');

  // 2) Array donde guardamos los productos “dessert”
  let productos = [];

  // 3) Fetch para cargar productos, con parámetro opcional sort
  async function fetchProducts(sort) {
    const url = new URL('http://localhost:8080/TestControllerPostgre/api/products');
    url.searchParams.set('type', 'dessert');
    if (sort) url.searchParams.set('sort', sort);
    const resp = await fetch(url);
    if (!resp.ok) throw new Error(`HTTP ${resp.status}`);
    return resp.json();
  }

  // 4) Renderiza un array en el contenedor
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
      console.error('Error cargando desserts:', err);
      container.innerHTML = '<p>Error cargando productos.</p>';
    }
  }

  // 6) Recarga CON orden al hacer click
  async function reloadWithSort(sort) {
    try {
      // Vuelves a pedir al back con sort
      productos = await fetchProducts(sort);
      renderProducts(productos);
    } catch (err) {
      console.error('Error al ordenar desserts:', err);
    }
  }

  // 7) Enlazamos botones
  lowBtn.addEventListener('click',  () => reloadWithSort('asc'));
  highBtn.addEventListener('click', () => reloadWithSort('desc'));

  // 8) Ejecutamos carga inicial
  initialLoad();
});
