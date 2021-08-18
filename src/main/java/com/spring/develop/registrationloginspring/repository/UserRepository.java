package com.spring.develop.registrationloginspring.repository;

import com.spring.develop.registrationloginspring.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
//    User findByFirstname(String firstname);
    User findByLogin(String login);

    @Query(value = "select * from users where email=lower(?)",nativeQuery = true)
    User findByEmail(@Param("email") String email);

    @Query(value = "select * from users where email=lower(?) or login = ?",nativeQuery = true)
    User findByEmailOrLogin(@Param("email") String email,@Param("login") String firstname);
    User findUserById(Long id);

    User findByActivationCode(String code);
//    @Transactional
//    @Modifying(clearAutomatically = true)
//    @Query(value = "delete from user_roles where user_id=?",nativeQuery = true)
//    void deleteFromUserRole(@Param("id") Long id);
}
