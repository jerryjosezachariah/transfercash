package org.jerry.transfercash.service;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.jerry.transfercash.dao.DAOFactory;
import org.jerry.transfercash.exception.CustomException;
import org.jerry.transfercash.model.MoneyUtil;
import org.jerry.transfercash.model.User;
import org.jerry.transfercash.model.UserTransaction;

@Path("/transactions")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionService {

	private final DAOFactory daoFactory = DAOFactory.getDAOFactory(DAOFactory.H2);
	private static Logger log = Logger.getLogger(TransactionService.class);
	
	/**
	 * Transfer fund between two accounts.
	 * @param transaction
	 * @return
	 * @throws CustomException
	 */
	@POST
	public Response transferFund(UserTransaction transaction) throws CustomException {

		String currency = transaction.getCurrencyCode();
		if (MoneyUtil.INSTANCE.validateCurrencyCode(currency)) {
			int updateCount = daoFactory.getAccountDAO().transferAccountBalance(transaction);
			if (updateCount == 2) {
				daoFactory.getTransactionDAO().createTransaction(transaction);
				return Response.status(Response.Status.OK).build();
			} else {
				// transaction failed
				throw new WebApplicationException("Transaction failed", Response.Status.BAD_REQUEST);
			}

		} else {
			throw new WebApplicationException("Currency Code Invalid ", Response.Status.BAD_REQUEST);
		}

	}

	 /**
    * Find transaction by Id
    * @param transactionId
    * @return
    * @throws CustomException
    */
   @GET
   @Path("/{transactionId}")
   public UserTransaction getTransactionById(@PathParam("transactionId") long transactionId) throws CustomException {
   	if (log.isDebugEnabled())
           log.debug("Request Received for get Transaction by Id " + transactionId);
       final UserTransaction transaction = daoFactory.getTransactionDAO().getTransactionById(transactionId);
       if (transaction == null) {
           throw new WebApplicationException("Transaction Not Found", Response.Status.NOT_FOUND);
       }
       return transaction;
   }
   
	 /**
    * Find transaction by user
    * @param userId
    * @return
    * @throws CustomException
    */
   @GET
   @Path("/user/{userId}")
   public List<UserTransaction> getTransactionByUser(@PathParam("userId") long userId) throws CustomException {
   	if (log.isDebugEnabled())
           log.debug("Request Received for get Transaction by User " + userId);
       final List<UserTransaction> transactions = daoFactory.getTransactionDAO().getTransactionsByUser(userId);
       if (transactions == null) {
           throw new WebApplicationException("Transaction Not Found", Response.Status.NOT_FOUND);
       }
       return transactions;
   }
    
    
    /**
	 * Find all transactions
	 * @return
	 * @throws CustomException
	 */
    @GET
    public List<UserTransaction> getAllTransactions() throws CustomException {
        return daoFactory.getTransactionDAO().getAllTransactions();
    }
    

}
