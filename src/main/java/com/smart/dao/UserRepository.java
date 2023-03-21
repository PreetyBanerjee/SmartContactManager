package com.smart.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import com.smart.entities.User;

@EnableJpaRepositories
public interface UserRepository extends JpaRepository<User, Integer>{
	@Query(value="select * from user u where u.email=?",nativeQuery=true)
	public User getUserByUserName(@Param("e") String username);

}
