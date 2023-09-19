package com.my.example.domain.repo;

import com.my.example.domain.EmailContents;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface EmailContentsRepository extends CrudRepository<EmailContents, Long> {
    Optional<EmailContents> findByName(String name);
}
