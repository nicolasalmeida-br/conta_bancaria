package com.senai.conta_bancaria.application.service;

import com.senai.conta_bancaria.application.dto.AuthDTO;
import com.senai.conta_bancaria.domain.entity.Usuario;
import com.senai.conta_bancaria.domain.exceptions.UsuarioNaoEncontradoException;
import com.senai.conta_bancaria.domain.repository.UsuarioRepository;
import com.senai.conta_bancaria.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável pela autenticação de usuários no sistema bancário.
 * Realiza validação de credenciais e emissão de token JWT.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    /**
     * Autentica um usuário com e-mail e senha.
     * Em caso de sucesso, retorna um token JWT para acesso às rotas protegidas.
     *
     * @param req DTO contendo e-mail e senha do usuário.
     * @return Token JWT gerado para o usuário autenticado.
     * @throws UsuarioNaoEncontradoException se o e-mail não estiver cadastrado.
     * @throws BadCredentialsException se a senha não corresponder ao usuário.
     */
    public String login(AuthDTO.LoginRequest req) {
        // Busca o usuário pelo e-mail no repositório
        Usuario usuario = usuarios.findByEmail(req.email())
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));

        // Valida a senha usando o PasswordEncoder
        if (!encoder.matches(req.senha(), usuario.getSenha())) {
            throw new BadCredentialsException("Credenciais inválidas");
        }

        // Gera e retorna o token JWT contendo e-mail e role do usuário
        return jwt.generateToken(usuario.getEmail(), usuario.getRole().name());
    }
}