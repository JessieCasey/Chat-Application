package com.chat.doubleA.repositories;

import com.chat.doubleA.entities.Chatroom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatroomRepository extends MongoRepository<Chatroom, String> {
    Optional<Chatroom> findByTitle(String title);

    boolean existsByTitle(String title);

    List<Chatroom> findAllByOwner_Id(String id);

}
