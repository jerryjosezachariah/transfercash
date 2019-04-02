package org.jerry.transfercash.dao;

import java.math.BigDecimal;
import java.util.List;

import org.jerry.transfercash.exception.CustomException;
import org.jerry.transfercash.model.Account;
import org.jerry.transfercash.model.UserTransaction;


public interface AccountDAO {

    List<Account> getAllAccounts() throws CustomException;
    Account getAccountById(long accountId) throws CustomException;
    List<Account> getAccountsByUserName(String userName) throws CustomException;
    long createAccount(Account account) throws CustomException;
    int deleteAccountById(long accountId) throws CustomException;

    int updateAccountBalance(long accountId, BigDecimal deltaAmount) throws CustomException;
    int transferAccountBalance(UserTransaction userTransaction) throws CustomException;
}
