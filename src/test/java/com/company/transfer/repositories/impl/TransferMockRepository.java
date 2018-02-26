/**
 * 
 */
package com.company.transfer.repositories.impl;

import java.util.concurrent.atomic.AtomicLong;

import com.company.transfer.domain.Transfer;
import com.company.transfer.repositories.ITransferRepository;

/**
 * @author Emiliano
 *
 */
public class TransferMockRepository extends AbstractMockRepository<Transfer, Long> implements ITransferRepository {

	private AtomicLong atomicId;

	public TransferMockRepository() {
		super();
		this.atomicId = new AtomicLong(1L);
	}

	@Override
	protected <R extends Transfer> R setValueID(R value, Long key) {
		value.setId(key);
		return value;
	}

	@Override
	protected Long createNewKey() {
		return this.atomicId.getAndIncrement();
	}

	@Override
	protected <R extends Transfer> Long getValueKey(R value) {
		return value.getId();
	}
}
