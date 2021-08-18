package com.spring.develop.registrationloginspring.validator;

import com.spring.develop.registrationloginspring.models.Post;
import com.spring.develop.registrationloginspring.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
@Component
public class PostValidator implements Validator {
    @Autowired
    private Environment environment;

    @Override
    public boolean supports(Class<?> aClass) {
        return Post.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Post post = (Post) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "title_error",
                environment.getRequiredProperty("Required"));
        if(post.getTitle().length()>1024){
            errors.rejectValue("title", "title_error", environment.getRequiredProperty("Size.user.field"));
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "shortDescription", "shortDescription_error",
                environment.getRequiredProperty("Required"));
        if(post.getShortDescription().length()>2048){
            errors.rejectValue("shortDescription", "shortDescription_error", environment.getRequiredProperty("Size.user.field"));
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "fullDescription", "fullDescription_error",
                environment.getRequiredProperty("Required"));
        if(post.getShortDescription().length()>4096){
            errors.rejectValue("fullDescription", "fullDescription_error", environment.getRequiredProperty("Size.user.field"));
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "category", "category_error",
                environment.getRequiredProperty("Required"));
        if(post.getPostDate() == null){
            errors.rejectValue("postDate", "postDate_error",  environment.getRequiredProperty("Required"));
        }
    }
}
