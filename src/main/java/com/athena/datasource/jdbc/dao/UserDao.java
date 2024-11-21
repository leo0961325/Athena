package com.athena.datasource.jdbc.dao;

import com.athena.datasource.jdbc.po.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {
}
