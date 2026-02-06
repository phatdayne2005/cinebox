package com.phat.cinebox.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.*;

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

    @Min(0)
    @Max(10)
    private double rating;

    @NotBlank(message = "Director's name cannot be blank")
    private String director;

    @NotBlank(message = "Cast's name cannot be blank")
    private String cast;

    private String posterUrl;

    private String backdropUrl;

    private String trailerUrl;

    private Date releaseDate;

    @Enumerated(EnumType.STRING)
    private MovieStatus status;

    private boolean isFeaturedMovie = false;
}
