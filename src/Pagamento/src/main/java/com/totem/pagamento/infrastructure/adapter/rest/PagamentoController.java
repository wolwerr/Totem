package com.totem.pagamento.infrastructure.adapter.rest;

import com.totem.pagamento.application.port.PagamentoServicePort;
import com.totem.pagamento.domain.DadosPagamentoException;
import com.totem.pagamento.domain.Pagamento;
import com.totem.pagamento.domain.StatusPagamento;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final PagamentoServicePort pagamentoService;

    public PagamentoController(PagamentoServicePort pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping
    public ResponseEntity<?> criarPagamentoComDadosPedido(@RequestBody Pagamento pagamento) {
        try {
            Pagamento pagamentoCriado = pagamentoService.criarPagamentoComDadosPedido(pagamento);
            return ResponseEntity.status(HttpStatus.CREATED).body(pagamentoCriado);
        } catch (DadosPagamentoException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar o pagamento: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizarPagamento(@PathVariable Long id, @RequestBody Pagamento pagamento) {
        pagamento.setId(id);
        pagamentoService.atualizarPagamento(pagamento);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPagamento(@PathVariable Long id) {
        pagamentoService.deletarPagamento(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pagamento> buscarPagamento(@PathVariable Long id) {
        return pagamentoService.buscarPagamento(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Pagamento>> buscarTodosPagamentos() {
        return ResponseEntity.ok(pagamentoService.buscarTodosPagamentos());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Pagamento> atualizarStatusPagamento(@PathVariable Long id, @RequestBody StatusPagamento novoStatus) {
        Pagamento pagamentoAtualizado = pagamentoService.atualizarStatusPagamento(id, novoStatus);
        return ResponseEntity.ok(pagamentoAtualizado);
    }
}
