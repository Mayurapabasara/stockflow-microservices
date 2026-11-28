package com.stockflow.notification.repository;
import java.util.*;
import jakarta.persistence.LockModeType;
import com.stockflow.notification.entity.Notification;
import com.stockflow.notification.enums.NotificationStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Optional<Notification> findByEventKey(String eventKey);
    List<Notification> findTop20ByStatusOrderByCreatedAtAsc(NotificationStatus status);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select n from Notification n where n.id = :id")
    Optional<Notification> findForUpdate(@Param("id") UUID id);
}
