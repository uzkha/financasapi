package com.ulabs.minhasfinancas.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ulabs.minhasfinancas.exception.ErroAutenticacao;
import com.ulabs.minhasfinancas.exception.RegraNegocioException;
import com.ulabs.minhasfinancas.model.entity.Usuario;
import com.ulabs.minhasfinancas.model.repository.UsuarioRepository;
import com.ulabs.minhasfinancas.services.UsuarioService;

@Service   //injeacao de dependencia - class vira um bean
public class UsuarioServiceImpl implements UsuarioService{
	

	private UsuarioRepository repository;
	
	@Autowired
	public UsuarioServiceImpl(UsuarioRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		
		Optional<Usuario> usuario = repository.findByEmail(email);
		
		if(!usuario.isPresent()) {
			throw new ErroAutenticacao("Usuário não encontrado");
		}
		
		if(!usuario.get().getSenha().equals(senha)) {
			throw new ErroAutenticacao("Senha Inválida");
		}
		
		return usuario.get();
	}

	@Override
	@Transactional //abre uma transacao na base de dados
	public Usuario salvarUsuario(Usuario usuario) {		
		validarEmail(usuario.getEmail());		
		return repository.save(usuario);		
	}

	@Override
	public void validarEmail(String email) {
		
		boolean existe = repository.existsByEmail(email);
		
		if(existe) {
			throw new RegraNegocioException("Já existe um usuário cadastrado com este email.");
		}
		
	}

	@Override
	public Optional<Usuario> obterPorId(Long id) {
		return repository.findById(id);
	}
	
	

}
