package com.example.locking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import java.util.Optional;

@ApplicationScoped
public class LockingService {

    @Inject
    private ItemRepository itemRepository;

    @Transactional
    public Item saveItem(Item item) {
        itemRepository.save(item);
        return item;
    }

    @Transactional
    public Optional<Item> updateWithOptimisticLock(Long itemId, String newName) {
        Optional<Item> optionalItem = itemRepository.findById(itemId, LockModeType.OPTIMISTIC);
        optionalItem.ifPresent(item -> {
            item.setName(newName);
            itemRepository.save(item);
        });
        return optionalItem;
    }

    @Transactional
    public Optional<Item> updateWithPessimisticLock(Long itemId, String newName) {
        Optional<Item> optionalItem = itemRepository.findById(itemId, LockModeType.PESSIMISTIC_WRITE);
        optionalItem.ifPresent(item -> {
            item.setName(newName);
            itemRepository.save(item);
        });
        return optionalItem;
    }
}
