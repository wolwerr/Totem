package com.totem.pedido.infrastruture.adapter.rest;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.totem.pedido.application.port.PedidoServicePort;
import com.totem.pedido.domain.Pedido;
import com.totem.pedido.domain.StatusPedido;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {
    private final BlockingQueue<Map.Entry<Long, String>> clienteDataQueue;
    private final PedidoServicePort pedidoService;


    public PedidoController(BlockingQueue<Map.Entry<Long, String>> clienteDataQueue, PedidoServicePort pedidoService) {
        this.clienteDataQueue = clienteDataQueue;
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<?> criarPedidoComDadosCliente(@RequestBody Pedido pedido) {
        try {
            Map.Entry<Long, String> clienteData = clienteDataQueue.poll(5, TimeUnit.SECONDS);
            if (clienteData != null) {
                Long clienteId = clienteData.getKey();
                String clienteJson = clienteData.getValue();

                // Configura os dados do cliente no pedido
                configurarDadosClienteEmPedido(clienteJson, pedido);

                // Verifica se o clienteId do JSON corresponde ao clienteId do pedido
                if (!pedido.getClienteId().equals(clienteId)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Inconsistência nos dados do cliente.");
                }
                // Configurar e salvar o pedido
                pedido.setDataCriacao(new Date());
                pedido.setStatus(StatusPedido.RECEBIDO);
                pedido.setItens(pedido.getItens());
                pedido.setValorTotal(pedido.getValorTotal());
                Pedido pedidoCriado = pedidoService.criarPedido(pedido);
                return ResponseEntity.status(HttpStatus.CREATED).body(pedidoCriado);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dados do cliente não disponíveis.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar o pedido: " + e.getMessage());
        }
    }

    private void configurarDadosClienteEmPedido(String json, Pedido pedido) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(json);
            if (rootNode.has("clienteId")) {
                pedido.setClienteId(rootNode.get("clienteId").asLong());
            }
            if (rootNode.has("clienteNome")) {
                pedido.setNomeCliente(rootNode.get("clienteNome").asText());
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao converter JSON para dados do cliente", e);
        }
    }



    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPedidoPorId(@PathVariable Long id) {
        return pedidoService.buscarPedidoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> listarTodosPedidos() {
        List<Pedido> pedidos = pedidoService.listarTodosPedidos();
        return ResponseEntity.ok(pedidos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pedido> atualizarPedido(@PathVariable Long id, @RequestBody Pedido pedido) {
        Pedido pedidoAtualizado = pedidoService.atualizarPedido(id, pedido);
        return ResponseEntity.ok(pedidoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPedido(@PathVariable Long id) {
        pedidoService.deletarPedido(id);
        return ResponseEntity.noContent().build();
    }

    // Novo endpoint para atualizar o status do pedido
    @PatchMapping("/{id}/status")
    public ResponseEntity<Pedido> atualizarStatusPedido(@PathVariable Long id, @RequestBody StatusPedido novoStatus) {
        Pedido pedidoAtualizado = pedidoService.atualizarStatusPedido(id, novoStatus);
        return ResponseEntity.ok(pedidoAtualizado);
    }
}