package org.test.jakarta.hello.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.test.jakarta.hello.model.Account;

public class AccountDAO {
    @PersistenceContext
    private EntityManager entityManager;

    public Account findByAccountNumber(String accountNumber) {
        return entityManager.createQuery(
                        "SELECT a FROM Account a WHERE a.accountNumber = :accountNumber", Account.class)
                .setParameter("accountNumber", accountNumber)
                .getSingleResult();
    }

    public void update(Account account) {
        entityManager.merge(account);
    }
}
