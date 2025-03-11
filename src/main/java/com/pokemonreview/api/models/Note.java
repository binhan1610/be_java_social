package com.pokemonreview.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="note")
@NoArgsConstructor
@AllArgsConstructor

public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "topic", nullable = false)
    private String topic;

    @Column(name = "important", nullable = false)
    private boolean important;

    @Column(name = "success", nullable = false)
    private boolean success;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private Date updatedAt;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "lable_id")
    private Lable lable;

    @JsonManagedReference
    @OneToMany(mappedBy = "note",cascade = CascadeType.ALL)
    private List<Title> titles= new ArrayList<Title>();

    @JsonManagedReference
    @OneToMany(mappedBy = "note",cascade = CascadeType.ALL)
    private  List<Image> images = new ArrayList<Image>();
}
