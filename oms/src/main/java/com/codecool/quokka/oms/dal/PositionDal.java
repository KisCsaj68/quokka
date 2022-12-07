package com.codecool.quokka.oms.dal;

import com.codecool.quokka.model.position.Position;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PositionDal extends JpaRepository<Position, UUID> {
    List<Position> findAllByExitOrderIdIsNull();
}
