package com.bank.account.controller;

import com.bank.account.dto.*;
import com.bank.account.service.AccountService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request) {

        AccountResponse response = accountService.createAccount(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getDetails(@PathVariable Long accountId) {
        AccountResponse res = accountService.getAccountDetails(accountId);
        if (res != null) {
            return ResponseEntity.status(HttpStatus.OK).body(res);

        } else {
            return ResponseEntity.notFound().build();

        }
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getALlAcounts() {
        List<AccountResponse> acc = accountService.getInfoForAllAccounts();
        return ResponseEntity.status(HttpStatus.OK).body(acc);
    }

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<TransactionResponse> depositeMoney(@Valid @RequestBody DepositRequest depositRequest, @PathVariable Long accountId) {
        TransactionResponse t = accountService.depositMoneyToAccount(depositRequest, accountId);
        return ResponseEntity.status(HttpStatus.OK).body(t);
    }

    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<TransactionResponse> withdrawAmount(@Valid @RequestBody WithdrawRequest withdrawRequest, @PathVariable Long accountId) {
        TransactionResponse transactionResponse = accountService.withdrawMoney(withdrawRequest, accountId);
        return ResponseEntity.status(HttpStatus.OK).body(transactionResponse);
    }

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<List<TransactionResponse>> transaction(
            @PathVariable Long accountId) {

        List<TransactionResponse> transactionResponses =
                accountService.getTransaction(accountId);

        return ResponseEntity.ok(transactionResponses);
    }
    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transferMoney(
            @Valid @RequestBody TransferRequest request) {

        TransferResponse response = accountService.transferMoney(request);

        return ResponseEntity.ok(response);
    }
    @PatchMapping("/{accountId}/freeze")
    public ResponseEntity<AccountStatusResponse> freezeAccount(
            @PathVariable Long accountId) {

        AccountStatusResponse response =
                accountService.freezeAccount(accountId);

        return ResponseEntity.ok(response);
    }
    @PatchMapping("/{accountId}/unfreeze")
    public ResponseEntity<AccountStatusResponse> unfreezeAccount(
            @PathVariable Long accountId) {

        AccountStatusResponse response =
                accountService.unfreezeAccount(accountId);

        return ResponseEntity.ok(response);
    }
    @PatchMapping("/{accountId}/close")
    public ResponseEntity<AccountStatusResponse> closeAccount(
            @PathVariable Long accountId) {

        AccountStatusResponse response =
                accountService.closeAccount(accountId);

        return ResponseEntity.ok(response);
    }
}