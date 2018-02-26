package com.company.transfer.repositories;

import org.springframework.data.repository.CrudRepository;

import com.company.transfer.domain.Transfer;

public interface ITransferRepository extends CrudRepository<Transfer, Long> {
}
