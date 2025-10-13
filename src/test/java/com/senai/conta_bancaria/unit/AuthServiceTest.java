package com.senai.conta_bancaria.unit;
import com.senai.conta_bancaria.application.dto.AuthDTO;
import com.senai.conta_bancaria.application.service.AuthService;
import com.senai.conta_bancaria.domain.entity.Gerente;
import com.senai.conta_bancaria.domain.entity.Usuario;
import com.senai.conta_bancaria.domain.enums.Role;
import com.senai.conta_bancaria.domain.repository.UsuarioRepository;
import com.senai.conta_bancaria.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarios;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtService jwt;

    @InjectMocks
    private AuthService service;

    @Test
    void deveGerarTokenQuandoCredenciaisValidas() {
        AuthDTO.LoginRequest req = new AuthDTO.LoginRequest("rafael@banco.com", "123");
        Usuario user = Gerente.builder()
                .email("rafael@senai.com")
                .senha("encoded")
                .role(Role.ADMIN)
                .build();

        when(usuarios.findByEmail("rafael@banco.com")).thenReturn(Optional.of(user));
        when(encoder.matches("123", "encoded")).thenReturn(true);
        when(jwt.generateToken("rafael@banco.com", "ADMIN")).thenReturn("token123");

        String token = service.login(req);

        assertEquals("token123", token);
    }

    @Test
    void deveLancarExcecaoQuandoSenhaIncorreta() {
        AuthDTO.LoginRequest req = new AuthDTO.LoginRequest("rafael@banco.com", "123");
        Usuario user = Gerente.builder()
                .email("rafael@banco.com")
                .senha("encoded")
                .role(Role.ADMIN)
                .build();

        when(usuarios.findByEmail("rafael@banco.com")).thenReturn(Optional.of(user));
        when(encoder.matches("123", "encoded")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> service.login(req));
    }
}