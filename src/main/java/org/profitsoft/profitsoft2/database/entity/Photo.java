package org.profitsoft.profitsoft2.database.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@Entity
@Table(name = "photos")
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "photo_name", nullable = false, length = 100)
    private String photoName;

    @Column(name = "photo_format", nullable = false, length = 10)
    private String photoFormat;

    @Column(name = "photo_path", nullable = false)
    private String photoPath;

    @Column(name = "photo_description", length = 500)
    private String photoDescription;

    @Column(name = "photo_tags", length = 500)
    private String photoTags;

    @Column(name = "upload_date", columnDefinition = "DATE", nullable = false)
    private LocalDate uploadDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
