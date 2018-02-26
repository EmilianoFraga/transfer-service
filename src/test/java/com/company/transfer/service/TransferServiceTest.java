package com.company.transfer.service;

import static com.company.transfer.testutils.AmountConstants.AMOUNT_10;
import static com.company.transfer.testutils.AmountConstants.AMOUNT_100;
import static com.company.transfer.testutils.AmountConstants.AMOUNT_1000;
import static com.company.transfer.testutils.AmountConstants.AMOUNT_NEGATIVE_ONE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.company.transfer.domain.Account;
import com.company.transfer.domain.Transfer;
import com.company.transfer.repositories.IAccountRepository;
import com.company.transfer.repositories.ITransferRepository;
import com.company.transfer.repositories.impl.AccountMockRepository;
import com.company.transfer.repositories.impl.TransferMockRepository;
import com.company.transfer.service.exception.TransferServiceException;


/**
 * Integration Tests (IT) for TransferService
 * 
 * @author emiliano
 */
public class TransferServiceTest {
    private TransferService transferService;	

    private IAccountRepository accountRepository;

    private ITransferRepository transferRepository;
	
	@Before
	public void setup() {
		this.accountRepository = new AccountMockRepository();
		this.transferRepository = new TransferMockRepository();
		
		this.transferService = new TransferService();
		this.transferService.setAccountRepository(this.accountRepository);
		this.transferService.setTransferRepository(this.transferRepository);
		this.setupMockPersistenceLayer();
	}
	
	@Test
	public void testCreateNewAccount() throws Exception {
		Account createdAccount = this.transferService.createNewAccount("Test", AMOUNT_100);

		assertEquals(createdAccount, this.accountRepository.findOne(createdAccount.getId()));
	}
	
	@Test
	public void testCreateInvalidAccount() throws Exception {
		// null account name
		this.assertCreateNewInvalidAccount(null, AMOUNT_100);

		// empty account name
		this.assertCreateNewInvalidAccount("", AMOUNT_100);

		// negative initial balance
		this.assertCreateNewInvalidAccount("Name", AMOUNT_NEGATIVE_ONE);
	}
	
	@Test
	public void testTransfer() throws Exception {
		BigDecimal balanceOne = this.accountRepository.findOne(1L).getBalance().subtract(AMOUNT_10);
		BigDecimal balanceTwo = this.accountRepository.findOne(2L).getBalance().add(AMOUNT_10);
		
		Transfer transfer = this.transferService.transfer(1L, 2L, AMOUNT_10);
		
		assertEquals(balanceOne, this.accountRepository.findOne(1L).getBalance());
		assertEquals(balanceTwo, this.accountRepository.findOne(2L).getBalance());
		
		assertEquals(1L, transfer.getSourceAccountId());
		assertEquals(2L, transfer.getDestinationAccountId());
		assertEquals(AMOUNT_10, transfer.getAmount());
	}
	
	@Test(expected = TransferServiceException.class)
	public void testInvalidTransferSameAccounts() throws Exception {
		this.transferService.transfer(1L, 1L, AMOUNT_10);
	}
	
	@Test(expected = TransferServiceException.class)
	public void testInvalidTransferNegativeAmount() throws Exception {
		this.transferService.transfer(1L, 2L, AMOUNT_NEGATIVE_ONE);
	}
	
	@Test(expected = TransferServiceException.class)
	public void testInvalidTransferZeroAmount() throws Exception {
		this.transferService.transfer(1L, 2L, BigDecimal.ZERO);
	}

	/*
	 * Helper method to assert TransferServiceException while calling createNewAccount with invalid arguments
	 * @param name
	 * @param initialBalance
	 */
	private void assertCreateNewInvalidAccount(String name, BigDecimal initialBalance) {
		try {
			this.transferService.createNewAccount(name, initialBalance);
			fail("Expected TransferServiceException");
		} catch (TransferServiceException e) {
			// empty block
		}
	}

	private void setupMockPersistenceLayer() {
		this.accountRepository.save(new Account("Account One", AMOUNT_100));
		this.accountRepository.save(new Account("Account Two", AMOUNT_1000));
	}
}
