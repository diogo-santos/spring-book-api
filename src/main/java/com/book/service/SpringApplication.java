package com.book.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

@EnableSwagger2
@SpringBootApplication
public class SpringApplication {

	@Autowired
	private Environment env;

	public static void main(String[] args) {
		org.springframework.boot.SpringApplication.run(SpringApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				String urls = env.getProperty("crossOriginUrls");
				registry.addMapping("/books/**")
						.allowedOrigins(urls)
						.allowedMethods("*");
			}
		};
	}

	@Bean
	public Docket swaggerConfiguration() {
		return new Docket(SWAGGER_2)
				.select()
				.paths(PathSelectors.ant("/books/**"))
				.apis(RequestHandlerSelectors.basePackage("com.book.service"))
				.build()
				.apiInfo(new ApiInfo("Book service API",
						"Provide access to books with pagination",
						"0.0.1",
						"This is free Licence version",
						new Contact("Diogo Santos", "http://github.com/diogo-santos", ""),
						"API License",
						"https://github.com/diogo-santos",
						Collections.emptyList()));
	}
}