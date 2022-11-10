package com.seedproject.seed;

import com.seedproject.seed.services.EncripttionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.inject.Inject;

@SpringBootApplication
public class SeedApplication implements CommandLineRunner {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(SeedApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("encrypted " + this.bCryptPasswordEncoder.encode("8815992"));
	}
}
