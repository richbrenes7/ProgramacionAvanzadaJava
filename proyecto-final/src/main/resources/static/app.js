const state = {
  token: localStorage.getItem("rb_token") || "",
  clientes: [],
  cuentas: JSON.parse(localStorage.getItem("rb_cuentas") || "[]"),
  lastOperation: localStorage.getItem("rb_last_operation") || "Sin actividad"
};

const els = {
  loginPanel: document.querySelector("#loginPanel"),
  loginForm: document.querySelector("#loginForm"),
  clienteForm: document.querySelector("#clienteForm"),
  cuentaForm: document.querySelector("#cuentaForm"),
  buscarCuentaForm: document.querySelector("#buscarCuentaForm"),
  saldoForm: document.querySelector("#saldoForm"),
  depositoForm: document.querySelector("#depositoForm"),
  retiroForm: document.querySelector("#retiroForm"),
  transferForm: document.querySelector("#transferForm"),
  loteForm: document.querySelector("#loteForm"),
  movimientosForm: document.querySelector("#movimientosForm"),
  refreshClientes: document.querySelector("#refreshClientes"),
  reporteCarteraButton: document.querySelector("#reporteCarteraButton"),
  logoutButton: document.querySelector("#logoutButton"),
  sessionLabel: document.querySelector("#sessionLabel"),
  clientesList: document.querySelector("#clientesList"),
  cuentasList: document.querySelector("#cuentasList"),
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

function saveCuentas() {
  const unique = new Map();
  state.cuentas.forEach(cuenta => {
    if (cuenta && cuenta.numeroCuenta) unique.set(cuenta.numeroCuenta, cuenta);
  });
  state.cuentas = Array.from(unique.values());
  localStorage.setItem("rb_cuentas", JSON.stringify(state.cuentas));
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
    showToast("Primero inicia sesion.", "error");
    return false;
  }
  return true;
}

function showModule(moduleName) {
  document.querySelectorAll("[data-module]").forEach(section => {
    section.classList.toggle("active", section.dataset.module === moduleName);
  });
  document.querySelectorAll("[data-module-link]").forEach(link => {
    link.classList.toggle("active", link.dataset.moduleLink === moduleName);
  });
  if (window.location.hash !== `#${moduleName}`) {
    history.replaceState(null, "", `#${moduleName}`);
  }
}

function renderSession() {
  const logged = Boolean(state.token);
  els.loginPanel.hidden = logged;
  els.logoutButton.hidden = !logged;
  els.sessionLabel.textContent = logged ? "Sesion activa" : "Sesion no iniciada";
}

function renderDashboard() {
  els.clientesCount.textContent = state.clientes.length;
  els.cuentasCount.textContent = state.cuentas.length;
  els.balanceTotal.textContent = money(state.cuentas.reduce((sum, cuenta) => sum + Number(cuenta.saldo || 0), 0));
  els.lastOperation.textContent = state.lastOperation;
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
    els.cuentasList.textContent = "Las cuentas creadas o buscadas aparecen aqui.";
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
    localStorage.setItem("rb_token", state.token);
    renderSession();
    showToast("Sesion iniciada.");
    await refreshClientes();
  } catch (error) {
    showToast("No se pudo iniciar sesion. Revisa usuario y contrasena.", "error");
  }
});

els.logoutButton.addEventListener("click", () => {
  state.token = "";
  localStorage.removeItem("rb_token");
  state.clientes = [];
  renderSession();
  renderClientes();
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

document.querySelectorAll("[data-module-link]").forEach(link => {
  link.addEventListener("click", event => {
    event.preventDefault();
    showModule(link.dataset.moduleLink);
  });
});

window.addEventListener("hashchange", () => {
  showModule((window.location.hash || "#dashboard").slice(1));
});

renderSession();
renderClientes();
renderCuentas();
renderDashboard();
showModule((window.location.hash || "#dashboard").slice(1));

if (state.token) {
  refreshClientes().catch(() => {
    state.token = "";
    localStorage.removeItem("rb_token");
    renderSession();
  });
}
