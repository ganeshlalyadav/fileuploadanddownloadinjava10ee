package com.example.locking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;
@ApplicationScoped
public class ItemRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Item> findById(Long id, LockModeType lockMode) {
        return Optional.ofNullable(entityManager.find(Item.class, id));
    }

    public void save(Item item) {
        entityManager.merge(item);
    }
}
