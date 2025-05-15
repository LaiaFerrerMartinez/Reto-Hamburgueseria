document.addEventListener('DOMContentLoaded', () => {
  // 1) Referencias al DOM
  const container   = document.querySelector('.cards-container');
  const lowBtn      = document.getElementById('low-price');
  const highBtn     = document.getElementById('high-price');
  const searchInput = document.querySelector('.buscador input');

  // 2) Array donde guardamos los productos “side”
  let productos = [];

  // 3) Fetch genérico al backend, con sort opcional
  async function fetchProducts(sort) {
    const url = new URL('http://localhost:8080/TestControllerPostgre/api/products');
    url.searchParams.set('type', 'side');
    if (sort) url.searchParams.set('sort', sort);
    const resp = await fetch(url);
    if (!resp.ok) throw new Error(`HTTP ${resp.status}`);
    return resp.json();
  }

  // 4) Renderiza un array de productos en el contenedor, incluyendo overlay de descripción
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

  // 5) Inicializar: carga sin ordenar
  async function initialLoad() {
    try {
      productos = await fetchProducts(null);
      renderProducts(productos);
    } catch (err) {
      console.error('Error cargando sides:', err);
      container.innerHTML = '<p>Error cargando productos.</p>';
    }
  }

  // 6) Filtra por búsqueda y ordena, luego renderiza
  function filterSortRender(sort) {
    const term = searchInput.value.trim().toLowerCase();
    let arr = productos.slice();

    // filtro por nombre o descripción
    if (term) {
      arr = arr.filter(p => {
        const name = p.nombre.toLowerCase();
        const desc = (p.desc || '').toLowerCase();
        return name.includes(term) || desc.includes(term);
      });
    }

    // orden si se pide
    if (sort) {
      arr.sort((a, b) => {
        const na = parseFloat((a.precioValor ?? a.precioFormateado).toString().replace(',', '.'));
        const nb = parseFloat((b.precioValor ?? b.precioFormateado).toString().replace(',', '.'));
        return sort === 'asc' ? na - nb : nb - na;
      });
    }

    renderProducts(arr);
  }

  // 7) Enlazamos eventos
  lowBtn.addEventListener('click',  () => filterSortRender('asc'));
  highBtn.addEventListener('click', () => filterSortRender('desc'));
  searchInput.addEventListener('input', () => filterSortRender(null));

  // 8) Ejecutar carga inicial
  initialLoad();
});
