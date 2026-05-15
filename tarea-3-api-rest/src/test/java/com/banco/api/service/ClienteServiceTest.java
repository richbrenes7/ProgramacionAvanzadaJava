package com.banco.api.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.banco.api.dto.CreateClienteRequest;
import com.banco.api.entity.Cliente;
import com.banco.api.exception.ResourceNotFoundException;
import com.banco.api.repository.ClienteRepository;

class ClienteServiceTest {

    @Test
    void createShouldReturnDto() {
        ClienteRepository repository = mock(ClienteRepository.class);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        ClienteService service = new ClienteService(repository);

        var dto = service.create(new CreateClienteRequest("Juan Perez", "123456789", "juan@example.com"));

        assertEquals("Juan Perez", dto.nombre());
    }

    @Test
    void findAllShouldMapEntities() {
        ClienteRepository repository = mock(ClienteRepository.class);
        when(repository.findAll()).thenReturn(List.of(new Cliente("Juan", "123", "juan@example.com")));
        ClienteService service = new ClienteService(repository);

        assertEquals(1, service.findAll().size());
    }

    @Test
    void findByIdWhenMissingShouldThrow() {
        ClienteRepository repository = mock(ClienteRepository.class);
        when(repository.findById(1L)).thenReturn(Optional.empty());
        ClienteService service = new ClienteService(repository);

        assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));
    }

    @Test
    void deleteShouldDeactivateCliente() {
        ClienteRepository repository = mock(ClienteRepository.class);
        Cliente cliente = new Cliente("Juan", "123", "juan@example.com");
        when(repository.findById(1L)).thenReturn(Optional.of(cliente));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        ClienteService service = new ClienteService(repository);

        service.delete(1L);

        assertFalse(cliente.isEstado());
    }
}