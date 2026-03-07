package com.vereinsverwaltung.vereinsverwaltungback.repository;

import com.vereinsverwaltung.vereinsverwaltungback.entity.Verein;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VereinRepository extends JpaRepository<Verein, Long> {
    boolean existsByName(String name);
}
