package com.vereinsverwaltung.vereinsverwaltungback.repository;

import com.vereinsverwaltung.vereinsverwaltungback.entity.Gruppe;
import com.vereinsverwaltung.vereinsverwaltungback.entity.Verein;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GruppeRepository extends JpaRepository<Gruppe, Long> {

    List<Gruppe> findByVerein(Verein verein);

    @Query("SELECT COUNT(g) > 0 FROM Gruppe g WHERE g.name = :name AND g.verein = :verein")
    boolean existsByNameAndVerein(@Param("name") String name, @Param("verein") Verein verein);
}