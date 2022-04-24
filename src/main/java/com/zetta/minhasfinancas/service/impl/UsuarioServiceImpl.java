package com.zetta.minhasfinancas.service.impl;

import com.zetta.minhasfinancas.exception.ErroAutenticacao;
import com.zetta.minhasfinancas.exception.RegraNegocioException;
import com.zetta.minhasfinancas.model.entity.Usuario;
import com.zetta.minhasfinancas.model.repository.UsuarioRepository;
import com.zetta.minhasfinancas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private UsuarioRepository repository;

    public UsuarioServiceImpl(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public Usuario autenticar(String email, String senha) {
        Optional<Usuario> usuario = repository.findByEmail(email);

        if (!usuario.isPresent()) {
            throw new ErroAutenticacao("Usuário não encontrado.");
        }
        if (!usuario.get().getSenha().equals(senha)) {
            throw new ErroAutenticacao("Senha inválida.");
        }
        return usuario.get();
    }

    @Override
    @Transactional
    public Usuario salvarUsuario(Usuario usuario) {
        validarEmail(usuario.getEmail());
        return repository.save(usuario);
    }

    @Override
    public void validarEmail(String email) {
        boolean existe = repository.existsByEmail(email);
        if (existe) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com esse email");
        }
    }
}
