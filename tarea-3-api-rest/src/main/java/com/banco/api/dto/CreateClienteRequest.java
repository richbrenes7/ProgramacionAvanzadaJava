package com.banco.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateClienteRequest(
        @NotBlank @Size(min = 3, max = 120) String nombre,
        @NotBlank @Size(min = 5, max = 30) String documento,
        @NotBlank @Email String email) {
}