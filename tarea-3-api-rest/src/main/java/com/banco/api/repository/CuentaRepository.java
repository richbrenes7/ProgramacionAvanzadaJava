package com.banco.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banco.api.entity.Cuenta;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);
}