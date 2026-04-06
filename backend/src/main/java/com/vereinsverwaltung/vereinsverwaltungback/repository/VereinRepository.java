package com.vereinsverwaltung.vereinsverwaltungback.repository;

import com.vereinsverwaltung.vereinsverwaltungback.entity.Verein;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VereinRepository extends JpaRepository<Verein, Long> {
    @Query("SELECT COUNT(v) > 0 FROM Verein v WHERE v.name = :name")
    boolean existsByName(@Param("name") String name);
}
