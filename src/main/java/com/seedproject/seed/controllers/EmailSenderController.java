package com.seedproject.seed.controllers;

import com.seedproject.seed.models.dao.SendReminderDao;
import com.seedproject.seed.models.entities.ResponseMessage;
import com.seedproject.seed.services.EmailSenderService;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.mail.MessagingException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/emailSender")
public class EmailSenderController {
    @Inject
    EmailSenderService emailSenderService;

    @PostMapping(value = "/sendApplicantResponse", consumes = "application/json", produces = "application/json")
    public ResponseMessage sendApplicantResponse() throws MessagingException {
        emailSenderService.sendEmailWithAttachment("nabogaria@gmail.com",
                "This is Email Body with Attachment...",
                "This email has attachment",
                "C:\\Users\\Public\\1612e34d6722a0cf175940120619.jpg");
        return null;
    }

    @PostMapping(value = "/sendReminders", consumes = "application/json", produces = "application/json")
    public ResponseMessage sendReminders(@RequestBody SendReminderDao sendReminderDao) throws MessagingException {
        emailSenderService.sendRemindersToSeeds(sendReminderDao);
        return null;
    }
}
