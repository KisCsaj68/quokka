package com.codecool.quokka.persister.dal;

import com.codecool.quokka.model.position.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionDal extends JpaRepository<Position, Long> {
}
