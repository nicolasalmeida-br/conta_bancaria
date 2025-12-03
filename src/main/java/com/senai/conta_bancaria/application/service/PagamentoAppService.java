package com.senai.conta_bancaria.application.service;

import com.senai.conta_bancaria.application.dto.PagamentoResumoDTO;
import com.senai.conta_bancaria.domain.entity.*;
import com.senai.conta_bancaria.domain.enums.StatusPagamento;
import com.senai.conta_bancaria.domain.repository.*;
import com.senai.conta_bancaria.domain.service.PagamentoDomainService;
import jakarta.persistence.EntityNotFoundException;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PagamentoAppService {

    private final PagamentoRepository pagamentoRepository;
    private final ContaRepository contaRepository;
    private final TaxaRepository taxaRepository;
    private final ClienteRepository clienteRepository;
    private final CodigoAutenticacaoRepository codigoAutenticacaoRepository;
    private final PagamentoDomainService pagamentoDomainService;
    private final MqttClient mqttClient;

    public PagamentoAppService(PagamentoRepository pagamentoRepository,
                               ContaRepository contaRepository,
                               TaxaRepository taxaRepository,
                               ClienteRepository clienteRepository,
                               CodigoAutenticacaoRepository codigoAutenticacaoRepository,
                               PagamentoDomainService pagamentoDomainService,
                               MqttClient mqttClient) {
        this.pagamentoRepository = pagamentoRepository;
        this.contaRepository = contaRepository;
        this.taxaRepository = taxaRepository;
        this.clienteRepository = clienteRepository;
        this.codigoAutenticacaoRepository = codigoAutenticacaoRepository;
        this.pagamentoDomainService = pagamentoDomainService;
        this.mqttClient = mqttClient;
    }

    // =========================================================
    // AUTENTICAÇÃO VIA IOT
    // =========================================================

    /**
     * Gera um código, salva no banco e envia via MQTT para o cliente.
     * Tópico: banco/autenticacao/{clienteId} | Payload: CODE:XXXXXX
     */
    @Transactional
    public Map<String, Object> iniciarAutenticacao(String clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado: " + clienteId));

        String codigo = gerarCodigoNumerico(6);

        CodigoAutenticacao auth = new CodigoAutenticacao();
        auth.setCliente(cliente);
        auth.setCodigo(codigo);
        auth.setExpiraEm(LocalDateTime.now().plusMinutes(5));
        auth.setValidado(false);

        codigoAutenticacaoRepository.save(auth);

        // Publica para o dispositivo IoT
        String topic = "banco/autenticacao/" + clienteId;
        String payload = "CODE:" + codigo;
        try {
            if (!mqttClient.isConnected()) {
                mqttClient.connect();
            }
            MqttMessage message = new MqttMessage(payload.getBytes(StandardCharsets.UTF_8));
            mqttClient.publish(topic, message);
        } catch (MqttException e) {
            throw new RuntimeException("Erro ao publicar código no MQTT", e);
        }

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("clienteId", clienteId);
        resposta.put("codigoEnviado", true);
        resposta.put("expiraEm", auth.getExpiraEm());

        return resposta;
    }

    /**
     * Validado pelo listener MQTT quando o dispositivo IoT responde.
     * Tópico: banco/validacao/{clienteId} | Payload: CODE:XXXXXX
     */
    @Transactional
    public void validarCodigo(String clienteId, String codigoRecebido) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado: " + clienteId));

        CodigoAutenticacao ultimoCodigo = codigoAutenticacaoRepository
                .findTopByClienteOrderByExpiraEmDesc(cliente)
                .orElseThrow(() -> new EntityNotFoundException("Nenhum código de autenticação encontrado para o cliente."));

        if (ultimoCodigo.isValidado()) {
            System.out.println("[AUTENTICACAO] Código já foi validado anteriormente.");
            return;
        }

        if (ultimoCodigo.getExpiraEm().isBefore(LocalDateTime.now())) {
            System.out.println("[AUTENTICACAO] Código expirado para cliente " + clienteId);
            return;
        }

        if (!ultimoCodigo.getCodigo().equals(codigoRecebido)) {
            System.out.println("[AUTENTICACAO] Código inválido para cliente " + clienteId);
            return;
        }

        ultimoCodigo.setValidado(true);
        codigoAutenticacaoRepository.save(ultimoCodigo);

        System.out.println("[AUTENTICACAO] Código validado com sucesso para cliente " + clienteId);
        // Aqui você pode acionar alguma lógica extra (ex: liberar pagamento pendente).
    }

    private String gerarCodigoNumerico(int tamanho) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tamanho; i++) {
            sb.append(random.nextInt(10)); // 0-9
        }
        return sb.toString();
    }

    // =========================================================
    // PAGAMENTO
    // =========================================================

    /**
     * Confirma o pagamento — aqui você pode, se quiser,
     * conferir se o último código de autenticação do cliente foi validado.
     */
    @Transactional
    public PagamentoResumoDTO confirmarPagamento(
            String contaId,
            String clienteId,
            String boleto,
            LocalDate dataVencimento,
            BigDecimal valorPrincipal,
            java.util.List<Long> taxaIds
    ) {
        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada: " + contaId));

        // (Opcional) validar que a conta pertence ao clienteId:
        // if (!conta.getCliente().getId().equals(clienteId)) { ... }

        Set<Taxa> taxas = (taxaIds == null || taxaIds.isEmpty())
                ? Set.of()
                : new HashSet<>(taxaRepository.findAllById(taxaIds));

        Pagamento pagamento = new Pagamento();
        pagamento.setConta(conta);
        pagamento.setBoleto(boleto);
        pagamento.setValorPago(valorPrincipal != null ? valorPrincipal : BigDecimal.ZERO);
        pagamento.setTaxas(taxas);
        pagamento.setDataPagamento(LocalDateTime.now());

        // Aqui você pode, se quiser, só permitir SUCESSO se o código de autenticação tiver sido validado
        pagamento.setStatus(StatusPagamento.SUCESSO);

        pagamentoDomainService.aplicarCalculoValorFinal(pagamento);

        Pagamento salvo = pagamentoRepository.save(pagamento);

        return PagamentoResumoDTO.fromEntity(salvo);
    }
}