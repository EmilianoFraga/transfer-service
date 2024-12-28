package com.company.transfer.domain;

import static com.company.transfer.testutils.AmountConstants.AMOUNT_1000;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

public class TransferTest {
	@Mock
	private Account accountOne;

	@Mock
	private Account accountTwo;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		when(this.accountOne.getId()).thenReturn(1L);
		when(this.accountOne.getName()).thenReturn("Acount One");
		
		when(this.accountTwo.getId()).thenReturn(2L);
		when(this.accountTwo.getName()).thenReturn("Acount Two");
	}
	
	@Test
	public void testHashCode() throws Exception {
		Transfer transferTestOne = new Transfer(this.accountOne, this.accountTwo, AMOUNT_1000);
		transferTestOne.setId(1);		

		// same as TestOne, but different balance
		Transfer transferSameTestOne = this.helperTransferCreator(transferTestOne);
		
		Transfer transferTestTwo = new Transfer(this.accountOne, this.accountTwo, AMOUNT_1000);
		transferTestTwo.setId(2);
		
		// same as TestTwo, but different balance
		Transfer transferSameTestTwo = this.helperTransferCreator(transferTestTwo);
		
		assertNotSame(transferTestOne, transferSameTestOne);
		assertNotSame(transferTestTwo, transferSameTestTwo);

		assertNotEquals(transferTestOne.hashCode(), transferTestTwo.hashCode());
		assertNotEquals(transferTestOne.hashCode(), transferSameTestTwo.hashCode());

		assertTrue(transferTestOne.hashCode() == transferSameTestOne.hashCode());
		assertTrue(transferTestTwo.hashCode() == transferSameTestTwo.hashCode());
	}

	@Test
	public void testEquals() throws Exception {
		Transfer transferTestOne = new Transfer(this.accountOne, this.accountTwo, AMOUNT_1000);
		transferTestOne.setId(1);		

		// same as TestOne, but different balance
		Transfer transferSameTestOne = this.helperTransferCreator(transferTestOne);
		
		Transfer transferTestTwo = new Transfer(this.accountOne, this.accountTwo, AMOUNT_1000);
		transferTestTwo.setId(2);
		
		// same as TestTwo, but different balance
		Transfer transferSameTestTwo = this.helperTransferCreator(transferTestTwo);
		
		assertNotSame(transferTestOne, transferSameTestOne);
		assertNotSame(transferTestTwo, transferSameTestTwo);

		assertNotEquals(transferTestOne.hashCode(), transferTestTwo.hashCode());
		assertNotEquals(transferTestOne.hashCode(), transferSameTestTwo.hashCode());

		assertTrue(transferTestOne.hashCode() == transferSameTestOne.hashCode());
		assertTrue(transferTestTwo.hashCode() == transferSameTestTwo.hashCode());
	}

	private Transfer helperTransferCreator(Transfer other) {
		Transfer transfer = new Transfer(other.getSourceAccount(), other.getDestinationAccount(), other.getAmount());
		transfer.setId(other.getId());
		
		return transfer;
	}
}
