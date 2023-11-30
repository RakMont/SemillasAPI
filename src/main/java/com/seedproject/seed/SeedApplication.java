package com.seedproject.seed;

import com.seedproject.seed.services.EncripttionService;
import com.seedproject.seed.utils.ReminderTask;
import com.seedproject.seed.utils.SeedConfirmationTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Timer;

@SpringBootApplication
@RestController
@EnableScheduling
public class SeedApplication implements CommandLineRunner {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Inject EncripttionService encripttionService;

	@Inject ReminderTask reminderTask;

	public static void main(String[] args) {
		SpringApplication.run(SeedApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//long fONCE_PER_DAY = 1000 * 60 * 60 * 24;
		//new Timer().scheduleAtFixedRate(this.reminderTask, 0, fONCE_PER_DAY);

		System.out.println("encrypted " + this.bCryptPasswordEncoder.encode("8815992"));
		System.out.println("encrypted " + this.bCryptPasswordEncoder.encode("$2a$10$1NLXKUxN61quYX8ZMukTF.OsaNFpZa14JLP5GYyMeVzx1TckQywO."));

		System.out.println("ids" +  this.encripttionService.encrypt("3"));
	}

}
