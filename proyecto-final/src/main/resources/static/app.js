const DEFAULT_EXCHANGE = "USD 1 = GTQ 7.80";

const state = {
  token: localStorage.getItem("rb_token") || "",
  user: localStorage.getItem("rb_user") || "",
  exchangeRate: localStorage.getItem("rb_exchange_rate") || DEFAULT_EXCHANGE,
  clientes: [],
  cuentas: [],
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
  reportes: "Reportes"
};

const els = {
  publicShell: document.querySelector("[data-public-shell]"),
  bankShell: document.querySelector("[data-bank-shell]"),
  loginForm: document.querySelector("#loginForm"),
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
  logoutButton: document.querySelector("#logoutButton"),
  contextUser: document.querySelector("#contextUser"),
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
  if (!state.user) {
    state.cuentas = [];
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
  const target = moduleTitles[moduleName] ? moduleName : "dashboard";
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

function productCard(cuenta) {
  const selected = cuenta.numeroCuenta === state.selectedProduct;
  return `
    <button type="button" class="product-card ${selected ? "selected" : ""}" data-product-number="${cuenta.numeroCuenta}">
      <span>${cuenta.tipoCuenta || "Producto bancario"}</span>
      <strong>${cuenta.numeroCuenta}</strong>
      <em>${money(cuenta.saldo)}</em>
      <small>${cuenta.estado || "SIN ESTADO"} - Cliente ${cuenta.clienteId || "-"}</small>
    </button>
  `;
}

function renderDashboardProducts() {
  if (!els.dashboardProductsList) return;
  if (!state.cuentas.length) {
    els.dashboardProductsList.className = "products-list empty-state";
    els.dashboardProductsList.textContent = "Asocia o crea una cuenta para ver tus productos.";
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
function renderDashboard() {
  els.clientesCount.textContent = state.clientes.length;
  els.cuentasCount.textContent = state.cuentas.length;
  els.balanceTotal.textContent = money(state.cuentas.reduce((sum, cuenta) => sum + Number(cuenta.saldo || 0), 0));
  els.lastOperation.textContent = state.lastOperation;
  renderProductSelectors();
  renderDashboardProducts();
  updateContextBar();
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
    els.cuentasList.textContent = "Los productos asociados a este usuario aparecen aqui.";
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
    ]
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

async function refreshClientes() {
  if (!requireSession()) return;
  state.clientes = await api("/api/clientes");
  renderClientes();
}

async function buscarCuenta(numeroCuenta) {
  const cuenta = await api(`/api/cuentas/numero/${encodeURIComponent(numeroCuenta)}`);
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

async function registrarMovimiento(form, tipoMovimiento) {
  if (!requireSession()) return;
  const data = formData(form);
  const payload = {
    cuentaId: Number(data.cuentaId),
    tipoMovimiento,
    monto: Number(data.monto)
  };

  const movimiento = await api("/api/movimientos", { method: "POST", body: JSON.stringify(payload) });
  await refreshKnownAccounts();
  setLastOperation(`${movimiento.tipoMovimiento} ${money(movimiento.monto)}`);
  showToast(`${tipoMovimiento.toLowerCase()} registrado.`);
  form.reset();
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
    loadUserPortfolio();
    localStorage.setItem("rb_token", state.token);
    localStorage.setItem("rb_user", state.user);
    localStorage.setItem("rb_exchange_rate", state.exchangeRate);
    showToast("Sesion iniciada.");
    await refreshClientes();
    routeBank("dashboard");
  } catch (error) {
    showToast("No se pudo iniciar sesion. Revisa usuario y contrasena.", "error");
  }
});

els.logoutButton.addEventListener("click", () => {
  state.token = "";
  state.user = "";
  state.clientes = [];
  state.cuentas = [];
  state.selectedProduct = "";
  localStorage.removeItem("rb_token");
  localStorage.removeItem("rb_user");
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
    state.cuentas.push(cuenta);
    state.selectedProduct = cuenta.numeroCuenta;
    saveCuentas();
    renderCuentas();
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

els.reporteCarteraButton.addEventListener("click", async () => {
  if (!requireSession()) return;
  try {
    const reporte = await api("/api/reportes/cartera");
    els.reporteResult.innerHTML = `
      <span>Total de cuentas</span><strong>${reporte.totalCuentas}</strong>
      <span>Saldo total</span><strong>${money(reporte.saldoTotal)}</strong>
      <span>Moneda base</span><strong>${reporte.monedaBase}</strong>
    `;
    showToast("Reporte generado.");
  } catch (error) {
    showToast(error.message, "error");
  }
});

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
  refreshClientes().catch(() => {
    state.token = "";
    state.user = "";
    localStorage.removeItem("rb_token");
    localStorage.removeItem("rb_user");
    routePublic("login");
  });
}
