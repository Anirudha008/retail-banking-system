package com.bank.account.service;

import com.bank.account.dto.*;
import com.bank.account.model.Account;
import com.bank.account.model.AccountTransaction;
import com.bank.account.repository.AccountRepository;

import com.bank.account.repository.AccountTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import com.bank.account.dto.TransferRequest;
import com.bank.account.dto.TransferResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountTransactionRepository accountTransactionRepository;

    public AccountService(AccountRepository accountRepository, AccountTransactionRepository accountTransactionRepository) {
        this.accountRepository = accountRepository;
        this.accountTransactionRepository = accountTransactionRepository;
    }

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {

        Account account = new Account();

        account.setCustomerId(request.getCustomerId());
        account.setAccountType(request.getAccountType());
        account.setBalance(request.getInitialDeposit());
        account.setStatus("ACTIVE");

        String accountNumber = generateAccountNumber();

        account.setAccountNumber(accountNumber);

        Account savedAccount = accountRepository.save(account);

        AccountResponse response = new AccountResponse();

        response.setAccountId(savedAccount.getAccountId());
        response.setCustomerId(savedAccount.getCustomerId());
        response.setAccountNumber(savedAccount.getAccountNumber());
        response.setAccountType(savedAccount.getAccountType());
        response.setBalance(savedAccount.getBalance());
        response.setStatus(savedAccount.getStatus());
        response.setMessage("Account created successfully");

        return response;
    }

    private String generateAccountNumber() {

        String accountNumber;

        do {
            accountNumber = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 12)
                    .toUpperCase();

        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }

    public AccountResponse getAccountDetails(Long id){
      Optional<Account> acc = accountRepository.findById(id);
        if (acc.isEmpty()) {
            throw new RuntimeException("Account not found");
        }
        Account account = acc.get();
      AccountResponse accountResponse=new AccountResponse();
      accountResponse.setAccountId(account.getAccountId());
        accountResponse.setCustomerId(account.getCustomerId());
        accountResponse.setAccountNumber(account.getAccountNumber());
        accountResponse.setAccountType(account.getAccountType());
        accountResponse.setBalance(account.getBalance());
        accountResponse.setStatus(account.getStatus());
        accountResponse.setMessage("Account details fetched successfully");

        return accountResponse;
    }
    public List<AccountResponse> getInfoForAllAccounts(){
      List<Account> ac= accountRepository.findAll();
       List<AccountResponse> responses = new ArrayList<>();

       for(Account account:ac){
           AccountResponse response = new AccountResponse();

           response.setAccountId(account.getAccountId());
           response.setCustomerId(account.getCustomerId());
           response.setAccountNumber(account.getAccountNumber());
           response.setAccountType(account.getAccountType());
           response.setBalance(account.getBalance());
           response.setStatus(account.getStatus());
           response.setMessage("Account details fetched successfully");

           responses.add(response);
       }

       return responses;

    }
    @Transactional
    public TransactionResponse depositMoneyToAccount(
            DepositRequest depositRequest,
            Long accountId) {

        Account acc = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        BigDecimal previousBalance = acc.getBalance();

        BigDecimal newBalance =
                previousBalance.add(depositRequest.getAmount());

        // Update account balance
        acc.setBalance(newBalance);
        accountRepository.save(acc);

        // Save transaction
        AccountTransaction tx = new AccountTransaction();

        tx.setAccountId(acc.getAccountId());
        tx.setTransactionType("DEPOSIT");
        tx.setAmount(depositRequest.getAmount());
        tx.setPreviousBalance(previousBalance);
        tx.setNewBalance(newBalance);
        tx.setStatus("SUCCESS");
        tx.setDescription(depositRequest.getDescription());
        tx.setTransactionDate(java.time.LocalDateTime.now());

        accountTransactionRepository.save(tx);

        // Build response
        TransactionResponse transactionResponse =
                new TransactionResponse();

        transactionResponse.setAccountId(acc.getAccountId());
        transactionResponse.setAccountNumber(acc.getAccountNumber());
        transactionResponse.setTransactionType("DEPOSIT");
        transactionResponse.setAmount(depositRequest.getAmount());
        transactionResponse.setPreviousBalance(previousBalance);
        transactionResponse.setNewBalance(newBalance);
        transactionResponse.setStatus("SUCCESS");
        transactionResponse.setMessage("Deposit successful");

        return transactionResponse;
    }
    @Transactional
    public TransactionResponse withdrawMoney(
            WithdrawRequest withdrawRequest,
            Long accountId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        BigDecimal previousBalance = account.getBalance();

        if (previousBalance.compareTo(withdrawRequest.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        BigDecimal newBalance =
                previousBalance.subtract(withdrawRequest.getAmount());

        // Update account balance
        account.setBalance(newBalance);
        accountRepository.save(account);

        // Save transaction
        AccountTransaction tx = new AccountTransaction();

        tx.setAccountId(account.getAccountId());
        tx.setTransactionType("WITHDRAW");
        tx.setAmount(withdrawRequest.getAmount());
        tx.setPreviousBalance(previousBalance);
        tx.setNewBalance(newBalance);
        tx.setStatus("SUCCESS");
        tx.setDescription(withdrawRequest.getDescription());
        tx.setTransactionDate(java.time.LocalDateTime.now());

        accountTransactionRepository.save(tx);

        // Build response
        TransactionResponse transactionResponse =
                new TransactionResponse();

        transactionResponse.setAccountId(account.getAccountId());
        transactionResponse.setAccountNumber(account.getAccountNumber());
        transactionResponse.setTransactionType("WITHDRAW");
        transactionResponse.setAmount(withdrawRequest.getAmount());
        transactionResponse.setPreviousBalance(previousBalance);
        transactionResponse.setNewBalance(newBalance);
        transactionResponse.setStatus("SUCCESS");
        transactionResponse.setMessage("Withdrawal successful");

        return transactionResponse;
    }
    public List<TransactionResponse> getTransaction(Long accountId) {

        List<AccountTransaction> transactions =
                accountTransactionRepository.findByAccountId(accountId);

        List<TransactionResponse> responses = new ArrayList<>();

        for (AccountTransaction transaction : transactions) {

            TransactionResponse response = new TransactionResponse();

            response.setAccountId(transaction.getAccountId());
            response.setTransactionType(transaction.getTransactionType());
            response.setAmount(transaction.getAmount());
            response.setPreviousBalance(transaction.getPreviousBalance());
            response.setNewBalance(transaction.getNewBalance());
            response.setStatus(transaction.getStatus());

            responses.add(response);
        }

        return responses;
    }
    @Transactional
    public TransferResponse transferMoney(TransferRequest request) {

        Long sourceId = request.getSourceAccountId();
        Long targetId = request.getTargetAccountId();

        // 1. Validate that source and target are different
        if (sourceId.equals(targetId)) {
            throw new RuntimeException(
                    "Source and target accounts cannot be the same"
            );
        }

        // 2. Lock accounts in ascending ID order to reduce deadlocks
        Account firstAccount;
        Account secondAccount;

        if (sourceId < targetId) {
            firstAccount = accountRepository.findByAccountIdForUpdate(sourceId)
                    .orElseThrow(() -> new RuntimeException(
                            "Source account not found"
                    ));

            secondAccount = accountRepository.findByAccountIdForUpdate(targetId)
                    .orElseThrow(() -> new RuntimeException(
                            "Target account not found"
                    ));
        } else {
            secondAccount = accountRepository.findByAccountIdForUpdate(targetId)
                    .orElseThrow(() -> new RuntimeException(
                            "Target account not found"
                    ));

            firstAccount = accountRepository.findByAccountIdForUpdate(sourceId)
                    .orElseThrow(() -> new RuntimeException(
                            "Source account not found"
                    ));
        }

        // 3. Identify the source and target accounts
        Account sourceAccount =
                sourceId.equals(firstAccount.getAccountId())
                        ? firstAccount : secondAccount;

        Account targetAccount =
                targetId.equals(firstAccount.getAccountId())
                        ? firstAccount : secondAccount;

        // 4. Validate account status
        if (!"ACTIVE".equals(sourceAccount.getStatus())) {
            throw new RuntimeException("Source account is not active");
        }

        if (!"ACTIVE".equals(targetAccount.getStatus())) {
            throw new RuntimeException("Target account is not active");
        }

        // 5. Validate amount and source balance
        BigDecimal amount = request.getAmount();

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Transfer amount must be greater than zero");
        }

        BigDecimal sourcePreviousBalance = sourceAccount.getBalance();
        BigDecimal targetPreviousBalance = targetAccount.getBalance();

        if (sourcePreviousBalance.compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // 6. Calculate new balances
        BigDecimal sourceNewBalance =
                sourcePreviousBalance.subtract(amount);

        BigDecimal targetNewBalance =
                targetPreviousBalance.add(amount);

        // 7. Update both account balances
        sourceAccount.setBalance(sourceNewBalance);
        targetAccount.setBalance(targetNewBalance);

        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);

        // 8. Create debit transaction
        AccountTransaction debitTransaction = new AccountTransaction();

        debitTransaction.setAccountId(sourceAccount.getAccountId());
        debitTransaction.setTransactionType("TRANSFER_DEBIT");
        debitTransaction.setAmount(amount);
        debitTransaction.setPreviousBalance(sourcePreviousBalance);
        debitTransaction.setNewBalance(sourceNewBalance);
        debitTransaction.setStatus("SUCCESS");
        debitTransaction.setDescription(request.getDescription());
        debitTransaction.setTransactionDate(LocalDateTime.now());

        AccountTransaction savedDebit =
                accountTransactionRepository.save(debitTransaction);

        // 9. Create credit transaction
        AccountTransaction creditTransaction = new AccountTransaction();

        creditTransaction.setAccountId(targetAccount.getAccountId());
        creditTransaction.setTransactionType("TRANSFER_CREDIT");
        creditTransaction.setAmount(amount);
        creditTransaction.setPreviousBalance(targetPreviousBalance);
        creditTransaction.setNewBalance(targetNewBalance);
        creditTransaction.setStatus("SUCCESS");
        creditTransaction.setDescription(request.getDescription());
        creditTransaction.setTransactionDate(LocalDateTime.now());

        accountTransactionRepository.save(creditTransaction);

        // 10. Build response
        TransferResponse response = new TransferResponse();

        // Debit transaction ID is used as the transfer ID for this version
        response.setTransferId(savedDebit.getTransactionId());

        response.setSourceAccountId(sourceAccount.getAccountId());
        response.setTargetAccountId(targetAccount.getAccountId());

        response.setAmount(amount);

        response.setSourcePreviousBalance(sourcePreviousBalance);
        response.setSourceNewBalance(sourceNewBalance);

        response.setTargetPreviousBalance(targetPreviousBalance);
        response.setTargetNewBalance(targetNewBalance);

        response.setStatus("SUCCESS");
        response.setMessage("Fund transfer completed successfully");

        return response;
    }
    @Transactional
    public AccountStatusResponse freezeAccount(Long accountId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new RuntimeException("Account not found"));

        if ("CLOSED".equals(account.getStatus())) {
            throw new RuntimeException(
                    "Closed account cannot be frozen"
            );
        }

        if ("FROZEN".equals(account.getStatus())) {
            throw new RuntimeException(
                    "Account is already frozen"
            );
        }

        account.setStatus("FROZEN");

        Account savedAccount = accountRepository.save(account);

        AccountStatusResponse response = new AccountStatusResponse();

        response.setAccountId(savedAccount.getAccountId());
        response.setStatus(savedAccount.getStatus());
        response.setMessage("Account frozen successfully");

        return response;
    }
    @Transactional
    public AccountStatusResponse unfreezeAccount(Long accountId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new RuntimeException("Account not found"));

        if ("CLOSED".equals(account.getStatus())) {
            throw new RuntimeException(
                    "Closed account cannot be unfrozen"
            );
        }

        if ("ACTIVE".equals(account.getStatus())) {
            throw new RuntimeException(
                    "Account is already active"
            );
        }

        account.setStatus("ACTIVE");

        Account savedAccount = accountRepository.save(account);

        AccountStatusResponse response = new AccountStatusResponse();
        response.setAccountId(savedAccount.getAccountId());
        response.setStatus(savedAccount.getStatus());
        response.setMessage("Account unfrozen successfully");

        return response;
    }
    @Transactional
    public AccountStatusResponse closeAccount(Long accountId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new RuntimeException("Account not found"));

        if ("CLOSED".equals(account.getStatus())) {
            throw new RuntimeException("Account is already closed");
        }

        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new RuntimeException(
                    "Account cannot be closed because balance is not zero"
            );
        }

        account.setStatus("CLOSED");

        Account savedAccount = accountRepository.save(account);

        AccountStatusResponse response = new AccountStatusResponse();
        response.setAccountId(savedAccount.getAccountId());
        response.setStatus(savedAccount.getStatus());
        response.setMessage("Account closed successfully");

        return response;
    }
}