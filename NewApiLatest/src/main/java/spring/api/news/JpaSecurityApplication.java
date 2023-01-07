package spring.api.news;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import spring.api.news.model.News;
import spring.api.news.model.User;
import spring.api.news.repository.NewsRepository;
import spring.api.news.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

@SpringBootApplication(scanBasePackages = "spring.api.news")
@EnableAutoConfiguration
public class JpaSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpaSecurityApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(UserRepository users, PasswordEncoder encoder, NewsRepository news) {
		return args -> {
			users.save(new User("user",encoder.encode("password"),"ROLE_JOURNALIST","sd","sw3e"));
			users.save(new User("admin",encoder.encode("password"),"ROLE_ADMIN"));

		};
	}
}
