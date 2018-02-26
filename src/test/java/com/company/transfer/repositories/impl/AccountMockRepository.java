/**
 * 
 */
package com.company.transfer.repositories.impl;

import java.util.concurrent.atomic.AtomicLong;

import com.company.transfer.domain.Account;
import com.company.transfer.repositories.IAccountRepository;

/**
 * @author Emiliano
 *
 */
public class AccountMockRepository extends AbstractMockRepository<Account, Long> implements IAccountRepository {

	private AtomicLong atomicId;

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
}
