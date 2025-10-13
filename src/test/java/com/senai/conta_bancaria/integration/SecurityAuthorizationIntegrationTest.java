package com.senai.conta_bancaria.integration;

import com.senai.conta_bancaria.application.service.GerenteService;
import com.senai.conta_bancaria.infrastructure.security.JwtService;
import com.senai.conta_bancaria.infrastructure.security.UsuarioDetailsService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityAuthorizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Mock
    private UsuarioDetailsService usuarioDetailsService;

    @Mock
    private GerenteService gerenteService;

    private void mockUsuario(String email, String role) {
        UserDetails user = User.withUsername(email)
                .password("encoded")
                .roles(role)
                .build();
        when(usuarioDetailsService.loadUserByUsername(email)).thenReturn(user);
    }

    @Test
    void devePermitirPostParaAdmin() throws Exception {
        String email = "admin@senai.com";
        String token = jwtService.generateToken(email, "ADMIN");
        mockUsuario(email, "ADMIN");

        String body = """
        {
            "nome": "Gerente Teste",
            "cpf": "111.222.333-44",
            "email": "gerente@banco.com",
            "senha": "123456"
        }
        """;

        mockMvc.perform(post("/gerentes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void deveNegarPostParaGerente() throws Exception {
        String email = "gerente@banco.com";
        String token = jwtService.generateToken(email, "GERENTE");
        mockUsuario(email, "GERENTE");

        mockMvc.perform(post("/gerentes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void devePermitirGetParaGerente() throws Exception {
        String email = "gerente@banco.com";
        String token = jwtService.generateToken(email, "GERENTE");
        mockUsuario(email, "GERENTE");

        mockMvc.perform(get("/gerentes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void devePermitirGetParaAdmin() throws Exception {
        String email = "admin@banco.com";
        String token = jwtService.generateToken(email, "ADMIN");
        mockUsuario(email, "ADMIN");

        mockMvc.perform(get("/gerentes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void deveNegarAcessoSemToken() throws Exception {
        mockMvc.perform(get("/gerentes"))
                .andExpect(status().isForbidden());
    }
}