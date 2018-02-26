package com.company.transfer.service;

import static com.company.transfer.testutils.AmountConstants.AMOUNT_10;
import static com.company.transfer.testutils.AmountConstants.AMOUNT_100;
import static com.company.transfer.testutils.AmountConstants.AMOUNT_NEGATIVE_ONE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.company.transfer.domain.Account;
import com.company.transfer.domain.Transfer;
import com.company.transfer.repositories.IAccountRepository;
import com.company.transfer.repositories.ITransferRepository;
import com.company.transfer.service.TransferService;
import com.company.transfer.service.exception.TransferServiceException;


/**
 * Integration Tests (IT) for TransferService
 * 
 * @author emiliano
 */
public class TransferServiceTest {
    private TransferService transferService;	

	@Mock
    private IAccountRepository accountRepository;

	@Mock
    private ITransferRepository transferRepository;

	private Account[] persistedAccounts;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.transferService = new TransferService();
		this.transferService.setAccountRepository(this.accountRepository);
		this.transferService.setTransferRepository(this.transferRepository);

		this.setupMockPersistenceLayer();
	}
	
	@Test
	public void testCreateNewAccount() throws Exception {
		Account persistedAccount = new Account("Test", AMOUNT_100);
		persistedAccount.setId(1L);
		
		when(this.accountRepository.save(any(Account.class))).thenReturn(persistedAccount);
		
		Account createdAccount = this.transferService.createNewAccount("Test", AMOUNT_100);

		assertEquals(persistedAccount, createdAccount);
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
		Transfer transfer = this.transferService.transfer(1L, 2L, AMOUNT_10);
		
		assertEquals(new BigDecimal(90), persistedAccounts[0].getBalance());
		assertEquals(new BigDecimal(110), persistedAccounts[1].getBalance());
		
		assertEquals(persistedAccounts[0].getId(), transfer.getSourceAccountId());
		assertEquals(persistedAccounts[1].getId(), transfer.getDestinationAccountId());
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
		this.persistedAccounts = new Account[]{
				new Account("Test One", AMOUNT_100),
				new Account("Test Two", AMOUNT_100)
		};

		persistedAccounts[0].setId(1L);
		persistedAccounts[1].setId(2L);
		
		when(this.accountRepository.findOne(1L)).thenReturn(persistedAccounts[0]);
		when(this.accountRepository.findOne(2L)).thenReturn(persistedAccounts[1]);
		
		when(this.accountRepository.save(persistedAccounts[0])).then(p -> {persistedAccounts[0] = p.getArgumentAt(0, Account.class); return persistedAccounts[0];});
		when(this.accountRepository.save(persistedAccounts[1])).then(p -> {persistedAccounts[1] = p.getArgumentAt(0, Account.class); return persistedAccounts[1];});

		when(this.transferRepository.save(any(Transfer.class))).then(p -> p.getArgumentAt(0, Transfer.class));
	}
}
