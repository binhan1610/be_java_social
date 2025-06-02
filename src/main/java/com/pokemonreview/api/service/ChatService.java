package com.pokemonreview.api.service;

import com.pokemonreview.api.models.ChatEntity;
import com.pokemonreview.api.repository.ChatRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {
    private final ChatRepository chatRepository;

    ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public List<ChatEntity> getAll(long id) {
        return chatRepository.findAllByIdWithLimit(id, 20, 0);
    }

    public ChatEntity getById(long id, long createTime) {
        return chatRepository.findByIdAndCreateTime(id, createTime);
    }
}
