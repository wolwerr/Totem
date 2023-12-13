package com.totem.pedido.infrastruture.repository;


import com.totem.pedido.domain.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteId(Long clienteId);

    List<Pedido> findByStatusPagamento(String statusPagamento);

    List<Pedido> findByStatusPedido(String statusPedido);

}
