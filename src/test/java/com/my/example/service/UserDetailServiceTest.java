package com.my.example.service;

import com.my.example.domain.AdminUser;
import com.my.example.domain.repo.AdminUserRepository;
import com.my.example.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class UserDetailServiceTest {

    private AdminUserRepository adminUserRepository = Mockito.mock(AdminUserRepository.class);
    private UserDetailsServiceImpl service;

    private JwtTokenProvider jwtTokenProvider;
    private AuthenticationManager authenticationManager;

    private UserDetailsServiceImpl userDetailsService;

    private AdminUserRoleService adminRoleService;

    private AuthOTPService authOTPService;

    @BeforeEach
    public void setUp() throws Exception {
        service = new UserDetailsServiceImpl(adminUserRepository);
        authenticationManager = new AuthenticationManager() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                return new Authentication() {
                    boolean isAuthenticated = true;
                    @Override
                    public Collection<? extends GrantedAuthority> getAuthorities() {
                        return null;
                    }

                    @Override
                    public Object getCredentials() {
                        return null;
                    }

                    @Override
                    public Object getDetails() {
                        return service;
                    }

                    @Override
                    public Object getPrincipal() {
                        return null;
                    }

                    @Override
                    public boolean isAuthenticated() {
                        return isAuthenticated;
                    }

                    @Override
                    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
                        this.isAuthenticated=isAuthenticated;
                    }

                    @Override
                    public String getName() {
                        return null;
                    }
                };
            }
        };
    }

    void test(){
        //given
        given(service.loadUserByUsername("user")).willReturn(
                AdminUser.builder().uid("user")
                        .password("user")
                        .passwordFailCnt(0)
                        .userStatus("LOCK")
                        .userName("user")
                        .roles(Collections.singletonList("TEST"))
                        .build()
        );
    }
}
