package com.phat.cinebox.repository;

import com.phat.cinebox.model.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends CrudRepository<Category,Long> {
    List<Category> findAll();
    Optional<Category> findById(Long id);
}
