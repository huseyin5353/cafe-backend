package com.restaurantbackend.restaurantbackend.service.notification;

import com.restaurantbackend.restaurantbackend.dto.notification.CreateNotificationDTO;
import com.restaurantbackend.restaurantbackend.dto.notification.NotificationDTO;
import com.restaurantbackend.restaurantbackend.entity.notification.Notification;
import com.restaurantbackend.restaurantbackend.entity.notification.enums.NotificationStatus;
import com.restaurantbackend.restaurantbackend.entity.notification.enums.NotificationType;
import com.restaurantbackend.restaurantbackend.mapper.notification.NotificationMapper;
import com.restaurantbackend.restaurantbackend.repository.notification.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public NotificationService(NotificationRepository notificationRepository, NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
    }

    /**
     * Yeni bildirim oluştur
     */
    public NotificationDTO createNotification(CreateNotificationDTO createDTO) {
        Notification notification = new Notification();
        notification.setType(createDTO.getType());
        notification.setTitle(createDTO.getTitle());
        notification.setMessage(createDTO.getMessage());
        notification.setTableNumber(createDTO.getTableNumber());
        notification.setOrderId(createDTO.getOrderId());
        notification.setSessionId(createDTO.getSessionId());
        notification.setTargetRole(createDTO.getTargetRole() != null ? createDTO.getTargetRole() : "ALL");
        notification.setPriority(createDTO.getPriority() != null ? createDTO.getPriority() : 1);
        notification.setStatus(NotificationStatus.UNREAD);
        notification.setCreatedAt(LocalDateTime.now());

        Notification savedNotification = notificationRepository.save(notification);
        return notificationMapper.toDTO(savedNotification);
    }

    /**
     * Otomatik sipariş bildirimi oluştur
     */
    public NotificationDTO createOrderNotification(String tableNumber, Long orderId, Long sessionId) {
        CreateNotificationDTO createDTO = new CreateNotificationDTO();
        createDTO.setType(NotificationType.NEW_ORDER);
        createDTO.setTitle("Yeni Sipariş");
        createDTO.setMessage("Masa " + tableNumber + " yeni sipariş verdi");
        createDTO.setTableNumber(tableNumber);
        createDTO.setOrderId(orderId);
        createDTO.setSessionId(sessionId);
        createDTO.setTargetRole("KITCHEN");
        createDTO.setPriority(2); // Yüksek öncelik

        return createNotification(createDTO);
    }

    /**
     * Sipariş hazır bildirimi oluştur
     */
    public NotificationDTO createOrderReadyNotification(String tableNumber, Long orderId) {
        CreateNotificationDTO createDTO = new CreateNotificationDTO();
        createDTO.setType(NotificationType.ORDER_READY);
        createDTO.setTitle("Sipariş Hazır");
        createDTO.setMessage("Masa " + tableNumber + " için sipariş hazır, servis edilsin");
        createDTO.setTableNumber(tableNumber);
        createDTO.setOrderId(orderId);
        createDTO.setTargetRole("WAITER");
        createDTO.setPriority(3); // Acil öncelik

        return createNotification(createDTO);
    }

    /**
     * Temizlik isteği bildirimi oluştur
     */
    public NotificationDTO createCleaningRequestNotification(String tableNumber) {
        CreateNotificationDTO createDTO = new CreateNotificationDTO();
        createDTO.setType(NotificationType.CLEANING_REQUEST);
        createDTO.setTitle("Temizlik İsteği");
        createDTO.setMessage("Masa " + tableNumber + " temizlik istiyor");
        createDTO.setTableNumber(tableNumber);
        createDTO.setTargetRole("WAITER");
        createDTO.setPriority(1); // Normal öncelik

        return createNotification(createDTO);
    }

    /**
     * Tüm bildirimleri getir
     */
    @Transactional(readOnly = true)
    public List<NotificationDTO> getAllNotifications() {
        return notificationRepository.findAll()
                .stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Role göre bildirimleri getir
     */
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByRole(String role) {
        return notificationRepository.findByTargetRoleOrderByCreatedAtDesc(role)
                .stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Okunmamış bildirimleri getir
     */
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotifications() {
        return notificationRepository.findByStatusOrderByPriorityDescCreatedAtDesc(NotificationStatus.UNREAD)
                .stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Role göre okunmamış bildirimleri getir
     */
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotificationsByRole(String role) {
        return notificationRepository.findByTargetRoleOrderByCreatedAtDesc(role)
                .stream()
                .filter(n -> n.getStatus() == NotificationStatus.UNREAD)
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Bildirimi okundu olarak işaretle
     */
    public NotificationDTO markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Bildirim bulunamadı"));

        notification.setStatus(NotificationStatus.READ);
        notification.setReadAt(LocalDateTime.now());

        Notification savedNotification = notificationRepository.save(notification);
        return notificationMapper.toDTO(savedNotification);
    }

    /**
     * Tüm bildirimleri okundu olarak işaretle
     */
    public void markAllAsRead() {
        List<Notification> unreadNotifications = notificationRepository.findByStatusOrderByPriorityDescCreatedAtDesc(NotificationStatus.UNREAD);
        LocalDateTime now = LocalDateTime.now();

        unreadNotifications.forEach(notification -> {
            notification.setStatus(NotificationStatus.READ);
            notification.setReadAt(now);
        });

        notificationRepository.saveAll(unreadNotifications);
    }

    /**
     * Bildirimi arşivle
     */
    public NotificationDTO archiveNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Bildirim bulunamadı"));

        notification.setStatus(NotificationStatus.ARCHIVED);

        Notification savedNotification = notificationRepository.save(notification);
        return notificationMapper.toDTO(savedNotification);
    }

    /**
     * Okunmamış bildirim sayısını getir
     */
    @Transactional(readOnly = true)
    public Long getUnreadCount() {
        return notificationRepository.countUnreadNotifications();
    }

    /**
     * Role göre okunmamış bildirim sayısını getir
     */
    @Transactional(readOnly = true)
    public Long getUnreadCountByRole(String role) {
        return notificationRepository.countUnreadNotificationsByRole(role);
    }

    /**
     * Son N bildirimi getir
     */
    @Transactional(readOnly = true)
    public List<NotificationDTO> getRecentNotifications(int limit) {
        return notificationRepository.findAll()
                .stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(limit)
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Bildirimi sil
     */
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}
