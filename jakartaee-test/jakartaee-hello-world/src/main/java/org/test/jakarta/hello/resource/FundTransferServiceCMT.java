package org.test.jakarta.hello.resource;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import org.test.jakarta.hello.dao.AccountDAO;
import org.test.jakarta.hello.model.Account;

@Stateless
public class FundTransferServiceCMT {

    private final AccountDAO accountDAO;

    public FundTransferServiceCMT(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void transferFunds(String fromAccount, String toAccount, Double amount) {
        Account source = accountDAO.findByAccountNumber(fromAccount);
        Account destination = accountDAO.findByAccountNumber(toAccount);

        if (source.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds in the source account.");
        }

        source.setBalance(source.getBalance() - amount);
        destination.setBalance(destination.getBalance() + amount);

        accountDAO.update(source);
        accountDAO.update(destination);
    }
}
