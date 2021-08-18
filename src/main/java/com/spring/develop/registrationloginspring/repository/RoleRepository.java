package com.spring.develop.registrationloginspring.repository;

import com.spring.develop.registrationloginspring.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Role findRoleById(Long aLong);
}
