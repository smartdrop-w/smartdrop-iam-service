package com.smart.drop.shared.infrastructure.outbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio relay para el Patrón Transactional Outbox.
 * Registra eventos en la tabla outbox de manera transaccional y procesa su publicación asíncrona hacia el broker.
 */
@Service
public class OutboxPublisherService {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisherService.class);

    private final OutboxMessageRepository outboxRepository;

    public OutboxPublisherService(OutboxMessageRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

    /**
     * Guarda un evento en la tabla Outbox dentro de la misma transacción local del negocio.
     */
    @Transactional
    public OutboxMessage stageEvent(String aggregateType, String aggregateId, String eventType, String payload) {
        OutboxMessage message = new OutboxMessage(aggregateType, aggregateId, eventType, payload);
        OutboxMessage saved = outboxRepository.save(message);
        log.info("[Outbox STAGED] Evento '{}' para agregado '{}#{}' guardado en Outbox con ID {}",
                eventType, aggregateType, aggregateId, saved.getId());
        return saved;
    }

    /**
     * Procesa los eventos pendientes simulando la entrega confiable al Message Broker (RabbitMQ / SQS).
     * En producción se ejecuta mediante un @Scheduled cron o un Change Data Capture (CDC / Debezium).
     */
    @Transactional
    public int dispatchPendingEvents() {
        List<OutboxMessage> pending = outboxRepository.findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
        for (OutboxMessage msg : pending) {
            try {
                // Simulación de publicación al Broker
                log.info("[Outbox DISPATCH] Publicando evento id={} al broker: {}", msg.getId(), msg.getEventType());
                msg.setStatus(OutboxStatus.PUBLISHED);
                msg.setProcessedAt(LocalDateTime.now());
                outboxRepository.save(msg);
            } catch (Exception e) {
                log.error("[Outbox ERROR] Falló publicación de mensaje id={}: {}", msg.getId(), e.getMessage());
                msg.setStatus(OutboxStatus.FAILED);
                outboxRepository.save(msg);
            }
        }
        return pending.size();
    }

    public long getPendingCount() {
        return outboxRepository.countByStatus(OutboxStatus.PENDING);
    }
}
