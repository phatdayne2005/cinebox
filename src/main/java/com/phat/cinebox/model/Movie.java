package com.phat.cinebox.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "movies")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title cannot be empty")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ToString.Exclude
    @ManyToMany(fetch =  FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "movie_category", // Tên bảng trung gian
            joinColumns = @JoinColumn(name = "movie_id"), // Khóa ngoại trỏ đến bảng Movie
            inverseJoinColumns = @JoinColumn(name = "category_id") // Khóa ngoại trỏ đến bảng Category
    )
    private Set<Category> categories = new HashSet<Category>();

    @Positive(message = "Duration must be positive")
    private int duration;

    private double rating;

    @NotBlank(message = "Director's name cannot be blank")
    private String director;

    @NotBlank(message = "Cast's name cannot be blank")
    private String cast;

    private String poster_url;

    private String trailer_url;

    @Enumerated(EnumType.STRING)
    private MovieStatus status;
}
