package com.vereinsverwaltung.vereinsverwaltungback.repository;

import com.vereinsverwaltung.vereinsverwaltungback.entity.Rolle;
import com.vereinsverwaltung.vereinsverwaltungback.entity.Verein;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RolleRepository extends JpaRepository<Rolle, Long> {

    List<Rolle> findByVerein(Verein verein);

    List<Rolle> findByIstGlobal(boolean istGlobal);

    @Query("SELECT COUNT(r) > 0 FROM Rolle r WHERE r.name = :name AND r.verein = :verein")
    boolean existsByNameAndVerein(@Param("name") String name, @Param("verein") Verein verein);

    @Query("SELECT COUNT(r) > 0 FROM Rolle r WHERE r.name = :name AND r.istGlobal = :istGlobal")
    boolean existsByNameAndIstGlobal(@Param("name") String name, @Param("istGlobal") boolean istGlobal);
}