package com.banco.api.dto;

public record ClienteDTO(Long id, String nombre, String documento, String email, boolean estado) {
}