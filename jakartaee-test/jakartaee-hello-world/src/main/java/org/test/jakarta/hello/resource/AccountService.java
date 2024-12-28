package org.test.jakarta.hello.resource;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.UserTransaction;
import org.test.jakarta.hello.model.Account;


@Stateless
public class AccountService {

    @Inject
    private UserTransaction userTransaction;

    @PersistenceContext
    private EntityManager entityManager;

    public void transferAmount(int fromAccountId, int toAccountId, double amount) throws Exception {
        userTransaction.begin();

        try {

            Account fromAccount = entityManager.find(Account.class, fromAccountId);
            Account toAccount = entityManager.find(Account.class, toAccountId);

            if (fromAccount.getBalance() < amount) {
                throw new IllegalArgumentException("Insufficient balance");
            }


            fromAccount.setBalance(fromAccount.getBalance() - amount);
            toAccount.setBalance(toAccount.getBalance() + amount);


            entityManager.merge(fromAccount);
            entityManager.merge(toAccount);


            userTransaction.commit();
        } catch (Exception e) {
            userTransaction.rollback();
            throw e;
        }
    }
}
