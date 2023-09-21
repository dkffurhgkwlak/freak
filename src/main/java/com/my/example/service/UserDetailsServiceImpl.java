package com.my.example.service;

import com.my.example.domain.AdminUser;
import com.my.example.domain.repo.AdminUserRepository;
import com.my.example.web.dto.AdminUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AdminUserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminUser adminUser = findByUid(username);
        return adminUser;
    }

    private AdminUser findById(Long id) throws UsernameNotFoundException{
        Optional<AdminUser> user =
                Optional.ofNullable(repository.findById(id)
                        .orElseThrow(() -> new UsernameNotFoundException("user not found")));

        AdminUser adminUser = user.get();
        return adminUser;
    }

    private AdminUser findByUid(String uid) throws UsernameNotFoundException{
        Optional<AdminUser> user =
                Optional.ofNullable(repository.findByUid(uid)
                        .orElseThrow(() -> new UsernameNotFoundException("user not found")));

        AdminUser adminUser = user.get();
        return adminUser;
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
