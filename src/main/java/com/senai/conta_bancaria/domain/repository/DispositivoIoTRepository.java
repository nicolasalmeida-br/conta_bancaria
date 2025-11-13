package com.senai.conta_bancaria.domain.repository;

import com.senai.conta_bancaria.domain.entity.Cliente;
import com.senai.conta_bancaria.domain.entity.DispositivoIoT;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DispositivoIoTRepository extends JpaRepository<DispositivoIoT, Long> {
    Optional<DispositivoIoT> findByClienteAndAtivoTrue(Cliente cliente);
}