package ru.uip.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.uip.model.EnumAccountStatus;
import ru.uip.model.JsonAccount;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
public class AccountService {
    private final List<JsonAccount> accounts = new CopyOnWriteArrayList<>();

    public AccountService() {
        final JsonAccount accountMike = new JsonAccount(
                "1",
                "MikeAccount",
                1000,
                EnumAccountStatus.ACTIVE);
        accounts.add(accountMike);

        final JsonAccount accountAlex = new JsonAccount(
                "2",
                "AlexAccount",
                200,
                EnumAccountStatus.INACTIVE
        );
        accounts.add(accountAlex);
    }

    public Flux<JsonAccount> accounts() {
        return Flux.fromStream(accounts.stream());
    }

    public ResponseEntity<Mono<JsonAccount>> findByNumber(String number) {
        if (number == null || "".equals(number.trim())) {
            return ResponseEntity.badRequest().build();
        }
        final Optional<JsonAccount> existingAccount = accounts.stream()
                .filter(a -> a.getAccountNumber().equals(number))
                .findFirst();
        return existingAccount.isEmpty() ?
                ResponseEntity
                        .notFound()
                        .header("Content-Type", APPLICATION_JSON_VALUE).
                        build() :
                ResponseEntity
                        .ok()
                        .body(Mono.just(existingAccount.get()));
    }

    private Mono<JsonAccount> findWithMono(String number) {
        if (number == null || "".equals(number.trim()))
            return Mono.empty();
        else
            return Mono.justOrEmpty(accounts.stream()
                .filter(a -> a.getAccountNumber().equals(number)).findFirst());
    }

    public Mono<JsonAccount> createOrUpdate(JsonAccount account) {
        return findWithMono(account.getAccountNumber())
                .switchIfEmpty(Mono.defer(() -> {
                    if (account.getAccountNumber() == null || "".equals(account.getAccountNumber().trim())) {
                        UUID uuid = UUID.randomUUID();
                        account.setAccountNumber(uuid.toString());
                    }
                    accounts.add(account);
                    return Mono.just(account); }))
                .flatMap(a -> {
                    a.setAccountName(account.getAccountName());
                    a.setAccountBalance(account.getAccountBalance());
                    a.setAccountStatus(a.getAccountStatus());
                    return Mono.just(a);
                });
    }

    public Mono<JsonAccount> delete(String accountNumber) {
        return findWithMono(accountNumber)
                .switchIfEmpty(Mono.error(new IllegalArgumentException()))
                .flatMap(a -> {
                    accounts.remove(a);
                    return Mono.just(a);
                });
    }
}
