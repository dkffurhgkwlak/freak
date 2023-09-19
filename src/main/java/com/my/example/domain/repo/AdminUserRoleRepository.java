package com.my.example.domain.repo;

import com.my.example.domain.AdminUserRole;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;

//@RepositoryRestResource(exported = false)
public interface AdminUserRoleRepository extends CrudRepository<AdminUserRole, Long> {

    AdminUserRole findByName(String name);
}
