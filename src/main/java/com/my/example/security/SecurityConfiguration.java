package com.my.example.security;

import com.my.example.service.AdminUserRoleService;
import com.my.example.service.PagesService;
import com.my.example.service.UserDetailsServiceImpl;
import com.my.example.web.dto.AdminUserRoleDto;
import com.my.example.web.dto.PageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//@ComponentScan("com.my.example")
@EnableGlobalMethodSecurity(securedEnabled = true)
@Slf4j
public class SecurityConfiguration{

    private final UserDetailsServiceImpl userDetailsService;
    private final AdminUserRoleService adminUserRoleService;
    private final PagesService pagesService;
    private final SecurityAccessDeniedHandler accessDeniedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final SecurityAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public AuthenticationManager getAuthenticationManager(HttpSecurity http) throws Exception{
        // Configure AuthenticationManagerBuilder
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(
                        new AdminUserAuthenticationProvider(userDetailsService, passwordEncoder())
        );
        // Get AuthenticationManager
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        return authenticationManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
                    http
                        .csrf().disable()
                        .httpBasic().disable()
                        .formLogin().disable()
                        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                            .and()
                            .exceptionHandling()
                            .accessDeniedHandler(accessDeniedHandler) //403
                            .authenticationEntryPoint(authenticationEntryPoint) //401
                            .and()
                            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
                        setRoles(http);
                        setPermitAll(http);

        return http.build();
    }

    public HttpSecurity setRoles(HttpSecurity http) throws Exception {

        List<AdminUserRoleDto> adminUserRoleDtoList = adminUserRoleService.getAdminUserRolesList();
        List<PageDto> pages = pagesService.getPages();
        Map<String, List<String>> pageHasRoles = new HashMap<String, List<String>>();
        for(PageDto page : pages){
            pageHasRoles.put(page.getId(),new ArrayList<>());
        }
        for(AdminUserRoleDto role : adminUserRoleDtoList){
            for(String id : role.getPages()){
                pageHasRoles.get(id).add(role.getName().replaceAll("ROLE_",""));
            }
        }

        pageHasRoles.forEach((key, value) -> {
            try {
                http.authorizeRequests().antMatchers(pagesService.getPagePath(key)).hasAnyRole(
                        value.toArray(new String[value.size()])
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return http;
    }

    public HttpSecurity setPermitAll(HttpSecurity http) throws Exception {
            http.authorizeRequests().antMatchers(HttpMethod.POST, "/auth").permitAll()
                .antMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api-docs/**").permitAll()
                .antMatchers(HttpMethod.GET, "**/api-docs").permitAll()
                    .anyRequest().authenticated();
        return http;
    }
}
