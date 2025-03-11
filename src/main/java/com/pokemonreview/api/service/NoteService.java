package com.pokemonreview.api.service;

import com.google.firebase.messaging.BatchResponse;
import com.pokemonreview.api.dto.*;
import com.pokemonreview.api.dto.note.NoteDto;
import com.pokemonreview.api.dto.note.ResponseNoteDto;
import com.pokemonreview.api.models.Note;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;


public interface NoteService {
    ResponseNoteDto findAllNoteByUser(String username, int page,int size );
    ResponseNoteDto findAllNoteByUserAndLable(FindNoteDto findNoteDto, int page, int size);
    ResponseNoteDto searchNote(String username, String keyword, int page, int size);
    Note createNote(CreateNoteDto createNoteDto, String image_link, String username);
    Note addNote(AddNoteDto addNoteDto,String username);
    Note updateTitle(UpdateTitleDto updateTitleDto,String username);
    Note updateImage(Long note_id, Long image_id, String image_link, String username);
    String deleteNote(Long note_id, String username);
    NoteDto markNote(Long note_id);
    NoteDto doneNote(Long note_id);
    NoteDto shareNote(ShareNoteDto shareNoteDto, String username);
    String sendTestMultiple(String title, String payload);
}
