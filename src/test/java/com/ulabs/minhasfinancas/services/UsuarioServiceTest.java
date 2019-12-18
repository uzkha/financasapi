package com.ulabs.minhasfinancas.services;


import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.ulabs.minhasfinancas.exception.ErroAutenticacao;
import com.ulabs.minhasfinancas.exception.RegraNegocioException;
import com.ulabs.minhasfinancas.model.entity.Usuario;
import com.ulabs.minhasfinancas.model.repository.UsuarioRepository;
import com.ulabs.minhasfinancas.services.impl.UsuarioServiceImpl;


@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {
	
	
	//UsuarioService service;	
	@SpyBean
	UsuarioServiceImpl service;
	
	@MockBean
	UsuarioRepository repository;
	
	/*@Before //antes da execucao de cada teste
	public void setUp() {

		service = Mockito.spy(UsuarioServiceImpl.class);
		
		//repository = Mockito.mock(UsuarioRepository.class); // criado pela injecao de dependencia
		//service = new UsuarioServiceImpl(repository);
		
	}*/
	
	@Test(expected = Test.None.class)
	public void deveAutenticarUmUsuarioComSucesso() {
		
		//cenario
		String email = "email@email.com";
		String senha = "senha";
		
		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();		
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
		
		//acao
		Usuario result = service.autenticar(email, senha);
		
		//verificao
		Assertions.assertThat(result).isNotNull();
		
	}
	
	
	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {
		
		//cenario
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());		
		
		//acao
		Throwable exception = Assertions.catchThrowable( () ->  service.autenticar("email@email.com", "senha"));
		
		//verificacao
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Usuário não encontrado");
		
				
	}
	
	@Test
	public void deveLancarErroQuandoSenhaNaoBater() {
		
		//cenario
		String senha = "senha";
		Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();		
		
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
		
		//acao
		Throwable exception = Assertions.catchThrowable( () -> service.autenticar("email@email.com", "123"));
		
		//verificado
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha Inválida");
				
	}
	
	
	//Test.none nao espera lancamento de excecao
	@Test(expected = Test.None.class)
	public void deveValidarEmail() {
		
		//cenario		
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		
		//acao execucao
		service.validarEmail("email@email.com");
		
		//verificacao
	}
	
	
	@Test(expected = RegraNegocioException.class)
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		
		//cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		
		//acao execucao
		service.validarEmail("email@email.com");
		
		//verificacao
		//exception
	}
	
	
	@Test(expected = Test.None.class)
	public void deveSalvarUmUsuario() {
		
		//cenario
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		Usuario usuario = Usuario.builder().id(1l).nome("nome").email("email@email.com").senha("senha").build();
		
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		//acao
		Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
		
		
		//verificacao
		Assertions.assertThat(usuarioSalvo).isNotNull();
		Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1l);
		Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
		Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
		Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
		
		
	}
	
	@Test(expected = RegraNegocioException.class)
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		
		//cenario
		String email = "email@email.com";
		Usuario usuario = Usuario.builder().email(email).build();
		
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);
		
		//acao
		service.salvarUsuario(usuario);
		
		//verificacao
		Mockito.verify(repository, Mockito.never()).save(usuario);
		
	}
}
