package com.my.example.service;

import com.my.example.domain.Page;
import com.my.example.domain.repo.PagesRepository;
import com.my.example.web.dto.PageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PagesService {
    private final PagesRepository pagesRepository;

    public List<PageDto> getPages(){
        List<PageDto> pages = new ArrayList<>();
        pagesRepository.findAll().forEach( (Page page) -> {
            pages.add(PageDto.builder().id(String.valueOf(page.getId())).path(page.getPath()).build());
        });

        return pages;
    }

    public String getPagePath(String id) {
        if(!pagesRepository.findById(Long.valueOf(id)).isPresent()) {
            throw new NotFoundException("page not found");
        }
        return pagesRepository.findById(Long.valueOf(id)).get().getPath();
    }


}
