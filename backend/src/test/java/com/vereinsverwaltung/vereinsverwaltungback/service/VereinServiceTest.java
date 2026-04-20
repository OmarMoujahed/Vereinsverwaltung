package com.vereinsverwaltung.vereinsverwaltungback.service;

import com.vereinsverwaltung.vereinsverwaltungback.entity.Verein;
import com.vereinsverwaltung.vereinsverwaltungback.repository.VereinRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VereinServiceTest {

    @Mock
    private VereinRepository vereinRepository;

    @InjectMocks
    private VereinService vereinService;

    private Verein testVerein;

    @BeforeEach
    void setUp() {
        testVerein = new Verein();
        testVerein.setVerein_id(1L);
        testVerein.setName("SV Hatzenbühl");
        testVerein.setBeschreibung("Fußballverein");
        testVerein.setEmail("svh@gmail.com");
        testVerein.setTelefon("0475827520");
    }

    @Test
    void alleVereine_gibtAlleVereineZurueck() {
        when(vereinRepository.findAll()).thenReturn(List.of(testVerein));

        List<Verein> ergebnis = vereinService.alleVereine();

        assertThat(ergebnis).hasSize(1).contains(testVerein);
        verify(vereinRepository).findAll();
    }

    @Test
    void vereinFinden_vereinVorhanden_gibtOptionalZurueck() {
        when(vereinRepository.findById(1L)).thenReturn(Optional.of(testVerein));

        Optional<Verein> ergebnis = vereinService.vereinFinden(1L);

        assertThat(ergebnis).isPresent().contains(testVerein);
    }

    @Test
    void vereinFinden_nichtVorhanden_gibtLeeresOptionalZurueck() {
        when(vereinRepository.findById(99L)).thenReturn(Optional.empty());
        assertThat(vereinService.vereinFinden(99L)).isEmpty();
    }

    @Test
    void vereinAnlegen_neuerName_speichertVerein() {
        when(vereinRepository.existsByName("SV Hatzenbühl")).thenReturn(false);
        when(vereinRepository.save(testVerein)).thenReturn(testVerein);

        Verein ergebnis = vereinService.vereinAnlegen(testVerein);

        assertThat(ergebnis).isEqualTo(testVerein);
        verify(vereinRepository).save(testVerein);
    }

    @Test
    void vereinAnlegen_nameBereitsVorhanden_wirftException() {
        when(vereinRepository.existsByName("SV Hatzenbühl")).thenReturn(true);
        assertThatThrownBy(() -> vereinService.vereinAnlegen(testVerein))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Ein Verein mit diesem Namen existiert bereits!");
        verify(vereinRepository, never()).save(any());
    }

    @Test
    void vereinBearbeiten_gueltigeDaten_aktualisiertVerein() {
        Verein neueDaten = new Verein();
        neueDaten.setName("SV Hatzenbühl");
        neueDaten.setBeschreibung("Fußballverein aus der Pfalz");
        neueDaten.setEmail("svh@hotmail.com");
        neueDaten.setTelefon("07275 3253562");

        when(vereinRepository.findById(1L)).thenReturn(Optional.of(testVerein));
        when(vereinRepository.save(testVerein)).thenReturn(testVerein);

        Verein ergebnis = vereinService.vereinBearbeiten(1L, neueDaten);

        assertThat(ergebnis.getBeschreibung()).isEqualTo("Fußballverein aus der Pfalz");
        assertThat(ergebnis.getEmail()).isEqualTo("svh@hotmail.com");
        verify(vereinRepository).save(testVerein);
    }

    @Test
    void vereinBearbeiten_vereinNichtGefunden_wirftException() {
        when(vereinRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> vereinService.vereinBearbeiten(99L, testVerein))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Verein nicht gefunden!");
    }

    @Test
    void vereinBearbeiten_neuerNameBereitsVergeben_wirftException() {
        Verein neueDaten = new Verein();
        neueDaten.setName("FC Andere Verein");

        when(vereinRepository.findById(1L)).thenReturn(Optional.of(testVerein));
        when(vereinRepository.existsByName("FC Andere Verein")).thenReturn(true);

        assertThatThrownBy(() -> vereinService.vereinBearbeiten(1L, neueDaten))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Ein Verein mit diesem Namen existiert bereits!");
        verify(vereinRepository, never()).save(any());
    }

    @Test
    void vereinLoeschen_vereinVorhanden_loeschtErfolgreich() {
        when(vereinRepository.existsById(1L)).thenReturn(true);
        vereinService.vereinLoeschen(1L);
        verify(vereinRepository).deleteById(1L);
    }

    @Test
    void vereinLoeschen_nichtVorhanden_wirftException() {
        when(vereinRepository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> vereinService.vereinLoeschen(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Verein nicht gefunden!");
        verify(vereinRepository, never()).deleteById(any());
    }
}