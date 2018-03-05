package com.company.transfer.service;

import com.company.transfer.domain.Account;
import com.company.transfer.domain.Transfer;
import com.company.transfer.repositories.IAccountRepository;
import com.company.transfer.repositories.ITransferRepository;
import com.company.transfer.repositories.impl.AccountMockRepository;
import com.company.transfer.repositories.impl.TransferMockRepository;
import static com.company.transfer.testutils.AmountConstants.AMOUNT_10;
import static com.company.transfer.testutils.AmountConstants.AMOUNT_100;
import static com.company.transfer.testutils.AmountConstants.AMOUNT_1000;
import static com.company.transfer.testutils.AmountConstants.AMOUNT_50;
import java.math.BigDecimal;
import java.util.function.Consumer;
import java.util.stream.Stream;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration Tests (IT) for TransferService
 * 
 * @author emiliano
 */
public class ConcurrentTransferServiceTest {
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
	}
	
	@Test
	public void testConcurrentAccountSave() throws InterruptedException {
		Account[][] accounts = {
			new Account[] {
					new Account("A", new BigDecimal(100)),
					new Account("B", new BigDecimal(200)),
					new Account("C", new BigDecimal(300)),
					new Account("D", new BigDecimal(400)),
					new Account("E", new BigDecimal(500))
			},
	
			new Account[] {
					new Account("F", new BigDecimal(100)),
					new Account("G", new BigDecimal(200)),
					new Account("H", new BigDecimal(300)),
					new Account("I", new BigDecimal(400)),
					new Account("J", new BigDecimal(500))
			},
	
			new Account[] {
					new Account("K", new BigDecimal(100)),
					new Account("L", new BigDecimal(200)),
					new Account("M", new BigDecimal(300)),
					new Account("N", new BigDecimal(400)),
					new Account("O", new BigDecimal(500))
			},
		};
		
		// expected quantity of accounts is the sum of our accounts array elements
		long expectedAccountQuantity = accounts[0].length + accounts[1].length + accounts[2].length;

		EntityCreator<?>[] accCreators = {
			new EntityCreator<Account>(accounts[0], this::helperFnCreateAccount),
			new EntityCreator<Account>(accounts[1], this::helperFnCreateAccount),
			new EntityCreator<Account>(accounts[2], this::helperFnCreateAccount),
		};

		// no account was created so far
		assertEquals(0, this.accountRepository.count());
		
		// trigger threads!
		for (EntityCreator<?> a : accCreators) a.batchCreate();

		// wait threads
		while (accCreators[0].isRunning() || accCreators[1].isRunning() || accCreators[2].isRunning()) {
			Thread.sleep(32L);
		}

		// now the repository must have the same amount of expected items
		assertEquals(expectedAccountQuantity, this.accountRepository.count());
		
		// we also sum the created items having the same unique name as the source accounts
		long currentAccountQuantity = 	
				Stream.of(accounts[0]).filter(a -> this.accountRepository.findByName(a.getName()) != null).count() +
				Stream.of(accounts[1]).filter(a -> this.accountRepository.findByName(a.getName()) != null).count() +
				Stream.of(accounts[2]).filter(a -> this.accountRepository.findByName(a.getName()) != null).count();

		// now the repository must have the same amount as the source account arrays
		assertEquals(expectedAccountQuantity, currentAccountQuantity);
	}
	
	
	@Test
	public void testConcurrentTransfers() throws InterruptedException {
		this.transferService.createNewAccount("A", AMOUNT_1000);
		this.transferService.createNewAccount("B", AMOUNT_1000);
		this.transferService.createNewAccount("C", AMOUNT_1000);
		
		Transfer[][] transfers = {
			new Transfer[] {
					new Transfer(1, 2, AMOUNT_100),
					new Transfer(1, 2, AMOUNT_100),
					new Transfer(1, 2, AMOUNT_100),
					new Transfer(1, 2, AMOUNT_100),
					new Transfer(1, 2, AMOUNT_100),
					new Transfer(1, 2, AMOUNT_100),
					new Transfer(1, 2, AMOUNT_100),
					new Transfer(1, 2, AMOUNT_100),
			},
			
			new Transfer[] {
					new Transfer(3, 1, AMOUNT_10),
					new Transfer(3, 1, AMOUNT_10),
					new Transfer(3, 1, AMOUNT_10),
					new Transfer(3, 1, AMOUNT_10),
					new Transfer(3, 1, AMOUNT_10),
					new Transfer(3, 1, AMOUNT_10),
					new Transfer(3, 1, AMOUNT_10),
					new Transfer(3, 1, AMOUNT_10),
			},
			
			new Transfer[] {
					new Transfer(2, 3, AMOUNT_50),
					new Transfer(2, 3, AMOUNT_50),
					new Transfer(2, 3, AMOUNT_50),
					new Transfer(2, 3, AMOUNT_50),
					new Transfer(2, 3, AMOUNT_50),
					new Transfer(2, 3, AMOUNT_50),
					new Transfer(2, 3, AMOUNT_50),
					new Transfer(2, 3, AMOUNT_50),
					new Transfer(2, 3, AMOUNT_50),
					new Transfer(2, 3, AMOUNT_50),
			},
		};
		
		// expected quantity of transfers is the sum of our transfers array elements
		long expectedTransfersQuantity = transfers[0].length + transfers[1].length + transfers[2].length;
		
		// expected balance of accounts is the sum of our transactions amounts
		BigDecimal transferAmount00 = AMOUNT_100.multiply(new BigDecimal(transfers[0].length));
		BigDecimal transferAmount01 = AMOUNT_10.multiply(new BigDecimal(transfers[1].length));
		BigDecimal transferAmount02 = AMOUNT_50.multiply(new BigDecimal(transfers[2].length));
		
		BigDecimal expectedBalanceAccountA = AMOUNT_1000.subtract(transferAmount00).add(transferAmount01);
		BigDecimal expectedBalanceAccountB = AMOUNT_1000.add(transferAmount00).subtract(transferAmount02);
		BigDecimal expectedBalanceAccountC = AMOUNT_1000.subtract(transferAmount01).add(transferAmount02);

		EntityCreator<?>[] transferCreators = {
			new EntityCreator<Transfer>(transfers[0], this::helperFnCreateTransfer),
			new EntityCreator<Transfer>(transfers[1], this::helperFnCreateTransfer),
			new EntityCreator<Transfer>(transfers[2], this::helperFnCreateTransfer)
		};

		// no transfer was created so far
		assertEquals(0, this.transferRepository.count());
		
		// trigger threads!
		for (EntityCreator<?> t : transferCreators) t.batchCreate();

		// wait threads
		while (transferCreators[0].isRunning() || transferCreators[1].isRunning() || transferCreators[2].isRunning()) {
			Thread.sleep(32L);
		}

		// now the repository must have the same amount of expected items
		assertEquals(expectedTransfersQuantity, this.transferRepository.count());
		
		// accounts must have the expected final balances
		assertEquals(expectedBalanceAccountA, this.accountRepository.findByName("A").getBalance());
		assertEquals(expectedBalanceAccountB, this.accountRepository.findByName("B").getBalance());
		assertEquals(expectedBalanceAccountC, this.accountRepository.findByName("C").getBalance());
	}
	
	private void helperFnCreateAccount(Account a) {
		this.transferService.createNewAccount(a.getName(),  a.getBalance());
	}
	
	private void helperFnCreateTransfer(Transfer t) {
		this.transferService.transfer(t.getSourceAccountId(), t.getDestinationAccountId(), t.getAmount());
	}
}


/**
 * @author Emiliano
 *
 * Helper class to create domain model entities using a threaded (concurrent) batch
 *
 * @param <T> The target entity that will be created
 */
class EntityCreator <T extends Object> implements Runnable {
	final private T[] entities;
	final private Thread thread;
	private boolean isRunning;
	final private Consumer<T> fnCreate;
	
	public EntityCreator(T[] entities, Consumer<T> fnCreate) {
		super();
		this.entities = entities;
		this.fnCreate = fnCreate;
		this.thread = new Thread(this);
		this.isRunning = false;
	}
	
	public void batchCreate() {
		if (!this.isRunning) {
			this.thread.start();
    		this.isRunning = true;
		}
	}

	@Override
	public void run() {
		// using parallel stream
		Stream.of(this.entities).parallel().forEach(this.fnCreate::accept);
		
		this.isRunning = false;
	}
	
	public boolean isRunning() {
		return this.isRunning;
	}
}
