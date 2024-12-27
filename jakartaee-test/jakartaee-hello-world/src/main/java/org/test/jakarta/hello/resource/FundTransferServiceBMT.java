package org.test.jakarta.hello.resource;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.transaction.UserTransaction;
import org.test.jakarta.hello.dao.AccountDAO;
import org.test.jakarta.hello.model.Account;

@Stateless
public class FundTransferServiceBMT {

   /* @Resource
    private UserTransaction userTransaction;*/

    private final AccountDAO accountDAO;
    private final UserTransaction userTransaction;


    public FundTransferServiceBMT(AccountDAO accountDAO, UserTransaction userTransaction) {
        this.accountDAO = accountDAO;
        this.userTransaction = userTransaction;
    }

    public void transferFunds(String fromAccount, String toAccount, Double amount) {
        try {
            userTransaction.begin();

            Account source = accountDAO.findByAccountNumber(fromAccount);
            Account destination = accountDAO.findByAccountNumber(toAccount);

            if (source.getBalance() < amount) {
                throw new IllegalArgumentException("Insufficient funds in the source account.");
            }

            source.setBalance(source.getBalance() - amount);
            destination.setBalance(destination.getBalance() + amount);

            accountDAO.update(source);
            accountDAO.update(destination);

            userTransaction.commit();
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (Exception rollbackEx) {
                throw new RuntimeException("Failed to rollback transaction", rollbackEx);
            }
            throw new RuntimeException("Transaction failed", e);
        }
    }
}
