package com.vereinsverwaltung.vereinsverwaltungback.repository;

import com.vereinsverwaltung.vereinsverwaltungback.entity.BeitragsStatus;
import com.vereinsverwaltung.vereinsverwaltungback.entity.Mitglied;
import com.vereinsverwaltung.vereinsverwaltungback.entity.Mitgliedsbeitrag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MitgliedsbeitragRepository extends JpaRepository <Mitgliedsbeitrag, Long> {
    //Alle Beiträge eines Mitglieds
    List<Mitgliedsbeitrag> findByMitglied(Mitglied mitglied);

    //Beiträge nach Status filtern
    List<Mitgliedsbeitrag> findByStatus(BeitragsStatus status);
}

