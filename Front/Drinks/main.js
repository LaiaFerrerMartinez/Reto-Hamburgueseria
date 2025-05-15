document.addEventListener('DOMContentLoaded', () => {
  // 1) Referencias en el DOM
  const container = document.querySelector('.cards-container');
  const lowBtn    = document.getElementById('low-price');
  const highBtn   = document.getElementById('high-price');

  // 2) Configuramos “types” igual que en combos, pero solo uno: drink
  const types = [
    { key: 'drink', container: container, cardClass: 'card' }
  ];

  // 3) Fetch genérico al servlet, con sort opcional
  async function fetchProducts(type, sort) {
    const url = new URL('http://localhost:8080/TestControllerPostgre/api/products');
    url.searchParams.set('type', type);
    if (sort) url.searchParams.set('sort', sort);
    const resp = await fetch(url);
    if (!resp.ok) throw new Error(`HTTP ${resp.status}`);
    return resp.json();
  }

  // 4) Renderiza cada array en su contenedor
  function renderProducts(container, products, cardClass) {
    container.innerHTML = '';
    products.forEach(prod => {
      const card = document.createElement('div');
      card.className = cardClass;
      card.innerHTML = `
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
      `;
      container.appendChild(card);
    });
  }

  // 5) Carga inicial SIN ordenar
  async function initialLoad() {
    try {
      for (const t of types) {
        const prods = await fetchProducts(t.key, null);
        renderProducts(t.container, prods, t.cardClass);
      }
    } catch (err) {
      console.error('Error cargando drinks:', err);
      container.innerHTML = '<p>Error cargando productos.</p>';
    }
  }

  // 6) Recarga CON sort al hacer click
  async function reloadWithSort(sort) {
    try {
      for (const t of types) {
        const prods = await fetchProducts(t.key, sort);
        renderProducts(t.container, prods, t.cardClass);
      }
    } catch (err) {
      console.error('Error al ordenar drinks:', err);
    }
  }

  // 7) Enlazamos eventos igual que en combos
  lowBtn.addEventListener('click',  () => reloadWithSort('asc'));
  highBtn.addEventListener('click', () => reloadWithSort('desc'));

  // 8) Lanzamos la carga inicial
  initialLoad();
});
