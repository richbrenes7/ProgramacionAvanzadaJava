const DEFAULT_EXCHANGE = "USD 1 = GTQ 7.80";

const state = {
  token: localStorage.getItem("rb_token") || "",
  user: localStorage.getItem("rb_user") || "",
  role: localStorage.getItem("rb_role") || "",
  clienteId: localStorage.getItem("rb_cliente_id") || "",
  exchangeRate: localStorage.getItem("rb_exchange_rate") || DEFAULT_EXCHANGE,
  clientes: [],
  cuentas: [],
  movimientosPorCuenta: {},
  selectedProduct: "",
  lastOperation: localStorage.getItem("rb_last_operation") || "Sin actividad"
};

const moduleTitles = {
  dashboard: "Panel principal",
  clientes: "Clientes",
  cuentas: "Cuentas",
  saldo: "Saldos",
  asignaciones: "Asignaciones",
  deposito: "Depositos",
  retiro: "Retiros",
  transferencia: "Transferencias",
  lote: "Lotes concurrentes",
  movimientos: "Movimientos",
  reportes: "Reportes",
  admin: "Administrador"
};

const els = {
  publicShell: document.querySelector("[data-public-shell]"),
  bankShell: document.querySelector("[data-bank-shell]"),
  loginForm: document.querySelector("#loginForm"),
  forgotPasswordForm: document.querySelector("#forgotPasswordForm"),
  forgotPasswordToggle: document.querySelector("#forgotPasswordToggle"),
  forgotPasswordCancel: document.querySelector("#forgotPasswordCancel"),
  clienteForm: document.querySelector("#clienteForm"),
  cuentaForm: document.querySelector("#cuentaForm"),
  buscarCuentaForm: document.querySelector("#buscarCuentaForm"),
  asignarCuentaForm: document.querySelector("#asignarCuentaForm"),
  clienteCuentasForm: document.querySelector("#clienteCuentasForm"),
  saldoForm: document.querySelector("#saldoForm"),
  depositoForm: document.querySelector("#depositoForm"),
  retiroForm: document.querySelector("#retiroForm"),
  transferForm: document.querySelector("#transferForm"),
  loteForm: document.querySelector("#loteForm"),
  movimientosForm: document.querySelector("#movimientosForm"),
  refreshClientes: document.querySelector("#refreshClientes"),
  reporteCarteraButton: document.querySelector("#reporteCarteraButton"),
  reporteOperativoButton: document.querySelector("#reporteOperativoButton"),
  reporteTecnicoButton: document.querySelector("#reporteTecnicoButton"),
  adminUsuarioForm: document.querySelector("#adminUsuarioForm"),
  adminResetForm: document.querySelector("#adminResetForm"),
  adminProductoForm: document.querySelector("#adminProductoForm"),
  adminClienteProductosForm: document.querySelector("#adminClienteProductosForm"),
  adminRefreshUsers: document.querySelector("#adminRefreshUsers"),
  adminUsersList: document.querySelector("#adminUsersList"),
  adminProductoResult: document.querySelector("#adminProductoResult"),
  adminClienteProductosList: document.querySelector("#adminClienteProductosList"),
  logoutButton: document.querySelector("#logoutButton"),
  contextUser: document.querySelector("#contextUser"),
  contextRole: document.querySelector("#contextRole"),
  contextExchange: document.querySelector("#contextExchange"),
  contextDate: document.querySelector("#contextDate"),
  workspaceTitle: document.querySelector("#workspaceTitle"),
  clientesList: document.querySelector("#clientesList"),
  cuentasList: document.querySelector("#cuentasList"),
  clienteCuentasList: document.querySelector("#clienteCuentasList"),
  dashboardProductsList: document.querySelector("#dashboardProductsList"),
  productSelector: document.querySelector("#productSelector"),
  selectedProductSummary: document.querySelector("#selectedProductSummary"),
  saldoProducto: document.querySelector("#saldoProducto"),
  depositoProducto: document.querySelector("#depositoProducto"),
  retiroProducto: document.querySelector("#retiroProducto"),
  transferProducto: document.querySelector("#transferProducto"),
  loteProducto: document.querySelector("#loteProducto"),
  movimientosProducto: document.querySelector("#movimientosProducto"),
  saldoResult: document.querySelector("#saldoResult"),
  loteResult: document.querySelector("#loteResult"),
  movimientosList: document.querySelector("#movimientosList"),
  reporteResult: document.querySelector("#reporteResult"),
  reporteOperativoResult: document.querySelector("#reporteOperativoResult"),
  reporteTecnicoResult: document.querySelector("#reporteTecnicoResult"),
  clientesCount: document.querySelector("#clientesCount"),
  cuentasCount: document.querySelector("#cuentasCount"),
  balanceTotal: document.querySelector("#balanceTotal"),
  lastOperation: document.querySelector("#lastOperation"),
  toast: document.querySelector("#toast")
};

function formData(form) {
  return Object.fromEntries(new FormData(form).entries());
}

