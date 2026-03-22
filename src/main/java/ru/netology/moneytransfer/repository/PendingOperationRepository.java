package ru.netology.moneytransfer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.netology.moneytransfer.domain.PendingOperation;

/**
 * Доступ к ожидающим подтверждения операциям в БД.
 */
public interface PendingOperationRepository extends JpaRepository<PendingOperation, String> {
}
