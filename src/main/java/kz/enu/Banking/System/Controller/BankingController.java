package kz.enu.Banking.System.Controller;
import kz.enu.Banking.System.Models.Account;
import kz.enu.Banking.System.Models.Bank;
import kz.enu.Banking.System.Models.Customer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
@RestController
@RequestMapping("/bank-system")
public class BankingController {
    private final Map<Long, Bank> banks = new ConcurrentHashMap<>();
    private final Map<Long, Customer> customers = new ConcurrentHashMap<>();
    private final Map<Long, Account> accounts = new ConcurrentHashMap<>();
    private final AtomicLong bankSeq = new AtomicLong(1);
    private final AtomicLong customerSeq = new AtomicLong(1);
    private final AtomicLong accountSeq = new AtomicLong(1);

    //test
    public BankingController() {
        long bankId1 = seedBank("Kaspi Bank", "KASPKZ44");
        long customerId1 = seedCustomer(bankId1, "Ivan Grozniy", "+770242567");
        seedAccount(customerId1, "KZ111", 125000.00, "KZT");

        long bankId2 = seedBank("Halyk Bank", "HSBKKZKX");
        long customerId2 = seedCustomer(bankId2, "Sapar", "+7704230001");
        seedAccount(customerId2, "KZ222", 84200.50, "KZT");

        long bankId3 = seedBank("ForteBank", "Forte");
        long customerId3 = seedCustomer(bankId3, "Bagdat", "+7703202");
        seedAccount(customerId3, "KZ333", 1900.75, "USD");

        long bankId4 = seedBank("Eurasian Bank", "EURBank");
        long customerId4 = seedCustomer(bankId4, "Amina", "+77512230003");
        seedAccount(customerId4, "KZ44412", 15890.10, "EUR");

        long bankId5 = seedBank("Jusan Bank", "Jubank");
        long customerId5 = seedCustomer(bankId5, "Nurlan", "+770812304");
        seedAccount(customerId5, "KZ55523", 501230.00, "KZT");

        long bankId6 = seedBank("Bank CenterCredit", "bankcentr");
        long customerId6 = seedCustomer(bankId6, "Miras", "+7522230005");
        seedAccount(customerId6, "KZ666", 7450.99, "USD");

        long bankId7 = seedBank("Bereke Bank", "berkbank");
        long customerId7 = seedCustomer(bankId7, "Oraz", "+77481230006");
        seedAccount(customerId7, "KZ70986", 33210.00, "KZT");

        long bankId8 = seedBank("Home Credit Bank", "homecred");
        long customerId8 = seedCustomer(bankId8, "Rustem", "+72221230007");
        seedAccount(customerId8, "KZ80890", 290.40, "EUR");

        long bankId9 = seedBank("Freedom Bank", "freebank");
        long customerId9 = seedCustomer(bankId9, "Alisa", "+7991230008");
        seedAccount(customerId9, "KZ99090", 10050.00, "USD");
    }
    @GetMapping("/hello")
    public String hello() {
        return "HELLO!!!";
    }
    @GetMapping("/banks")
    public List<Bank> getAllBanks() {
        return new ArrayList<>(banks.values());
    }
    //{
    //     "name": "",
    //     "bic": ""
    //}
    @PostMapping("/banks")
    public ResponseEntity<Bank> createBank(@RequestBody CreateBankRequest request) {
        long id = bankSeq.getAndIncrement();
        Bank bank = new Bank();
        bank.setId(id);
        bank.setName(request.name());
        bank.setBic(request.bic());
        banks.put(id, bank);
        return ResponseEntity.status(HttpStatus.CREATED).body(bank);
    }

    //{
    //      "fullName": "",
    //      "phone": ""
    //}
    @PostMapping("/banks/{bankId}/customers")
    public ResponseEntity<Customer> createCustomer(@PathVariable Long bankId, @RequestBody CreateCustomerRequest request) {
        if (!banks.containsKey(bankId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bank not found");
        }
        long customerId = customerSeq.getAndIncrement();
        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setFullName(request.fullName());
        customer.setPhone(request.phone());
        customer.setBankId(bankId);
        customers.put(customerId, customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }

    //{
    //  "accountNumber": "",
    //  "balance": ,
    //  "currency": ""
    //}
    @PostMapping("/customers/{customerId}/accounts")
    public ResponseEntity<Account> createAccount(@PathVariable Long customerId, @RequestBody CreateAccountRequest request) {
        if (!customers.containsKey(customerId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }
        long accountId = accountSeq.getAndIncrement();
        Account account = new Account();
        account.setId(accountId);
        account.setAccountNumber(request.accountNumber());
        account.setBalance(request.balance());
        account.setCurrency(request.currency());
        account.setCustomerId(customerId);
        accounts.put(accountId, account);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    @GetMapping("/customers")
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers.values());
    }

    @GetMapping("/accounts")
    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    @GetMapping("/accounts/{accountId}")
    public Account getAccount(@PathVariable Long accountId) {
        Account account = accounts.get(accountId);
        if (account == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
        return account;
    }
    @GetMapping("/banks/{bankId}/overview")
    public BankOverviewResponse getOverview(@PathVariable Long bankId) {
        Bank bank = banks.get(bankId);
        if (bank == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bank not found");

        List<CustomerInfo> cList = customers.values().stream()
                .filter(c -> bankId.equals(c.getBankId()))
                .map(this::mapCustomer).toList();

        List<AccountInfo> aList = accounts.values().stream()
                .filter(a -> {
                    Customer owner = customers.get(a.getCustomerId());
                    return owner != null && bankId.equals(owner.getBankId());
                })
                .map(this::mapAccount).toList();

        return new BankOverviewResponse(bank.getId(), bank.getName(), bank.getBic(), cList, aList);
    }
    private CustomerInfo mapCustomer(Customer customer) {
        return new CustomerInfo(customer.getId(), customer.getFullName(), customer.getPhone());
    }

    private AccountInfo mapAccount(Account account) {
        return new AccountInfo(account.getId(), account.getAccountNumber(), account.getBalance(), account.getCurrency());
    }

    private long seedBank(String name, String bic) {
        long bankId = bankSeq.getAndIncrement();
        Bank bank = new Bank();
        bank.setId(bankId);
        bank.setName(name);
        bank.setBic(bic);
        banks.put(bankId, bank);
        return bankId;
    }

    private long seedCustomer(Long bankId, String fullName, String phone) {
        long customerId = customerSeq.getAndIncrement();
        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setFullName(fullName);
        customer.setPhone(phone);
        customer.setBankId(bankId);
        customers.put(customerId, customer);
        return customerId;
    }

    private void seedAccount(Long customerId, String accountNumber, double balance, String currency) {
        long accountId = accountSeq.getAndIncrement();
        Account account = new Account();
        account.setId(accountId);
        account.setAccountNumber(accountNumber);
        account.setBalance(balance);
        account.setCurrency(currency);
        account.setCustomerId(customerId);
        accounts.put(accountId, account);
    }

    public record CreateBankRequest(String name, String bic) {}
    public record CreateCustomerRequest(String fullName, String phone) {}
    public record CreateAccountRequest(String accountNumber, double balance, String currency) {}
    public record CustomerInfo(Long id, String fullName, String phone) {}
    public record AccountInfo(Long id, String accountNumber, double balance, String currency) {}
    public record BankOverviewResponse(Long bankId, String bankName, String bic, List<CustomerInfo> customers, List<AccountInfo> accounts) {}
}
