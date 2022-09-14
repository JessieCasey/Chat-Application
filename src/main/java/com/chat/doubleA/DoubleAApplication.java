package com.chat.doubleA;

import com.chat.doubleA.entities.Role;
import com.chat.doubleA.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DoubleAApplication {

	public static void main(String[] args) {
		SpringApplication.run(DoubleAApplication.class, args);
	}
}
