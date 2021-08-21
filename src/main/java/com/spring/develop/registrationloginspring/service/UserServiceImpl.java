package com.spring.develop.registrationloginspring.service;



import com.spring.develop.registrationloginspring.models.Post;
import com.spring.develop.registrationloginspring.models.Role;
import com.spring.develop.registrationloginspring.models.User;
import com.spring.develop.registrationloginspring.repository.PostRepository;
import com.spring.develop.registrationloginspring.repository.RoleRepository;
import com.spring.develop.registrationloginspring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MailSender mailSender;

    @Override
    public User saveUser(User user) {
        User userEmailFromDb = userRepository.findByEmail(user.getEmail());
        if (userEmailFromDb != null) {
            throw new UsernameNotFoundException("User with the same Email already exists");
        }
        User userLoginFromDb = userRepository.findByLogin(user.getLogin());
        if(userLoginFromDb!= null){
            throw new UsernameNotFoundException("User with the same Login already exists");
        }
        user.setEmail(user.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findRoleById(1L));//Get Role with ID = 1 long
        user.setRoles(roles);
        user.setActivationCode(UUID.randomUUID().toString());

        if (!StringUtils.isEmpty(user.getEmail())) {
            String message = String.format(
                    "Hello, %s %s! \n" +
                            "Welcome to our amazing website Blog. Please, visit next link to confirm registration: http://localhost:8080/registration/activate/%s",
                    user.getFirstname(), user.getLastname(),
                    user.getActivationCode()
            );
            mailSender.send(user.getEmail(), "Activation code", message);
        }
        userRepository.save(user);
        return user;
    }

    @Override
    public Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication;
    }

    @Override
    public boolean activateUser(String code) {
        User user = userRepository.findByActivationCode(code);
        if(user == null){
            return  false;
        }
        user.setActivationCode(null);
        userRepository.save(user);
        return true;
    }

    @Override
    public User updateUser(User updateUser, String email) {
        User newUser = userRepository.findByEmail(email);
        try{
            newUser.setFirstname(updateUser.getFirstname());
            newUser.setLastname(updateUser.getLastname());
            newUser.setAge(updateUser.getAge());
            newUser.setCity(updateUser.getCity());
            newUser.setPhone(updateUser.getPhone());
            newUser.setLogin(updateUser.getLogin());
            newUser.setEmail(updateUser.getEmail().toLowerCase());
            if(!StringUtils.isEmpty(updateUser.getPassword())){
                newUser.setPassword(passwordEncoder.encode(updateUser.getPassword()));
            }
            if(!StringUtils.isEmpty(updateUser.getImage())){
                newUser.setImage(updateUser.getImage());
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        userRepository.save(newUser);
        return newUser;
    }

    @Override
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || authentication instanceof AnonymousAuthenticationToken){
            return null;
        }
        User user = userRepository.findByEmail(authentication.getName());
        return user;
    }


    //Lets you get a user object from a data source and form a
    // UserDetails object from it to be used by the Spring Security context.

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailOrLogin(email,email);
        if(user == null){
            throw new UsernameNotFoundException("Invalid username or password");
        }
        Set<GrantedAuthority> grantedAuthoritySet = new HashSet<>();
        for(Role role: user.getRoles()){
            grantedAuthoritySet.add(new SimpleGrantedAuthority(role.getName()));
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword(),grantedAuthoritySet);
    }
}
