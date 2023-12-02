package com.totem.cliente.infrastructure.repository;

import com.totem.cliente.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByEmail(String email);

    Optional<Cliente> findByCpf(String cpf);

}
