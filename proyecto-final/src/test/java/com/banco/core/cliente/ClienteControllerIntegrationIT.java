package com.banco.core.cliente;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClienteControllerIntegrationIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rest;

    @Test
    void crearYObtenerCliente() {
        Cliente c = new Cliente();
        c.setNombre("Test Cliente");
        c.setDocumento("12345678");
        c.setEmail("test@example.com");
        c.setTelefono("555-0101");
        c.setEstado("ACTIVO");

        ResponseEntity<Cliente> res = rest.postForEntity("http://localhost:" + port + "/api/clientes", c, Cliente.class);
        assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
        Cliente saved = res.getBody();
        assertThat(saved).isNotNull();
        ResponseEntity<Cliente> getRes = rest.getForEntity("http://localhost:" + port + "/api/clientes/" + saved.getId(), Cliente.class);
        assertThat(getRes.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(getRes.getBody().getDocumento()).isEqualTo("12345678");
    }
}
