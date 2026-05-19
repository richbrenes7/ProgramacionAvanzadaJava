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
import org.springframework.transaction.annotation.Transactional;

import com.banco.core.movimiento.Movimiento;
import com.banco.core.movimiento.MovimientoRepository;
import com.banco.core.cuenta.Cuenta;
import com.banco.core.cuenta.CuentaRepository;

@Service
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;

    public MovimientoService(MovimientoRepository movimientoRepository, CuentaRepository cuentaRepository) {
        this.movimientoRepository = movimientoRepository;
        this.cuentaRepository = cuentaRepository;
    }

    @Transactional
    public Movimiento registrarMovimiento(Movimiento m) {
        Cuenta cuenta = cuentaRepository.findById(m.getCuentaId()).orElseThrow();
        BigDecimal saldoAnterior = cuenta.getSaldo();
        if ("DEPOSITO".equalsIgnoreCase(m.getTipoMovimiento())) {
            cuenta.setSaldo(cuenta.getSaldo().add(m.getMonto()));
        } else if ("RETIRO".equalsIgnoreCase(m.getTipoMovimiento())) {
            if (cuenta.getSaldo().compareTo(m.getMonto()) < 0) throw new IllegalArgumentException("Saldo insuficiente");
            cuenta.setSaldo(cuenta.getSaldo().subtract(m.getMonto()));
        }
        cuentaRepository.save(cuenta);
        m.setSaldoAnterior(saldoAnterior);
        m.setSaldoNuevo(cuenta.getSaldo());
        if (m.getReferencia() == null || m.getReferencia().isBlank()) m.setReferencia("MV-" + System.currentTimeMillis());
        return movimientoRepository.save(m);
    }

    public List<Movimiento> listarPorCuenta(Long cuentaId) {
        return movimientoRepository.findByCuentaIdOrderByFechaMovimientoDesc(cuentaId);
    }
}
