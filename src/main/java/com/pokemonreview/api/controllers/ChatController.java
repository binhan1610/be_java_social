package com.pokemonreview.api.controllers;

import com.pokemonreview.api.dto.ChatDto;
import com.pokemonreview.api.dto.ChatUpdateDto;
import com.pokemonreview.api.models.ChatEntity;
import com.pokemonreview.api.service.ChatService;
import com.pokemonreview.api.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService chatService;
    private final ChatRepository chatRepository;

    @Autowired
    public ChatController(ChatService chatService, ChatRepository chatRepository) {
        this.chatService = chatService;
        this.chatRepository = chatRepository;
    }

    // ✅ GET /chats/{id} - Lấy danh sách chat theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getAllById(@PathVariable("id") long id) {
        List<ChatEntity> list =  chatService.getAll(id);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // ✅ PUT /chats/{id} - Cập nhật chat
    @PutMapping("/{id}/{createTime}")
    public ResponseEntity<?> updateChat(@PathVariable("id") long id, @PathVariable("createTime") long createTime, @RequestBody ChatUpdateDto chatDto) {
        ChatEntity existing = chatService.getById(id, createTime);
        if (existing == null) {
            return ResponseEntity.badRequest().body("Not found message");
        }

        // Ví dụ: cập nhật một số field, bạn tùy chỉnh theo thực tế
        existing.setContent(chatDto.getContent());
        existing.setUpdateTime(new Date().getTime());

        chatRepository.save(existing);
        return new ResponseEntity<>(existing, HttpStatus.OK);
    }
}
