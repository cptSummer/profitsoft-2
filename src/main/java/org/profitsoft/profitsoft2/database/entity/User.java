package org.profitsoft.profitsoft2.database.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "join_date", nullable = false)
    private LocalDate joinDate;

    @Column(name = "role", nullable = false, length = 20)
    private String role;

    @OneToMany(mappedBy = "user")
    private Set<Photo> photos = new LinkedHashSet<>();

}
