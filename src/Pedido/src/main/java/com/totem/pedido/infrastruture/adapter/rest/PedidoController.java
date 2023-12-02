package com.totem.pedido.infrastruture.adapter.rest;


import com.totem.pedido.application.port.PedidoServicePort;
import com.totem.pedido.domain.Pedido;
import com.totem.pedido.domain.StatusPedido;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoServicePort pedidoService;
    private final BlockingQueue<Map.Entry<Long, String>> clienteDataQueue;


    public PedidoController(PedidoServicePort pedidoService, BlockingQueue<Map.Entry<Long, String>> clienteDataQueue) {
        this.pedidoService = pedidoService;
        this.clienteDataQueue = clienteDataQueue;
    }

    @PostMapping
    public ResponseEntity<?> criarPedidoComDadosCliente(@RequestBody Pedido pedido) {
        try {
            // Certifique-se de que os campos do cliente (clienteId e nomeCliente) estão incluídos no objeto Pedido.
 // Substitua nomeCliente pelo campo real no JSON.
            pedido.setDataCriacao(new Date());
            pedido.setStatus(StatusPedido.RECEBIDO);
            pedido.setItens(pedido.getItens());
            pedido.setValorTotal(pedido.getValorTotal());

            Pedido pedidoCriado = pedidoService.criarPedido(pedido);

            return ResponseEntity.status(HttpStatus.CREATED).body(pedidoCriado);
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