package com.phat.cinebox.dto.request;

import com.phat.cinebox.model.Category;
import com.phat.cinebox.model.MovieStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieRequest {

    @NotBlank(message = "Title cannot be empty")
    private String title;

    private String description;

    private List<Long> categoryIds;

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

    private LocalDate releaseDate;

    @Enumerated(EnumType.STRING)
    private MovieStatus status;

    private boolean isFeaturedMovie = false;
}