function money(value) {
  const amount = Number(value || 0);
  return `GTQ ${amount.toLocaleString("es-GT", { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
}

function systemDate() {
  return new Date().toLocaleDateString("es-GT", {
    weekday: "short",
    year: "numeric",
    month: "short",
    day: "2-digit"
  });
}

function updateContextBar() {
  els.contextUser.textContent = state.user || "Usuario";
  if (els.contextRole) els.contextRole.textContent = state.role || "-";
  els.contextExchange.textContent = state.exchangeRate;
  els.contextDate.textContent = systemDate();
}

function showToast(message, type = "ok") {
  els.toast.textContent = message;
  els.toast.className = `toast show ${type === "error" ? "error" : ""}`;
  window.clearTimeout(showToast.timer);
  showToast.timer = window.setTimeout(() => {
    els.toast.className = "toast";
  }, 3200);
}

function setLastOperation(text) {
  state.lastOperation = text;
  localStorage.setItem("rb_last_operation", text);
  renderDashboard();
}

function accountStorageKey() {
  return `rb_cuentas_${state.user || "anon"}`;
}

function selectedProductStorageKey() {
  return `rb_selected_product_${state.user || "anon"}`;
}

function loadUserPortfolio() {
  if (!state.user || state.role === "ADMIN") {
    state.cuentas = [];
    state.movimientosPorCuenta = {};
    state.selectedProduct = "";
    return;
  }
  state.cuentas = JSON.parse(localStorage.getItem(accountStorageKey()) || "[]");
  state.selectedProduct = localStorage.getItem(selectedProductStorageKey()) || "";
  if (state.cuentas.length && !state.cuentas.some(cuenta => cuenta.numeroCuenta === state.selectedProduct)) {
    state.selectedProduct = state.cuentas[0].numeroCuenta;
    localStorage.setItem(selectedProductStorageKey(), state.selectedProduct);
  }
}

function selectedCuenta() {
  return state.cuentas.find(cuenta => cuenta.numeroCuenta === state.selectedProduct) || state.cuentas[0] || null;
}

function saveCuentas() {
  if (state.role === "ADMIN") {
    state.cuentas = [];
    state.movimientosPorCuenta = {};
    state.selectedProduct = "";
    if (state.user) localStorage.removeItem(accountStorageKey());
    if (state.user) localStorage.removeItem(selectedProductStorageKey());
    return;
  }
  const unique = new Map();
  state.cuentas.forEach(cuenta => {
    if (cuenta && cuenta.numeroCuenta) unique.set(cuenta.numeroCuenta, cuenta);
  });
  state.cuentas = Array.from(unique.values());
  if (state.user) localStorage.setItem(accountStorageKey(), JSON.stringify(state.cuentas));
  if (!state.selectedProduct && state.cuentas[0]) state.selectedProduct = state.cuentas[0].numeroCuenta;
  if (state.user && state.selectedProduct) localStorage.setItem(selectedProductStorageKey(), state.selectedProduct);
}

async function api(path, options = {}) {
  const headers = { ...(options.headers || {}) };
  if (state.token) headers.Authorization = `Bearer ${state.token}`;
  if (options.body && !headers["Content-Type"]) headers["Content-Type"] = "application/json";

  const response = await fetch(path, { ...options, headers });
  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || `Solicitud fallida (${response.status})`);
  }

  const contentType = response.headers.get("content-type") || "";
  if (contentType.includes("application/json")) return response.json();
  const text = await response.text();
  return text ? JSON.parse(text) : null;
}

function requireSession() {
  if (!state.token) {
    showToast("Primero inicia sesion en Banca en linea.", "error");
    routePublic("login");
    return false;
  }
  return true;
}

function routePublic(viewName = "inicio") {
  els.publicShell.hidden = false;
  els.bankShell.hidden = true;
  const target = ["inicio", "servicios", "seguridad", "login"].includes(viewName) ? viewName : "inicio";
  document.querySelectorAll("[data-public-view]").forEach(section => {
    section.classList.toggle("active", section.dataset.publicView === target);
  });
  document.querySelectorAll("[data-public-link]").forEach(link => {
    link.classList.toggle("active", link.dataset.publicLink === target);
  });
  if (window.location.hash !== `#${target}`) history.replaceState(null, "", `#${target}`);
}

function routeBank(moduleName = "dashboard") {
  if (!requireSession()) return;
  let target = moduleTitles[moduleName] ? moduleName : "dashboard";
  if (target === "admin" && state.role !== "ADMIN") {
    showToast("El modulo administrador requiere rol ADMIN.", "error");
    target = "dashboard";
  }
  els.publicShell.hidden = true;
  els.bankShell.hidden = false;
  updateContextBar();
  showModule(target);
}

function showModule(moduleName) {
  document.querySelectorAll("[data-module]").forEach(section => {
    section.classList.toggle("active", section.dataset.module === moduleName);
  });
  document.querySelectorAll("[data-module-link]").forEach(link => {
    link.classList.toggle("active", link.dataset.moduleLink === moduleName);
  });
  els.workspaceTitle.textContent = moduleTitles[moduleName] || "Panel principal";
  const hash = moduleName === "dashboard" ? "#banca" : `#${moduleName}`;
  if (window.location.hash !== hash) history.replaceState(null, "", hash);
}

function routeFromHash() {
  const route = (window.location.hash || "#inicio").slice(1);
  if (route === "banca" || moduleTitles[route]) {
    routeBank(route === "banca" ? "dashboard" : route);
    return;
  }
  routePublic(route || "inicio");
}


function movementSign(tipoMovimiento) {
  const tipo = String(tipoMovimiento || "").toUpperCase();
  return ["RETIRO", "TRANSFERENCIA_ENVIADA"].includes(tipo) ? -1 : 1;
}

function chartPoints(values) {
  const width = 164;
  const height = 52;
  const min = Math.min(...values);
  const max = Math.max(...values);
  const spread = max - min || 1;
  return values.map((value, index) => {
    const x = values.length === 1 ? width : (index / (values.length - 1)) * width;
    const y = height - ((value - min) / spread) * height;
    return `${x.toFixed(1)},${y.toFixed(1)}`;
  }).join(" ");
}

