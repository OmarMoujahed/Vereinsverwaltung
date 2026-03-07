package com.vereinsverwaltung.vereinsverwaltungback.repository;

import com.vereinsverwaltung.vereinsverwaltungback.entity.Mitglied;
import com.vereinsverwaltung.vereinsverwaltungback.entity.Verein;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MitgliedRepository extends JpaRepository<Mitglied, Long> {
    //Mitglieder eines Vereins finden
    List<Mitglied> findByVerein(Verein verein);
}

