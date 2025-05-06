package com.siemens.internship.controller;

import com.siemens.internship.model.Item;
import com.siemens.internship.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        return new ResponseEntity<>(itemService.findAll(), HttpStatus.OK);
    }

    // modified the HttpStatus.BAD_REQUEST to HttpStatus.CREATED when the item was created successfully
    // modified the HttpStatus.CREATED to HttpStatus.BAD_REQUEST when the item is not valid
    // modified createdItem to put the errors in a map and returns them in the response

    @PostMapping
    public ResponseEntity<?> createItem(@Valid @RequestBody Item item, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(itemService.save(item), HttpStatus.CREATED);
    }

    // modified the HttpStatus.NO_CONTENT to HttpStatus.NOT_FOUND when the item is not found

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        return itemService.findById(id)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // modified the HttpStatus.ACCEPTED to HttpStatus.NOT_FOUND when the item is not found
    // modified the HttpStatus.CREATED to HttpStatus.OK when the item is updated

    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @RequestBody Item item) {
        Optional<Item> existingItem = itemService.findById(id);
        if (existingItem.isPresent()) {
            item.setId(id);
            return new ResponseEntity<>(itemService.save(item), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // verification if there is an item with given id was missing

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        Optional<Item> existingItem = itemService.findById(id);
        if (existingItem.isPresent()) {
            itemService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // modified to wait for result using get() (since processItemsAsync is asynchronous)

    @GetMapping("/process")
    public ResponseEntity<List<Item>> processItems() {
        try {
            List<Item> result = itemService.processItemsAsync().get();
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (CancellationException e) {
            return new ResponseEntity<>(HttpStatus.REQUEST_TIMEOUT);
        } catch (ExecutionException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