function productInsight(cuenta) {
  const movimientos = state.movimientosPorCuenta[cuenta.id] || [];
  const saldoActual = Number(cuenta.saldo || 0);
  const creditos = movimientos
    .filter(mov => movementSign(mov.tipoMovimiento) > 0)
    .reduce((sum, mov) => sum + Number(mov.monto || 0), 0);
  const debitos = movimientos
    .filter(mov => movementSign(mov.tipoMovimiento) < 0)
    .reduce((sum, mov) => sum + Number(mov.monto || 0), 0);
  const maxBar = Math.max(creditos, debitos, 1);
  const serie = movimientos.length
    ? movimientos.map(mov => Number(mov.saldoNuevo ?? saldoActual)).slice(-8)
    : [saldoActual, saldoActual];
  const puntos = chartPoints(serie);
  const balanceLabel = movimientos.length ? `${movimientos.length} movimientos` : "Sin movimientos";

  return `
    <div class="product-insight" aria-label="Grafica de balance y movimientos">
      <svg class="balance-chart" viewBox="0 0 164 56" role="img" aria-label="Tendencia de saldo">
        <polyline points="${puntos}" fill="none" stroke="currentColor" stroke-width="4" stroke-linecap="round" stroke-linejoin="round"></polyline>
      </svg>
      <div class="movement-bars" aria-label="Creditos y debitos">
        <span style="--bar:${Math.round((creditos / maxBar) * 100)}%"><i></i><strong>Creditos ${money(creditos)}</strong></span>
        <span class="debit" style="--bar:${Math.round((debitos / maxBar) * 100)}%"><i></i><strong>Debitos ${money(debitos)}</strong></span>
      </div>
      <small>${balanceLabel} - Saldo ${money(saldoActual)}</small>
    </div>
  `;
}

function productCard(cuenta) {
  const selected = cuenta.numeroCuenta === state.selectedProduct;
  return `
    <button type="button" class="product-card ${selected ? "selected" : ""}" data-product-number="${cuenta.numeroCuenta}">
      <span>${cuenta.tipoCuenta || "Producto bancario"}</span>
      <strong>${cuenta.numeroCuenta}</strong>
      <em>${money(cuenta.saldo)}</em>
      <small>${cuenta.estado || "SIN ESTADO"} - Cliente ${cuenta.clienteId || "-"}</small>
      ${productInsight(cuenta)}
    </button>
  `;
}

function renderDashboardProducts() {
  if (!els.dashboardProductsList) return;
  if (!state.cuentas.length) {
    els.dashboardProductsList.className = "products-list empty-state";
    els.dashboardProductsList.textContent = state.role === "ADMIN" ? "El administrador gestiona productos desde el modulo Administrador; no posee productos bancarios." : "Asocia o crea una cuenta para ver tus productos.";
    return;
  }
  els.dashboardProductsList.className = "products-list";
  els.dashboardProductsList.innerHTML = state.cuentas.map(productCard).join("");
  els.dashboardProductsList.querySelectorAll("[data-product-number]").forEach(button => {
    button.addEventListener("click", () => {
      state.selectedProduct = button.dataset.productNumber;
      if (state.user) localStorage.setItem(selectedProductStorageKey(), state.selectedProduct);
      renderDashboard();
    });
  });
  refreshProductInsights();
}
function renderProductSelectors() {
  const options = state.cuentas.map(cuenta => {
    const label = `${cuenta.tipoCuenta || "CUENTA"} ${cuenta.numeroCuenta} - ${money(cuenta.saldo)}`;
    return { numero: cuenta.numeroCuenta, id: cuenta.id, label };
  });
  const emptyOption = `<option value="">Sin productos asociados</option>`;
  const numeroOptions = options.length ? options.map(opt => `<option value="${opt.numero}">${opt.label}</option>`).join("") : emptyOption;
  const idOptions = options.length ? options.map(opt => `<option value="${opt.id}">${opt.label}</option>`).join("") : emptyOption;

  [els.productSelector, els.saldoProducto, els.transferProducto, els.loteProducto].forEach(select => {
    if (!select) return;
    select.innerHTML = numeroOptions;
    select.value = state.selectedProduct || "";
  });
  [els.depositoProducto, els.retiroProducto, els.movimientosProducto].forEach(select => {
    if (!select) return;
    select.innerHTML = idOptions;
    const cuenta = selectedCuenta();
    select.value = cuenta && cuenta.id ? String(cuenta.id) : "";
  });

  const cuenta = selectedCuenta();
  if (!els.selectedProductSummary) return;
  if (!cuenta) {
    els.selectedProductSummary.textContent = "Asocia o crea una cuenta para operar con ella.";
    return;
  }
  els.selectedProductSummary.innerHTML = `
    <span>${cuenta.tipoCuenta || "Producto bancario"}</span><strong>${cuenta.numeroCuenta}</strong>
    <span>Saldo disponible</span><strong>${money(cuenta.saldo)}</strong>
  `;
}
function renderAdminAccess() {
  document.querySelectorAll("[data-admin-only]").forEach(element => {
    element.hidden = state.role !== "ADMIN";
  });
}

function renderDashboard() {
  els.clientesCount.textContent = state.clientes.length;
  els.cuentasCount.textContent = state.cuentas.length;
  els.balanceTotal.textContent = money(state.cuentas.reduce((sum, cuenta) => sum + Number(cuenta.saldo || 0), 0));
  els.lastOperation.textContent = state.lastOperation;
  renderProductSelectors();
  renderDashboardProducts();
  updateContextBar();
  renderAdminAccess();
}

