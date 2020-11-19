	package com.example.sweater.repos;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.sweater.domain.Message;
import com.example.sweater.domain.User;

public interface MessageRepo extends CrudRepository<Message, Long>{
	Page<Message> findAll(Pageable pageable); //pageable - пагинация spring
	
	Page<Message> findByTag(String tag, Pageable pageable);

	@Query("from Message m where m.author = :author")
	Page<Message> findByUser(Pageable pageable, @Param("author") User author);
}
