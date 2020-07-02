package ru.uip.service;

import org.springframework.stereotype.Service;
import ru.uip.model.CreateJsonAccount;
import ru.uip.model.EnumAccountStatus;
import ru.uip.model.JsonAccount;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

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

    public List<JsonAccount> accounts() {
        return Collections.unmodifiableList(accounts);
    }

    public Optional<JsonAccount> findByNumber(String number) {
        return accounts.stream()
                .filter(a -> a.getAccountNumber().equals(number))
                .findFirst();
    }

    private String generateId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    private JsonAccount update(CreateJsonAccount changes, JsonAccount existingAccount) {
        existingAccount.setAccountName(changes.getAccountName());
        existingAccount.setAccountBalance(changes.getAccountBalance());
        existingAccount.setAccountStatus(changes.getAccountStatus());
        return existingAccount;
    }

    private JsonAccount create(CreateJsonAccount account) {
        if (account.getAccountNumber() == null) {
            account.setAccountNumber(generateId());
        }
        return new JsonAccount(
                account.getAccountNumber(),
                account.getAccountName(),
                account.getAccountBalance(),
                account.getAccountStatus()
        );
    }

    public JsonAccount createOrUpdate(CreateJsonAccount account) {
        final Optional<JsonAccount> existingAccount = findByNumber(account.getAccountNumber());
        if (existingAccount.isPresent()) {
            return update(account, existingAccount.get());
        } else {
            final JsonAccount jsonAccount = create(account);
            accounts.add(jsonAccount);
            return jsonAccount;
        }
    }

    public Optional<JsonAccount> delete(String accountNumber) {
        final Optional<JsonAccount> existingAccount = findByNumber(accountNumber);
        existingAccount.ifPresent(accounts::remove);
        return existingAccount;
    }
}
