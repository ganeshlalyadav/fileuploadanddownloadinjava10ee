package org.test.jakarta.hello.resource;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.transaction.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.test.jakarta.hello.dao.AccountDAO;
import org.test.jakarta.hello.model.Account;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FundTransferServiceTest {

    @Mock
    private AccountDAO accountDAO;

    @Mock
    private EntityManager entityManager;

    @Mock
    private EntityTransaction entityTransaction;

    @Mock
    private UserTransaction userTransaction;

    @InjectMocks
    private FundTransferServiceCMT fundTransferServiceCMT;

    @InjectMocks
    private FundTransferServiceBMT fundTransferServiceBMT;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCMTTransferFundsSuccess() {
        Account sourceAccount = new Account();
        sourceAccount.setAccountNumber("12345");
        sourceAccount.setBalance(1000.0);

        Account destinationAccount = new Account();
        destinationAccount.setAccountNumber("67890");
        destinationAccount.setBalance(500.0);

        when(accountDAO.findByAccountNumber("12345")).thenReturn(sourceAccount);
        when(accountDAO.findByAccountNumber("67890")).thenReturn(destinationAccount);



        fundTransferServiceCMT.transferFunds("12345", "67890", 200.0);

        assertEquals(800.0, sourceAccount.getBalance());
        assertEquals(700.0, destinationAccount.getBalance());
        verify(accountDAO, times(2)).update(any(Account.class));
    }

    @Test
    void testCMTTransferFundsInsufficientBalance() {
        Account sourceAccount = new Account();
        sourceAccount.setAccountNumber("12345");
        sourceAccount.setBalance(100.0);

        Account destinationAccount = new Account();
        destinationAccount.setAccountNumber("67890");
        destinationAccount.setBalance(500.0);

        when(accountDAO.findByAccountNumber("12345")).thenReturn(sourceAccount);
        when(accountDAO.findByAccountNumber("67890")).thenReturn(destinationAccount);

        assertThrows(IllegalArgumentException.class, () ->
                fundTransferServiceCMT.transferFunds("12345", "67890", 200.0)
        );

        verify(accountDAO, never()).update(any(Account.class));
    }

    @Test
    void testTransferFailsInsufficientFunds() {

        String fromAccount = "12345";
        String toAccount = "67890";
        Double amount = 1000.0;

        Account source = new Account(fromAccount, 100.0);
        Account destination = new Account(toAccount, 0.0);

        when(accountDAO.findByAccountNumber(fromAccount)).thenReturn(source);
        when(accountDAO.findByAccountNumber(toAccount)).thenReturn(destination);


        assertThrows(RuntimeException.class, () -> fundTransferServiceBMT.transferFunds(fromAccount, toAccount, amount));
    }

    @Test

    void testTransferFundsSuccess() {
        Account sourceAccount = new Account();
        sourceAccount.setAccountNumber("12345");
        sourceAccount.setBalance(1000.0);
        Account destinationAccount = new Account();
        destinationAccount.setAccountNumber("67890");
        destinationAccount.setBalance(500.0);
        when(accountDAO.findByAccountNumber("12345")).thenReturn(sourceAccount);
        when(accountDAO.findByAccountNumber("67890")).thenReturn(destinationAccount);
        fundTransferServiceBMT.transferFunds("12345", "67890", 200.0);
        assertEquals(800.0, sourceAccount.getBalance());
        assertEquals(700.0, destinationAccount.getBalance());
        verify(accountDAO, times(2)).update(any(Account.class));


    }



}
