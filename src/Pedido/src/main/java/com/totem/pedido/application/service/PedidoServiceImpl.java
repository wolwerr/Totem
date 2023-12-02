package com.totem.pedido.application.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.totem.pedido.application.port.PedidoServicePort;
import com.totem.pedido.domain.DadosClienteException;
import com.totem.pedido.domain.Pedido;
import com.totem.pedido.domain.StatusPedido;
import com.totem.pedido.infrastruture.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Service
public class PedidoServiceImpl implements PedidoServicePort {

    private final PedidoRepository pedidoRepository;

    private final BlockingQueue<Map.Entry<Long, String>> clienteDataQueue;

    private final ObjectMapper mapper;

    public PedidoServiceImpl(PedidoRepository pedidoRepository, ObjectMapper objectMapper, BlockingQueue<Map.Entry<Long, String>> clienteDataQueue) {
        this.pedidoRepository = pedidoRepository;
        this.mapper = objectMapper;
        this.clienteDataQueue = clienteDataQueue;
    }

    @Override
    public Pedido criarPedidoComDadosCliente(Pedido pedido) throws DadosClienteException {
        try {
            Map.Entry<Long, String> clienteData = clienteDataQueue.poll(5, TimeUnit.SECONDS);
            if (clienteData == null) {
                throw new DadosClienteException("Dados do cliente não disponíveis.");
            }
            Long clienteId = clienteData.getKey();
            String clienteJson = clienteData.getValue();

            try {
                configurarDadosClienteEmPedido(clienteJson, pedido);
            } catch (IOException e) {
                throw new DadosClienteException("Erro ao processar dados do cliente: " + e.getMessage(), e);
            }
            if (!pedido.getClienteId().equals(clienteId)) {
                throw new DadosClienteException("Inconsistência nos dados do cliente.");
            }
            return criarPedido(pedido);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DadosClienteException("Operação interrompida enquanto esperava os dados do cliente.", e);
        }
    }
    @Override
    public Pedido criarPedido(Pedido pedido) {
        pedido.setDataCriacao(new Date());
        pedido.setStatus(StatusPedido.RECEBIDO);
        return pedidoRepository.save(pedido);
    }

    private void configurarDadosClienteEmPedido(String json, Pedido pedido) throws IOException {
        JsonNode rootNode = mapper.readTree(json);
        if (rootNode.has("clienteId")) {
            pedido.setClienteId(rootNode.get("clienteId").asLong());
        }
        if (rootNode.has("clienteNome")) {
            pedido.setNomeCliente(rootNode.get("clienteNome").asText());
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