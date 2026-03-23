package com.phat.cinebox.service;

import com.phat.cinebox.dto.request.MovieRequest;
import com.phat.cinebox.dto.response.MovieResponse;
import com.phat.cinebox.model.Category;
import com.phat.cinebox.model.Movie;
import com.phat.cinebox.repository.CategoryRepository;
import com.phat.cinebox.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final CategoryRepository categoryRepository;

    public MovieResponse getFeatureMovie(){
        Movie movie = movieRepository.findFirstByIsFeaturedMovieTrue();
        return movieMapping(movie);
    }

    public Page<MovieResponse> getAllMovies(Pageable pageable) {
        // 1. Lấy dữ liệu từ Database (trả về Page<Movie>)
        Page<Movie> moviePage = this.movieRepository.findAll(pageable);

        // 2. Chuyển đổi Page<Movie> sang Page<MovieResponse>
        // Sử dụng method reference để gọi hàm movieMapping cho từng phần tử
        return moviePage.map(this::movieMapping);
    }

    public MovieResponse getMovieDetail(Long id){
        Optional<Movie> movieOptional = movieRepository.findById(id);
        if (movieOptional.isPresent()){
            Movie movie = movieOptional.get();
            return movieMapping(movie);
        }
        else {
            return null;
        }
    }

    @Transactional
    public Long deleteMovie(Long id){
        return movieRepository.removeById(id);
    }

    @Transactional
    public MovieResponse updateMovie(Long id, MovieRequest movieRequest){
        Optional<Movie> movieOptional = movieRepository.findById(id);
        if (movieOptional.isPresent()){
            Movie movie = movieOptional.get();
            applyMovieRequest(movie, movieRequest);
            movieRepository.save(movie);
            return movieMapping(movie);
        }
        else
            return null;
    }

    private void applyMovieRequest(Movie movie, MovieRequest movieRequest){
        if (movie == null || movieRequest == null){
            return;
        }
        movie.setTitle(movieRequest.getTitle());
        movie.setDescription(movieRequest.getDescription());
        movie.setRating(movieRequest.getRating());
        movie.setReleaseDate(movieRequest.getReleaseDate());
        movie.setFeaturedMovie(movieRequest.isFeaturedMovie());
        movie.setCast(movieRequest.getCast());
        movie.setDirector(movieRequest.getDirector());
        HashSet<Category> categories = new HashSet<>();
        for (Long categoryId : movieRequest.getCategoryIds()) {
            Optional<Category> category = this.categoryRepository.findById(categoryId);
            if (category.isPresent())
                categories.add(category.get());
        }
        movie.setCategories(categories);
        movie.setDuration(movieRequest.getDuration());
        movie.setBackdropUrl(movieRequest.getBackdropUrl());
        movie.setPosterUrl(movieRequest.getPosterUrl());
        movie.setStatus(movieRequest.getStatus());
        movie.setTrailerUrl(movieRequest.getTrailerUrl());
    }

    private MovieResponse movieMapping(Movie movie){
        if (movie == null)
            return null;
        MovieResponse movieResponse = new MovieResponse();
        movieResponse.setId(movie.getId());
        movieResponse.setTitle(movie.getTitle());
        movieResponse.setDescription(movie.getDescription());
        movieResponse.setRating(movie.getRating());
        movieResponse.setReleaseDate(movie.getReleaseDate());
        movieResponse.setFeaturedMovie(movie.isFeaturedMovie());
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