function record(title, meta, extra = "") {
  return `
    <article class="record">
      <div class="record-title">${title}</div>
      <div class="record-meta">${meta.map(item => `<span class="pill ${item.kind || ""}">${item.text}</span>`).join("")}</div>
      ${extra}
    </article>
  `;
}

function renderClientes() {
  if (!state.clientes.length) {
    els.clientesList.className = "data-list empty-state";
    els.clientesList.textContent = state.token ? "No hay clientes cargados." : "Inicia sesion para consultar clientes.";
    renderDashboard();
    return;
  }

  els.clientesList.className = "data-list";
  els.clientesList.innerHTML = state.clientes.map(cliente => record(
    `<span>${cliente.nombre || "Cliente"}</span><span>#${cliente.id}</span>`,
    [
      { text: cliente.documento || "Sin documento" },
      { text: cliente.email || "Sin email" },
      { text: cliente.estado || "SIN ESTADO", kind: cliente.estado === "ACTIVO" ? "success" : "" }
    ]
  )).join("");
  renderDashboard();
}

function renderCuentas() {
  if (!state.cuentas.length) {
    els.cuentasList.className = "data-list empty-state";
    els.cuentasList.textContent = state.role === "ADMIN" ? "El administrador no tiene productos propios; gestiona productos desde la pestaña Administrador." : "Los productos asociados a este usuario aparecen aqui.";
    renderDashboard();
    return;
  }

  els.cuentasList.className = "data-list";
  els.cuentasList.innerHTML = state.cuentas.map(cuenta => record(
    `<span>${cuenta.numeroCuenta}</span><span>${money(cuenta.saldo)}</span>`,
    [
      { text: `ID ${cuenta.id}` },
      { text: cuenta.tipoCuenta || "CUENTA" },
      { text: cuenta.estado || "SIN ESTADO", kind: cuenta.estado === "ACTIVO" ? "success" : "" },
      { text: `Cliente ${cuenta.clienteId || "-"}` }
    ],
    productInsight(cuenta)
  )).join("");
  renderDashboard();
}

function renderClienteCuentas(cuentas) {
  if (!els.clienteCuentasList) return;
  if (!cuentas || !cuentas.length) {
    els.clienteCuentasList.className = "data-list empty-state";
    els.clienteCuentasList.textContent = "Este cliente no tiene cuentas asignadas.";
    return;
  }

  els.clienteCuentasList.className = "data-list";
  els.clienteCuentasList.innerHTML = cuentas.map(cuenta => record(
    `<span>${cuenta.numeroCuenta}</span><span>${money(cuenta.saldo)}</span>`,
    [
      { text: `ID ${cuenta.id}` },
      { text: cuenta.tipoCuenta || "CUENTA" },
      { text: cuenta.moneda || "GTQ" },
      { text: cuenta.estado || "SIN ESTADO", kind: cuenta.estado === "ACTIVO" ? "success" : "" }
    ]
  )).join("");
}

function renderMovimientos(movimientos) {
  if (!movimientos || !movimientos.length) {
    els.movimientosList.className = "data-list empty-state";
    els.movimientosList.textContent = "No hay movimientos para esta cuenta.";
    return;
  }

  els.movimientosList.className = "data-list";
  els.movimientosList.innerHTML = movimientos.map(mov => {
    const isDebit = ["RETIRO", "TRANSFERENCIA_ENVIADA"].includes(String(mov.tipoMovimiento).toUpperCase());
    return record(
      `<span>${mov.tipoMovimiento}</span><span>${isDebit ? "-" : "+"}${money(mov.monto)}</span>`,
      [
        { text: `Cuenta ${mov.cuentaId}` },
        { text: `Antes ${money(mov.saldoAnterior)}` },
        { text: `Despues ${money(mov.saldoNuevo)}`, kind: isDebit ? "danger" : "success" },
        { text: mov.referencia || "Sin referencia" }
      ]
    );
  }).join("");
}



function renderAdminClienteProductos(cuentas) {
  if (!els.adminClienteProductosList) return;
  if (!cuentas || !cuentas.length) {
    els.adminClienteProductosList.className = "data-list empty-state";
    els.adminClienteProductosList.textContent = "Este cliente no tiene productos bancarios asociados.";
    return;
  }
  els.adminClienteProductosList.className = "data-list";
  els.adminClienteProductosList.innerHTML = cuentas.map(cuenta => record(
    `<span>${cuenta.numeroCuenta}</span><span>${money(cuenta.saldo)}</span>`,
    [
      { text: `ID ${cuenta.id}` },
      { text: cuenta.tipoCuenta || "PRODUCTO" },
      { text: cuenta.moneda || "GTQ" },
      { text: cuenta.estado || "SIN ESTADO", kind: cuenta.estado === "ACTIVO" ? "success" : "" }
    ]
  )).join("");
}

async function cargarAdminClienteProductos(clienteId) {
  const cuentas = await api(`/api/cuentas/cliente/${clienteId}`);
  renderAdminClienteProductos(cuentas);
  return cuentas;
}
function renderAdminUsers(usuarios) {
  if (!els.adminUsersList) return;
  if (!usuarios || !usuarios.length) {
    els.adminUsersList.className = "data-list empty-state";
    els.adminUsersList.textContent = "No hay usuarios registrados.";
    return;
  }
  els.adminUsersList.className = "data-list";
  els.adminUsersList.innerHTML = usuarios.map(usuario => record(
    `<span>${usuario.username}</span><span>ID ${usuario.id}</span>`,
    [
      { text: usuario.role || "USER", kind: usuario.role === "ADMIN" ? "success" : "" },
      { text: usuario.nombre || "Sin nombre" },
      { text: usuario.clienteId ? `Cliente ${usuario.clienteId}` : "Sin cliente" },
      { text: usuario.estado || "SIN ESTADO" }
    ]
  )).join("");
}

