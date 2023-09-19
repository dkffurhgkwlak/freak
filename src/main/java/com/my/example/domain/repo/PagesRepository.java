package com.my.example.domain.repo;

import com.my.example.domain.Page;
import org.springframework.data.repository.CrudRepository;

//@RepositoryRestResource(exported = false)
public interface PagesRepository extends CrudRepository<Page, Long> {
}
