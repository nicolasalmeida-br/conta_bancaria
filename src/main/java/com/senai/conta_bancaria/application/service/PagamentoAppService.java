package com.senai.conta_bancaria.application.service;

import com.senai.conta_bancaria.domain.entity.Cliente;
import com.senai.conta_bancaria.domain.entity.Conta;
import com.senai.conta_bancaria.domain.entity.Pagamento;
import com.senai.conta_bancaria.domain.entity.CodigoAutenticacao;

import com.senai.conta_bancaria.domain.enums.StatusPagamento;
import com.senai.conta_bancaria.domain.exceptions.SaldoInsuficienteException;

import com.senai.conta_bancaria.domain.repository.PagamentoRepository;
import com.senai.conta_bancaria.domain.repository.TaxaRepository;
import com.senai.conta_bancaria.domain.repository.CodigoAutenticacaoRepository;
import com.senai.conta_bancaria.domain.repository.DispositivoIoTRepository;

import com.senai.conta_bancaria.domain.service.PagamentoDomainService;
import com.senai.conta_bancaria.infrastructure.mqtt.MqttGateway;
import com.senai.conta_bancaria.domain.exceptions.AutenticacaoIoTExpiradaException;
import com.senai.conta_bancaria.domain.exceptions.PagamentoInvalidoException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
public class PagamentoAppService {

    private final PagamentoRepository pagamentoRepository;
    private final TaxaRepository taxaRepository;
    private final CodigoAutenticacaoRepository codigoRepo;
    private final DispositivoIoTRepository dispositivoRepo;
    private final PagamentoDomainService domainService;
    private final MqttGateway mqtt;
    private final ContaService contaService;
    private final ClienteService clienteService;

    public PagamentoAppService(PagamentoRepository pagamentoRepository,
                               TaxaRepository taxaRepository,
                               CodigoAutenticacaoRepository codigoRepo,
                               DispositivoIoTRepository dispositivoRepo,
                               PagamentoDomainService domainService,
                               MqttGateway mqtt,
                               ContaService contaService,
                               ClienteService clienteService) {
        this.pagamentoRepository = pagamentoRepository;
        this.taxaRepository = taxaRepository;
        this.codigoRepo = codigoRepo;
        this.dispositivoRepo = dispositivoRepo;
        this.domainService = domainService;
        this.mqtt = mqtt;
        this.contaService = contaService;
        this.clienteService = clienteService;
    }

    /** Dispara solicitação de autenticação via MQTT e registra o código pendente. */
    @Transactional
    public CodigoAutenticacao iniciarAutenticacao(String clienteId) {
        Cliente cliente = clienteService.buscarPorId(clienteId);

        // Garante que existe dispositivo IoT ativo para esse cliente
        dispositivoRepo.findByClienteAndAtivoTrue(cliente)
                .orElseThrow(PagamentoInvalidoException::new);

        // Gera código simples de 6 caracteres
        String codigo = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        CodigoAutenticacao auth = new CodigoAutenticacao();
        auth.setCliente(cliente);
        auth.setCodigo(codigo);
        auth.setValidado(false);
        auth.setExpiraEm(LocalDateTime.now().plusMinutes(2));
        codigoRepo.save(auth);

        // Envia o código via MQTT
        String idClienteTopico = String.valueOf(cliente.getId());
        mqtt.enviarCodigoAutenticacao(idClienteTopico, codigo);

        return auth;
    }

    /** Validação do código (pelo listener MQTT ou endpoint). */
    @Transactional
    public void validarCodigo(String clienteId, String codigo) {
        Cliente cliente = clienteService.buscarPorId(clienteId);

        CodigoAutenticacao ultimo = codigoRepo.findTopByClienteOrderByIdDesc(cliente)
                .orElseThrow(AutenticacaoIoTExpiradaException::new);

        if (LocalDateTime.now().isAfter(ultimo.getExpiraEm())) {
            throw new AutenticacaoIoTExpiradaException();
        }

        if (!ultimo.getCodigo().equals(codigo)) {
            throw new PagamentoInvalidoException();
        }

        ultimo.setValidado(true);
        codigoRepo.save(ultimo);

        // Opcional: notifica o dispositivo IoT que a validação deu certo
        String idClienteTopico = String.valueOf(cliente.getId());
        mqtt.enviarConfirmacaoValidacao(idClienteTopico, codigo);
    }

    /** Confirma e processa o pagamento após autenticação IoT válida. */
    @Transactional
    public Pagamento confirmarPagamento(String contaId,
                                        String clienteId,
                                        String boleto,
                                        LocalDate dataVencimento,
                                        BigDecimal valorPrincipal,
                                        List<Long> taxaIds) {

        Cliente cliente = clienteService.buscarPorId(clienteId);
        Conta conta = contaService.buscarPorId(contaId);

        // Autenticação IoT deve estar válida e dentro do prazo
        CodigoAutenticacao ultimo = codigoRepo.findTopByClienteOrderByIdDesc(cliente)
                .orElseThrow(AutenticacaoIoTExpiradaException::new);

        if (!ultimo.isValidado() || LocalDateTime.now().isAfter(ultimo.getExpiraEm())) {
            throw new AutenticacaoIoTExpiradaException();
        }

        // Validação de boleto (não permite pagar boleto vencido)
        if (dataVencimento != null && dataVencimento.isBefore(LocalDate.now())) {
            throw new PagamentoInvalidoException();
        }

        // Monta entidade Pagamento
        Pagamento p = new Pagamento();
        p.setConta(conta);
        p.setBoleto(boleto);
        p.setValorPago(valorPrincipal);
        p.setDataPagamento(LocalDateTime.now());
        p.setTaxas(new HashSet<>(taxaRepository.findAllById(taxaIds)));

        // Calcula valor final (valor + taxas)
        BigDecimal valorFinal = domainService.calcularValorFinal(p);

        // Tenta sacar — se não houver saldo, salva como SALDO_INSUFICIENTE
        try {
            conta.sacar(valorFinal);
        } catch (SaldoInsuficienteException e) {
            p.setStatus(StatusPagamento.SALDO_INSUFICIENTE);
            pagamentoRepository.save(p);
            throw e;
        }

        p.setStatus(StatusPagamento.SUCESSO);
        return pagamentoRepository.save(p);
    }
}