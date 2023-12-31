package com.totem.cliente.application.service;

import com.totem.cliente.application.port.ClienteServicePort;
import com.totem.cliente.domain.Cliente;
import com.totem.cliente.infrastructure.config.ClienteKafkaProducer;
import com.totem.cliente.infrastructure.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteServicePort {

    private final ClienteRepository clienteRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.topic}")
    private String topic;

    @Override
    public Cliente criarCliente(Cliente cliente) {
        if (cliente.getNome() == null) {
            String naoId = "NÃO IDENTIFICADO";
            cliente.setNome(naoId);
            cliente.setCpf(naoId);
            cliente.setEmail(naoId);
        }
        Cliente novoCliente = clienteRepository.save(cliente);
        ClienteKafkaProducer producer = new ClienteKafkaProducer(bootstrapServers, topic);
        try {
            producer.enviarMensagemCliente(novoCliente);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.close();
        }
        return novoCliente;
    }

    @Override
    public Optional<Cliente> buscarClientePorId(Long id) {
        // Retorna o cliente ou Optional.empty() se não encontrado
        return clienteRepository.findById(id);
    }

    @Override
    public List<Cliente> listarTodosClientes() {
        return clienteRepository.findAll();
    }
    public Optional<Cliente> buscarClientePorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    @Override
    public Optional<Cliente> buscarClientePorCpf(String cpf) {
        return clienteRepository.findByCpf(cpf);
    }

    @Override
    public Cliente atualizarCliente(Long id, Cliente clienteAtualizado) {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    cliente.setNome(clienteAtualizado.getNome());
                    cliente.setCpf(clienteAtualizado.getCpf());
                    cliente.setEmail(clienteAtualizado.getEmail());
                    return clienteRepository.save(cliente);
                })
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado")); // Substitua por uma exceção mais específica
    }

    @Override
    public void deletarCliente(Long id) {
        clienteRepository.deleteById(id);
    }
}
