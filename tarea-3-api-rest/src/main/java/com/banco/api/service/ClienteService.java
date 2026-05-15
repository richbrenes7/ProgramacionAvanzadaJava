package com.banco.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.banco.api.dto.ClienteDTO;
import com.banco.api.dto.CreateClienteRequest;
import com.banco.api.entity.Cliente;
import com.banco.api.exception.ResourceNotFoundException;
import com.banco.api.repository.ClienteRepository;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public ClienteDTO create(CreateClienteRequest request) {
        Cliente saved = clienteRepository.save(new Cliente(request.nombre(), request.documento(), request.email()));
        return toDto(saved);
    }

    public List<ClienteDTO> findAll() {
        return clienteRepository.findAll().stream().map(this::toDto).toList();
    }

    public ClienteDTO findById(Long id) {
        return clienteRepository.findById(id).map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
    }

    public ClienteDTO update(Long id, CreateClienteRequest request) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
        cliente.setNombre(request.nombre());
        cliente.setDocumento(request.documento());
        cliente.setEmail(request.email());
        return toDto(clienteRepository.save(cliente));
    }

    public void delete(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
        cliente.setEstado(false);
        clienteRepository.save(cliente);
    }

    private ClienteDTO toDto(Cliente cliente) {
        return new ClienteDTO(cliente.getId(), cliente.getNombre(), cliente.getDocumento(), cliente.getEmail(), cliente.isEstado());
    }
}