package com.my.example.domain.repo;

import com.my.example.domain.AdminUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

//@RepositoryRestResource(exported = false)
public interface AdminUserRepository extends CrudRepository<AdminUser, Long> {
   Optional<AdminUser> findByUid(String uid);

}
