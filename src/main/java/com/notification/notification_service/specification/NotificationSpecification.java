package com.notification.notification_service.specification;

import com.notification.notification_service.model.Notification;
import org.springframework.data.jpa.domain.Specification;

public class NotificationSpecification {

    public static Specification<Notification> hasStatus(String status) {

        return (root, query, criteriaBuilder) -> {
                if (status == null) return criteriaBuilder.conjunction();
                return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    public static Specification<Notification> hasType(String type) {
        return (root, query, criteriaBuilder) -> {
            if (type == null) return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(root.get("type"), type);
        };
    }

    public static Specification<Notification> hasRecipient(String recipient) {
        return (root, query, criteriaBuilder) -> {
            if (recipient == null) return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(root.get("recipient"), recipient);
        };
    }
}
