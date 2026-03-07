package com.vereinsverwaltung.vereinsverwaltungback.repository;

import com.vereinsverwaltung.vereinsverwaltungback.entity.Rolle;
import com.vereinsverwaltung.vereinsverwaltungback.entity.Verein;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RolleRepository extends JpaRepository<Rolle, Long> {
    //Alle Rollen eines Vereins
    List<Rolle> findByVerein(Verein verein);

    //Alle globalen Rollen
    List<Rolle> findByIstGlobal(boolean istGlobal);

    //existiert Name + Verein-Kombination
    boolean existsByNameAndVerein(String name, Verein verein);

    //existiert globale Rolle mit diesem Namen
    boolean existsByNameAndIstGlobal(String name, boolean istGlobal);
}