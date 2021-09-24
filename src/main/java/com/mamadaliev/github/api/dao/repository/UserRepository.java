package com.mamadaliev.github.api.dao.repository;

import com.mamadaliev.github.api.dao.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, Long> {
}
