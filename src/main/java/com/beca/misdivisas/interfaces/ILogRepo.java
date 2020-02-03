package com.beca.misdivisas.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beca.misdivisas.jpa.Log;

public interface ILogRepo extends JpaRepository<Log, Integer> {

}
