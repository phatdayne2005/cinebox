package com.phat.cinebox.dto.response;

import com.phat.cinebox.model.MovieStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponse {
    private Long id;

    private String title;

    private String description;

    private List<String> categories;

    private int duration;

    private double rating;

    private String director;

    private String cast;

    private String posterUrl;

    private String backdropUrl;

    private String trailerUrl;

    private LocalDate releaseDate;

    private MovieStatus status;

    private boolean isFeaturedMovie;
}
