document.addEventListener('DOMContentLoaded', async () => {
  const containers = document.querySelectorAll('.cards-container');
  if (containers.length < 2) {
    console.error('Se esperaban al menos 2 contenedores .cards-container');
    return;
  }

  const menuContainer    = containers[0];
  const menuInfContainer = containers[1];
  const lowBtn           = document.getElementById('low-price');
  const highBtn          = document.getElementById('high-price');
  const searchInput      = document.querySelector('.buscador input');

  // 1) Arrays para guardar los datos brutos
  let productsMenu    = [];
  let productsMenuInf = [];

  // 2) Renderizado con overlay de descripción
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
            <p>${prod.desc}</p>
          </div>
        </div>
      `);
    });
  }

  // 3) Carga inicial de ambos tipos
  try {
    const [respMenu, respMenuInf] = await Promise.all([
      fetch('http://localhost:8080/TestControllerPostgre/api/products?type=burger'),
      fetch('http://localhost:8080/TestControllerPostgre/api/products?type=burger-veg')
    ]);
    if (!respMenu.ok || !respMenuInf.ok) throw new Error('HTTP ' + (respMenu.status || respMenuInf.status));

    productsMenu    = await respMenu.json();
    productsMenuInf = await respMenuInf.json();

    renderProducts(menuContainer,    productsMenu,    'card');
    renderProducts(menuInfContainer, productsMenuInf, 'card-inf');
  } catch (err) {
    console.error('Error cargando productos:', err);
    menuContainer.innerHTML = menuInfContainer.innerHTML = '<p>Error cargando productos.</p>';
    return;
  }

  // 4) Comparador numérico que admite punto o coma
  const cmpPrecio = (a, b) => {
    const na = parseFloat((a.precioValor ?? a.precioFormateado).toString().replace(',', '.'));
    const nb = parseFloat((b.precioValor ?? b.precioFormateado).toString().replace(',', '.'));
    return na - nb;
  };

  // 5) Función que aplica filtro de búsqueda y orden, y renderiza
  function filterSortRender(sort) {
    const term = searchInput.value.trim().toLowerCase();

    // Generamos las dos listas filtradas y ordenadas
    [ { data: productsMenu,    container: menuContainer,    cls: 'card'     },
      { data: productsMenuInf, container: menuInfContainer, cls: 'card-inf' }]
      .forEach(({ data, container, cls }) => {
        let arr = data.slice();

        // Filtro por nombre o descripción
        if (term) {
          arr = arr.filter(p => {
            const name = p.nombre.toLowerCase();
            const desc = (p.desc || '').toLowerCase();
            return name.includes(term) || desc.includes(term);
          });
        }

        // Orden si se pide
        if (sort) {
          arr.sort((a, b) => sort === 'asc' ? cmpPrecio(a, b) : cmpPrecio(b, a));
        }

        renderProducts(container, arr, cls);
      });
  }

  // 6) Enlazamos eventos
  lowBtn.addEventListener('click',  () => filterSortRender('asc'));
  highBtn.addEventListener('click', () => filterSortRender('desc'));
  searchInput.addEventListener('input', () => filterSortRender(null));
});