async function refreshAdminUsers() {
  if (!requireSession()) return;
  if (state.role !== "ADMIN") {
    showToast("El modulo administrador requiere rol ADMIN.", "error");
    return;
  }
  const usuarios = await api("/api/admin/usuarios");
  renderAdminUsers(usuarios);
}

async function refreshUserPortfolio() {
  if (!state.token || state.role === "ADMIN") {
    state.cuentas = [];
    state.movimientosPorCuenta = {};
    state.selectedProduct = "";
    renderCuentas();
    return [];
  }
  const cuentas = await api("/api/cuentas/mis-productos");
  state.cuentas = cuentas || [];
  if (state.selectedProduct && !state.cuentas.some(cuenta => cuenta.numeroCuenta === state.selectedProduct)) {
    state.selectedProduct = "";
  }
  if (!state.selectedProduct && state.cuentas[0]) {
    state.selectedProduct = state.cuentas[0].numeroCuenta;
  }
  saveCuentas();
  renderCuentas();
  return state.cuentas;
}

async function refreshClientes() {
  if (!requireSession()) return;
  state.clientes = await api("/api/clientes");
  renderClientes();
}

async function buscarCuenta(numeroCuenta) {
  const cuenta = await api(`/api/cuentas/numero/${encodeURIComponent(numeroCuenta)}`);
  if (cuenta.id) delete state.movimientosPorCuenta[cuenta.id];
  state.cuentas.push(cuenta);
  state.selectedProduct = cuenta.numeroCuenta;
  saveCuentas();
  renderCuentas();
  return cuenta;
}

async function refreshKnownAccounts() {
  const numeros = state.cuentas.map(cuenta => cuenta.numeroCuenta).filter(Boolean);
  const refreshed = [];
  for (const numero of numeros) {
    try {
      refreshed.push(await api(`/api/cuentas/numero/${encodeURIComponent(numero)}`));
    } catch (error) {
      // La cuenta pudo desaparecer si se reinicio la base en memoria.
    }
  }
  state.cuentas = refreshed;
  saveCuentas();
  renderCuentas();
}


async function refreshProductInsights() {
  if (!state.token || state.role === "ADMIN" || !state.cuentas.length) return;
  const pendientes = state.cuentas
    .filter(cuenta => cuenta.id && !state.movimientosPorCuenta[cuenta.id])
    .map(async cuenta => {
      try {
        state.movimientosPorCuenta[cuenta.id] = await api(`/api/movimientos/cuenta/${cuenta.id}`);
      } catch (error) {
        state.movimientosPorCuenta[cuenta.id] = [];
      }
    });
  if (!pendientes.length) return;
  await Promise.all(pendientes);
  renderDashboardProducts();
  if (els.cuentasList && els.cuentasList.classList.contains("data-list")) {
    renderCuentas();
  }
}

async function registrarMovimiento(form, tipoMovimiento) {
  if (!requireSession()) return;
  const data = formData(form);
  const payload = {
    cuentaId: Number(data.cuentaId),
    tipoMovimiento,
    monto: Number(data.monto)
  };

  const movimiento = await api("/api/movimientos", { method: "POST", body: JSON.stringify(payload) });
  delete state.movimientosPorCuenta[payload.cuentaId];
  await refreshKnownAccounts();
  setLastOperation(`${movimiento.tipoMovimiento} ${money(movimiento.monto)}`);
  showToast(`${tipoMovimiento.toLowerCase()} registrado.`);
  form.reset();
}


if (els.forgotPasswordToggle) {
  els.forgotPasswordToggle.addEventListener("click", () => {
    els.forgotPasswordForm.hidden = false;
    els.forgotPasswordToggle.hidden = true;
  });
}

if (els.forgotPasswordCancel) {
  els.forgotPasswordCancel.addEventListener("click", () => {
    els.forgotPasswordForm.reset();
    els.forgotPasswordForm.hidden = true;
    els.forgotPasswordToggle.hidden = false;
  });
}

if (els.forgotPasswordForm) {
  els.forgotPasswordForm.addEventListener("submit", async event => {
    event.preventDefault();
    const data = formData(event.currentTarget);
    try {
      await api("/api/auth/forgot-password", {
        method: "POST",
        body: JSON.stringify(data)
      });
      event.currentTarget.reset();
      els.forgotPasswordForm.hidden = true;
      els.forgotPasswordToggle.hidden = false;
      showToast("Contrasena actualizada. Inicia sesion con la nueva contrasena.");
    } catch (error) {
      showToast("No se pudo actualizar la contrasena.", "error");
    }
  });
}
els.loginForm.addEventListener("submit", async event => {
  event.preventDefault();
  const data = formData(event.currentTarget);
  try {
    const response = await api("/api/auth/login", {
      method: "POST",
      body: JSON.stringify(data)
    });
    state.token = response.token;
    state.user = response.username || data.username;
    state.role = response.role || "USER";
    state.clienteId = response.clienteId || "";
    loadUserPortfolio();
    localStorage.setItem("rb_token", state.token);
    localStorage.setItem("rb_user", state.user);
    localStorage.setItem("rb_role", state.role);
    if (state.clienteId) localStorage.setItem("rb_cliente_id", state.clienteId);
    else localStorage.removeItem("rb_cliente_id");
    localStorage.setItem("rb_exchange_rate", state.exchangeRate);
    showToast("Sesion iniciada.");
    await refreshUserPortfolio();
    await refreshClientes();
    routeBank("dashboard");
  } catch (error) {
    showToast("No se pudo iniciar sesion. Revisa usuario y contrasena.", "error");
  }
});

