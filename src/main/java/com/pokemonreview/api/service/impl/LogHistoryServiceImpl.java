package com.pokemonreview.api.service.impl;

import com.pokemonreview.api.models.Log_History;
import com.pokemonreview.api.repository.LogHistoryRepository;
import com.pokemonreview.api.service.LogHistorySerivce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogHistoryServiceImpl implements LogHistorySerivce {

    private LogHistoryRepository logHistoryRepository;

    @Autowired
    public LogHistoryServiceImpl(LogHistoryRepository logHistoryRepository) {
        this.logHistoryRepository = logHistoryRepository;
    }

    @Override
    public  void saveLogCreateNote(Long user_id, Long note_id)
    {
        Log_History log_history = new Log_History();
        log_history.setType(Log_History.LogType.CREATE);
        log_history.setNoteId(note_id);
        log_history.setUserId(user_id);
        log_history.setPayload("đã tạo thành công");
        logHistoryRepository.save(log_history);
    }

    @Override
    public  void saveLogAddNote(Long user_id, Long note_id)
    {
        Log_History log_history = new Log_History();
        log_history.setType(Log_History.LogType.ADD);
        log_history.setNoteId(note_id);
        log_history.setUserId(user_id);
        log_history.setPayload("đã thêm thành công");
        logHistoryRepository.save(log_history);
    }

    @Override
    public  void saveLogUpdateNote(Long user_id, Long note_id, String payload)
    {
        Log_History log_history = new Log_History();
        log_history.setType(Log_History.LogType.UPDATE);
        log_history.setNoteId(note_id);
        log_history.setUserId(user_id);
        log_history.setPayload(payload);
        logHistoryRepository.save(log_history);
    }

    @Override
    public  void saveLogDeleteNote(Long user_id, Long note_id)
    {
        Log_History log_history = new Log_History();
        log_history.setType(Log_History.LogType.DELETE);
        log_history.setNoteId(note_id);
        log_history.setUserId(user_id);
        log_history.setPayload("đã xóa thành công");
        logHistoryRepository.save(log_history);
    }



}
