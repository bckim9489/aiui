package com.aiui.project.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class InventoryController {

    public record InventoryItem(long id, String name, int stock) {
    }

    @GetMapping("/inventory")
    public List<InventoryItem> list() {
        return List.of(
                new InventoryItem(1L, "\uC0D8\ud50c", 12),
                new InventoryItem(2L, "\ud14c\uc2a4\ud2b8", 5)
        );
    }
}

