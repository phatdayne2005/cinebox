package com.phat.cinebox.service;

import com.phat.cinebox.dto.response.MovieResponse;
import com.phat.cinebox.model.Category;
import com.phat.cinebox.model.Movie;
import com.phat.cinebox.repository.CategoryRepository;
import com.phat.cinebox.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final CategoryRepository categoryRepository;

    public MovieResponse getFeatureMovie(){
        Movie movie = movieRepository.findFirstByIsFeaturedMovieTrue();
        MovieResponse movieResponse = new MovieResponse();
        movieResponse.setTitle(movie.getTitle());
        movieResponse.setDescription(movie.getDescription());
        movieResponse.setRating(movie.getRating());
        movieResponse.setReleaseDate(movie.getReleaseDate());
        movieResponse.setFeaturedMovie(movieResponse.isFeaturedMovie());
        movieResponse.setCast(movie.getCast());
        movieResponse.setDirector(movie.getDirector());
        List<String> categories = new ArrayList<>();
        for (Category category : movie.getCategories()) {
            categories.add(category.getName());
        }
        movieResponse.setCategories(categories);
        movieResponse.setDuration(movie.getDuration());
        movieResponse.setBackdropUrl(movie.getBackdropUrl());
        movieResponse.setPosterUrl(movie.getPosterUrl());
        movieResponse.setStatus(movie.getStatus());
        movieResponse.setTrailerUrl(movie.getTrailerUrl());
        return movieResponse;
    }
}
