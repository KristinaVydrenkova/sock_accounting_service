package ru.vydrenkova.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.vydrenkova.models.Sock;

import java.util.List;
import java.util.Optional;

public interface SockRepository extends JpaRepository<Sock, Long>, JpaSpecificationExecutor<Sock> {
    Optional<Sock> findByColorAndCottonPercentage(String color, Integer colorPercentage);

    List<Sock> findAllByCottonPercentageBetween(Integer from, Integer to);

}
