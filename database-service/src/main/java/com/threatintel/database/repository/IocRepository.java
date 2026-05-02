package com.threatintel.database.repository;

import com.threatintel.database.entity.IocEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IocRepository extends JpaRepository<IocEntity, Long> {
}

