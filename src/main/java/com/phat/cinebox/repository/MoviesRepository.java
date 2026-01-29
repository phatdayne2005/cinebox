package com.phat.cinebox.repository;

import com.phat.cinebox.model.Movie;
import org.springframework.data.repository.CrudRepository;

public interface MoviesRepository extends CrudRepository<Movie,Long> {

}
