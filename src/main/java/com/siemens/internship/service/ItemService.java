package com.siemens.internship.service;

import com.siemens.internship.model.Item;
import com.siemens.internship.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;
    private static ExecutorService executor = Executors.newFixedThreadPool(10);

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    public Item save(Item item) {
        return itemRepository.save(item);
    }

    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }


    /**
     * Your Tasks
     * Identify all concurrency and asynchronous programming issues in the code
     * Fix the implementation to ensure:
     * All items are properly processed before the CompletableFuture completes
     * Thread safety for all shared state
     * Proper error handling and propagation
     * Efficient use of system resources
     * Correct use of Spring's @Async annotation
     * Add appropriate comments explaining your changes and why they fix the issues
     * Write a brief explanation of what was wrong with the original implementation
     * <p>
     * Hints
     * Consider how CompletableFuture composition can help coordinate multiple async operations
     * Think about appropriate thread-safe collections
     * Examine how errors are handled and propagated
     * Consider the interaction between Spring's @Async and CompletableFuture
     */
    // The problems with this function were the following
    // - it did not use thread-safe variables and collections (concurrency issues)
    // - async tasks were launched, but not waiting for completion (could return an empty list or incomplete list)
    // - the exceptions were only printed (can fail silently)
    // - it used @Async, but returned a List instead of CompletableFuture (using List would run the method synchronously)
    // - processedItems was a shared field (memory issues) and was not reset

    @Async
    public CompletableFuture<List<Item>> processItemsAsync() {
        List<Long> itemIds = itemRepository.findAllIds();

        // made processedItems and processedCount local
        List<Item> processedItems = new CopyOnWriteArrayList<>(); // use thread-safe collection
        AtomicInteger processedCount = new AtomicInteger(0); // use atomic variable for thread-safety

        // switched to streams for cleaner processing
        List<CompletableFuture<Void>> completableFutures = itemIds.stream()
                .map(id -> CompletableFuture.runAsync(() -> {
                    try {
                        Thread.sleep(100);

                        // modified findById to use ifPresent - easier to maintain
                        itemRepository.findById(id).ifPresent(item -> {
                            item.setStatus("PROCESSED");
                            itemRepository.save(item);
                            processedItems.add(item);
                            processedCount.incrementAndGet();
                        });
                    } catch (InterruptedException e) { // proper exception handling
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(
                                "Thread interrupted while processing item with ID " +
                                id, e);
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException(
                                "Illegal argument ", e
                        );
                    } catch (Exception e) {
                        throw new RuntimeException(
                                "Failed to process item with ID " + id, e);
                    }
                }, executor))
                .toList();

        // wait for all tasks to complete, then return processedItems
        return CompletableFuture.allOf(completableFutures
                        .toArray(new CompletableFuture[0]))
                .thenApply(i -> processedItems);
    }
}

