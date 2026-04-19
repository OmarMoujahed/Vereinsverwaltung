package com.vereinsverwaltung.vereinsverwaltungback.domain;

import com.vereinsverwaltung.vereinsverwaltungback.entity.BeitragsStatus;
import com.vereinsverwaltung.vereinsverwaltungback.entity.Mitglied;
import com.vereinsverwaltung.vereinsverwaltungback.entity.Mitgliedsbeitrag;
import com.vereinsverwaltung.vereinsverwaltungback.repository.MitgliedsbeitragRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BeitragsPruefService {

    private final MitgliedsbeitragRepository beitragRepository;

    public BeitragsPruefService(MitgliedsbeitragRepository beitragRepository) {
        this.beitragRepository = beitragRepository;
    }

    public boolean hatOffeneBeitraege(Mitglied mitglied) {
        List<Mitgliedsbeitrag> beitraege = beitragRepository.findByMitglied(mitglied);
        return beitraege.stream()
                .anyMatch(b -> b.getStatus() == BeitragsStatus.OFFEN
                        || b.getStatus() == BeitragsStatus.UEBERFAELLIG);
    }
}