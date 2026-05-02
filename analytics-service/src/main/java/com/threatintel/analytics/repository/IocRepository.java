package com.threatintel.analytics.repository;

import com.threatintel.analytics.entity.IocEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IocRepository extends JpaRepository<IocEntity, Long> {

    List<IocEntity> findByRiskLevelAndTypeAndSource(String riskLevel, String type, String source);

    @Query("SELECT i FROM IocEntity i WHERE i.severityScore >= 50")
    List<IocEntity> findHighRisk();

    List<IocEntity> findBySource(String source);
}

