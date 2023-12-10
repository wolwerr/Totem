package com.totem.pagamento.application.port;

import com.totem.pagamento.domain.DadosPagamentoException;
import com.totem.pagamento.domain.Pagamento;
import com.totem.pagamento.domain.StatusPagamento;

import java.util.List;
import java.util.Optional;

public interface PagamentoServicePort {

    Pagamento criarPagamento(Pagamento pagamento) throws InterruptedException;

    Pagamento criarPagamentoComDadosPedido(Pagamento pagamento) throws DadosPagamentoException;

    void atualizarPagamento(Pagamento pagamento);

    void deletarPagamento(Long id);

    Optional<Pagamento> buscarPagamento(Long id);

    List<Pagamento> buscarTodosPagamentos();

    Pagamento atualizarStatusPagamento(Long id, StatusPagamento novoStatus);
}