els.logoutButton.addEventListener("click", () => {
  state.token = "";
  state.user = "";
  state.role = "";
  state.clientes = [];
  state.cuentas = [];
  state.movimientosPorCuenta = {};
  state.clienteId = "";
  state.selectedProduct = "";
  localStorage.removeItem("rb_token");
  localStorage.removeItem("rb_user");
  localStorage.removeItem("rb_cliente_id");
    localStorage.removeItem("rb_role");
  renderClientes();
  renderCuentas();
  routePublic("inicio");
  showToast("Sesion cerrada.");
});

els.refreshClientes.addEventListener("click", async () => {
  try {
    await refreshClientes();
    showToast("Clientes actualizados.");
  } catch (error) {
    showToast(error.message, "error");
  }
});

els.clienteForm.addEventListener("submit", async event => {
  event.preventDefault();
  if (!requireSession()) return;
  const form = event.currentTarget;
  const data = formData(form);
  try {
    const cliente = await api("/api/clientes", { method: "POST", body: JSON.stringify(data) });
    state.clientes.unshift(cliente);
    form.reset();
    renderClientes();
    setLastOperation(`Cliente #${cliente.id}`);
    showToast("Cliente creado.");
  } catch (error) {
    showToast(error.message, "error");
  }
});

els.cuentaForm.addEventListener("submit", async event => {
  event.preventDefault();
  if (!requireSession()) return;
  const form = event.currentTarget;
  const data = formData(form);
  data.clienteId = Number(data.clienteId);
  data.saldo = Number(data.saldo);
  try {
    const cuenta = await api("/api/cuentas", { method: "POST", body: JSON.stringify(data) });
    state.selectedProduct = cuenta.numeroCuenta;
    await refreshUserPortfolio();
    setLastOperation(`Cuenta ${cuenta.numeroCuenta}`);
    showToast("Cuenta creada.");
  } catch (error) {
    showToast(error.message, "error");
  }
});

els.buscarCuentaForm.addEventListener("submit", async event => {
  event.preventDefault();
  if (!requireSession()) return;
  const { numeroCuenta } = formData(event.currentTarget);
  try {
    await buscarCuenta(numeroCuenta);
    showToast("Cuenta consultada.");
  } catch (error) {
    showToast("No se encontro la cuenta.", "error");
  }
});

els.asignarCuentaForm.addEventListener("submit", async event => {
  event.preventDefault();
  if (!requireSession()) return;
  const form = event.currentTarget;
  const { numeroCuenta, clienteId } = formData(form);
  try {
    const cuenta = await api(`/api/cuentas/numero/${encodeURIComponent(numeroCuenta)}/cliente/${clienteId}`, { method: "POST" });
    state.cuentas.push(cuenta);
    state.selectedProduct = cuenta.numeroCuenta;
    saveCuentas();
    renderCuentas();
    const cuentasCliente = await api(`/api/cuentas/cliente/${clienteId}`);
    renderClienteCuentas(cuentasCliente);
    form.reset();
    setLastOperation(`Cuenta ${cuenta.numeroCuenta} asignada a cliente ${clienteId}`);
    showToast("Cuenta asignada al cliente.");
  } catch (error) {
    showToast(error.message || "No se pudo asignar la cuenta.", "error");
  }
});

els.clienteCuentasForm.addEventListener("submit", async event => {
  event.preventDefault();
  if (!requireSession()) return;
  const { clienteId } = formData(event.currentTarget);
  try {
    const cuentas = await api(`/api/cuentas/cliente/${clienteId}`);
    renderClienteCuentas(cuentas);
    showToast("Cuentas del cliente cargadas.");
  } catch (error) {
    showToast(error.message || "No se pudieron cargar las cuentas.", "error");
  }
});
els.saldoForm.addEventListener("submit", async event => {
  event.preventDefault();
  if (!requireSession()) return;
  const { numeroCuenta } = formData(event.currentTarget);
  try {
    const saldo = await api(`/api/cuentas/${encodeURIComponent(numeroCuenta)}/saldo`);
    els.saldoResult.innerHTML = `<span>Saldo disponible</span><strong>${money(saldo)}</strong>`;
    setLastOperation(`Saldo ${numeroCuenta}`);
    showToast("Saldo consultado.");
  } catch (error) {
    els.saldoResult.textContent = "No se pudo consultar el saldo.";
    showToast(error.message, "error");
  }
});

els.depositoForm.addEventListener("submit", async event => {
  event.preventDefault();
  try {
    await registrarMovimiento(event.currentTarget, "DEPOSITO");
  } catch (error) {
    showToast(error.message, "error");
  }
});

els.retiroForm.addEventListener("submit", async event => {
  event.preventDefault();
  try {
    await registrarMovimiento(event.currentTarget, "RETIRO");
  } catch (error) {
    showToast(error.message, "error");
  }
});

els.transferForm.addEventListener("submit", async event => {
  event.preventDefault();
  if (!requireSession()) return;
  const form = event.currentTarget;
  const data = formData(form);
  const payload = { ...data, monto: Number(data.monto) };
  try {
    await api("/api/transacciones", { method: "POST", body: JSON.stringify(payload) });
    await Promise.all([buscarCuenta(data.origen), buscarCuenta(data.destino)]);
    setLastOperation(`Transferencia ${money(data.monto)}`);
    showToast("Transferencia realizada.");
  } catch (error) {
    showToast(error.message || "No se pudo transferir.", "error");
  }
});

