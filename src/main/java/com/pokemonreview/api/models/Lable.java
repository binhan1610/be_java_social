package com.pokemonreview.api.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.*;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "lable")

public class Lable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="lable_name",nullable = false)
    private String lable_name;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at",nullable = false,updatable = false)
    @CreationTimestamp
    private Date created_at;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private Date updatedAt;

    @OneToMany(mappedBy = "lable")
    @JsonBackReference
    private List<Note> notes= new ArrayList<Note>();

    @OneToOne
    @JsonIgnore
    @JoinColumn(name= "user_id",referencedColumnName = "id")
    private UserEntity user;

}
