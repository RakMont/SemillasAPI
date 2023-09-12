package com.seedproject.seed;

import com.seedproject.seed.services.EncripttionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@SpringBootApplication
@RestController
public class SeedApplication implements CommandLineRunner {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Inject EncripttionService encripttionService;

	public static void main(String[] args) {
		SpringApplication.run(SeedApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("encrypted " + this.bCryptPasswordEncoder.encode("8815992"));
		System.out.println("encrypted " + this.bCryptPasswordEncoder.encode("denis"));

		System.out.println("ids" +  this.encripttionService.encrypt("3"));
	}
	@GetMapping(path = {"/init"})
	public String init(){
		return "Inicializado";
	}
}
