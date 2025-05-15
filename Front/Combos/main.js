document.addEventListener('DOMContentLoaded', () => {
  const [menuContainer, menuInfContainer] = document.querySelectorAll('.cards-container');
  const lowBtn  = document.getElementById('low-price');
  const highBtn = document.getElementById('high-price');

  // Arrays donde guardamos los productos
  let listMenu = [];
  let listInf  = [];

  // Configuración de tipos
  const types = [
    { key: 'menu',          list: () => listMenu, setter: v => listMenu = v,    container: menuContainer,    cardClass: 'card'     },
    { key: 'menu-infantil', list: () => listInf,  setter: v => listInf  = v,    container: menuInfContainer, cardClass: 'card-inf' }
  ];

  // Fetch genérico: ya no usamos sort en la URL
  async function fetchProducts(type) {
    const url = new URL('http://localhost:8080/TestControllerPostgre/api/products');
    url.searchParams.set('type', type);
    const resp = await fetch(url);
    if (!resp.ok) throw new Error(`HTTP ${resp.status}`);
    return resp.json();
  }

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
                <p>${prod.precioFormateado}</p>
                <button onclick="addToCart(${prod.id})">I WANT IT!</button>
              </div>
            </div>
          </div>
        </div>
      `);
    });
  }

  // Carga inicial sin ordenar
  async function initialLoad() {
    try {
      for (const t of types) {
        const prods = await fetchProducts(t.key);
        t.setter(prods);
        renderProducts(t.container, prods, t.cardClass);
      }
    } catch (err) {
      console.error('Error al cargar:', err);
      types.forEach(({ container }) =>
        container.innerHTML = '<p>Error cargando productos.</p>'
      );
    }
  }

  // Re-render ordenando en cliente según sort ('asc' o 'desc')
  function sortAndRender(sort) {
    types.forEach(t => {
      const arr = t.list().slice(); // clonamos
      arr.sort((a, b) => {
        const diff = parseFloat(a.precioValor) - parseFloat(b.precioValor);
        return sort === 'asc' ? diff : -diff;
      });
      renderProducts(t.container, arr, t.cardClass);
    });
  }

  // Eventos
  lowBtn.addEventListener('click',  () => sortAndRender('asc'));
  highBtn.addEventListener('click', () => sortAndRender('desc'));

  initialLoad();
});
