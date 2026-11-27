package com.stockflow.auth.repository;

import java.util.Optional;

import com.stockflow.auth.entity.Role;
import com.stockflow.auth.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

	Optional<Role> findByName(RoleType name);
}
