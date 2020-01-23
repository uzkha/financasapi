package com.ulabs.minhasfinancas.apiresource;

import java.util.List;
import java.util.Optional;

import javax.persistence.Entity;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.BadRequest;

import com.ulabs.minhasfinancas.api.dto.AtualizaStatusDTO;
import com.ulabs.minhasfinancas.api.dto.LancamentoDTO;
import com.ulabs.minhasfinancas.exception.RegraNegocioException;
import com.ulabs.minhasfinancas.model.entity.Lancamento;
import com.ulabs.minhasfinancas.model.entity.Usuario;
import com.ulabs.minhasfinancas.model.enums.StatusLancamento;
import com.ulabs.minhasfinancas.model.enums.TipoLancamento;
import com.ulabs.minhasfinancas.services.LancamentoService;
import com.ulabs.minhasfinancas.services.UsuarioService;

import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoResource {
	
	private final LancamentoService service;
	private final UsuarioService usuarioService;

	/*public LancamentoResource(LancamentoService service, UsuarioService usuarioService) {
		super();
		this.service = service;
		this.usuarioService = usuarioService;
	}*/
	
	@PostMapping
	public ResponseEntity salvar(@RequestBody LancamentoDTO dto) {
		
		try {
			Lancamento entidade = converter(dto);
			
			entidade = service.salvar(entidade);
			
			return new ResponseEntity(entidade, HttpStatus.CREATED);
			
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

		
	}
	
	//post gera novo, put atualiza
	@PutMapping("{id}")
	private ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
		
		return service.obterPorId(id).map(entity -> {
			try {				
				Lancamento lancamento = converter(dto);
				lancamento.setId(entity.getId());
				service.atualizar(lancamento);
				return ResponseEntity.ok(lancamento);
			}catch (Exception e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet( () -> new ResponseEntity("Lancamento não encontrado na base de dados", HttpStatus.BAD_REQUEST) );
	}
	
	
	@DeleteMapping("{id}")
	private ResponseEntity deletar(@PathVariable("id") Long id) {
		try {
			
			return service.obterPorId(id).map( entidade -> {
				service.deletar(entidade);
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}).orElseGet( () -> new ResponseEntity("Lancamento não encontrado na base de dados", HttpStatus.BAD_REQUEST) );
			
		}catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping
	public ResponseEntity buscar(@RequestParam(value = "descricao", required = false) String descricao,
								 @RequestParam(value = "mes", required = false) Integer mes, 
								 @RequestParam(value = "ano", required = false) Integer ano,
								 @RequestParam("usuario") Long idUsuario) {
		
		Lancamento lancamentoFiltro = new Lancamento();
		lancamentoFiltro.setAno(ano);
		lancamentoFiltro.setDescricao(descricao);
		lancamentoFiltro.setMes(mes);
		
		Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
		
		if(!usuario.isPresent()) {
			return ResponseEntity.badRequest().body("Não foi possivel realizar a consulta. Usuário não encontrado.");
		}else {
			lancamentoFiltro.setUsuario(usuario.get());
		}
		
		List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
		
		return ResponseEntity.ok(lancamentos);
		
	}
	
	@PutMapping("{id}/atualiza-status")
	public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO dto) {
		
		return service.obterPorId(id).map(entity -> {
			try {				
				
				StatusLancamento statusSelecionado =  StatusLancamento.valueOf(dto.getStatus());
				
				if(statusSelecionado == null) {
					return ResponseEntity.badRequest().body("Não foi possível atualizar o status do lancamento. Envie um status válido");
				}else {

					entity.setStatus(statusSelecionado);
					service.atualizar(entity);
					return ResponseEntity.ok(entity);
				}
				
			
			}catch (Exception e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet( () -> new ResponseEntity("Lancamento não encontrado na base de dados", HttpStatus.BAD_REQUEST) );
		
	}
	
	private Lancamento converter(LancamentoDTO dto) {
		
		Lancamento lancamento = new Lancamento();
		
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setAno(dto.getAno());
		lancamento.setMes(dto.getMes());
		lancamento.setId(dto.getId());
		lancamento.setValor(dto.getValor());
		
		Usuario usuario = usuarioService.obterPorId(dto.getUsuario()).orElseThrow(() -> new RegraNegocioException("Usuario não encontrado")) ;
		
		lancamento.setUsuario(usuario);
		
		if (dto.getTipo() != null) {
			lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
		}		
		
		if (dto.getStatus() != null) {
			lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
		}
		
		return lancamento;
	}
	
	

}
