package ru.uip.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.uip.model.JsonAccount;
import ru.uip.model.JsonAccountNumber;
import ru.uip.service.AccountService;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public Flux<JsonAccount> getAccounts() {
        return accountService.accounts();
    }

    @GetMapping(value = "/{accountNumber}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<JsonAccount>> getAccount(
            @PathVariable(name = "accountNumber") String accountNumber) {

        return accountService.findByNumber(accountNumber);
    }

    @PostMapping
    public Mono<JsonAccount> createOrUpdate(@RequestBody @Valid JsonAccount account) {
        return accountService.createOrUpdate(account);
    }

    @DeleteMapping
    public Mono<JsonAccount> delete(@RequestBody @Valid JsonAccountNumber accountNumber) {
        return accountService.delete(accountNumber.getAccountNumber());
    }

}
