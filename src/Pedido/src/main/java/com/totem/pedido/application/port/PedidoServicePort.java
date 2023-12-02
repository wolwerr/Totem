package com.totem.pedido.application.port;

import com.totem.pedido.domain.DadosClienteException;
import com.totem.pedido.domain.Pedido;
import com.totem.pedido.domain.StatusPedido;

import java.util.List;
import java.util.Optional;

public interface PedidoServicePort {

    Pedido criarPedido(Pedido pedido) throws InterruptedException;

    Pedido criarPedidoComDadosCliente(Pedido pedido) throws DadosClienteException;

    Optional<Pedido> buscarPedidoPorId(Long id);

    List<Pedido> listarTodosPedidos();

    Pedido atualizarPedido(Long id, Pedido pedido);

    void deletarPedido(Long id);

    Pedido atualizarStatusPedido(Long id, StatusPedido novoStatus);

    Pedido prepararPedido(Long id);


}
