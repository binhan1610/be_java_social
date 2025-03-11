package com.pokemonreview.api.service.impl;


import com.google.firebase.messaging.BatchResponse;
import com.pokemonreview.api.dto.*;
import com.pokemonreview.api.dto.lable.LableDto;
import com.pokemonreview.api.dto.note.NoteDto;
import com.pokemonreview.api.dto.note.ResponseNoteDto;
import com.pokemonreview.api.models.*;
import com.pokemonreview.api.repository.*;
import com.pokemonreview.api.service.LogHistorySerivce;
import com.pokemonreview.api.service.NoteService;
import com.pokemonreview.api.service.NotificationService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NoteServiceImpl implements NoteService {
    private NoteRepository noteRepository;
    private LableRepository lableRepository;
    private UserRepository userRepository;
    private LogHistorySerivce  logHistorySerivce;
    private TitleRepository titleRepository;
    private ImageRepository imageRepository;
    private NotificationService notificationService;
    @Autowired
    public NoteServiceImpl(NotificationService notificationService,TitleRepository titleRepository, ImageRepository imageRepository ,LogHistorySerivce logHistorySerivce,NoteRepository noteRepository, LableRepository lableRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.lableRepository = lableRepository;
        this.userRepository = userRepository;
        this.logHistorySerivce = logHistorySerivce;
        this.titleRepository = titleRepository;
        this.imageRepository = imageRepository;
        this.notificationService = notificationService;
    }
    @Override
    public ResponseNoteDto searchNote(String username,String keyword,int page, int size)
    {
        System.out.println(keyword);
        Pageable pageable = PageRequest.of(page,size);
        Page<Note> notes= noteRepository.findNoteByTopic(keyword,username,pageable);
        List<Note> listOfNote= notes.getContent();
        List<NoteDto> content = listOfNote.stream().map(n->mapToDto(n)).collect(Collectors.toList());
        ResponseNoteDto responseNoteDto = new ResponseNoteDto();
        responseNoteDto.setContent(content);
        responseNoteDto.setPageNo(notes.getNumber());
        responseNoteDto.setPageSize(notes.getSize());
        responseNoteDto.setTotal(notes.getTotalElements());
        responseNoteDto.setTotalPages(notes.getTotalPages());
        return responseNoteDto;
    }

    @Override
    public ResponseNoteDto findAllNoteByUser(String username, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Note> notes = noteRepository.findNoteByUserId(username,pageable);
        List<Note> listOfNote= notes.getContent();
        List<NoteDto> content = listOfNote.stream().map(n->mapToDto(n)).collect(Collectors.toList());
        ResponseNoteDto responseNoteDto = new ResponseNoteDto();
        responseNoteDto.setContent(content);
        responseNoteDto.setPageNo(notes.getNumber());
        responseNoteDto.setPageSize(notes.getSize());
        responseNoteDto.setTotal(notes.getTotalElements());
        responseNoteDto.setTotalPages(notes.getTotalPages());
        return responseNoteDto;
    }

    @Override
    public ResponseNoteDto findAllNoteByUserAndLable(FindNoteDto findNoteDto, int page,int size)
    {
        Pageable pageable = PageRequest.of(page, size);
        Page<Note> notes = noteRepository.findNoteByUserIdAndLabelId(findNoteDto.getUsername(),findNoteDto.getLable_id(),pageable);
        List<Note> listOfNote= notes.getContent();
        List<NoteDto> content = listOfNote.stream().map(n->mapToDto(n)).collect(Collectors.toList());
        ResponseNoteDto responseNoteDto = new ResponseNoteDto();
        responseNoteDto.setContent(content);
        responseNoteDto.setPageNo(notes.getNumber());
        responseNoteDto.setPageSize(notes.getSize());
        responseNoteDto.setTotal(notes.getTotalElements());
        responseNoteDto.setTotalPages(notes.getTotalPages());
        return responseNoteDto;
    }

    @Override
    public Note createNote(CreateNoteDto createNoteDto, String image_link, String username)
    {
        UserEntity user = userRepository.findByUsername(username).orElseThrow(
                ()-> new RuntimeException("User not found")
        );
        List<Title> list_title = new ArrayList<Title>();
        List<Image> list_image = new ArrayList<Image>();
        Title title = new Title();
        title.setTitle(createNoteDto.getTitle());
        list_title.add(title);
        Image image = new Image();
        image.setImage_link(image_link);
        list_image.add(image);
        Note note = new Note();
        note.setTopic(createNoteDto.getTopic());

        note.setTitles(list_title);
        note.setImages(list_image);
        note.setUser(user);
        if(createNoteDto.getId_lable() != null)
        {
            createNoteDto.getId_lable().ifPresent(id-> {
                Lable lable = lableRepository.findById(id).get();
                note.setLable(lable);
            });
        }
        Note newNote = noteRepository.save(note);
        title.setNote(newNote);
        image.setNote(newNote);
        titleRepository.save(title);
        imageRepository.save(image);
        //save log create
        logHistorySerivce.saveLogCreateNote(user.getId(),newNote.getId());
        return newNote;
    }

    @Override
    public Note addNote(AddNoteDto addNoteDto,String username) {
        UserEntity user= userRepository.findByUsername(username).orElse(null);
        if(user== null)
        {
            throw new RuntimeException("user not found");
        }
        Note note = noteRepository.findById(addNoteDto.getNote_id()).orElse(null);
        if (note == null) {
            throw new RuntimeException("Note not found");
        }

        // Xử lý link_image nếu có
        if(addNoteDto.getLink_image()!= null)
        {
        addNoteDto.getLink_image().ifPresent(link -> {
        Image image = new Image();
        image.setImage_link(link);
        image.setNote(note);
        imageRepository.save(image);
        note.getImages().add(image);
        });
        }

        // Xử lý title nếu có
        if(addNoteDto.getTitle()!= null)
        {
        addNoteDto.getTitle().ifPresent(t -> {
        Title noteTitle = new Title();
        noteTitle.setTitle(t);
        noteTitle.setNote(note);
        titleRepository.save(noteTitle);
        note.getTitles().add(noteTitle);
    });
}
        // Lưu thay đổi vào repository
        logHistorySerivce.saveLogAddNote(user.getId(),addNoteDto.getNote_id());
        noteRepository.save(note);

        return note;
    }

    @Override
    public Note updateTitle(UpdateTitleDto updateTitleDto, String username)
    {
        UserEntity user= userRepository.findByUsername(username).orElse(null);
        if(user== null)
        {
            throw new RuntimeException("user not found");
        }
         Title title = titleRepository.findById(updateTitleDto.getTitle_id()).orElse(null);
         if (title == null) {
             throw new RuntimeException("Title not found");
         }
         title.setTitle(updateTitleDto.getTitle());
         titleRepository.save(title);
         Note note = noteRepository.findById(updateTitleDto.getNote_id()).orElse(null);
         logHistorySerivce.saveLogUpdateNote(user.getId(),updateTitleDto.getNote_id(),"sửa nội dung thành công");
         return note;
    }

    @Override
    public Note updateImage( Long note_id,Long image_id, String image_link,String username)
    {
        UserEntity user= userRepository.findByUsername(username).orElse(null);
        if(user== null)
        {
            throw new RuntimeException("user not found");
        }
        Image image = imageRepository.findById(image_id).orElse(null);
        if (image == null) {
            throw new RuntimeException("image not found");
        }
        image.setImage_link(image_link);
        imageRepository.save(image);
        Note note = noteRepository.findById(note_id).orElse(null);
        logHistorySerivce.saveLogUpdateNote(user.getId(),note_id,"sửa ảnh thành công");
        return note;
    }

    @Override
    public String  deleteNote(Long note_id,String username)
    {
        UserEntity user= userRepository.findByUsername(username).orElse(null);
        if(user== null){
            throw new RuntimeException("user not found");
        }
        Note note = noteRepository.findById(note_id).orElse(null);
        if(note==null)
        {
            throw new RuntimeException("note not found");
        }
        logHistorySerivce.saveLogDeleteNote(user.getId(),note_id);
        noteRepository.delete(note);
        return "Xóa ghi chú thành công";
    }

    @Override
    public  NoteDto markNote(Long note_id)
    {
        Note note = noteRepository.findById(note_id).orElse(null);
        if(note==null)
        {
            throw new RuntimeException("note not found");
        }
        note.setImportant(true);
        Note mNote = noteRepository.save(note);
        NoteDto noteDto = mapToDto(mNote);
        return noteDto;
    }

    @Override
    public  NoteDto doneNote(Long note_id)
    {
        Note note = noteRepository.findById(note_id).orElse(null);
        if(note==null)
        {
            throw new RuntimeException("Note not found");
        }
        note.setSuccess(true);
        Note sNote = noteRepository.save(note);
        NoteDto noteDto = mapToDto(sNote);
        return noteDto;
    }

    @Override
    public NoteDto shareNote(ShareNoteDto shareNoteDto, String username) {
        // Get user share
        UserEntity userShare = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User sharing not found"));

        // Get note
        Note note = noteRepository.findById(shareNoteDto.getNote_id())
                .orElseThrow(() -> new RuntimeException("Note not found"));

        // Get user
        UserEntity user = userRepository.findById(shareNoteDto.getUser_id())
                .orElseThrow(() -> new RuntimeException("User receiving note not found"));

        // Create new note
        Note newNote = new Note();
        newNote.setSuccess(false);
        newNote.setImportant(false);
        newNote.setTitles(new ArrayList<>(note.getTitles())); // Sao chép danh sách titles
        newNote.setImages(new ArrayList<>(note.getImages())); // Sao chép danh sách images
        newNote.setTopic(note.getTopic());
        newNote.setUser(user);

        // Check and copy label
        Lable lable = note.getLable();
        if (lable != null) {
            Lable newLable = new Lable();
            newLable.setLable_name(lable.getLable_name());
            newLable.setUser(user);
            lableRepository.save(newLable);
            newNote.setLable(newLable);
        }

        // Save new note
        Note sNote = noteRepository.save(newNote);

        // Send notification
        String title = "Thông báo chia sẻ ghi chú";
        String payload = userShare.getUsername() + " đã chia sẻ ghi chú " + note.getTopic() + " cho bạn";
        String fcm_token = user.getFcm_token();
        notificationService.sendNotification(title, payload, fcm_token);

        // Map to DTO and return
        return mapToDto(sNote);
    }

    public String sendTestMultiple(String title, String payload)
    {
        List<String> list_fcm = new ArrayList<>() ;
        List<UserEntity> list_user= userRepository.findAll();
        for(UserEntity user:list_user)
        {
            if(user.getFcm_token()!=null)
            {
                list_fcm.add(user.getFcm_token());
            }
        }
        return notificationService.sendNotificationMul(title,payload,list_fcm);
    }

    ///function
    private NoteDto mapToDto(Note note)
    {
        NoteDto noteDto = new NoteDto();
        Lable lable= note.getLable();
        LableDto lableDto = new LableDto();
        if(lable!=null)
        {
            lableDto.setLabelName(lable.getLable_name());
            lableDto.setId(lable.getId());
            noteDto.setLable(lableDto);
        }
        noteDto.setId(note.getId());
        noteDto.setImportant((note.isImportant()));
        noteDto.setSuccess(note.isSuccess());
        noteDto.setTopic(note.getTopic());
        noteDto.setImageList(note.getImages());
        noteDto.setTitleList(note.getTitles());
        return noteDto;
    }



}
