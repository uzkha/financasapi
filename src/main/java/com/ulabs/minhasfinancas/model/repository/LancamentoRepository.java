package com.ulabs.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ulabs.minhasfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>{

}
