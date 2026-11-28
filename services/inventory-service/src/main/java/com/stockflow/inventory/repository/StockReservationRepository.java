package com.stockflow.inventory.repository;

import java.util.Optional;
import java.util.UUID;

import com.stockflow.inventory.entity.StockReservation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockReservationRepository extends JpaRepository<StockReservation, UUID> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select reservation from StockReservation reservation where reservation.id = :id")
	Optional<StockReservation> findByIdForUpdate(@Param("id") UUID id);
}
