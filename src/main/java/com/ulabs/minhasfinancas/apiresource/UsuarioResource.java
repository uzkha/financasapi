package com.ulabs.minhasfinancas.apiresource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ulabs.minhasfinancas.api.dto.UsuarioDTO;
import com.ulabs.minhasfinancas.exception.RegraNegocioException;
import com.ulabs.minhasfinancas.model.entity.Usuario;
import com.ulabs.minhasfinancas.services.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioResource {
	
	private UsuarioService service;
	
	public UsuarioResource(UsuarioService service) {
		this.service = service;
	}
	
	
	//@requesbody transforma json para objeto, mesmo nome de campos
	@PostMapping
	public ResponseEntity salvar(@RequestBody UsuarioDTO dto ) {
		
		Usuario usuario = Usuario.builder().nome(dto.getNome()).email(dto.getEmail()).senha(dto.getSenha()).build();
		
		try {
			Usuario usuarioSalvo = service.salvarUsuario(usuario);
			
			return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
			
		}catch (RegraNegocioException e) {
			
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}
	

}
