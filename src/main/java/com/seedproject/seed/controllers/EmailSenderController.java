package com.seedproject.seed.controllers;

import com.seedproject.seed.models.dao.SendReminderDao;
import com.seedproject.seed.utils.ReminderTask;
import com.seedproject.seed.models.entities.ResponseMessage;
import com.seedproject.seed.services.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.inject.Inject;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/emailSender")
public class EmailSenderController {
    @Inject
    EmailSenderService emailSenderService;


    @Autowired
    private JavaMailSender mailSender;

    public static final String NEW_USER_ACCOUNT_VERIFICATION = "New User Account Verification";
    public static final String UTF_8_ENCODING = "UTF-8";
    public static final String EMAIL_TEMPLATE = "emailtemplate.html";
    public static final String TEXT_HTML_ENCONDING = "text/html";



    @Value("${spring.mail.username}")
    private String fromEmail;

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

    @PostMapping("/send_email_inline_image")
    public String sendHTMLEmailWithInlineImage(Model model) throws MessagingException, UnsupportedEncodingException {
        try {

            String fullname = "Cielo Sangueza";
            String email = "nabogaria@gmail.com";
            String subject = "Prueba Message";
            String content = "Contenido";


            MimeMessage message  = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            String mailSubject = fullname + "has sent a message";
            String mailContent = "<p><b> Sender Name: </b> " + fullname + "</p>";
            mailContent +="<p><b> Sender E-mail: </b> " + email + "</p>";
            mailContent +="<p><b> Sender Subject: </b> " + subject + "</p>";
            mailContent +="<p><b> Sender Content: </b> " + content + "</p>";
            mailContent +="<hr> <img src='cid:semillaslogo'> /> " ;

            helper.setFrom("forlezmont@gmail.com", " Shopme contact");
            helper.setTo("nabogaria@gmail.com");
            helper.setSubject(mailSubject);
            helper.setText(mailContent, true);

            ClassPathResource resource = new ClassPathResource("/statics/semillaslogo.png");
            helper.addInline("semillaslogo", resource);

            mailSender.send(message);

            return  "message";
        }catch (Exception exception){
            return null;
        }

    }


    @PostMapping("/sendHtmlEmailWithEmbeddedFiles")
    public void sendHtmlEmailWithEmbeddedFiles()throws MessagingException, UnsupportedEncodingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            helper.setFrom(fromEmail);
            helper.setTo("nabogaria@gmail.com");

            /////////////////////////////////////////


