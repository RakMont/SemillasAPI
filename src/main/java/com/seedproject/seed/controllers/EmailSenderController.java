package com.seedproject.seed.controllers;

import com.seedproject.seed.models.dao.SendReminderDao;
import com.seedproject.seed.models.dto.VolunterDTO;
import com.seedproject.seed.models.entities.ResponseMessage;
import com.seedproject.seed.models.entities.Volunter;
import com.seedproject.seed.services.EmailSenderService;
import com.seedproject.seed.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import javax.inject.Inject;
import javax.mail.MessagingException;

import java.io.UnsupportedEncodingException;
import java.security.Principal;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/emailSender")
public class EmailSenderController {
    @Inject
    EmailSenderService emailSenderService;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JavaMailSender mailSender;

    /*@PostMapping(value = "/sendReminders", consumes = "application/json", produces = "application/json")
    public ResponseMessage sendReminders(@RequestBody SendReminderDao sendReminderDao) throws MessagingException {
        emailSenderService.sendRemindersToSeeds(sendReminderDao);
        return null;
    }*/

    @PostMapping("/send_email_inline_image")
    public void sendHTMLEmailWithInlineImage(Principal principal) throws MessagingException, UnsupportedEncodingException {
            if (principal!= null){
                Volunter volunter = (Volunter) this.userDetailsService.loadUserByUsername(principal.getName());
                this.emailSenderService.sendSeedConfirmationEmailWithInlineImage(volunter.getUser());
            }
    }

    @PostMapping("/sendHtmlEmailWithEmbeddedFiles")
    public void sendHtmlEmailWithEmbeddedFiles(Principal principal) throws MessagingException{
        if (principal!= null){
            Volunter volunter = (Volunter) this.userDetailsService.loadUserByUsername(principal.getName());
            this.emailSenderService.sendHtmlEmailWithEmbeddedFiles(volunter.getUser());
        }
    }



}
