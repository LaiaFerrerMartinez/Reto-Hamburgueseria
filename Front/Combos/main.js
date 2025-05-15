document.addEventListener('DOMContentLoaded', () => {
  // 1) Referencias al DOM
  const [menuContainer, menuInfContainer] = document.querySelectorAll('.cards-container');
  const lowBtn      = document.getElementById('low-price');
  const highBtn     = document.getElementById('high-price');
  const searchInput = document.querySelector('.buscador input');

  // 2) Arrays donde guardamos los productos
  let listMenu = [];
  let listInf  = [];

  // 3) Configuración de tipos
  const types = [
    { key: 'menu',          list: () => listMenu, setter: v => listMenu = v,    container: menuContainer,    cardClass: 'card'     },
    { key: 'menu-infantil', list: () => listInf,  setter: v => listInf  = v,    container: menuInfContainer, cardClass: 'card-inf' }
  ];

  // 4) Fetch genérico (sin sort)
  async function fetchProducts(type) {
    const url = new URL('http://localhost:8080/TestControllerPostgre/api/products');
    url.searchParams.set('type', type);
    const resp = await fetch(url);
    if (!resp.ok) throw new Error(`HTTP ${resp.status}`);
    return resp.json();
  }

  // 5) Renderizar array en un contenedor, con overlay de descripción
  function renderProducts(container, products, cardClass) {
    container.innerHTML = '';
    products.forEach(prod => {
      container.insertAdjacentHTML('beforeend', `
        <div class="${cardClass}">
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

  // 6) Carga inicial SIN ordenar
  async function initialLoad() {
    try {
      for (const t of types) {
        const prods = await fetchProducts(t.key);
        t.setter(prods);
        renderProducts(t.container, prods, t.cardClass);
      }
    } catch (err) {
      console.error('Error al cargar combos:', err);
      types.forEach(({ container }) =>
        container.innerHTML = '<p>Error cargando productos.</p>'
      );
    }
  }

  // 7) Función que aplica filtro de búsqueda y orden y renderiza
  function filterSortRender(sort) {
    const term = searchInput.value.trim().toLowerCase();
    types.forEach(t => {
      // clonamos la lista original
      let arr = t.list().slice();

      // filtramos por nombre o descripción
      if (term) {
        arr = arr.filter(p => {
          const name = p.nombre.toLowerCase();
          const desc = (p.descripcion || p.desc || p.descripcion_producto || '').toLowerCase();
          return name.includes(term) || desc.includes(term);
        });
      }

      // ordenamos si hace falta
      if (sort) {
        arr.sort((a, b) => {
          const na = parseFloat(a.precioValor ?? a.precioFormateado.replace(',', '.'));
          const nb = parseFloat(b.precioValor ?? b.precioFormateado.replace(',', '.'));
          return sort === 'asc' ? na - nb : nb - na;
        });
      }

      renderProducts(t.container, arr, t.cardClass);
    });
  }

  // 8) Enlazar eventos
  lowBtn.addEventListener('click',  () => filterSortRender('asc'));
  highBtn.addEventListener('click', () => filterSortRender('desc'));
  searchInput.addEventListener('input', () => filterSortRender(
    // al escribir no cambiamos el sort, así que pasamos null para mantener sólo filtro
    null
  ));

  // 9) Ejecutar carga inicial
  initialLoad();
});
