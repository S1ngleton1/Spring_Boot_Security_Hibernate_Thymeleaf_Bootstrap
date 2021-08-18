package com.spring.develop.registrationloginspring.config;

import com.spring.develop.registrationloginspring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserService userService;
    @Autowired
    private LoginSuccessHandler loginSuccessHandler;

    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    //Стандартная и наиболее распространенная реализация - это DaoAuthenticationProvider,
    // который извлекает сведения о пользователе из простого пользовательского DAO,
    // доступного только для чтения, - UserDetailsService.
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
//        auth.jdbcAuthentication()
//                .dataSource(dataSource)
//                .passwordEncoder(passwordEncoder())
//                .usersByUsernameQuery("select firstname, email, password from users where email=?")
//                .authoritiesByUsernameQuery("SELECT u.email, r.name from users u inner join user_roles ur on u.id = ur.user_id inner join roles r on r.id = ur.role_id where email='a@gmail.com'");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers(
                "/**",
                "/registration**",
                "/login*",
                "/js/**",
                "/css/**",
                "/img/**",
                "/registration/activate/*").permitAll()
                .antMatchers("/homepage_admin/**").hasAnyAuthority("ROLE_ADMIN")
                .antMatchers("/homepage/**").hasAnyAuthority("ROLE_USER")
                .anyRequest().authenticated()
                .and()
                    .formLogin()
                    .loginPage("/login")
                    .permitAll()
                    .successHandler(loginSuccessHandler) //Choosing a role for redirect
//                    .usernameParameter("username")

                .and()
                    .logout()
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/?logout")
                    .permitAll()
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler());///Auto redirect if forbidden
    }
}
