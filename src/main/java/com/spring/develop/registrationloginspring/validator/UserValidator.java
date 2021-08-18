package com.spring.develop.registrationloginspring.validator;

import com.spring.develop.registrationloginspring.models.User;
import com.spring.develop.registrationloginspring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UserValidator implements Validator {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Environment environment;
    private static Pattern VALID_PHONE_NUMBER =
            Pattern.compile("[0-9]{3}-[0-9]{3}-[0-9]{4}");

    private boolean isValidPhoneNumber(String s) {
        Matcher m = VALID_PHONE_NUMBER.matcher(s);
        return m.matches();
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        User user = (User) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstname", "firstname_error",
                environment.getRequiredProperty("Required"));
        if(user.getFirstname().length()>255){
            errors.rejectValue("firstname", "firstname_error", environment.getRequiredProperty("Size.user.field"));
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastname", "lastname_error",
                environment.getRequiredProperty("Required"));
        if(user.getLastname().length()>255){
            errors.rejectValue("lastname", "lastname_error", environment.getRequiredProperty("Size.user.field"));
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "age", "age_error",
                environment.getRequiredProperty("Required"));
        if (user.getAge() <= 0) {
            errors.rejectValue("age", "age_error", environment.getRequiredProperty("Invalid.user.age"));
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phone", "phone_error",
                environment.getRequiredProperty("Required"));
        if (!isValidPhoneNumber(user.getPhone())) {
            errors.rejectValue("phone", "phone_error", environment.getRequiredProperty("Invalid.phone.number.format"));
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "city", "city_error",
                environment.getRequiredProperty("Required"));
        if(user.getCity().length()>255){
            errors.rejectValue("city", "city_error", environment.getRequiredProperty("Size.user.field"));
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            User previousUser = userRepository.findByEmail(authentication.getName());
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "login", "login_error",
                    environment.getRequiredProperty("Required"));
            if (user.getLogin().length() < 5 || user.getLogin().length() > 30) {
                errors.rejectValue("login", "login_error", environment.getRequiredProperty("Size.user.login"));
            }
            if(!user.getLogin().equals(previousUser.getLogin())){
                if (userRepository.findByLogin(user.getLogin()) != null) {
                    errors.rejectValue("login", "login_error", environment.getRequiredProperty("Duplicate.user.login"));
                }
            }
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "email_error",
                    environment.getRequiredProperty("Required"));
            if (!user.getEmail().contains("@")) {
                errors.rejectValue("email", "email_error", environment.getRequiredProperty("Valid.user.email"));
            }
            if(!user.getEmail().equals(previousUser.getEmail())){
                if (userRepository.findByEmail(user.getEmail()) != null) {
                    errors.rejectValue("email", "email_error", environment.getRequiredProperty("Duplicate.user.email"));
                }
            }
//            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstname", "firstname_error",
//                    environment.getRequiredProperty("Required"));
//            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastname", "lastname_error",
//                    environment.getRequiredProperty("Required"));
//            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "age", "age_error",
//                    environment.getRequiredProperty("Required"));
//            if (user.getAge() <= 0) {
//                errors.rejectValue("age", "age_error", environment.getRequiredProperty("Invalid.user.age"));
//            }
//            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phone", "phone_error",
//                    environment.getRequiredProperty("Required"));
//            if (!isValidPhoneNumber(user.getPhone())) {
//                errors.rejectValue("phone", "phone_error", environment.getRequiredProperty("Invalid.phone.number.format"));
//            }
//            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "city", "city_error",
//                    environment.getRequiredProperty("Required"));
            if(!StringUtils.isEmpty(user.getPassword())) {
                if (user.getPassword().length() < 5) {
                    errors.rejectValue("password", "password_error", environment.getRequiredProperty("Size.user.password"));
                }
                if (!user.getConfirmPassword().equals(user.getPassword())) {
                    errors.rejectValue("confirmPassword", "confirmPassword_error", environment.getRequiredProperty("Different.user.password"));
                }
            }

        } else {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "login", "login_error",
                    environment.getRequiredProperty("Required"));
            if (user.getLogin().length() < 5 || user.getLogin().length() > 30) {
                errors.rejectValue("login", "login_error", environment.getRequiredProperty("Size.user.login"));
            }
            if (userRepository.findByLogin(user.getLogin()) != null) {
                errors.rejectValue("login", "login_error", environment.getRequiredProperty("Duplicate.user.login"));
            }
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "email_error",
                    environment.getRequiredProperty("Required"));
            if (!user.getEmail().contains("@")) {
                errors.rejectValue("email", "email_error", environment.getRequiredProperty("Valid.user.email"));
            }
            if (userRepository.findByEmail(user.getEmail()) != null) {
                errors.rejectValue("email", "email_error", environment.getRequiredProperty("Duplicate.user.email"));
            }
//            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstname", "firstname_error",
//                    environment.getRequiredProperty("Required"));
//            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastname", "lastname_error",
//                    environment.getRequiredProperty("Required"));
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "image", "image_error",
                    environment.getRequiredProperty("Required"));
            if (!(user.getImage().contains(".png") || user.getImage().contains(".jpg"))) {
                errors.rejectValue("image", "user_image_error", environment.getRequiredProperty("Upload.invalid.file.type"));
            }
//            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "age", "age_error",
//                    environment.getRequiredProperty("Required"));
//            if (user.getAge() <= 0) {
//                errors.rejectValue("age", "age_error", environment.getRequiredProperty("Invalid.user.age"));
//            }
//            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phone", "phone_error",
//                    environment.getRequiredProperty("Required"));
//            if (!isValidPhoneNumber(user.getPhone())) {
//                errors.rejectValue("phone", "phone_error", environment.getRequiredProperty("Invalid.phone.number.format"));
//            }
//            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "city", "city_error",
//                    environment.getRequiredProperty("Required"));
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "password_error",
                    environment.getRequiredProperty("Required"));
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "confirmPassword_error",
                    environment.getRequiredProperty("Required"));
            if (user.getPassword().length() < 5) {
                errors.rejectValue("password", "password_error", environment.getRequiredProperty("Size.user.password"));
            }
            if (!user.getConfirmPassword().equals(user.getPassword())) {
                errors.rejectValue("confirmPassword", "confirmPassword_error", environment.getRequiredProperty("Different.user.password"));
            }
        }
    }
}
