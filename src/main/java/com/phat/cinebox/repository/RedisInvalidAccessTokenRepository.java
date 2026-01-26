package com.phat.cinebox.repository;

import com.phat.cinebox.model.RedisInvalidAccessToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisInvalidAccessTokenRepository extends CrudRepository<RedisInvalidAccessToken, String> {
}
