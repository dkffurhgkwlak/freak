package com.my.example.service;

import com.my.example.domain.AdminUserRole;
import com.my.example.domain.Page;
import com.my.example.domain.repo.AdminUserRoleRepository;
import com.my.example.domain.repo.PagesRepository;
import com.my.example.web.dto.AdminUserRoleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminUserRoleService  {

    private final AdminUserRoleRepository adminUserRoleRepository;
    private final PagesRepository pagesRepository;

    public AdminUserRoleDto getAdminUserRoleAndPages(String name){
        AdminUserRole adminUserRole = adminUserRoleRepository.findByName(name);
        Iterable<Page> pageInfo = pagesRepository.findAll();
        ArrayList<String> pages = new ArrayList<String>();
        for (String id : adminUserRole.getPages()) {
            pageInfo.forEach( (element) -> {
                if( String.valueOf(element.getId()).equals(id)){
                    pages.add(element.getPath());
                }
            });
        }
        return AdminUserRoleDto.builder().name(adminUserRole.getName())
                .pages(pages).build();
    }

    public List<AdminUserRoleDto> getAdminUserRolesList(){
        Iterable<AdminUserRole> adminUserRoles = adminUserRoleRepository.findAll();
        List<AdminUserRoleDto> adminUserRolesList = new ArrayList<AdminUserRoleDto>();
        adminUserRoles.forEach( (adminUserRole) -> {
            adminUserRolesList.add(AdminUserRoleDto.builder().name(adminUserRole.getName())
                    .pages(adminUserRole.getPages()).build());
        });

        return adminUserRolesList;
    }

}