            Context context = new Context();
            context.setVariables(Map.of("name", "name", "url", "getVerificationUrl(host, token)"));
           //////////////////////////////////////////////////////////////////////////
            TemplateEngine templateEngine = new TemplateEngine();
            String text = templateEngine.process("<table class=\"body-wrap\"\n" +
                    "       style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; width: 100%; background-color: #f6f6f6; margin: 0;\"\n" +
                    "       bgcolor=\"#f6f6f6\">\n" +
                    "    <tbody>\n" +
                    "    <tr\n" +
                    "            style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; margin: 0;\">\n" +
                    "        <td\n" +
                    "                style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; vertical-align: top; margin: 0;\"\n" +
                    "                valign=\"top\"></td>\n" +
                    "        <td class=\"container\" width=\"600\"\n" +
                    "            style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; vertical-align: top; display: block !important; max-width: 600px !important; clear: both !important; margin: 0 auto;\"\n" +
                    "            valign=\"top\">\n" +
                    "            <div class=\"content\"\n" +
                    "                 style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; max-width: 600px; display: block; margin: 0 auto; padding: 20px;\">\n" +
                    "                <table class=\"main\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"\n" +
                    "                       style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; border-radius: 3px; background-color: #fff; margin: 0; border: 1px solid #e9e9e9;\"\n" +
                    "                       bgcolor=\"#fff\">\n" +
                    "                    <tbody>\n" +
                    "                    <tr\n" +
                    "                            style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; margin: 0;\">\n" +
                    "                        <td class=\"\"\n" +
                    "                            style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 16px; vertical-align: top; color: #fff; font-weight: 500; text-align: center; border-radius: 3px 3px 0 0; background-color: #38414a; margin: 0; padding: 20px;\"\n" +
                    "                            align=\"center\" bgcolor=\"#71b6f9\" valign=\"top\">\n" +
                    "                            <a href=\"#\" style=\"font-size:32px;color:#fff;\"> Your Company Name</a> <br>\n" +
                    "                            <span style=\"margin-top: 10px;display: block;\">Please Verify You Email Address</span>\n" +
                    "                        </td>\n" +
                    "                    </tr>\n" +
                    "                    <tr\n" +
                    "                            style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; margin: 0;\">\n" +
                    "                        <td class=\"content-wrap\"\n" +
                    "                            style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; vertical-align: top; margin: 0; padding: 20px;\"\n" +
                    "                            valign=\"top\">\n" +
                    "                            <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"\n" +
                    "                                   style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; margin: 0;\">\n" +
                    "                                <tbody>\n" +
                    "                                <tr\n" +
                    "                                        style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; margin: 0;\">\n" +
                    "                                    <td class=\"content-block\"\n" +
                    "                                        style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; vertical-align: top; margin: 0; padding: 0 0 20px;\"\n" +
                    "                                        valign=\"top\">\n" +
                    "                                        Hello <strong style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; margin: 0;\" th:text=\"${name}\"></strong>\n" +
                    "                                    </td>\n" +
                    "                                </tr>\n" +
                    "                                <tr\n" +
                    "                                        style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; margin: 0;\">\n" +
                    "                                    <td class=\"content-block\"\n" +
                    "                                        style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; vertical-align: top; margin: 0; padding: 0 0 20px;\"\n" +
                    "                                        valign=\"top\">\n" +
                    "                                        <p>My name is John Smith, and I am very happy to welcome you on board with my company!</p>\n" +
                    "                                        <p>You joined thousands of users who are already skyrocketing their sales with my company by savings at least 60% on all items</p>\n" +
                    "                                        <p>There's just one more tiny step you need to take to achieve all these amazing things. Please click below to verify your new account:</p>\n" +
                    "                                    </td>\n" +
                    "                                </tr>\n" +
                    "                                <tr\n" +
                    "                                        style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; margin: 0;\">\n" +
                    "                                    <td class=\"content-block\"\n" +
                    "                                        style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; vertical-align: top; margin: 0; padding: 0 0 20px;\"\n" +
                    "                                        valign=\"top\">\n" +
                    "                                        <img src=\"cid:image\" style=\"width: 300px; display: block; margin-bottom: 35px;\">\n" +
                    "                                        <a class=\"btn-primary\"\n" +
                    "                                           style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; color: #FFF; text-decoration: none; line-height: 2em; font-weight: bold; text-align: center; cursor: pointer; display: inline-block; border-radius: 5px; text-transform: capitalize; background-color: #f1556c; margin: 0; border-color: #f1556c; border-style: solid; border-width: 8px 16px;\"\n" +
                    "                                           th:href=\"${url}\">\n" +
                    "                                            Verify My Account</a>\n" +
                    "                                    </td>\n" +
                    "                                </tr>\n" +
                    "                                <tr\n" +
                    "                                        style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; margin: 0;\">\n" +
                    "                                    <td class=\"content-block\"\n" +
                    "                                        style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; vertical-align: top; margin: 0; padding: 0 0 20px;\"\n" +
                    "                                        valign=\"top\">\n" +
                    "                                        Thanks for choosing <b>Our</b> Company.\n" +
                    "                                    </td>\n" +
                    "                                </tr>\n" +
                    "                                </tbody>\n" +
                    "                            </table>\n" +
                    "                        </td>\n" +
                    "                    </tr>\n" +
                    "                    </tbody>\n" +
                    "                </table>\n" +
                    "                <div class=\"footer\"\n" +
                    "                     style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; width: 100%; clear: both; color: #999; margin: 0; padding: 20px;\">\n" +
                    "                    <table width=\"100%\"\n" +
                    "                           style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; margin: 0;\">\n" +
                    "                        <tbody>\n" +
                    "                        <tr\n" +
                    "                                style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; margin: 0;\">\n" +
                    "                            <td class=\"aligncenter content-block\"\n" +
                    "                                style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 12px; vertical-align: top; color: #999; text-align: center; margin: 0; padding: 0 0 20px;\"\n" +
                    "                                align=\"center\" valign=\"top\"><a href=\"#\"\n" +
                    "                                                               style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 12px; color: #999; text-decoration: underline; margin: 0;\">Unsubscribe</a>\n" +
                    "                                from these alerts.\n" +
                    "                            </td>\n" +
                    "                        </tr>\n" +
                    "                        </tbody>\n" +
                    "                    </table>\n" +
                    "                </div>\n" +
                    "            </div>\n" +
                    "        </td>\n" +
                    "        <td\n" +
                    "                style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; vertical-align: top; margin: 0;\"\n" +
                    "                valign=\"top\"></td>\n" +
                    "    </tr>\n" +
                    "    </tbody>\n" +
                    "</table>", context);

            // Add HTML email body
            MimeMultipart mimeMultipart = new MimeMultipart("related");
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(text, TEXT_HTML_ENCONDING);
            mimeMultipart.addBodyPart(messageBodyPart);

            // Add images to the email body
            BodyPart imageBodyPart = new MimeBodyPart();
            ClassPathResource resource = new ClassPathResource("/statics/semillaslogo.png");

            ClassPathResource resource1 = new ClassPathResource("/statics/semillaslogo.png");
            helper.addInline("semillaslogo", resource1);

            DataSource dataSource = new FileDataSource(resource.getFile());

            imageBodyPart.setDataHandler(new DataHandler(dataSource));
            imageBodyPart.setHeader("Content-ID", "image");
            mimeMultipart.addBodyPart(imageBodyPart);

            message.setContent(mimeMultipart);
            mailSender.send(message);
        }
        catch (Exception exception) {
            System.out.println(exception.getMessage());
            throw new RuntimeException(exception.getMessage());
        }

    }



}
