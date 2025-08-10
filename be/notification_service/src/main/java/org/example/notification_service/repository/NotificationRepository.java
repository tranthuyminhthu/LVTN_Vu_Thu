package org.example.notification_service.repository;

import org.example.notification_service.entity.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    
    List<NotificationEntity> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
    
    List<NotificationEntity> findByUserIdAndStatusOrderByCreatedAtDesc(String userId, NotificationEntity.NotificationStatus status, Pageable pageable);
    
    List<NotificationEntity> findByTypeAndStatus(NotificationEntity.NotificationType type, NotificationEntity.NotificationStatus status);
    
    List<NotificationEntity> findByScheduledAtBeforeAndStatus(LocalDateTime scheduledAt, NotificationEntity.NotificationStatus status);
    
    @Query("SELECT n FROM NotificationEntity n WHERE n.userId = :userId AND n.status = :status ORDER BY n.createdAt DESC")
    List<NotificationEntity> findPendingNotificationsByUserId(@Param("userId") String userId, @Param("status") NotificationEntity.NotificationStatus status);
    
    @Query("SELECT COUNT(n) FROM NotificationEntity n WHERE n.userId = :userId AND n.status != :status")
    Long countUnreadNotificationsByUserId(@Param("userId") String userId, @Param("status") NotificationEntity.NotificationStatus status);
    
    List<NotificationEntity> findByStatusAndRetryCountLessThan(NotificationEntity.NotificationStatus status, Integer maxRetryCount);
    
    // PostgreSQL-specific queries for better performance
    @Query(value = """
        SELECT n.* FROM notifications n 
        WHERE n.user_id = :userId 
        AND n.created_at >= :since 
        ORDER BY n.created_at DESC
        """, nativeQuery = true)
    List<NotificationEntity> findRecentNotificationsByUserId(@Param("userId") String userId, @Param("since") LocalDateTime since);
    
    @Query(value = """
        SELECT 
            n.type,
            COUNT(*) as total,
            COUNT(CASE WHEN n.status = 'SENT' THEN 1 END) as sent,
            COUNT(CASE WHEN n.status = 'FAILED' THEN 1 END) as failed
        FROM notifications n 
        WHERE n.created_at >= :since
        GROUP BY n.type
        """, nativeQuery = true)
    List<Object[]> getNotificationStats(@Param("since") LocalDateTime since);
    
    @Query(value = """
        SELECT 
            DATE_TRUNC('day', n.created_at) as day,
            n.type,
            COUNT(*) as count
        FROM notifications n 
        WHERE n.created_at >= :since
        GROUP BY DATE_TRUNC('day', n.created_at), n.type
        ORDER BY day DESC, n.type
        """, nativeQuery = true)
    List<Object[]> getDailyNotificationStats(@Param("since") LocalDateTime since);
    
    // Find notifications that need retry
    @Query(value = """
        SELECT n.* FROM notifications n 
        WHERE n.status = 'FAILED' 
        AND n.retry_count < :maxRetries
        AND n.created_at >= :since
        ORDER BY n.created_at ASC
        """, nativeQuery = true)
    List<NotificationEntity> findNotificationsForRetry(@Param("maxRetries") Integer maxRetries, @Param("since") LocalDateTime since);
    
    // Clean up old notifications
    @Query(value = """
        DELETE FROM notifications 
        WHERE created_at < :cutoffDate 
        AND status IN ('SENT', 'READ')
        """, nativeQuery = true)
    int deleteOldNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);
} 