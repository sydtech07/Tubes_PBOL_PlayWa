package com.playwa.repository;

import com.playwa.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByName(String name);
    Optional<Player> findByUsername(String username);
}
