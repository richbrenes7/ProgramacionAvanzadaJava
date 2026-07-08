package com.banco.api.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.banco.api.entity.Cliente;
import com.banco.api.entity.Cuenta;
import com.banco.api.entity.Movimiento;
import com.banco.api.repository.ClienteRepository;
import com.banco.api.repository.CuentaRepository;
import com.banco.api.repository.MovimientoRepository;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(ClienteRepository clienteRepository, CuentaRepository cuentaRepository,
            MovimientoRepository movimientoRepository) {
        return args -> {
            if (clienteRepository.count() > 0) {
                return;
            }

            Cliente cliente = clienteRepository.save(new Cliente("Juan Pérez", "123456789", "juan@example.com"));
            Cuenta cuenta = cuentaRepository.save(new Cuenta("001-0001-0000001", "AHORRO", BigDecimal.valueOf(1250.50), "GTQ", cliente));

            movimientoRepository.save(new Movimiento(cuenta, BigDecimal.valueOf(500), "CREDITO", "Depósito inicial", "DEP-001"));
            movimientoRepository.save(new Movimiento(cuenta, BigDecimal.valueOf(250), "DEBITO", "Pago de servicio", "PAG-001"));
        };
    }
}