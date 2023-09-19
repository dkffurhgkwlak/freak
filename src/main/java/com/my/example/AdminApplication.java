package com.my.example;

import com.my.example.domain.AdminUser;
import com.my.example.domain.AdminUserRole;
import com.my.example.domain.EmailContents;
import com.my.example.domain.Page;
import com.my.example.domain.repo.AdminUserRoleRepository;
import com.my.example.domain.repo.AdminUserRepository;
import com.my.example.domain.repo.EmailContentsRepository;
import com.my.example.domain.repo.PagesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class AdminApplication implements CommandLineRunner {
	private static final Logger logger = LoggerFactory.getLogger(AdminApplication.class);

	@Autowired
	private AdminUserRepository userRepository;

	@Autowired
	private AdminUserRoleRepository userRoleRepository;

	@Autowired
	private PagesRepository pagesRepository;

	@Autowired
	private EmailContentsRepository emailContentsRepository;

	public static void main(String[] args) {

		SpringApplication.run(AdminApplication.class, args);
		logger.info("Application started");
	}

	@Override
	public void run(String... args) throws Exception {


//		Page pages2 = Page.builder().subject("공지사항").path("/notice").build();
//		Page pages3 = Page.builder().subject("AMl").path("/aml").build();
//
//		pagesRepository.save(pages2);
//		pagesRepository.save(pages3);
//
//		AdminUserRole adminUserRole1 = AdminUserRole.builder().name("ADMIN").description("MASTER").build();
//		AdminUserRole adminUserRole2 = AdminUserRole.builder().name("USER").description("GENERAL").pages(Collections.singletonList("1")).build();
//		AdminUserRole adminUserRole3 = AdminUserRole.builder().name("AML").description("AML").pages(Collections.singletonList("3")).build();
//
//		userRoleRepository.save(adminUserRole1);
//		userRoleRepository.save(adminUserRole2);
//		userRoleRepository.save(adminUserRole3);
//
//		userRepository.save(AdminUser.builder().roles(Collections.singletonList("ADMIN")).userName("a").userEmail("oneoxygen@gmail.com").uid("admin").password("$2a$10$7hv.qCqrzlbWyH8Y1b6D8OyIlRyf83Ssi7B7tBnTiSSOCOA9IugXe").userStatus("ACTIVE").passwordFailCnt(0).build());
//		userRepository.save(AdminUser.builder().roles(Collections.singletonList("USER")).userName("b").userEmail("oneoxygen@gmail.com").uid("user").password("$2a$10$2/c9RrsJTxyVROo5WV2hEevbdIDNN43Z/v7.ILUcM0LNZIhUoFLwa").userStatus("ACTIVE").passwordFailCnt(0).build());
//		userRepository.save(AdminUser.builder().roles(Collections.singletonList("AML")).userName("c").userEmail("oneoxygen@gmail.com").uid("aml").password("$2a$10$xNysWVftd0IxhNfN9OB9LezADyM/IkIAPT2SGDpg2SKxsqxhUjLkK").userStatus("ACTIVE").passwordFailCnt(0).build());
//
//		emailContentsRepository.save(EmailContents.builder().name("AUTH_OTP").subject("{otp}").content("{otp}").build());
	}
}