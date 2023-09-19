package com.my.example.service;

import com.my.example.domain.AdminUser;
import com.my.example.domain.EmailContents;
import com.my.example.domain.repo.AdminUserRepository;
import com.my.example.exception.NotFoundException;
import com.my.example.web.dto.AdminUserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private AdminUserRepository repository;

    @Autowired
    public UserDetailsServiceImpl(AdminUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminUser adminUser = findByUid(username);
        if(null==adminUser){
            throw new UsernameNotFoundException("user not found");
        }
        return adminUser;
    }

    private AdminUser findById(Long id) throws UsernameNotFoundException{
        Optional<AdminUser> user =
                Optional.ofNullable(repository.findById(id)
                        .orElseThrow(() -> new UsernameNotFoundException("user not found")));

        return user.get();
    }

    private AdminUser findByUid(String uid) throws UsernameNotFoundException{
        Optional<AdminUser> user =
                Optional.ofNullable(repository.findByUid(uid)
                        .orElseThrow(() -> new UsernameNotFoundException("user not found")));

        return user.get();
    }

    public AdminUserDto login(String username) {

        AdminUser adminUser = findByUid(username);
        adminUser.setPasswordFailCnt(0);
        adminUser.setUserStatus("ACTIVE");
        adminUser.setLoginDate(LocalDateTime.now());

        repository.save(adminUser);

        AdminUserDto adminUserDto = new AdminUserDto();
        BeanUtils.copyProperties(adminUser,adminUserDto);
        return adminUserDto;
    }

    public AdminUserDto passwordFail(String username){
        AdminUser adminUser = findByUid(username);

        if (adminUser.getPasswordFailCnt() < 5) {
            adminUser.setPasswordFailCnt(adminUser.getPasswordFailCnt() + 1);
        }

        if (adminUser.getPasswordFailCnt() == 5) {
            adminUser.setUserStatus("PASSWORD_LOCK");
        }

        repository.save(adminUser);
        AdminUserDto adminUserDto = new AdminUserDto();
        BeanUtils.copyProperties(adminUser,adminUserDto);
        return adminUserDto;
    }

}
