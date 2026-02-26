package com.phat.cinebox.controller;

import com.phat.cinebox.dto.response.MovieResponse;
import com.phat.cinebox.model.Movie;
import com.phat.cinebox.service.MovieService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class MovieController {
    private final MovieService movieService;
    @GetMapping("/get-featured-movie")
    public ResponseEntity<?> getFeatureMovie(){
        MovieResponse movie = movieService.getFeatureMovie();
        return ResponseEntity.ok().body(movie);
    }
}
