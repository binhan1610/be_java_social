package com.pokemonreview.api.controllers;


import com.fasterxml.jackson.databind.JsonNode;
import com.google.firebase.messaging.BatchResponse;
import com.pokemonreview.api.dto.*;
import com.pokemonreview.api.dto.note.NoteDto;
import com.pokemonreview.api.dto.note.ResponseNoteDto;
import com.pokemonreview.api.firebase.FirebaseService;
import com.pokemonreview.api.models.Note;
import com.pokemonreview.api.security.JWTGenerator;
import com.pokemonreview.api.service.FreemarkerService;
import com.pokemonreview.api.service.NoteService;
import com.pokemonreview.api.service.NotificationService;
import com.pokemonreview.api.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/note")
public class NoteController {

    private FirebaseService firebaseService;
    private NoteService noteService;
    private JWTGenerator jwtGenerator;
    private NotificationService notificationService;
    private TemplateService templateService;

    @Autowired
    public NoteController(TemplateService templateService,FirebaseService firebaseService, NoteService noteService, JWTGenerator jwtGenerator, NotificationService notificationService) {
        this.firebaseService = firebaseService;
        this.noteService = noteService;
        this.jwtGenerator = jwtGenerator;
        this.notificationService = notificationService;
        this.templateService = templateService;

    }

    @PostMapping("/create")
    public ResponseEntity<?> createNote(
            @RequestParam("file") MultipartFile file,
            @RequestParam("topic") String topic,
            @RequestParam("lable_id") Optional<String> lable_id,
            @RequestParam("title") String title,
            @RequestHeader("Authorization") String token
    ) {
       try {
           String jwtToken = token.substring(7);
           String username = jwtGenerator.getUsernameFromJWT((jwtToken));

           String image_link = this.firebaseService.upload(file);
           CreateNoteDto createNoteDto = new CreateNoteDto();
           createNoteDto.setTitle(title);
           createNoteDto.setTopic(topic);
           lable_id.ifPresent(l -> {
               try {
                   Long lableIdValue = Long.parseLong(l);
                   createNoteDto.setId_lable(Optional.of(lableIdValue));
               } catch (NumberFormatException e) {
                   e.printStackTrace();
               }
           });
           Map<String, Object> model = new HashMap<>();
           Note note = noteService.createNote(createNoteDto,image_link,username);
           model.put("note", note);
           JsonNode jsonNode = this.templateService.generateJsonFromTemplate("responseNote.ftl",model);
           return new ResponseEntity<>(jsonNode, HttpStatus.CREATED);
       }
       catch (Exception e)
       {
           e.printStackTrace();
           return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    @PostMapping("/add")
    public ResponseEntity<Note> addNote(
            @RequestParam("file") Optional<MultipartFile> file,
            @RequestParam("note_id") String note_id,
            @RequestParam("title") Optional<String> title,
            @RequestHeader("Authorization") String token

    ) {
        String jwtToken = token.substring(7);
        String username = jwtGenerator.getUsernameFromJWT((jwtToken));
        AddNoteDto addNoteDto = new AddNoteDto();
        file.ifPresent(f -> {
            String image_link = this.firebaseService.upload(f);
            addNoteDto.setLink_image(Optional.of(image_link));
        });
        title.ifPresent(t -> {
            addNoteDto.setTitle(Optional.of(t));
        });
        addNoteDto.setNote_id(Long.parseLong(note_id));
        return new ResponseEntity<>(noteService.addNote(addNoteDto,username), HttpStatus.CREATED);
    }

    @PutMapping("/update-title")
    public ResponseEntity<Note> updateTitle(@RequestBody UpdateTitleDto updateTitleDto,@RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7);
        String username = jwtGenerator.getUsernameFromJWT((jwtToken));
        return new ResponseEntity<>(noteService.updateTitle(updateTitleDto,username), HttpStatus.OK);
    }

    @PutMapping("/update-image")
    public ResponseEntity<Note> updateImage(@RequestHeader("Authorization") String token ,@RequestParam("file") MultipartFile file, @RequestParam("note_id") String note_id, @RequestParam("image_id") String image_id) {
        String image_link = this.firebaseService.upload(file);
        String jwtToken = token.substring(7);
        String username = jwtGenerator.getUsernameFromJWT((jwtToken));
        return new ResponseEntity<>(noteService.updateImage(Long.parseLong(note_id), Long.parseLong(image_id), image_link, username), HttpStatus.OK);
    }

    @GetMapping("search")
    public  ResponseEntity<ResponseNoteDto> searchNote(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword") String keyword
    )
    {
        String jwtToken = token.substring(7);
        String username = jwtGenerator.getUsernameFromJWT((jwtToken));
        return new ResponseEntity<>(noteService.searchNote(username,keyword,page,size),HttpStatus.OK);
    }

    @GetMapping("get-by-user")
    public ResponseEntity<?> getNoteByUser(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size, @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            String username = jwtGenerator.getUsernameFromJWT((jwtToken));
            Map<String,Object> mode = new HashMap<>();
            ResponseNoteDto notes = noteService.findAllNoteByUser(username,page,size);
            mode.put("notes", notes);
            JsonNode jsonNode = templateService.generateJsonFromTemplate("responseListNote.ftl",mode);
            return new ResponseEntity<>(jsonNode, HttpStatus.OK);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }


    @GetMapping("/get-by-lable")
    public ResponseEntity<ResponseNoteDto> getNoteByLable(@RequestParam("lable_id") Long lable_id, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size, @RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7);
        String username = jwtGenerator.getUsernameFromJWT((jwtToken));
        FindNoteDto findNoteDto = new FindNoteDto();
        findNoteDto.setLable_id(lable_id);
        findNoteDto.setUsername(username);
        return new ResponseEntity<>(noteService.findAllNoteByUserAndLable(findNoteDto, page, size), HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public  ResponseEntity<String> deleteBote (@RequestParam("note_id") Long note_id, @RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7);
        String username = jwtGenerator.getUsernameFromJWT((jwtToken));
        return new ResponseEntity<>(noteService.deleteNote(note_id,username), HttpStatus.OK);
    }

    @GetMapping("/mark")
    public ResponseEntity<NoteDto> markNote(@RequestParam("note_id") Long note_id)
    {
        return new ResponseEntity<>(noteService.markNote(note_id),HttpStatus.OK);
    }

    @GetMapping("/done")
    public  ResponseEntity<NoteDto> doneNote(@RequestParam("note_id") Long note_id)
    {
        return new ResponseEntity<>(noteService.doneNote(note_id),HttpStatus.OK);
    }

    @PostMapping("/share")
    public  ResponseEntity<NoteDto> shareNote(@RequestHeader("Authorization") String token, @RequestBody()ShareNoteDto shareNoteDto)
    {
        String jwtToken = token.substring(7);
        String username = jwtGenerator.getUsernameFromJWT((jwtToken));
        return new ResponseEntity<>(noteService.shareNote(shareNoteDto,username), HttpStatus.OK);
    }

    @PostMapping("test")
    public  ResponseEntity<?> test(@RequestParam("title") String title,@RequestParam("payload") String payload)
    {
        String fcm = "fJlTQyxNVJrQth6TbmeVOg:APA91bG7--Vj4kZGOzwanc-LaELUK5qH02_fa3yL2J77bZH-8niNR_7Tn1lFwDtq2UAlqIdIo6xBLqybJT8L7erv2obLFYAVyavT9QIxP-EtnyhRL93sxzew-5T5os-MuUCZS3t39V3i";
        return  new ResponseEntity<>(noteService.sendTestMultiple(title,payload),HttpStatus.OK);
    }

}
