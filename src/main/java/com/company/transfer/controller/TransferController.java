package com.company.transfer.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.transfer.domain.Account;
import com.company.transfer.domain.Transfer;
import com.company.transfer.service.TransferService;

@RestController
public class TransferController {
	private final TransferService transferService;

	@Autowired
	public TransferController(TransferService transferService) {
		this.transferService = transferService;
	}

	@RequestMapping(value = "/accounts", method = RequestMethod.GET)
	public Iterable<Account>accounts() {
		return this.transferService.findAllAccounts();
	}

	@RequestMapping(value = "/transfers", method = RequestMethod.GET)
	public Iterable<Transfer>transfers() {
		return this.transferService.findAllTransfers();
	}

	@RequestMapping(value = "/accounts/new", method = RequestMethod.PUT)
	public Account newAccount(@RequestParam String name, @RequestParam String initialBalance) {
		BigDecimal value = new BigDecimal(initialBalance);
		
		return this.transferService.createNewAccount(name, value);
	}

	@RequestMapping(value = "/accounts/transfer", method = RequestMethod.PUT)
	public Transfer newAccount(@RequestParam Long sourceId, @RequestParam Long destId, @RequestParam String amount) {
		BigDecimal value = new BigDecimal(amount);
		
		return this.transferService.transfer(sourceId,  destId,  value);
	}
}
