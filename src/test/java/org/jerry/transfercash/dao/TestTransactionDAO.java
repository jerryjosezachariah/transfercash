package org.jerry.transfercash.dao;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;
import org.jerry.transfercash.dao.AccountDAO;
import org.jerry.transfercash.dao.DAOFactory;
import org.jerry.transfercash.dao.H2DAOFactory;
import org.jerry.transfercash.exception.CustomException;
import org.jerry.transfercash.model.Account;
import org.jerry.transfercash.model.UserTransaction;
import org.jerry.transfercash.service.TestService;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

import static junit.framework.TestCase.assertTrue;

public class TestTransactionDAO extends TestService{

	private static Logger log = Logger.getLogger(TestAccountDAO.class);
	private static final DAOFactory h2DaoFactory = DAOFactory.getDAOFactory(DAOFactory.H2);
	private static final int THREADS_COUNT = 100;

	
	@Test
	public void testAccountSingleThreadSameCcyTransfer() throws CustomException {

		final AccountDAO accountDAO = h2DaoFactory.getAccountDAO();

		BigDecimal transferAmount = new BigDecimal(50.01234).setScale(4, RoundingMode.HALF_EVEN);

		UserTransaction transaction = new UserTransaction(4L, 5L, transferAmount, "EUR");

		long startTime = System.currentTimeMillis();

		accountDAO.transferAccountBalance(transaction);
		long endTime = System.currentTimeMillis();

		log.info("TransferAccountBalance finished, time taken: " + (endTime - startTime) + "ms");

		Account accountFrom = accountDAO.getAccountById(4);

		Account accountTo = accountDAO.getAccountById(5);

		log.debug("Account From: " + accountFrom);

		log.debug("Account From: " + accountTo);

		assertTrue(
				accountFrom.getBalance().compareTo(new BigDecimal(149.9877).setScale(4, RoundingMode.HALF_EVEN)) == 0);
		assertTrue(accountTo.getBalance().equals(new BigDecimal(350.0123).setScale(4, RoundingMode.HALF_EVEN)));

	}

	@Test
	public void testAccountMultiThreadedTransfer() throws InterruptedException, CustomException {
		final AccountDAO accountDAO = h2DaoFactory.getAccountDAO();
		// transfer a total of 200USD from 100USD balance in multi-threaded
		// mode, expect half of the transaction fail
		final CountDownLatch latch = new CountDownLatch(THREADS_COUNT);
		for (int i = 0; i < THREADS_COUNT; i++) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						UserTransaction transaction = new UserTransaction( 1L, 2L, new BigDecimal(2).setScale(4, RoundingMode.HALF_EVEN), "USD");
						accountDAO.transferAccountBalance(transaction);
					} catch (Exception e) {
						log.error("Error occurred during transfer ", e);
					} finally {
						latch.countDown();
					}
				}
			}).start();
		}

		latch.await();

		Account accountFrom = accountDAO.getAccountById(1);

		Account accountTo = accountDAO.getAccountById(2);

		log.debug("Account From: " + accountFrom);

		log.debug("Account From: " + accountTo);

		assertTrue(accountFrom.getBalance().equals(new BigDecimal(0).setScale(4, RoundingMode.HALF_EVEN)));
		assertTrue(accountTo.getBalance().equals(new BigDecimal(300).setScale(4, RoundingMode.HALF_EVEN)));

	}

	@Test
	public void testTransferFailOnDBLock() throws CustomException, SQLException {
		final String SQL_LOCK_ACC = "SELECT * FROM Account WHERE AccountId = 5 FOR UPDATE";
		

		BigDecimal sourceAccountOriginalBalance = h2DaoFactory.getAccountDAO().getAccountById(6).getBalance();
		BigDecimal destinationAccountOriginalBalance = h2DaoFactory.getAccountDAO().getAccountById(5).getBalance();
		
		Connection conn = null;
		PreparedStatement lockStmt = null;
		ResultSet rs = null;
		Account fromAccount = null;

		try {
			conn = H2DAOFactory.getConnection();
			conn.setAutoCommit(false);
			// lock account for writing:
			lockStmt = conn.prepareStatement(SQL_LOCK_ACC);
			rs = lockStmt.executeQuery();
			if (rs.next()) {
				fromAccount = new Account(rs.getLong("AccountId"), rs.getString("UserName"),
						rs.getBigDecimal("Balance"), rs.getString("CurrencyCode"));
				if (log.isDebugEnabled())
					log.debug("Locked Account: " + fromAccount);
			}

			if (fromAccount == null) {
				throw new CustomException("Locking error during test, SQL = " + SQL_LOCK_ACC);
			}
			// after lock account 5, try to transfer from account 6 to 5
			// default h2 timeout for acquire lock is 1sec
			BigDecimal transferAmount = new BigDecimal(50).setScale(4, RoundingMode.HALF_EVEN);

			UserTransaction transaction = new UserTransaction(6L, 5L, transferAmount, "EUR");
			h2DaoFactory.getAccountDAO().transferAccountBalance(transaction);
			conn.commit();
		} catch (Exception e) {
			log.error("Exception occurred, initiate a rollback");
			try {
				if (conn != null)
					conn.rollback();
			} catch (SQLException re) {
				log.error("Fail to rollback transaction", re);
			}
		} finally {
			DbUtils.closeQuietly(conn);
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(lockStmt);
		}

		// now inspect account 6 and 5 to verify no transaction occurred
		assertTrue(h2DaoFactory.getAccountDAO().getAccountById(6).getBalance().equals(sourceAccountOriginalBalance));
		assertTrue(h2DaoFactory.getAccountDAO().getAccountById(5).getBalance().equals(destinationAccountOriginalBalance));
	}

}
