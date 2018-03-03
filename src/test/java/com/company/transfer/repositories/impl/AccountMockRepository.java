/**
 *
 */
package com.company.transfer.repositories.impl;

import com.company.transfer.domain.Account;
import com.company.transfer.repositories.IAccountRepository;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Emiliano
 *
 */
public class AccountMockRepository extends AbstractMockRepository<Account, Long> implements IAccountRepository {

    final private AtomicLong atomicId;

    public AccountMockRepository() {
        super();
        this.atomicId = new AtomicLong(1L);
    }

    @Override
    protected <R extends Account> R setValueID(R value, Long key) {
        value.setId(key);
        return value;
    }

    @Override
    protected Long createNewKey() {
        return this.atomicId.getAndIncrement();
    }

    @Override
    protected <R extends Account> Long getValueKey(R value) {
        return value.getId();
    }

    @Override
    public Account findByName(String name) {
        try {
            return this.mapRepository.values().stream().filter(a -> a.getName().equals(name)).findFirst().get();
        } catch (NoSuchElementException e) {
            return null;
        }
    }
}
