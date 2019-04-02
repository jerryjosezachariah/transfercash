package org.jerry.transfercash.dao;

import java.util.List;

import org.jerry.transfercash.exception.CustomException;
import org.jerry.transfercash.model.User;

public interface UserDAO {
	
	List<User> getAllUsers() throws CustomException;

	User getUserById(long userId) throws CustomException;

	User getUserByName(String userName) throws CustomException;

	long insertUser(User user) throws CustomException;

	int updateUser(Long userId, User user) throws CustomException;

	int deleteUser(long userId) throws CustomException;

}
