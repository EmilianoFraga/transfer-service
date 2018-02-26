package com.company.transfer.repositories;

import org.springframework.data.repository.CrudRepository;

import com.company.transfer.domain.Account;

public interface IAccountRepository extends CrudRepository<Account, Long> {

	Account findByName(String name);
}
