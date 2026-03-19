package com.phat.cinebox.controller;

import com.phat.cinebox.dto.response.MovieResponse;
import com.phat.cinebox.service.MovieService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class MovieController {
    private final MovieService movieService;
    @GetMapping("/api/movies/featured")
    public ResponseEntity<?> getFeatureMovie(){
        MovieResponse movie = movieService.getFeatureMovie();
        return ResponseEntity.ok().body(movie);
    }

    @GetMapping("/api/movies")
    public ResponseEntity<?> getAllMovies(@RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<MovieResponse> allMovies = this.movieService.getAllMovies(pageable);
        return ResponseEntity.ok().body(allMovies);
    }

    @GetMapping("/api/movies/{id}")
    public ResponseEntity<?> getMovieDetail(@PathVariable Long id){
        MovieResponse movie = movieService.getMovieDetail(id);
        if (movie == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(movie);
    }

    @DeleteMapping("/api/movies/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable Long id){
        Long deleteResult = movieService.deleteMovie(id);
        if (deleteResult == 1L){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
