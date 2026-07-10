/*
 * Nelson Ricardo Brenes Lemus
 * Carnet: 092-20-3971
 * Curso: Programacion Avanzada en Java
 * Tarea: Proyecto Final
 * Guatemala, 2026 - NRBL.
 */
package com.banco.core.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.banco.core.cuenta.Cuenta;
import com.banco.core.cuenta.CuentaRepository;
import com.banco.core.movimiento.Movimiento;
import com.banco.core.movimiento.MovimientoRepository;

@Service
public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;
    private final TransactionTemplate transactionTemplate;

    public CuentaService(CuentaRepository cuentaRepository, MovimientoRepository movimientoRepository,
                         PlatformTransactionManager transactionManager) {
        this.cuentaRepository = cuentaRepository;
        this.movimientoRepository = movimientoRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public Cuenta crearCuenta(Cuenta c) {
        if (c.getNumeroCuenta() == null || c.getNumeroCuenta().isBlank() || "AUTO".equalsIgnoreCase(c.getNumeroCuenta())) {
            c.setNumeroCuenta(generarNumeroProducto(c.getTipoCuenta()));
        }
        if (c.getEstado() == null || c.getEstado().isBlank()) c.setEstado("ACTIVO");
        if (c.getMoneda() == null || c.getMoneda().isBlank()) c.setMoneda("GTQ");
        if (c.getSaldo() == null) c.setSaldo(BigDecimal.ZERO);
        return cuentaRepository.save(c);
    }

    public Cuenta crearProductoParaCliente(Long clienteId, String tipoCuenta, String moneda, BigDecimal saldoInicial, String estado) {
        Cuenta cuenta = new Cuenta();
        cuenta.setClienteId(clienteId);
        cuenta.setTipoCuenta(tipoCuenta);
        cuenta.setNumeroCuenta(generarNumeroProducto(tipoCuenta));
        cuenta.setMoneda(moneda == null || moneda.isBlank() ? "GTQ" : moneda);
        cuenta.setSaldo(saldoInicial == null ? BigDecimal.ZERO : saldoInicial);
        cuenta.setEstado(estado == null || estado.isBlank() ? "ACTIVO" : estado);
        return cuentaRepository.save(cuenta);
    }

    public Cuenta obtenerPorNumero(String numero) {
        return cuentaRepository.findByNumeroCuenta(numero).orElse(null);
    }

    public List<Cuenta> obtenerPorCliente(Long clienteId) {
        return cuentaRepository.findByClienteId(clienteId);
    }

    public Cuenta asignarCuentaACliente(String numeroCuenta, Long clienteId) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta).orElseThrow();
        cuenta.setClienteId(clienteId);
        return cuentaRepository.save(cuenta);
    }

    public List<Movimiento> movimientos(Long cuentaId) {
        return movimientoRepository.findByCuentaIdOrderByFechaMovimientoDesc(cuentaId);
    }

    public synchronized void transferir(String origenNum, String destinoNum, BigDecimal monto) {
        transactionTemplate.executeWithoutResult(status -> {
            Cuenta origen = cuentaRepository.findByNumeroCuenta(origenNum).orElseThrow();
            Cuenta destino = cuentaRepository.findByNumeroCuenta(destinoNum).orElseThrow();
            if (origen.getSaldo().compareTo(monto) < 0) throw new IllegalArgumentException("Saldo insuficiente");
            origen.setSaldo(origen.getSaldo().subtract(monto));
            destino.setSaldo(destino.getSaldo().add(monto));
            cuentaRepository.save(origen);
            cuentaRepository.save(destino);
            Movimiento m1 = new Movimiento();
            m1.setCuentaId(origen.getId());
            m1.setTipoMovimiento("TRANSFERENCIA_ENVIADA");
            m1.setMonto(monto);
            m1.setSaldoAnterior(origen.getSaldo().add(monto));
            m1.setSaldoNuevo(origen.getSaldo());
            m1.setReferencia("TRX-" + System.currentTimeMillis());
            movimientoRepository.save(m1);
            Movimiento m2 = new Movimiento();
            m2.setCuentaId(destino.getId());
            m2.setTipoMovimiento("TRANSFERENCIA_RECIBIDA");
            m2.setMonto(monto);
            m2.setSaldoAnterior(destino.getSaldo().subtract(monto));
            m2.setSaldoNuevo(destino.getSaldo());
            m2.setReferencia(m1.getReferencia());
            movimientoRepository.save(m2);
        });
    }

    private String generarNumeroProducto(String tipoCuenta) {
        String tipo = tipoCuenta == null ? "AHORROS" : tipoCuenta.toUpperCase(Locale.ROOT);
        String prefijo;
        int longitud;

        if (tipo.contains("CREDITO") || tipo.contains("TARJETA")) {
            prefijo = "TC";
            longitud = 16;
        } else if (tipo.contains("MONETARIA")) {
            prefijo = "MON";
            longitud = ThreadLocalRandom.current().nextInt(10, 13);
        } else {
            prefijo = "AHO";
            longitud = 7;
        }

        String numero;
        do {
            numero = prefijo + "-" + randomDigits(longitud);
        } while (cuentaRepository.existsByNumeroCuenta(numero));
        return numero;
    }

    private String randomDigits(int length) {
        StringBuilder value = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            value.append(ThreadLocalRandom.current().nextInt(0, 10));
        }
        return value.toString();
    }
}
