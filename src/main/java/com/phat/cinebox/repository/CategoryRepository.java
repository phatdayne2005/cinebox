package com.phat.cinebox.repository;

import com.phat.cinebox.model.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends CrudRepository<Category,Integer> {
    List<Category> findAll();
    Category findById(Long id);
}
