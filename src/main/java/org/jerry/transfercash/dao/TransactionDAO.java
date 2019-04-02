package org.jerry.transfercash.dao;

import java.math.BigDecimal;
import java.util.List;

import org.jerry.transfercash.exception.CustomException;
import org.jerry.transfercash.model.Account;
import org.jerry.transfercash.model.UserTransaction;


public interface TransactionDAO {

    List<UserTransaction> getAllTransactions() throws CustomException;
    
    List<UserTransaction> getTransactionsByUser(long userId) throws CustomException;
    
    UserTransaction getTransactionById(long transactionId) throws CustomException;
    
    long createTransaction(UserTransaction transaction) throws CustomException;
}
