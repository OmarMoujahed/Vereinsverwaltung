package com.vereinsverwaltung.vereinsverwaltungback.repository;

import com.vereinsverwaltung.vereinsverwaltungback.entity.Gruppe;
import com.vereinsverwaltung.vereinsverwaltungback.entity.Veranstaltung;
import com.vereinsverwaltung.vereinsverwaltungback.entity.Verein;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VeranstaltungRepository extends JpaRepository<Veranstaltung, Long> {
    //Alle Veranstaltungen eines Vereins
    List<Veranstaltung> findByVerein(Verein verein);

    //Alle Veranstaltungen einer Gruppe
    List<Veranstaltung> findByGruppe(Gruppe gruppe);
}
