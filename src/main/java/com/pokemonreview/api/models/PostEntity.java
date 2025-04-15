package com.pokemonreview.api.models;

import com.pokemonreview.api.service.impl.LongArrayConverter;
import com.pokemonreview.api.service.impl.StringArrayConverter;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
@Entity
@Table(name = "post")
@Data
@NoArgsConstructor
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long postId;

    @Column(name = "lables")
    @Convert(converter = StringArrayConverter.class)
    private String[] lables;

    @Column(name = "tags")
    @Convert(converter = LongArrayConverter.class) // Sử dụng converter để chuyển đổi Long[]
    private Long[] tags;


    @Column(name = "images")
    @Convert(converter = StringArrayConverter.class) // Sử dụng converter để chuyển đổi String[]
    private String[] images;


    @Column(name = "caption")
    private String caption;

    @Column(name = "createTime", nullable = false, updatable = false)
    private long createTime;

    @Column(name = "updateTime", nullable = false)
    private long updatedTime;
}
