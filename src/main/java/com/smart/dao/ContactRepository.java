package com.smart.dao;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;

@EnableJpaRepositories
public interface ContactRepository extends JpaRepository<Contact, Integer> {
	//pagination...
	@Query(value="select * from Contact c where c.user_id=?",nativeQuery = true)
	//currentPage-page
	//Contact Per page- 5
	public List<Contact> findContactsByUser(@Param("user_id") int userId);

}
