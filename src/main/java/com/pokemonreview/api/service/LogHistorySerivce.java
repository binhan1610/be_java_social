package com.pokemonreview.api.service;

public interface LogHistorySerivce {
    void saveLogCreateNote(Long user_id,Long note_id);
    void saveLogUpdateNote(Long user_id,Long note_id,String payload);
    void saveLogDeleteNote(Long user_id,Long note_id);
    void saveLogAddNote(Long user_id,Long note_id);
}
