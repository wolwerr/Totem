package com.totem.pedido.infrastruture.adapter.rest;


import com.totem.pedido.application.port.PedidoServicePort;
import com.totem.pedido.domain.DadosClienteException;
import com.totem.pedido.domain.Pedido;
import com.totem.pedido.domain.StatusPedido;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoServicePort pedidoService;

    public PedidoController(BlockingQueue<Map.Entry<Long, String>> clienteDataQueue, PedidoServicePort pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<?> criarPedidoComDadosCliente(@RequestBody Pedido pedido) {
        try {
            Pedido pedidoCriado = pedidoService.criarPedidoComDadosCliente(pedido);
            return ResponseEntity.status(HttpStatus.CREATED).body(pedidoCriado);
        } catch (DadosClienteException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar o pedido: " + e.getMessage());
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