package com.seedproject.seed.utils;


import com.seedproject.seed.services.EmailSenderService;
import com.seedproject.seed.services.VolunterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@Service
public class ReminderTask extends TimerTask {
    @Inject
    VolunterService volunterService;
    @Inject
    EmailSenderService emailSenderService;


    @Override
    public void run() {
        System.out.println("Email sent at: "
                + LocalDateTime.ofInstant(Instant.ofEpochMilli(scheduledExecutionTime()), ZoneId.systemDefault()));


       Random random = new Random();
        int value = random.ints(1, 7)
                .findFirst()
                .getAsInt();
        try {
            List<String> list = volunterService.getVolunteerEmails();
            System.out.println("list: " +list );

            list.forEach(email->{
                emailSenderService.send_email_inline_image(email);
                System.out.println("The duration of sending the mail will took: " +email );

            });
            TimeUnit.SECONDS.sleep(value);
        }catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
