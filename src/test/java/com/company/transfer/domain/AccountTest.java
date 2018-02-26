package com.company.transfer.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;

import static com.company.transfer.testutils.AmountConstants.*;

/**
 * Unit Test class for Account Entity<br>
 * Since this Account is an entity backed by JPA, this class will test only hash code and equals  
 *  
 * @author emiliano
 */
public class AccountTest {
	/**
	 * Test case for hashCode
	 */
	@Test
	public void testHashCode() throws Exception {
		Account accountTestOne = new Account("TestOne", AMOUNT_1000);
		accountTestOne.setId(1);		

		// same as TestOne, but different balance
		Account accountSameTestOne = this.helperAccountCreator(accountTestOne);
		accountSameTestOne.setBalance(AMOUNT_1000000);
		
		Account accountTestTwo = new Account("TestTwo", AMOUNT_1000);
		accountTestTwo.setId(2);
		
		// same as TestTwo, but different balance
		Account accountSameTestTwo = this.helperAccountCreator(accountTestTwo);
		accountSameTestTwo.setBalance(AMOUNT_1000000);
		
		assertNotSame(accountTestOne, accountSameTestOne);
		assertNotSame(accountTestTwo, accountSameTestTwo);

		assertNotEquals(accountTestOne.hashCode(), accountTestTwo.hashCode());
		assertNotEquals(accountTestOne.hashCode(), accountSameTestTwo.hashCode());

		assertTrue(accountTestOne.hashCode() == accountSameTestOne.hashCode());
		assertTrue(accountTestTwo.hashCode() == accountSameTestTwo.hashCode());
	}

	/**
	 * Test case for equals
	 */
	@Test
	public void testEquals() throws Exception {
		Account accountTestOne = new Account("TestOne", AMOUNT_1000);
		accountTestOne.setId(1);		

		// same as TestOne, but different balance
		Account accountSameTestOne = this.helperAccountCreator(accountTestOne);
		accountSameTestOne.setBalance(AMOUNT_1000000);
		
		Account accountTestTwo = new Account("TestTwo", AMOUNT_1000);
		accountTestTwo.setId(2);
		
		// same as TestTwo, but different balance
		Account accountSameTestTwo = this.helperAccountCreator(accountTestTwo);
		accountSameTestTwo.setBalance(AMOUNT_1000000);
		
		assertNotSame(accountTestOne, accountSameTestOne);
		assertNotSame(accountTestTwo, accountSameTestTwo);

		assertNotEquals(accountTestOne, accountTestTwo);
		assertNotEquals(accountTestOne, accountSameTestTwo);

		assertEquals(accountTestOne, accountSameTestOne);
		assertEquals(accountTestTwo, accountSameTestTwo);
	}

	@Test
	public void testHasEnoughBalanceForTransfer() throws Exception {
		// 1000 in balance
		Account accountBalanceOneThousand = new Account("TestOne", AMOUNT_1000);

		assertTrue(accountBalanceOneThousand.hasEnoughBalanceForTransfer(BigDecimal.ZERO));

		assertTrue(accountBalanceOneThousand.hasEnoughBalanceForTransfer(BigDecimal.TEN));

		assertTrue(accountBalanceOneThousand.hasEnoughBalanceForTransfer(AMOUNT_100));
		
		assertTrue(accountBalanceOneThousand.hasEnoughBalanceForTransfer(AMOUNT_1000));

		assertFalse(accountBalanceOneThousand.hasEnoughBalanceForTransfer(AMOUNT_1000.add(BigDecimal.ONE)));

		// 0 in balance
		Account accountBalanceZero = new Account("TestTwo", BigDecimal.ZERO);

		assertTrue(accountBalanceZero.hasEnoughBalanceForTransfer(BigDecimal.ZERO));

		assertFalse(accountBalanceZero.hasEnoughBalanceForTransfer(BigDecimal.ONE));

		assertFalse(accountBalanceZero.hasEnoughBalanceForTransfer(BigDecimal.TEN));

		assertFalse(accountBalanceZero.hasEnoughBalanceForTransfer(AMOUNT_100));
		
		assertFalse(accountBalanceZero.hasEnoughBalanceForTransfer(AMOUNT_1000));

		assertFalse(accountBalanceZero.hasEnoughBalanceForTransfer(AMOUNT_1000.add(BigDecimal.ONE)));

		// 1 in balance
		Account accountBalanceOne = new Account("TestTwo", BigDecimal.ONE);

		assertTrue(accountBalanceOne.hasEnoughBalanceForTransfer(BigDecimal.ZERO));

		assertTrue(accountBalanceOne.hasEnoughBalanceForTransfer(BigDecimal.ONE));

		assertFalse(accountBalanceOne.hasEnoughBalanceForTransfer(BigDecimal.TEN));

		assertFalse(accountBalanceOne.hasEnoughBalanceForTransfer(AMOUNT_100));
		
		assertFalse(accountBalanceOne.hasEnoughBalanceForTransfer(AMOUNT_1000));

		assertFalse(accountBalanceOne.hasEnoughBalanceForTransfer(AMOUNT_1000.add(BigDecimal.ONE)));
	}

	private Account helperAccountCreator(Account other) {
		Account account = new Account(other.getName(), other.getBalance());
		account.setId(other.getId());
		return account;
	}
}