els.loteForm.addEventListener("submit", async event => {
  event.preventDefault();
  if (!requireSession()) return;
  const data = formData(event.currentTarget);
  const cantidad = Number(data.cantidad);
  const lote = Array.from({ length: cantidad }, () => ({
    origen: data.origen,
    destino: data.destino,
    monto: Number(data.monto)
  }));

  try {
    const procesadas = await api("/api/transacciones/lote", { method: "POST", body: JSON.stringify(lote) });
    await Promise.all([buscarCuenta(data.origen), buscarCuenta(data.destino)]);
    els.loteResult.innerHTML = `<span>Transacciones procesadas</span><strong>${procesadas}</strong>`;
    setLastOperation(`Lote ${procesadas} trx`);
    showToast("Lote procesado.");
  } catch (error) {
    showToast(error.message, "error");
  }
});

els.movimientosForm.addEventListener("submit", async event => {
  event.preventDefault();
  if (!requireSession()) return;
  const { cuentaId } = formData(event.currentTarget);
  try {
    const movimientos = await api(`/api/movimientos/cuenta/${cuentaId}`);
    renderMovimientos(movimientos);
    showToast("Movimientos cargados.");
  } catch (error) {
    showToast(error.message, "error");
  }
});



document.querySelectorAll("[data-admin-tab]").forEach(tab => {
  tab.addEventListener("click", () => {
    const target = tab.dataset.adminTab;
    document.querySelectorAll("[data-admin-tab]").forEach(item => {
      item.classList.toggle("active", item.dataset.adminTab === target);
    });
    document.querySelectorAll("[data-admin-panel]").forEach(panel => {
      panel.classList.toggle("active", panel.dataset.adminPanel === target);
    });
  });
});
if (els.adminRefreshUsers) {
  els.adminRefreshUsers.addEventListener("click", async () => {
    try {
      await refreshAdminUsers();
      showToast("Usuarios actualizados.");
    } catch (error) {
      showToast(error.message, "error");
    }
  });
}

if (els.adminUsuarioForm) {
  els.adminUsuarioForm.addEventListener("submit", async event => {
    event.preventDefault();
    if (!requireSession()) return;
    const data = formData(event.currentTarget);
    try {
      if (data.clienteId) data.clienteId = Number(data.clienteId);
      await api("/api/admin/usuarios", { method: "POST", body: JSON.stringify(data) });
      event.currentTarget.reset();
      await refreshAdminUsers();
      showToast("Usuario creado.");
    } catch (error) {
      showToast(error.message, "error");
    }
  });
}

if (els.adminResetForm) {
  els.adminResetForm.addEventListener("submit", async event => {
    event.preventDefault();
    if (!requireSession()) return;
    const data = formData(event.currentTarget);
    try {
      await api(`/api/admin/usuarios/${data.id}/reset-password`, {
        method: "POST",
        body: JSON.stringify({ newPassword: data.newPassword })
      });
      event.currentTarget.reset();
      await refreshAdminUsers();
      showToast("Contrasena actualizada.");
    } catch (error) {
      showToast(error.message, "error");
    }
  });
}

if (els.adminProductoForm) {
  els.adminProductoForm.addEventListener("submit", async event => {
    event.preventDefault();
    if (!requireSession()) return;
    const data = formData(event.currentTarget);
    const payload = {
      clienteId: Number(data.clienteId),
      tipoCuenta: data.tipoCuenta,
      moneda: data.moneda,
      saldoInicial: Number(data.saldoInicial),
      estado: data.estado
    };
    try {
      const cuenta = await api("/api/admin/productos", { method: "POST", body: JSON.stringify(payload) });
      await cargarAdminClienteProductos(cuenta.clienteId);
      if (els.adminProductoResult) {
        els.adminProductoResult.innerHTML = `<span>Producto generado para cliente</span><strong>${cuenta.numeroCuenta}</strong><span>Cliente</span><strong>${cuenta.clienteId}</strong>`;
      }
      setLastOperation(`Producto ${cuenta.numeroCuenta}`);
      showToast("Producto creado y asociado.");
    } catch (error) {
      showToast(error.message, "error");
    }
  });
}


function reportMapList(title, data, formatter = value => value) {
  const entries = Object.entries(data || {});
  if (!entries.length) return "";
  return `
    <section class="report-block">
      <h3>${title}</h3>
      <div class="report-list">
        ${entries.map(([key, value]) => `<span>${key}</span><strong>${formatter(value)}</strong>`).join("")}
      </div>
    </section>
  `;
}

