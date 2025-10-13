package com.senai.conta_bancaria.unit;

import com.senai.conta_bancaria.domain.entity.Gerente;
import com.senai.conta_bancaria.domain.entity.Usuario;
import com.senai.conta_bancaria.domain.enums.Role;
import com.senai.conta_bancaria.domain.repository.UsuarioRepository;
import com.senai.conta_bancaria.infrastructure.security.UsuarioDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioDetailsServiceTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UsuarioDetailsService service;

    @Test
    void deveRetornarUserDetailsQuandoUsuarioEncontrado() {
        Usuario user = Gerente.builder()
                .email("rafael@banco.com")
                .senha("123")
                .role(Role.ADMIN)
                .build();

        when(repository.findByEmail("rafael@banco.com")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("rafael@banco.com");

        assertEquals("rafael@banco.com", details.getUsername());
        assertTrue(details.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(repository.findByEmail("x@banco.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("x@banco.com"));
    }
}