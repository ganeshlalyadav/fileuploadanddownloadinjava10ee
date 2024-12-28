package org.test.jakarta.hello.resource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.transaction.UserTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.test.jakarta.hello.dao.AccountDAO;
import org.test.jakarta.hello.model.Account;

public class AccountServiceTest {

    @Mock
    private AccountDAO accountDAO;

    @Mock
    private UserTransaction userTransaction;

    @InjectMocks
    private FundTransferServiceBMT accountService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        accountService = new FundTransferServiceBMT(accountDAO,userTransaction);
    }

    @Test
    public void testSuccessfulTransfer() throws Exception {
        Account source = new Account("12345", 500.0);
        Account destination = new Account("67890", 200.0);

        when(accountDAO.findByAccountNumber("12345")).thenReturn(source);
        when(accountDAO.findByAccountNumber("67890")).thenReturn(destination);

        accountService.transferFunds("12345", "67890", 10.0);

        assertEquals(490.0, source.getBalance());
        assertEquals(210.0, destination.getBalance());

        verify(userTransaction).begin();
        verify(accountDAO).update(source);
        verify(accountDAO).update(destination);
        verify(userTransaction).commit();
    }

    @Test
    public void testInsufficientFunds() throws Exception {
        Account source = new Account("12345", 50.0);
        Account destination = new Account("67890", 200.0);

        when(accountDAO.findByAccountNumber("12345")).thenReturn(source);
        when(accountDAO.findByAccountNumber("67890")).thenReturn(destination);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            accountService.transferFunds("12345", "67890", 100.0);
        });

        assertEquals("Transaction failed", exception.getMessage());

        verify(userTransaction).begin();
        verify(userTransaction).rollback();
    }

    @Test
    public void testSourceAccountNotFound() throws Exception {
        when(accountDAO.findByAccountNumber("12345")).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            accountService.transferFunds("12345", "67890", 100.0);
        });

        assertTrue(exception.getMessage().contains("Transaction failed"));

        verify(userTransaction).begin();
        verify(userTransaction).rollback();
    }

    @Test
    public void testDestinationAccountNotFound() throws Exception {
        Account source = new Account("12345", 500.0);
        when(accountDAO.findByAccountNumber("12345")).thenReturn(source);
        when(accountDAO.findByAccountNumber("67890")).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            accountService.transferFunds("12345", "67890", 100.0);
        });

        assertTrue(exception.getMessage().contains("Transaction failed"));

        verify(userTransaction).begin();
        verify(userTransaction).rollback();
    }



    @Test
    public void testZeroTransferAmount() throws Exception {
        Account source = new Account("12345", 500.0);
        Account destination = new Account("67890", 200.0);

        when(accountDAO.findByAccountNumber("12345")).thenReturn(source);
        when(accountDAO.findByAccountNumber("67890")).thenReturn(destination);

        accountService.transferFunds("12345", "67890", 0.0);

        assertEquals(500.0, source.getBalance());
        assertEquals(200.0, destination.getBalance());

        verify(userTransaction).begin();
        verify(accountDAO).update(source);
        verify(accountDAO).update(destination);
        verify(userTransaction).commit();
    }



    @Test
    public void testTransactionFailureDuringUpdate() throws Exception {
        Account source = new Account("12345", 500.0);
        Account destination = new Account("67890", 200.0);

        when(accountDAO.findByAccountNumber("12345")).thenReturn(source);
        when(accountDAO.findByAccountNumber("67890")).thenReturn(destination);

        doThrow(new RuntimeException("Database update failed")).when(accountDAO).update(source);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            accountService.transferFunds("12345", "67890", 100.0);
        });

        assertTrue(exception.getMessage().contains("Transaction failed"));

        verify(userTransaction).begin();
        verify(userTransaction).rollback();
    }


}
