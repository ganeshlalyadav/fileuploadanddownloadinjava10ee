package org.test.jakarta.hello.resource;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.test.jakarta.hello.model.OrderEntity;

@Stateless
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @PersistenceContext
    private EntityManager em;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void processOrder(OrderEntity order) {
        logger.info("Processing order for product: {}", order.getProduct());

        em.persist(order);

        order.setStatus("Processing");

        try {
            logUserActivity(order);
        } catch (Exception e) {
            logger.warn("User activity logging failed for order {}: {}", order.getId(), e.getMessage());
        }

        try {
            sendConfirmationEmail(order);
        } catch (Exception e) {
            logger.warn("Email sending failed for order {}: {}", order.getId(), e.getMessage());
        }

        order.setStatus("Completed");
        logger.info("Order {} processed successfully with status: {}", order.getId(), order.getStatus());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void logUserActivity(OrderEntity order) {
        logger.info("Logging user activity for order: {}", order.getId());

        throw new RuntimeException("Simulated failure while logging activity for order: " + order.getId());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void sendConfirmationEmail(OrderEntity order) {
        logger.info("Sending confirmation email for order: {}", order.getId());

        if ("FailProduct".equals(order.getProduct())) {
            throw new RuntimeException("Simulated email sending failure for product: " + order.getProduct());
        }

        logger.info("Email sent successfully for order: {}", order.getId());
    }
}
