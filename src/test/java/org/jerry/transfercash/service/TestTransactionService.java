package org.jerry.transfercash.service;


import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.jerry.transfercash.model.Account;
import org.jerry.transfercash.model.UserTransaction;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertTrue;


/**
 * Integration testing for RestAPI
 * Test data are initialised from src/test/resources/demo.sql
 */
public class TestTransactionService extends TestService {
    //test transaction related operations in the account

    /*
       TC B1 Positive Category = AccountService
       Scenario: test deposit money to given account number
                 return 200 OK
    */
    @Test
    public void testDeposit() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/accounts/1/deposit/100").build();
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 200);
        String jsonString = EntityUtils.toString(response.getEntity());
        Account afterDeposit = mapper.readValue(jsonString, Account.class);
        //check balance is increased from 100 to 200
        assertTrue(afterDeposit.getBalance().equals(new BigDecimal(200).setScale(4, RoundingMode.HALF_EVEN)));

    }

    /*
      TC B2 Positive Category = AccountService
      Scenario: test withdraw money from account given account number, account has sufficient fund
                return 200 OK
    */
    @Test
    public void testWithDrawSufficientFund() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/accounts/2/withdraw/100").build();
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 200);
        String jsonString = EntityUtils.toString(response.getEntity());
        Account afterDeposit = mapper.readValue(jsonString, Account.class);
        //check balance is decreased from 200 to 100
        assertTrue(afterDeposit.getBalance().equals(new BigDecimal(100).setScale(4, RoundingMode.HALF_EVEN)));

    }

    /*
       TC B3 Negative Category = AccountService
       Scenario: test withdraw money from account given account number, no sufficient fund in account
                 return 500 INTERNAL SERVER ERROR
    */
    @Test
    public void testWithDrawNonSufficientFund() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/accounts/2/withdraw/1000.23456").build();
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        String responseBody = EntityUtils.toString(response.getEntity());
        assertTrue(statusCode == 500);
        assertTrue(responseBody.contains("Not sufficient Fund"));
    }

    /*
       TC B4 Positive Category = AccountService
       Scenario: test transaction from one account to another with source account has sufficient fund
                 return 200 OK
    */
    @Test
    public void testTransactionEnoughFund() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/transactions").build();
        BigDecimal amount = new BigDecimal(10).setScale(4, RoundingMode.HALF_EVEN);
        UserTransaction transaction = new UserTransaction(4L, 5L, amount, "EUR");

        String jsonInString = mapper.writeValueAsString(transaction);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 200);
    }

    /*
        TC B5 Negative Category = AccountService
        Scenario: test transaction from one account to another with source account has no sufficient fund
                  return 500 INTERNAL SERVER ERROR
     */
    @Test
    public void testTransactionNotEnoughFund() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/transactions").build();
        BigDecimal amount = new BigDecimal(100000).setScale(4, RoundingMode.HALF_EVEN);
        UserTransaction transaction = new UserTransaction(4L, 5L, amount, "EUR");

        String jsonInString = mapper.writeValueAsString(transaction);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 500);
    }

    /*
       TC C1 Negative Category = TransactionService
       Scenario: test transaction from one account to another with source/destination account with different currency code
                 return 500 INTERNAL SERVER ERROR
    */
    @Test
    public void testTransactionDifferentCcy() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/transactions").build();
        BigDecimal amount = new BigDecimal(100).setScale(4, RoundingMode.HALF_EVEN);
        UserTransaction transaction = new UserTransaction(3L, 4L, amount, "USD");

        String jsonInString = mapper.writeValueAsString(transaction);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 500);

    }

    @Test
    public void testGetTransactions() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/transactions").build();
        BigDecimal amount = new BigDecimal(10).setScale(4, RoundingMode.HALF_EVEN);
        UserTransaction transaction = new UserTransaction(5L, 4L, amount, "EUR");

        String jsonInString = mapper.writeValueAsString(transaction);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        client.execute(request);
       
        /*Test getAllTransactions()
         * 
         */
        uri = builder.setPath("/transactions").build();
        HttpGet getRequest = new HttpGet(uri);
        HttpResponse response = client.execute(getRequest);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 200);
        
        String jsonString = EntityUtils.toString(response.getEntity());
        UserTransaction[] transactions = mapper.readValue(jsonString, UserTransaction[].class);
        assertTrue(transactions.length > 0);


        
        /*Test getTransactionById()
         * 
         */
        uri = builder.setPath("/transactions/1").build();
        getRequest = new HttpGet(uri);
        response = client.execute(getRequest);
        statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 200);
        
        jsonString = EntityUtils.toString(response.getEntity());
        transaction = null;
        transaction = mapper.readValue(jsonString, UserTransaction.class);
        assertTrue(transaction != null);
    
    /*Test getTransactionByUser()
     * 
     */
    uri = builder.setPath("/transactions/user/4").build();
    getRequest = new HttpGet(uri);
    response = client.execute(getRequest);
    statusCode = response.getStatusLine().getStatusCode();
    assertTrue(statusCode == 200);
    
    jsonString = EntityUtils.toString(response.getEntity());
    transactions = mapper.readValue(jsonString, UserTransaction[].class);
    assertTrue(transactions.length > 0);
    }
}
