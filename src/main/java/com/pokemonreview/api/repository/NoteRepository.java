package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    @Query("SELECT n from  Note n "+"LEFT JOIN n.user u"+" WHERE n.user.username= :username"+" ORDER BY n.important DESC, n.createdAt DESC")
    public Page<Note> findNoteByUserId(String username, Pageable pageable);

    @Query("SELECT n FROM Note n " +
            "LEFT JOIN n.lable l " +
            "LEFT JOIN n.user u " +
            "WHERE n.user.username = :username AND n.lable.id= :lable_id"+
            " ORDER BY n.important DESC, n.createdAt DESC"
    )
    Page<Note> findNoteByUserIdAndLabelId(String username,  Long lable_id, Pageable pageable);

    @Query("SELECT n FROM Note n " +
            "LEFT JOIN n.user u " +
            "WHERE n.topic LIKE CONCAT('%', :keyword, '%') " +
            "AND u.username = :username " +
            "ORDER BY n.important DESC, n.createdAt DESC")
    Page<Note> findNoteByTopic(String keyword,String username, Pageable pageable);
}
