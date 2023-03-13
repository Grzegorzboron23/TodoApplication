package io.github.todoapp;

import io.github.todoapp.model.TaskRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import jakarta.validation.Validator;

@SpringBootApplication
public class TodoAppApplication  {

	public static void main(String[] args) {
		SpringApplication.run(TodoAppApplication.class, args);
	}



	@Bean
	Validator validator() {
return new LocalValidatorFactoryBean();
	}
}
