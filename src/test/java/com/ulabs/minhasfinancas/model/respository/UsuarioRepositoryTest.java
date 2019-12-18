package com.ulabs.minhasfinancas.model.respository;


import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.ulabs.minhasfinancas.model.entity.Usuario;
import com.ulabs.minhasfinancas.model.repository.UsuarioRepository;

//@SpringBootTest carrega todo o contexto spring
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE) //para o @DataJpaTest nao sobrescrever a base configurada, senao cria instancia propria do datajpatest
public class UsuarioRepositoryTest {

	@Autowired
	UsuarioRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void deveVerificarAExistenciaDeUmEmail() {
		
		//cenario
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		//ação / execucao
		boolean result = repository.existsByEmail("usuario@email.com");
		
		//verificacao
		Assertions.assertThat(result).isTrue();
		
	}
	
	@Test
	public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComOEmail() {
		
		//cenario
		
		//acao / execucao
		boolean result = repository.existsByEmail("usuario@email.com");
				
		//verificacao
		Assertions.assertThat(result).isFalse();
	}
	
	
	@Test
	public void devePersistirUmUsuarioNaBaseDeDados() {
		
		//cenario
		Usuario usuario = criarUsuario();
		
		//acao
		Usuario usuarioSalvo = repository.save(usuario);
		
		//verificacao
		Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
		
	}
	
	@Test
	public void deveBuscarUmUsuarioPorEmail() {
		
		//cenario
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		//verificacao
		Optional<Usuario> result = repository.findByEmail("usuario@email.com");
		
		Assertions.assertThat(result.isPresent()).isTrue();
		
	}
	
	
	@Test
	public void deveRetornarVazioAoBuscarUsuarioPorEmailQuandoNaoExisteNaBase() {
				
		//verificacao
		Optional<Usuario> result = repository.findByEmail("usuario@email.com");
		
		Assertions.assertThat(result.isPresent()).isFalse();
		
	}
	
	public static Usuario criarUsuario() {
		return  Usuario.builder().nome("Usuario").email("usuario@email.com").senha("senha").build();
	}
	
}
