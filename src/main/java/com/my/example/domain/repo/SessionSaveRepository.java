package com.my.example.domain.repo;

import com.my.example.domain.SessionSave;
import org.springframework.data.repository.CrudRepository;

public interface SessionSaveRepository extends CrudRepository<SessionSave, Long> {
    SessionSave findBySessionKey(String sessionKey);

}
