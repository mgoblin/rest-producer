package ru.uip.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.uip.model.CreateJsonAccount;
import ru.uip.model.JsonAccount;
import ru.uip.service.AccountService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import java.util.Optional;

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
        return Flux.fromIterable(accountService.accounts());
    }

    @GetMapping(value = "/{accountNumber}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<JsonAccount>> getAccount(
            @NotBlank @PathVariable(name = "accountNumber") String accountNumber) {
        final Optional<JsonAccount> existingAccount = accountService.findByNumber(accountNumber);
        return existingAccount
                .map(jsonAccount -> ResponseEntity.ok(Mono.just(jsonAccount)))
                .orElseGet(() -> ResponseEntity
                        .notFound()
                        .header("Content-type", APPLICATION_JSON_VALUE)
                        .build());
    }

    @PostMapping
    public ResponseEntity<Mono<JsonAccount>> createOrUpdate(@RequestBody @Valid CreateJsonAccount account) {
        final JsonAccount updatedAccount = accountService.createOrUpdate(account);
        return ResponseEntity.ok(Mono.just(updatedAccount));
    }

    @DeleteMapping(value = "/{accountNumber}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<JsonAccount>> delete(@NotBlank @PathVariable(name = "accountNumber") String accountNumber) {
        final Optional<JsonAccount> deletedAccount = accountService.delete(accountNumber);
        return deletedAccount
                .map(jsonAccount -> ResponseEntity.ok(Mono.just(jsonAccount)))
                .orElseGet(() -> ResponseEntity
                        .notFound()
                        .header("Content-type", APPLICATION_JSON_VALUE)
                        .build());
    }

}
