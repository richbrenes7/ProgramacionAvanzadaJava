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
        return cuentaRepository.save(c);
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
}
