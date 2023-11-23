package com.seedproject.seed.services;

import com.seedproject.seed.models.dao.SendReminderDao;
import com.seedproject.seed.utils.ReminderTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;


@Service
public class EmailSenderService{
    @Autowired
    private JavaMailSender mailSender;

    private ReminderTask reminderTask;

    @Autowired
    private TaskScheduler taskScheduler;

    Map<String, ScheduledFuture<?>> jobsMap = new HashMap<>();

    @Value("${spring.mail.username}")
    private String fromEmail;


    public void sendEmailWithAttachment(String toEmail,
                                        String body,
                                        String subject,
                                        String attachment) throws MessagingException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper
                = new MimeMessageHelper(mimeMessage, true);

        mimeMessageHelper.setFrom("spring.email.from@gmail.com");
        mimeMessageHelper.setTo(toEmail);
        mimeMessageHelper.setText(body);
        mimeMessageHelper.setSubject(subject);


        FileSystemResource fileSystem
                = new FileSystemResource(new File(attachment));

        mimeMessageHelper.addAttachment(fileSystem.getFilename(),
                fileSystem);

        mailSender.send(mimeMessage);
        System.out.println("Mail Send...");

    }

    public void sendRemindersToSeeds(SendReminderDao sendReminderDao)throws MessagingException{
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(sendReminderDao.getEmailBody(),"US-ASCII", "html");


        MimeMessageHelper mimeMessageHelper
                = new MimeMessageHelper(mimeMessage, true);

        mimeMessageHelper.setFrom("spring.email.from@gmail.com");
        mimeMessageHelper.setTo(sendReminderDao.getSeedsEmails().get(1));
        mimeMessageHelper.setSubject(sendReminderDao.getEmailSubject());

        mimeMessageHelper.setText("<html><body>" + sendReminderDao.getEmailBody() + "</html></body>", true);
        // mimeMessage.setContent(sendReminderDao.getEmailBody(), "text/html");
        /*FileSystemResource fileSystem
                = new FileSystemResource(new File(attachment));

        mimeMessageHelper.addAttachment(fileSystem.getFilename(),
                fileSystem);
*/
        mailSender.send(mimeMessage);
        System.out.println("Mail Send...");
    }


    public void send_email_inline_image(String destinyEmail){
        try {

            String fullname = "Cielo Sangueza";
            String subject = "Prueba Message";
            String content = "Contenido";


            MimeMessage message  = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            String mailSubject = fullname + "has sent a message";
            String mailContent = "<p><b> Sender Name: </b> " + fullname + "</p>";
            mailContent +="<p><b> Sender E-mail: </b> " + destinyEmail + "</p>";
            mailContent +="<p><b> Sender Subject: </b> " + subject + "</p>";
            mailContent +="<p><b> Sender Content: </b> " + content + "</p>";
            mailContent +="<hr> <img src='cid:semillaslogo'> /> " ;

            helper.setFrom(fromEmail, " Shopme contact");
            helper.setTo(destinyEmail);
            helper.setSubject(mailSubject);
            helper.setText(mailContent, true);

            ClassPathResource resource = new ClassPathResource("/statics/semillaslogo.png");
            helper.addInline("semillaslogo", resource);

            mailSender.send(message);

        }catch (Exception exception){
            throw new RuntimeException(exception.getMessage());
        }

    }
}
