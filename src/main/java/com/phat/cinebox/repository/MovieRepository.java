package com.phat.cinebox.repository;

import com.phat.cinebox.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    Movie findFirstByIsFeaturedMovieTrue();
    Optional<Movie> findById(Long id);
    Long removeById(Long id);
}
