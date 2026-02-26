package com.phat.cinebox.repository;

import com.phat.cinebox.model.Movie;
import org.springframework.data.repository.CrudRepository;

public interface MovieRepository extends CrudRepository<Movie, Long> {
    Movie findFirstByIsFeaturedMovieTrue();
}
