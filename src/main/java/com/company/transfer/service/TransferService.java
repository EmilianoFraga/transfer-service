package com.company.transfer.service;

import com.company.transfer.domain.Account;
import com.company.transfer.domain.Transfer;
import com.company.transfer.domain.exception.EntityCreationException;
import com.company.transfer.repositories.IAccountRepository;
import com.company.transfer.repositories.ITransferRepository;
import com.company.transfer.service.exception.TransferServiceException;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferService {

	private IAccountRepository accountRepository;
	private ITransferRepository transferRepository;

	public TransferService() {
		super();
	}

	@Autowired
	public void setAccountRepository(IAccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Autowired
	public void setTransferRepository(ITransferRepository transferRepository) {
		this.transferRepository = transferRepository;
	}
	
	@Transactional
	public synchronized Account createNewAccount(String name, BigDecimal initialBalance) throws TransferServiceException {
		try {
			Account account = new Account(name, initialBalance);
			return this.accountRepository.save(account);
		} catch (DataAccessException e) {
			throw new TransferServiceException("DataAccess exception: " + e.getMessage(), e);
		} catch (EntityCreationException e) {
			throw new TransferServiceException("Invalid entity data: " + e.getMessage(), e);
		}
	}

	@Transactional
	public synchronized Transfer transfer(long sourceAccountId, long destinationAccountId, BigDecimal transferAmount)
			throws TransferServiceException {
		try {
			// transfer amount must be greater than zero
			if (transferAmount.compareTo(BigDecimal.ZERO) < 0) {
				throw new TransferServiceException("Transaction amount must be greater than zero");
			}
			
			// find source account
			Account sourceAccount = Optional.of(this.accountRepository.findOne(sourceAccountId)).get();
			
			// find destination account
			Account destinationAccount = Optional.of(this.accountRepository.findOne(destinationAccountId)).get();
			
			if (!sourceAccount.subtract(transferAmount)) {
				throw new TransferServiceException("Source account hasn't enough balance for transfer.");
			}// if
			
			destinationAccount.add(transferAmount);
			
			sourceAccount = this.accountRepository.save(sourceAccount);
			destinationAccount = this.accountRepository.save(destinationAccount);
			
			Transfer transfer = new Transfer(sourceAccount, destinationAccount, transferAmount);
			return this.transferRepository.save(transfer);
		} catch (EntityCreationException | NullPointerException e) {
			throw new TransferServiceException(e);
		}
	}

	public Iterable<Account> findAllAccounts() {
		return this.accountRepository.findAll();
	}

	public Iterable<Transfer> findAllTransfers() {
		return this.transferRepository.findAll();
	}
}
