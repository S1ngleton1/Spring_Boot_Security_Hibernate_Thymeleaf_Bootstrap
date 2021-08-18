package com.spring.develop.registrationloginspring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {
    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.port}")
    private int port;

//    @Value("${spring.mail.properties.mail.smtp.auth}")
//    private String auth;
//    @Value("$spring.mail.properties.mail.smtp.starttls.enable}")
//    private String enable;
//
//    @Value("${mail.debug}")
//    private String debug;
    @Bean
    public JavaMailSender getMailSender(){
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(host);
        javaMailSender.setPort(port);
        javaMailSender.setUsername(username);
        javaMailSender.setPassword(password);

        Properties properties = javaMailSender.getJavaMailProperties();

        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.debug", "true");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");


//        properties.setProperty("spring.mail.properties.mail.smtp.auth", auth);
//        properties.setProperty("spring.mail.properties.mail.smtp.starttls.enable", enable);
//
//
//        properties.setProperty("mail.debug", debug);

        return javaMailSender;
    }
}
