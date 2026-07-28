package com.banco.core.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banco.core.cliente.ClienteRepository;
import com.banco.core.cuenta.Cuenta;
import com.banco.core.cuenta.CuentaRepository;
import com.banco.core.movimiento.Movimiento;
import com.banco.core.movimiento.MovimientoRepository;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final CuentaRepository cuentaRepository;
    private final ClienteRepository clienteRepository;
    private final MovimientoRepository movimientoRepository;
    private final ApiUsageLogService apiUsageLogService;

    public ReporteController(CuentaRepository cuentaRepository,
                             ClienteRepository clienteRepository,
                             MovimientoRepository movimientoRepository,
                             ApiUsageLogService apiUsageLogService) {
        this.cuentaRepository = cuentaRepository;
        this.clienteRepository = clienteRepository;
        this.movimientoRepository = movimientoRepository;
        this.apiUsageLogService = apiUsageLogService;
    }

    @GetMapping("/cartera")
    public ResponseEntity<Map<String, Object>> cartera() {
        var cuentas = cuentaRepository.findAll();
        return ResponseEntity.ok(carteraResumen(cuentas));
    }

    @GetMapping("/operativo")
    public ResponseEntity<Map<String, Object>> operativo() {
        var clientes = clienteRepository.findAll();
        var cuentas = cuentaRepository.findAll();
        var movimientos = movimientoRepository.findAll();

        BigDecimal creditos = movimientos.stream()
                .filter(mov -> signoMovimiento(mov.getTipoMovimiento()) > 0)
                .map(this::montoSeguro)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal debitos = movimientos.stream()
                .filter(mov -> signoMovimiento(mov.getTipoMovimiento()) < 0)
                .map(this::montoSeguro)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("clientes", clientes.size());
        resumen.put("productos", cuentas.size());
        resumen.put("movimientos", movimientos.size());
        resumen.put("totalCreditos", creditos);
        resumen.put("totalDebitos", debitos);
        resumen.put("saldoTotal", saldoTotal(cuentas));
        resumen.put("monedaBase", "GTQ");

        Map<String, Object> reporte = new LinkedHashMap<>();
        reporte.put("generadoEn", LocalDateTime.now());
        reporte.put("resumen", resumen);
        reporte.put("cartera", carteraResumen(cuentas));
        reporte.put("movimientosPorTipo", agruparConteo(movimientos, Movimiento::getTipoMovimiento));
        reporte.put("montoPorTipoMovimiento", agruparMontoMovimiento(movimientos));
        reporte.put("ultimosMovimientos", ultimosMovimientos(movimientos));
        reporte.put("apis", catalogoApis());
        return ResponseEntity.ok(reporte);
    }


    @GetMapping("/tecnico")
    public ResponseEntity<Map<String, Object>> tecnico() {
        Map<String, Object> reporte = new LinkedHashMap<>();
        reporte.put("generadoEn", LocalDateTime.now());
        reporte.put("alcance", "Observabilidad tecnica y diagnostico operativo del servicio");
        reporte.put("health", Map.of(
                "endpoint", "/actuator/health",
                "proposito", "Verificar disponibilidad de la aplicacion y sus componentes de salud",
                "uso", "Monitoreo, pruebas locales y validacion de despliegue"));
        reporte.put("logs", List.of(
                "Spring Boot registra arranque, errores HTTP, seguridad y consultas relevantes en consola o proveedor de nube.",
                "En ejecucion local con run-local-h2.bat los logs quedan en la consola/proceso iniciado.",
                "En Docker o nube, los logs se consultan desde docker logs, GitHub Actions, AWS CloudWatch o Azure Monitor segun el despliegue."));
        reporte.put("endpointsTecnicos", List.of(
                api("Observabilidad", "GET", "/actuator/health", "Estado tecnico de salud del servicio."),
                api("Documentacion", "GET", "/swagger-ui/index.html", "Interfaz Swagger/OpenAPI para explorar endpoints."),
                api("Documentacion", "GET", "/v3/api-docs", "Especificacion OpenAPI en formato JSON."),
                api("Reportes", "GET", "/api/reportes/tecnico", "Resumen documental de observabilidad tecnica.")));
        reporte.put("consumoApisReciente", apiUsageLogService.recientes(25));
        reporte.put("consumoApisPorRuta", apiUsageLogService.resumenPorRuta());
        reporte.put("nota", "Swagger documenta el catalogo; consumoApisReciente muestra llamadas capturadas en memoria desde el ultimo arranque.");
        return ResponseEntity.ok(reporte);
    }

    private Map<String, Object> carteraResumen(List<Cuenta> cuentas) {
        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("totalCuentas", cuentas.size());
        resumen.put("saldoTotal", saldoTotal(cuentas));
        resumen.put("monedaBase", "GTQ");
        resumen.put("cuentasPorTipo", agruparConteo(cuentas, Cuenta::getTipoCuenta));
        resumen.put("cuentasPorEstado", agruparConteo(cuentas, Cuenta::getEstado));
        resumen.put("saldoPorTipo", saldoPorTipo(cuentas));
        resumen.put("clientesConProductos", cuentas.stream()
                .map(Cuenta::getClienteId)
                .filter(id -> id != null)
                .collect(Collectors.toSet())
                .size());
        return resumen;
    }

    private BigDecimal saldoTotal(List<Cuenta> cuentas) {
        return cuentas.stream()
                .map(cuenta -> cuenta.getSaldo() == null ? BigDecimal.ZERO : cuenta.getSaldo())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal montoSeguro(Movimiento movimiento) {
        return movimiento.getMonto() == null ? BigDecimal.ZERO : movimiento.getMonto();
    }

    private int signoMovimiento(String tipoMovimiento) {
        String tipo = tipoMovimiento == null ? "" : tipoMovimiento.toUpperCase();
        return tipo.equals("RETIRO") || tipo.equals("TRANSFERENCIA_ENVIADA") ? -1 : 1;
    }

    private <T> Map<String, Long> agruparConteo(List<T> datos, java.util.function.Function<T, String> clasificador) {
        return datos.stream().collect(Collectors.groupingBy(
                item -> normalizar(clasificador.apply(item)),
                LinkedHashMap::new,
                Collectors.counting()
        ));
    }

    private Map<String, BigDecimal> saldoPorTipo(List<Cuenta> cuentas) {
        return cuentas.stream().collect(Collectors.groupingBy(
                cuenta -> normalizar(cuenta.getTipoCuenta()),
                LinkedHashMap::new,
                Collectors.mapping(
                        cuenta -> cuenta.getSaldo() == null ? BigDecimal.ZERO : cuenta.getSaldo(),
                        Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                )
        ));
    }

    private Map<String, BigDecimal> agruparMontoMovimiento(List<Movimiento> movimientos) {
        return movimientos.stream().collect(Collectors.groupingBy(
                movimiento -> normalizar(movimiento.getTipoMovimiento()),
                LinkedHashMap::new,
                Collectors.mapping(this::montoSeguro, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
        ));
    }

    private List<Map<String, Object>> ultimosMovimientos(List<Movimiento> movimientos) {
        return movimientos.stream()
                .sorted(Comparator.comparing(Movimiento::getFechaMovimiento, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .limit(10)
                .map(mov -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("id", mov.getId());
                    item.put("cuentaId", mov.getCuentaId());
                    item.put("tipo", mov.getTipoMovimiento());
                    item.put("monto", mov.getMonto());
                    item.put("saldoAnterior", mov.getSaldoAnterior());
                    item.put("saldoNuevo", mov.getSaldoNuevo());
                    item.put("fecha", mov.getFechaMovimiento());
                    return item;
                })
                .toList();
    }

    private String normalizar(String valor) {
        return valor == null || valor.isBlank() ? "SIN_DATO" : valor;
    }

    private List<Map<String, String>> catalogoApis() {
        return List.of(
                api("Autenticacion", "POST", "/api/auth/login", "Inicia sesion y entrega JWT para operar."),
                api("Autenticacion", "POST", "/api/auth/forgot-password", "Reset demo de contrasena por usuario."),
                api("Clientes", "POST", "/api/clientes", "Crea registros de clientes."),
                api("Clientes", "GET", "/api/clientes", "Lista clientes registrados."),
                api("Clientes", "GET", "/api/clientes/{id}", "Consulta un cliente especifico."),
                api("Clientes", "PUT", "/api/clientes/{id}", "Actualiza informacion de cliente."),
                api("Productos", "POST", "/api/cuentas", "Crea producto bancario/cuenta."),
                api("Productos", "GET", "/api/cuentas/numero/{numero}", "Consulta producto por numeracion."),
                api("Productos", "GET", "/api/cuentas/cliente/{clienteId}", "Lista productos asociados a un cliente."),
                api("Productos", "GET", "/api/cuentas/{numero}/saldo", "Consulta saldo de un producto."),
                api("Movimientos", "POST", "/api/movimientos", "Registra deposito o retiro."),
                api("Movimientos", "GET", "/api/movimientos/cuenta/{cuentaId}", "Consulta historial por producto."),
                api("Transacciones", "POST", "/api/transacciones", "Ejecuta transferencia individual."),
                api("Transacciones", "POST", "/api/transacciones/lote", "Procesa lote concurrente."),
                api("Administracion", "GET", "/api/admin/usuarios", "Lista usuarios para gestion administrativa."),
                api("Administracion", "POST", "/api/admin/usuarios", "Crea usuarios operativos/admin."),
                api("Administracion", "POST", "/api/admin/productos", "Genera y asocia productos a clientes."),
                api("Reportes", "GET", "/api/reportes/cartera", "Resumen financiero de cartera."),
                api("Reportes", "GET", "/api/reportes/operativo", "Reporte de registros, movimientos y catalogo de APIs."),
                api("Reportes", "GET", "/api/reportes/tecnico", "Reporte documental de observabilidad tecnica.")
        );
    }

    private Map<String, String> api(String modulo, String metodo, String ruta, String descripcion) {
        Map<String, String> item = new LinkedHashMap<>();
        item.put("modulo", modulo);
        item.put("metodo", metodo);
        item.put("ruta", ruta);
        item.put("descripcion", descripcion);
        return item;
    }
}
