document.addEventListener('DOMContentLoaded', async () => {
  const containers = document.querySelectorAll('.cards-container');
  if (containers.length < 2) {
    console.error('Se esperaban al menos 2 contenedores .cards-container');
    return;
  }

  const menuContainer    = containers[0];
  const menuInfContainer = containers[1];
  const lowBtn  = document.getElementById('low-price');
  const highBtn = document.getElementById('high-price');

  // Arrays para guardar los productos
  let productsMenu    = [];
  let productsMenuInf = [];

  // Función para renderizar
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

  // Carga inicial sin ordenar
  try {
    const respMenu    = await fetch('http://localhost:8080/TestControllerPostgre/api/products?type=burger');
    const respMenuInf = await fetch('http://localhost:8080/TestControllerPostgre/api/products?type=burger-veg');
    if (!respMenu.ok || !respMenuInf.ok) throw new Error('HTTP ' + (respMenu.status || respMenuInf.status));

    productsMenu    = await respMenu.json();
    productsMenuInf = await respMenuInf.json();

    renderProducts(menuContainer, productsMenu,    'card');
    renderProducts(menuInfContainer, productsMenuInf, 'card-inf');
  } catch (err) {
    console.error('Error cargando productos:', err);
    menuContainer.innerHTML = menuInfContainer.innerHTML = '<p>Error cargando productos.</p>';
    return;
  }

  // Comparador numérico (convierte string con punto o coma a número)
  const cmpPrecio = (a, b) => {
    const na = parseFloat(a.precio.replace(',', '.'));
    const nb = parseFloat(b.precio.replace(',', '.'));
    return na - nb;
  };

  // Orden ascendente
  lowBtn.addEventListener('click', () => {
    renderProducts(menuContainer,    [...productsMenu].sort(cmpPrecio),        'card');
    renderProducts(menuInfContainer, [...productsMenuInf].sort(cmpPrecio),     'card-inf');
  });

  // Orden descendente
  highBtn.addEventListener('click', () => {
    renderProducts(menuContainer,    [...productsMenu].sort((a, b) => cmpPrecio(b, a)),        'card');
    renderProducts(menuInfContainer, [...productsMenuInf].sort((a, b) => cmpPrecio(b, a)),     'card-inf');
  });
});
