package com.phat.cinebox.repository;

import com.phat.cinebox.model.RedisValidRefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RedisValidRefreshTokenRepository extends CrudRepository<RedisValidRefreshToken, String> {
}
