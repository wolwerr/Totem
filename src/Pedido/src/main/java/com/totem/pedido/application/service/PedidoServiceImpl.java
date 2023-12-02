package com.totem.pedido.application.service;


import com.totem.pedido.application.port.PedidoServicePort;
import com.totem.pedido.domain.Pedido;
import com.totem.pedido.domain.StatusPedido;
import com.totem.pedido.infrastruture.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;



@Service
public class PedidoServiceImpl implements PedidoServicePort {

    private final PedidoRepository pedidoRepository;



    @Autowired
    public PedidoServiceImpl(PedidoRepository pedidoRepository, BlockingQueue<Map.Entry<Long, String>> clienteDataQueue) {
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public Pedido criarPedido(Pedido pedido) {
        try {
            // Configure dados adicionais do pedido
            pedido.setDataCriacao(new Date());
            pedido.setStatus(StatusPedido.RECEBIDO);

            // Salvar o pedido no banco de dados
            System.out.println("Pedido criado com sucesso!");
            return pedidoRepository.save(pedido);

        } catch (Exception e) {
            // Tratar exceções específicas se necessário
            throw new RuntimeException("Erro ao criar o pedido: " + e.getMessage());
        }
    }

    @Override
    public Optional<Pedido> buscarPedidoPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    @Override
    public List<Pedido> listarTodosPedidos() {
        return pedidoRepository.findAll();
    }

    @Override
    public Pedido atualizarPedido(Long id, Pedido pedidoAtualizado) {
        return pedidoRepository.findById(id)
                .map(pedidoExistente -> {
                    pedidoExistente.setDataCriacao(pedidoAtualizado.getDataCriacao());
                    pedidoExistente.setItens(pedidoAtualizado.getItens());
                    pedidoExistente.setStatus(pedidoAtualizado.getStatus());
                    pedidoExistente.setValorTotal(pedidoAtualizado.getValorTotal());
                    return pedidoRepository.save(pedidoExistente);
                })
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
    }

    @Override
    public void deletarPedido(Long id) {
        pedidoRepository.deleteById(id);
    }

    public Pedido atualizarStatusPedido(Long id, StatusPedido novoStatus) {
        return pedidoRepository.findById(id)
                .map(pedido -> {
                    pedido.setStatus(novoStatus);
                    return pedidoRepository.save(pedido);
                })
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
    }

    public Pedido prepararPedido(Long id) {
        return pedidoRepository.findById(id)
                .map(pedido -> {
                    if (pedido.getStatus() == StatusPedido.RECEBIDO) {
                        pedido.setStatus(StatusPedido.EMPREPARACAO);
                        // Lógica adicional para preparação do pedido
                    } else {
                        throw new RuntimeException("Pedido não está no estado apropriado para preparação");
                    }
                    return pedidoRepository.save(pedido);
                })
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
    }
}