package org.jerry.transfercash.dao.impl;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;
import org.jerry.transfercash.dao.AccountDAO;
import org.jerry.transfercash.dao.H2DAOFactory;
import org.jerry.transfercash.dao.TransactionDAO;
import org.jerry.transfercash.exception.CustomException;
import org.jerry.transfercash.model.Account;
import org.jerry.transfercash.model.MoneyUtil;
import org.jerry.transfercash.model.UserTransaction;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAOImpl implements TransactionDAO {

	private static Logger log = Logger.getLogger(TransactionDAOImpl.class);
	private final static String SQL_GET_TXN_BY_ID = "SELECT * FROM UserTransaction WHERE TransactionId = ? ";
	private final static String SQL_GET_TXN_BY_USER = "SELECT * FROM UserTransaction WHERE FromAccountId = ? OR ToAccountId = ?";
	private final static String SQL_CREATE_TXN = "INSERT INTO UserTransaction (FromAccountId, ToAccountId, Amount, CurrencyCode, TransactionDate) VALUES (?, ?, ?, ?, NOW())";
	private final static String SQL_GET_ALL_TXN = "SELECT * FROM UserTransaction";
	

	/**
	 * Get all transactions.
	 */
	@Override
	public List<UserTransaction> getAllTransactions() throws CustomException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<UserTransaction> allTransactions = new ArrayList<UserTransaction>();
		try {
			conn = H2DAOFactory.getConnection();
			stmt = conn.prepareStatement(SQL_GET_ALL_TXN);
			rs = stmt.executeQuery();
			while (rs.next()) {
				UserTransaction transaction = new UserTransaction(rs.getLong("FromAccountId"), rs.getLong("ToAccountId"),
						rs.getBigDecimal("Amount"), rs.getString("CurrencyCode"), rs.getString("TransactionDate"));
				if (log.isDebugEnabled())
					log.debug("getAllTransactions(): Get  UserTransaction " + transaction);
				allTransactions.add(transaction);
			}
			return allTransactions;
		} catch (SQLException e) {
			throw new CustomException("getAccountById(): Error reading transaction data", e);
		} finally {
			DbUtils.closeQuietly(conn, stmt, rs);
		}
	}

	/**
	 * Get all transactions.
	 */
	@Override
	public List<UserTransaction> getTransactionsByUser(long userId) throws CustomException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<UserTransaction> transactions = new ArrayList<UserTransaction>();
		try {
			conn = H2DAOFactory.getConnection();
			stmt = conn.prepareStatement(SQL_GET_TXN_BY_USER);
			stmt.setLong(1, userId);
			stmt.setLong(2, userId);
			rs = stmt.executeQuery();
			while (rs.next()) {
				UserTransaction transaction = new UserTransaction(rs.getLong("FromAccountId"), rs.getLong("ToAccountId"),
						rs.getBigDecimal("Amount"), rs.getString("CurrencyCode"), rs.getString("TransactionDate"));
				if (log.isDebugEnabled())
					log.debug("getAllTransactions(): Get  UserTransaction " + transaction);
				transactions.add(transaction);
			}
			return transactions;
		} catch (SQLException e) {
			throw new CustomException("getTransactionByUser(): Error reading transaction data", e);
		} finally {
			DbUtils.closeQuietly(conn, stmt, rs);
		}
	}

	/**
	 * Get transaction by id
	 */
	@Override
	public UserTransaction getTransactionById(long transactionId) throws CustomException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		UserTransaction txn = null;
		try {
			conn = H2DAOFactory.getConnection();
			stmt = conn.prepareStatement(SQL_GET_TXN_BY_ID);
			stmt.setLong(1, transactionId);
			rs = stmt.executeQuery();
			if (rs.next()) {
				txn = new UserTransaction(rs.getLong("FromAccountId"), rs.getLong("ToAccountId"),
						rs.getBigDecimal("Amount"), rs.getString("CurrencyCode"), rs.getString("TransactionDate"));
				if (log.isDebugEnabled())
					log.debug("Retrieve Transaction By Id: " + txn);
			}
			return txn;
		} catch (SQLException e) {
			throw new CustomException("getAccountById(): Error reading account data", e);
		} finally {
			DbUtils.closeQuietly(conn, stmt, rs);
		}

	}
	

	/**
	 * Create transaction
	 */
	@Override
	public long createTransaction(UserTransaction transaction) throws CustomException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet generatedKeys = null;
		try {
			conn = H2DAOFactory.getConnection();
			stmt = conn.prepareStatement(SQL_CREATE_TXN);
			stmt.setLong(1, transaction.getFromAccountId());
			stmt.setLong(2, transaction.getToAccountId());
			stmt.setBigDecimal(3, transaction.getAmount());
			stmt.setString(4, transaction.getCurrencyCode());
			int affectedRows = stmt.executeUpdate();
			if (affectedRows == 0) {
				log.error("createTransaction(): Creating transaction failed, no rows affected.");
				throw new CustomException("Transaction Cannot be created");
			}
			generatedKeys = stmt.getGeneratedKeys();
			if (generatedKeys.next()) {
				return generatedKeys.getLong(1);
			} else {
				log.error("Creating transaction failed, no ID obtained.");
				throw new CustomException("Transaction Cannot be created");
			}
		} catch (SQLException e) {
			log.error("Error Inserting Transaction  " + transaction);
			throw new CustomException("createTransaction(): Error creating transaction " + transaction, e);
		} finally {
			DbUtils.closeQuietly(conn, stmt, generatedKeys);
		}
	}
	
}
