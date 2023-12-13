package com.totem.pagamento.application.service;

import com.totem.pagamento.application.port.PagamentoServicePort;
import com.totem.pagamento.domain.DadosPagamentoException;
import com.totem.pagamento.domain.FormaPagamento;
import com.totem.pagamento.domain.Pagamento;
import com.totem.pagamento.domain.StatusPagamento;
import com.totem.pagamento.infrastructure.repository.PagamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PagamentoServiceImpl implements PagamentoServicePort {

    private final PagamentoRepository pagamentoRepository;

    private final BlockingQueue<Map.Entry<Long, Double>> pagamentoDataQueue;

    @Override
    public Pagamento criarPagamentoComDadosPedido(Pagamento pagamento) throws DadosPagamentoException {
        try {
            Map.Entry<Long, Double> pedidoData = pagamentoDataQueue.poll(5, TimeUnit.SECONDS);
            if (pedidoData == null) {
                throw new DadosPagamentoException("Dados do pedido não disponíveis.");
            }
            Long pedidoId = pedidoData.getKey();
            Double valorTotal = pedidoData.getValue();
            configurarDadosPedidoEmPagamento(pedidoId, valorTotal, pagamento);
            return criarPagamento(pagamento);
        } catch (InterruptedException | IOException e) {
            Thread.currentThread().interrupt();
            throw new DadosPagamentoException("Erro ao processar dados do pedido: " + e.getMessage(), e);
        }
    }

    @Override
    public Pagamento criarPagamento(Pagamento pagamento) {
        pagamento.setFormaPagamento(FormaPagamento.QR_CODE);
        pagamento.setDataPagamento(new Date());
        pagamento.setStatusPagamento(StatusPagamento.PENDENTE);
        return pagamentoRepository.save(pagamento);
    }

    private void configurarDadosPedidoEmPagamento(Long pedidoId, Double valorTotal, Pagamento pagamento) throws IOException {
        pagamento.setPedidoId(pedidoId);
        pagamento.setValorTotal(valorTotal);
    }


    @Override
    public void atualizarPagamento(Pagamento pagamento) {

        pagamentoRepository.save(pagamento);
    }

    @Override
    public void deletarPagamento(Long id) {
        pagamentoRepository.deleteById(id);
    }

    @Override
    public Optional<Pagamento> buscarPagamento(Long id) {
        return pagamentoRepository.findById(id);
    }

    @Override
    public List<Pagamento> buscarTodosPagamentos() {
        return pagamentoRepository.findAll();
    }

    public Pagamento atualizarStatusPagamento(Long id, StatusPagamento novoStatusPagamento) {
        return pagamentoRepository.findById(id)
                .map(pedido -> {
                    pedido.setStatusPagamento(novoStatusPagamento);
                    pedido.setDataPagamento(new Date());
                    return pagamentoRepository.save(pedido);
                })
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
    }
}
