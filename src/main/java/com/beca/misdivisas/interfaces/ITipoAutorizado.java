package com.beca.misdivisas.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beca.misdivisas.jpa.TipoAutorizado;

public interface ITipoAutorizado extends JpaRepository<TipoAutorizado, Integer>{

}
