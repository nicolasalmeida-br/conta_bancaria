package com.senai.conta_bancaria.interface_ui.controller;

import com.senai.conta_bancaria.application.dto.TaxaDTO;
import com.senai.conta_bancaria.domain.entity.Taxa;
import com.senai.conta_bancaria.domain.repository.TaxaRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/taxas")
@SecurityRequirement(name = "bearerAuth")
public class TaxaController {

    private final TaxaRepository repo;

    public TaxaController(TaxaRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('GERENTE','ADMIN')")
    public List<Taxa> listar() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GERENTE','ADMIN')")
    public ResponseEntity<Taxa> buscar(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('GERENTE','ADMIN')")
    public ResponseEntity<Taxa> criar(@Valid @RequestBody TaxaDTO dto) {
        Taxa taxa = new Taxa();
        taxa.setDescricao(dto.descricao());
        taxa.setPercentual(dto.percentual());
        taxa.setValorFixo(dto.valorFixo());

        Taxa salvo = repo.save(taxa);
        return ResponseEntity.created(URI.create("/taxas/" + salvo.getId())).body(salvo);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('GERENTE','ADMIN')")
    public ResponseEntity<Taxa> atualizar(@PathVariable Long id, @Valid @RequestBody TaxaDTO dto) {
        return repo.findById(id)
                .map(existente -> {
                    existente.setDescricao(dto.descricao());
                    existente.setPercentual(dto.percentual());
                    existente.setValorFixo(dto.valorFixo());
                    Taxa atualizado = repo.save(existente);
                    return ResponseEntity.ok(atualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('GERENTE','ADMIN')")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}