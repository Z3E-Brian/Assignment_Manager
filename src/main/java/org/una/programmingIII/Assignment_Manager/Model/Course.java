package org.una.programmingIII.Assignment_Manager.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "courses")
@Data
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "professor_id")
    private User professor;


    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "career_id")
    private Career career;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "course_users",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users;

}