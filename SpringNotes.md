# Spring Notes - Project Version 1.4
## Notes for Project Files
### com.company.transfer.App.java
* Annotated class `@SpringBootApplication`
* Run SpringApplication `SpringApplication.run(App.class, args);`


### com.company.transfer.controller.TransferController
* Annotated class
`
@RestController
`
* Annotated constructor
`
	@Autowired
	public TransferController(TransferService transferService)
`
* Annotated request methods
`
	@RequestMapping(value = "/accounts/new", method = RequestMethod.PUT)
	public Account newAccount(@RequestParam String name, @RequestParam String initialBalance) {
`


### com.company.transfer.service.TransferService
* Annotated class
`
@Service
`
* Annotated setters
`
	@Autowired
	public void setAccountRepository(IAccountRepository accountRepository) {
`
* Annotated Repository Interaction + `synchronized`
`
	@Transactional
	public synchronized Account createNewAccount(String name, BigDecimal initialBalance) throws TransferServiceException {
`

### package com.company.transfer.repositories.IAccountRepository

* Extends Spring Interface CrudeRepository
`
    public interface IAccountRepository extends CrudRepository<Account, Long>
` 
