package com.ulabs.minhasfinancas.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ulabs.minhasfinancas.model.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
	
	//query method jpa monta a query
	Optional<Usuario> findByEmail(String email);

	boolean existsByEmail(String email);

}
