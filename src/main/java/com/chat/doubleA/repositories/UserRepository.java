package com.chat.doubleA.repositories;

import com.chat.doubleA.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

	List<User> findAll();

	Optional<User> findById(String id);

	Optional<User> findByUsername(String username);

	Optional<User> findByEmail(String email);

	Boolean existsByEmail(String email);
}
