package com.seedproject.seed.services;

import com.seedproject.seed.models.entities.Contributor;
import com.seedproject.seed.models.entities.User;
import com.seedproject.seed.utils.ReminderTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.ResourceUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.nio.file.Files;
import java.util.Map;


@Service
public class EmailSenderService{
    @Autowired
    private JavaMailSender mailSender;

    private ReminderTask reminderTask;

    @Autowired
    private TaskScheduler taskScheduler;

    public String NEW_USER_ACCOUNT_VERIFICATION = "New User Account Verification";
    public String UTF_8_ENCODING = "UTF-8";
    public String EMAIL_TEMPLATE = "emailtemplate.html";
    public String TEXT_HTML_ENCONDING = "text/html";


    @Value("${spring.mail.username}")
    private String fromEmail;


    public void sendSeedConfirmationEmailWithInlineImage(User contributor){
        try {

            String fullname = "Programa Semillas";
            String email = contributor.getEmail();
            MimeMessage message  = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            Context context = new Context();
            context.setVariables(Map.of("name", contributor.getFullName(),
                    "url", "getVerificationUrl(host, token)"));

            File file = ResourceUtils.getFile("src/main/resources/templates/confirmationtemplate.html");
            String htmlTemplate = new String(Files.readAllBytes(file.toPath()));

            TemplateEngine templateEngine = new TemplateEngine();
            htmlTemplate = templateEngine.process(htmlTemplate, context);

            String mailSubject = fullname + " Te ha enviado un mensaje";
            helper.setFrom(fromEmail, "Mensaje de Confirmación.");
            helper.setTo(email);
            helper.setSubject(mailSubject);
            helper.setText(htmlTemplate, true);

            ClassPathResource resource = new ClassPathResource("/statics/logo.png");
            helper.addInline("semillaslogo", resource);

            mailSender.send(message);
        }catch (Exception exception){
            throw new RuntimeException(exception.getMessage());
        }
    }


    public void sendHtmlEmailWithEmbeddedFiles(User user){
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
            File file = ResourceUtils.getFile("src/main/resources/templates/emailtemplate.html");
            String htmlTemplate = new String(Files.readAllBytes(file.toPath()));

            TemplateEngine templateEngine = new TemplateEngine();
            String text = templateEngine.process(htmlTemplate, context);

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
    /*public void sendEmailWithAttachment(String toEmail,
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
*/

}