function renderReporteOperativo(reporte) {
  if (!els.reporteOperativoResult) return;
  const resumen = reporte.resumen || {};
  const movimientos = reporte.ultimosMovimientos || [];
  const apis = reporte.apis || [];
  els.reporteOperativoResult.className = "report-stack";
  els.reporteOperativoResult.innerHTML = `
    <section class="report-metrics">
      <article><span>Clientes</span><strong>${resumen.clientes || 0}</strong></article>
      <article><span>Productos</span><strong>${resumen.productos || 0}</strong></article>
      <article><span>Movimientos</span><strong>${resumen.movimientos || 0}</strong></article>
      <article><span>Saldo total</span><strong>${money(resumen.saldoTotal)}</strong></article>
    </section>
    ${reportMapList("Productos por tipo", reporte.cartera?.cuentasPorTipo)}
    ${reportMapList("Productos por estado", reporte.cartera?.cuentasPorEstado)}
    ${reportMapList("Saldo por tipo", reporte.cartera?.saldoPorTipo, money)}
    ${reportMapList("Movimientos por tipo", reporte.movimientosPorTipo)}
    ${reportMapList("Monto por tipo de movimiento", reporte.montoPorTipoMovimiento, money)}
    <section class="report-block">
      <h3>Ultimos movimientos</h3>
      <div class="report-table">
        ${movimientos.length ? movimientos.map(mov => `<div><span>${mov.tipo || "MOVIMIENTO"}</span><span>Cuenta ${mov.cuentaId || "-"}</span><strong>${money(mov.monto)}</strong><small>${mov.fecha || "Sin fecha"}</small></div>`).join("") : `<p>No hay movimientos registrados.</p>`}
      </div>
    </section>
    <section class="report-block">
      <h3>Catalogo de APIs</h3>
      <div class="api-catalog">
        ${apis.map(api => `<article><span>${api.modulo}</span><strong>${api.metodo} ${api.ruta}</strong><p>${api.descripcion}</p></article>`).join("")}
      </div>
    </section>
  `;
}


function renderReporteTecnico(reporte) {
  if (!els.reporteTecnicoResult) return;
  const health = reporte.health || {};
  const endpoints = reporte.endpointsTecnicos || [];
  const logs = reporte.logs || [];
  els.reporteTecnicoResult.className = "report-stack";
  els.reporteTecnicoResult.innerHTML = `
    <section class="report-block">
      <h3>Health check</h3>
      <div class="report-list">
        <span>Endpoint</span><strong>${health.endpoint || "/actuator/health"}</strong>
        <span>Uso</span><strong>${health.uso || "Validacion tecnica"}</strong>
      </div>
    </section>
    <section class="report-block">
      <h3>Logs del sistema</h3>
      <div class="api-catalog">
        ${logs.map(item => `<article><p>${item}</p></article>`).join("")}
      </div>
    </section>
    <section class="report-block">
      <h3>Endpoints tecnicos</h3>
      <div class="api-catalog">
        ${endpoints.map(api => `<article><span>${api.modulo}</span><strong>${api.metodo} ${api.ruta}</strong><p>${api.descripcion}</p></article>`).join("")}
      </div>
    </section>
  `;
}

if (els.adminClienteProductosForm) {
  els.adminClienteProductosForm.addEventListener("submit", async event => {
    event.preventDefault();
    if (!requireSession()) return;
    const { clienteId } = formData(event.currentTarget);
    try {
      await cargarAdminClienteProductos(clienteId);
      showToast("Productos del cliente cargados.");
    } catch (error) {
      showToast(error.message || "No se pudieron cargar los productos del cliente.", "error");
    }
  });
}
els.reporteCarteraButton.addEventListener("click", async () => {
  if (!requireSession()) return;
  try {
    const reporte = await api("/api/reportes/cartera");
    els.reporteResult.innerHTML = `
      <span>Total de cuentas</span><strong>${reporte.totalCuentas}</strong>
      <span>Clientes con productos</span><strong>${reporte.clientesConProductos}</strong>
      <span>Saldo total</span><strong>${money(reporte.saldoTotal)}</strong>
      <span>Moneda base</span><strong>${reporte.monedaBase}</strong>
      ${reportMapList("Cuentas por tipo", reporte.cuentasPorTipo)}
      ${reportMapList("Saldo por tipo", reporte.saldoPorTipo, money)}
    `;
    showToast("Reporte generado.");
  } catch (error) {
    showToast(error.message, "error");
  }
});

if (els.reporteOperativoButton) {
  els.reporteOperativoButton.addEventListener("click", async () => {
    if (!requireSession()) return;
    try {
      const reporte = await api("/api/reportes/operativo");
      renderReporteOperativo(reporte);
      showToast("Reporte operativo generado.");
    } catch (error) {
      showToast(error.message, "error");
    }
  });
}


if (els.reporteTecnicoButton) {
  els.reporteTecnicoButton.addEventListener("click", async () => {
    if (!requireSession()) return;
    try {
      const reporte = await api("/api/reportes/tecnico");
      renderReporteTecnico(reporte);
      showToast("Reporte tecnico generado.");
    } catch (error) {
      showToast(error.message, "error");
    }
  });
}

if (els.productSelector) {
  els.productSelector.addEventListener("change", event => {
    state.selectedProduct = event.currentTarget.value;
    if (state.user && state.selectedProduct) localStorage.setItem(selectedProductStorageKey(), state.selectedProduct);
    renderProductSelectors();
  });
}

document.querySelectorAll("[data-public-link]").forEach(link => {
  link.addEventListener("click", event => {
    event.preventDefault();
    routePublic(link.dataset.publicLink);
  });
});

document.querySelectorAll("[data-module-link]").forEach(link => {
  link.addEventListener("click", event => {
    event.preventDefault();
    routeBank(link.dataset.moduleLink);
  });
});

window.addEventListener("hashchange", routeFromHash);
window.setInterval(updateContextBar, 60000);

loadUserPortfolio();
renderClientes();
renderCuentas();
renderDashboard();
routeFromHash();

if (state.token) {
  refreshUserPortfolio().catch(() => {});
  refreshClientes().catch(() => {
    state.token = "";
    state.user = "";
    state.role = "";
    localStorage.removeItem("rb_token");
    localStorage.removeItem("rb_user");
  localStorage.removeItem("rb_cliente_id");
    localStorage.removeItem("rb_role");
    localStorage.removeItem("rb_cliente_id");
    routePublic("login");
  });
}
