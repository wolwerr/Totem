package com.totem.pagamento.domain;

public class DadosPagamentoException extends Exception {

    public DadosPagamentoException(String message) {
        super(message);
    }

    // Você pode adicionar construtores adicionais, por exemplo, para incluir a causa da exceção
    public DadosPagamentoException(String message, Throwable cause) {
        super(message, cause);
    }

    // Outros métodos úteis podem ser incluídos aqui, dependendo das suas necessidades
}
