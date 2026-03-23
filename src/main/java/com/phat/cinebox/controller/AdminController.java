package com.phat.cinebox.controller;

import com.phat.cinebox.dto.request.MovieRequest;
import com.phat.cinebox.model.Category;
import com.phat.cinebox.model.Movie;
import com.phat.cinebox.repository.CategoryRepository;
import com.phat.cinebox.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class AdminController {
    private final CategoryRepository categoryRepository;
    private final MovieRepository movieRepository;

    @GetMapping("/admin")
    public String getAdminPage() {
        return "/admin/dashboard";
    }

    @GetMapping("/admin/movies")
    public String getMoviesPage(Model model) {
        model.addAttribute("movie", new Movie());
        model.addAttribute("allCategories", categoryRepository.findAll());
        return "/admin/movies";
    }

    @PostMapping("/admin/movies")
    public ResponseEntity addMovie(@RequestBody MovieRequest movieRequest) {
        try {
            Set<Category> categories = new HashSet<>();
            for (Long id:movieRequest.getCategoryIds()) {
                Optional<Category> category = this.categoryRepository.findById(id);
                if (category.isPresent())
                    categories.add(category.get());
            }
            Movie movie = new Movie();
            movie.setCategories(categories);
            movie.setTitle(movieRequest.getTitle());
            movie.setDescription(movieRequest.getDescription());
            movie.setRating(movieRequest.getRating());
            movie.setReleaseDate(movieRequest.getReleaseDate());
            movie.setStatus(movieRequest.getStatus());
            movie.setFeaturedMovie(movieRequest.isFeaturedMovie());
            movie.setCast(movieRequest.getCast());
            movie.setDirector(movieRequest.getDirector());
            movie.setBackdropUrl(movieRequest.getBackdropUrl());
            movie.setPosterUrl(movieRequest.getPosterUrl());
            movie.setTrailerUrl(movieRequest.getTrailerUrl());
            movie.setDuration(movieRequest.getDuration());
            movieRepository.save(movie);

        } catch(Exception ex){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body("Add movie successfully");
    }
}
