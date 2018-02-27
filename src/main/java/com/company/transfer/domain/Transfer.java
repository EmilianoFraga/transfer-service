package com.company.transfer.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

import com.company.transfer.domain.exception.EntityCreationException;

@Entity
public class Transfer {
	/**
	 * Timestamp formatter for a transfer Date and Time using yyyyMMdd-HHmmss pattern 
	 */
	public static final DateTimeFormatter TRANSFER_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

	@Id @GeneratedValue(strategy = GenerationType.AUTO) // primary key
    private Long id;
    
    @Version
    private long version;

    @Column(nullable=false)
	private long sourceAccountId;

    @Column(nullable=false)
	private long destinationAccountId;

    @Column(nullable=false)
	private BigDecimal amount;
    
    @Column(nullable=false)
    private String isoTimestamp; 

    protected Transfer() {
    	super();
	}

	public Transfer(long sourceAccountId, long destinationAccountId, BigDecimal transferAmount) {
		this.validateBeforeCreation(sourceAccountId, destinationAccountId, transferAmount);
		
		this.sourceAccountId = sourceAccountId;
		
		this.destinationAccountId = destinationAccountId;
		
		this.amount = transferAmount;
		
		this.isoTimestamp = LocalDateTime.now().format(TRANSFER_TIMESTAMP_FORMATTER);
	}

	public Long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getVersion() {
		return this.version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public long getSourceAccountId() {
		return this.sourceAccountId;
	}

	public void setSourceAccountId(long sourceAccountId) {
		this.sourceAccountId = sourceAccountId;
	}

	public long getDestinationAccountId() {
		return this.destinationAccountId;
	}

	public void setDestinationAccountId(long destinationAccountId) {
		this.destinationAccountId = destinationAccountId;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getTimestamp() {
		return this.isoTimestamp;
	}

	public void setTimestamp(String timestamp) {
		this.isoTimestamp = timestamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transfer other = (Transfer) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/**
	 * Helper method to validate business data before entity creation 
	 */
	private void validateBeforeCreation(long sourceAccountId, long destinationAccountId, BigDecimal transferAmount) throws EntityCreationException {
		try {
			if (sourceAccountId == destinationAccountId) {
				throw new EntityCreationException("Source and Destination account cannot be the same");
			}
			
			transferAmount = Optional.of(transferAmount).get();
			if (transferAmount.compareTo(BigDecimal.ZERO) <= 0) {
				throw new EntityCreationException("Transfer amount must be greater than zero");
			}
		} catch (NullPointerException e) {
			throw new EntityCreationException(e);
		}
	}
}
