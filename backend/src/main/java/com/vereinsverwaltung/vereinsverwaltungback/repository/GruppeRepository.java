package com.vereinsverwaltung.vereinsverwaltungback.repository;

import com.vereinsverwaltung.vereinsverwaltungback.entity.Gruppe;
import com.vereinsverwaltung.vereinsverwaltungback.entity.Verein;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GruppeRepository extends JpaRepository<Gruppe, Long> {

    //Liste der Gruppen in Verein
    List<Gruppe> findByVerein(Verein verein);

    // existiert Gruppe in Verein
    boolean existsByNameAndVerein(String name, Verein verein);
}
