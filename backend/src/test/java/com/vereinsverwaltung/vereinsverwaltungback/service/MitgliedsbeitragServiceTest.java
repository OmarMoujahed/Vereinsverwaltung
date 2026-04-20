package com.vereinsverwaltung.vereinsverwaltungback.service;

import com.vereinsverwaltung.vereinsverwaltungback.domain.Geldbetrag;
import com.vereinsverwaltung.vereinsverwaltungback.entity.BeitragsStatus;
import com.vereinsverwaltung.vereinsverwaltungback.entity.Mitglied;
import com.vereinsverwaltung.vereinsverwaltungback.entity.Mitgliedsbeitrag;
import com.vereinsverwaltung.vereinsverwaltungback.repository.MitgliedRepository;
import com.vereinsverwaltung.vereinsverwaltungback.repository.MitgliedsbeitragRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MitgliedsbeitragServiceTest {

    @Mock
    private MitgliedsbeitragRepository beitragRepository;

    @Mock
    private MitgliedRepository mitgliedRepository;

    @InjectMocks
    private MitgliedsbeitragService beitragService;

    private Mitglied testMitglied;
    private Mitgliedsbeitrag offenerBeitrag;
    private Mitgliedsbeitrag ueberfaelligerBeitrag;

    @BeforeEach
    void setUp() {
        testMitglied = new Mitglied();
        testMitglied.setMitglied_id(1L);
        testMitglied.setVorname("Omar");
        testMitglied.setNachname("Moujahed");

        offenerBeitrag = new Mitgliedsbeitrag();
        offenerBeitrag.setId(1L);
        offenerBeitrag.setMitglied(testMitglied);
        offenerBeitrag.setBetrag(new Geldbetrag(50.0, "EUR"));
        offenerBeitrag.setStatus(BeitragsStatus.OFFEN);
        offenerBeitrag.setFaelligkeitsdatum(LocalDate.now().plusDays(30));

        ueberfaelligerBeitrag = new Mitgliedsbeitrag();
        ueberfaelligerBeitrag.setId(2L);
        ueberfaelligerBeitrag.setMitglied(testMitglied);
        ueberfaelligerBeitrag.setBetrag(new Geldbetrag(50.0, "EUR"));
        ueberfaelligerBeitrag.setStatus(BeitragsStatus.OFFEN);
        // Fälligkeitsdatum liegt 10 Tage in der Vergangenheit
        ueberfaelligerBeitrag.setFaelligkeitsdatum(LocalDate.now().minusDays(10));
    }

    @Test
    void beitragAnlegen_nochNichtFaellig_bleibtOffen() {
        when(mitgliedRepository.findById(1L)).thenReturn(Optional.of(testMitglied));
        when(beitragRepository.save(offenerBeitrag)).thenReturn(offenerBeitrag);

        Mitgliedsbeitrag ergebnis = beitragService.beitragAnlegen(offenerBeitrag);

        assertThat(ergebnis.getStatus()).isEqualTo(BeitragsStatus.OFFEN);
        verify(beitragRepository).save(offenerBeitrag);
    }

    @Test
    void beitragAnlegen_faelligkeitsdatumUeberschritten_setzteStatusUeberfaellig() {
        when(mitgliedRepository.findById(1L)).thenReturn(Optional.of(testMitglied));
        when(beitragRepository.save(ueberfaelligerBeitrag)).thenReturn(ueberfaelligerBeitrag);

        Mitgliedsbeitrag ergebnis = beitragService.beitragAnlegen(ueberfaelligerBeitrag);

        assertThat(ergebnis.getStatus()).isEqualTo(BeitragsStatus.UEBERFAELLIG);
    }

    @Test
    void beitragOhneMitglied_wirftException() {
        offenerBeitrag.setMitglied(null);
        assertThatThrownBy(() -> beitragService.beitragAnlegen(offenerBeitrag))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Beitrag muss einem Mitglied zugeordnet sein!");
        verify(beitragRepository, never()).save(any());
    }

    @Test
    void beitragAnlegen_mitgliedNichtGefunden_wirftException() {
        when(mitgliedRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> beitragService.beitragAnlegen(offenerBeitrag))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mitglied nicht gefunden!");
    }

    @Test
    void beitragAlsBezahltMarkieren_setzteStatusBezahlt() {
        when(beitragRepository.findById(1L)).thenReturn(Optional.of(offenerBeitrag));
        when(beitragRepository.save(offenerBeitrag)).thenReturn(offenerBeitrag);

        Mitgliedsbeitrag ergebnis = beitragService.beitragAlsBezahltMarkieren(1L);

        assertThat(ergebnis.getStatus()).isEqualTo(BeitragsStatus.BEZAHLT);
        verify(beitragRepository).save(offenerBeitrag);
    }

    @Test
    void beitragAlsBezahltMarkieren_nichtGefunden_wirftException() {
        when(beitragRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> beitragService.beitragAlsBezahltMarkieren(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Beitrag nicht gefunden!");
    }

    @Test
    void ueberfaelligeBeitraegeAktualisieren_aendertStatusKorrekt() {
        when(beitragRepository.findByStatus(BeitragsStatus.OFFEN))
                .thenReturn(List.of(ueberfaelligerBeitrag, offenerBeitrag));

        beitragService.ueberfaelligeBeitraegeAktualisieren();

        // nur überfällige soll geändert werden
        assertThat(ueberfaelligerBeitrag.getStatus()).isEqualTo(BeitragsStatus.UEBERFAELLIG);
        assertThat(offenerBeitrag.getStatus()).isEqualTo(BeitragsStatus.OFFEN);
        verify(beitragRepository).saveAll(anyList());
    }

    @Test
    void beitragLoeschen_vorhandenerBeitrag_loeschtErfolgreich() {
        when(beitragRepository.existsById(1L)).thenReturn(true);
        beitragService.beitragLoeschen(1L);
        verify(beitragRepository).deleteById(1L);
    }

    @Test
    void beitragLoeschen_nichtVorhanden_wirftException() {
        when(beitragRepository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> beitragService.beitragLoeschen(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Beitrag nicht gefunden!");
        verify(beitragRepository, never()).deleteById(any());
    }

    @Test
    void beitraegeNachStatus_gibtRichtigeBeitraegeZurueck() {
        when(beitragRepository.findByStatus(BeitragsStatus.OFFEN)).thenReturn(List.of(offenerBeitrag));

        List<Mitgliedsbeitrag> ergebnis = beitragService.beitraegeNachStatus(BeitragsStatus.OFFEN);

        assertThat(ergebnis).hasSize(1);
        assertThat(ergebnis.get(0).getStatus()).isEqualTo(BeitragsStatus.OFFEN);
    }
}