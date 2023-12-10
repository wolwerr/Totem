package com.totem.pagamento.infrastructure.repository;

import com.totem.pagamento.domain.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long>{

    Optional<Pagamento> findByPedidoId(Long pedidoId);

}
