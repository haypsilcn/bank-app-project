package bank;

import bank.exceptions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BankTest {

    static Bank bank;

    @BeforeAll
    public static void setUp() throws AccountAlreadyExistsException, IOException, AccountInvalidException, AccountDoesNotExistException, TransactionAlreadyExistsException {
        bank = new Bank("TestBank", 0, 0.12);

        bank.createAccount("Hagen");
        bank.addTransaction("Hagen", new Payment("19-01-2011", -789, "Payment", 0.9, 0.25));

        bank.createAccount("Tim");
        bank.addTransaction("Tim", new IncomingTransfer("03-03-2000", 80, "IncomingTransfer from Adam to Tim; 80", "Adam", "Tim"));
    }

    @DisplayName("Testing constructor")
    @Test
    @Order(0)
    public void constructorTest() {
        assertAll("Bank",
                () -> assertEquals("TestBank", bank.getName()),
                () -> assertEquals(0, bank.getIncomingInterest()),
                () -> assertEquals(0.12, bank.getOutgoingInterest()));
    }

    @DisplayName("Create a duplicate account")
    @ParameterizedTest
    @Order(1)
    @ValueSource(strings = {"Hagen", "Tim"})
    public void createDuplicateAccountTest(String account) {
        Exception e = assertThrows(AccountAlreadyExistsException.class,
                () -> bank.createAccount(account));
        System.out.println(e.getMessage());
    }

    @DisplayName("Create a valid account")
    @ParameterizedTest
    @Order(2)
    @ValueSource(strings = {"Dinesh", "Bob", "Narsha"})
    public void createValidAccountTest(String account) {
        assertDoesNotThrow(
                () -> bank.createAccount(account)
        );
    }

    @DisplayName("Create an invalid (empty) account")
    @Test
    @Order(3)
    public void createInvalidAccountTest() {
        Exception e = assertThrows(AccountInvalidException.class,
                () -> bank.createAccount(""));
        System.out.println(e.getMessage());
    }

    @DisplayName("Create a valid account with a transactions list")
    @ParameterizedTest
    @Order(4)
    @ValueSource(strings = {"Klaus", "Harsh", "Rastla"})
    public void createValidAccountWithTransactionsListTest(String account) {
        assertDoesNotThrow(
                () -> bank.createAccount(account, List.of(
                        new Payment("23-09-1897", -2500, "Payment 02", 0.8, 0.5),
                        new OutgoingTransfer("30-07-2020", 1890, "OutgoingTransfer to Hagen", account, "Hagen")
                ))
        );
    }

    @DisplayName("Create a duplicate account with a transactions list")
    @ParameterizedTest
    @Order(5)
    @ValueSource(strings = {"Hagen", "Klaus", "Tim", "Bob", "Dinesh", "Narsha", "Harsh", "Rastla"})
    public void createDuplicateAccountWithTransactionsListTest(String account) {
        Exception e = assertThrows(AccountAlreadyExistsException.class,
                () -> bank.createAccount(account, List.of(
                        new Payment("23-09-1897", -2500, "Payment 02", 0.8, 0.5),
                        new OutgoingTransfer("30-07-2020", 1890, "OutgoingTransfer to Hagen", account, "Hagen")
                ))
        );
        System.out.println(e.getMessage());
    }

    @DisplayName("Create an invalid (empty) account with a transactions list")
    @Test
    @Order(6)
    public void createInvalidAccountWithTransactionsListTest() {
        Exception e = assertThrows(AccountInvalidException.class,
                () -> bank.createAccount("", List.of(
                        new Payment("23-09-1897", -2500, "Payment 02", 0.8, 0.5),
                        new OutgoingTransfer("30-07-2020", 1890, "OutgoingTransfer to Hagen", "", "Hagen")
                )));
        System.out.println(e.getMessage());
    }

    @DisplayName("Add a valid transaction to a valid account")
    @ParameterizedTest
    @Order(7)
    @ValueSource(strings = {"Hagen", "Bob", "Narsha", "Dinesh", "Klaus"})
    public void addValidTransactionValidAccountTest(String account) {
        assertDoesNotThrow(
                () -> bank.addTransaction(account, new IncomingTransfer("30-07-2020", 1890, "OutgoingTransfer to Hagen", "Tom", account))
        );
    }

    @DisplayName("Add a duplicate transaction to a valid account")
    @ParameterizedTest
    @Order(8)
    @ValueSource(strings = {"Klaus", "Harsh", "Rastla"})
    public void addDuplicateTransactionTest(String account) {
        Exception e = assertThrows(TransactionAlreadyExistsException.class,
                () -> bank.addTransaction(account, new Payment("23-09-1897", -2500, "Payment 02", 0.8, 0.5))
        );
        System.out.println(e.getMessage());
    }

    @DisplayName("Add a valid transaction to a non-exist account")
    @ParameterizedTest
    @Order(9)
    @ValueSource(strings = {"Gina", "Bona", "Yang", ""})
    public void addTransactionInvalidAccountTest(String account) {
        Exception e = assertThrows(AccountDoesNotExistException.class,
                () -> bank.addTransaction(account, new Payment("19-01-2011", -789, "Payment", 0.9, 0.25))
        );
        System.out.println(e.getMessage());
    }

    @DisplayName("Remove a valid transaction")
    @ParameterizedTest
    @Order(10)
    @ValueSource(strings = {"Harsh", "Rastla", "Klaus"})
    public void removeValidTransactionTest(String account) {
        assertDoesNotThrow(
                () -> bank.removeTransaction(account, new Payment("23-09-1897", -2500, "Payment 02", 0.8, 0.5))
        );
    }

    @DisplayName("Remove an invalid transaction")
    @ParameterizedTest
    @Order(11)
    @ValueSource(strings = {"Harsh", "Rastla", "Klaus"})
    public void removeInvalidTransactionTest(String account) {
        Exception e = assertThrows(TransactionDoesNotExistException.class,
                () -> bank.removeTransaction(account, new Payment("19-01-2011", -789, "Payment", 0.9, 0.25))
        );
        System.out.println(e.getMessage());
    }

    @DisplayName("Contains a transaction is true")
    @ParameterizedTest
    @Order(12)
    @ValueSource(strings = {"Harsh", "Rastla", "Klaus"})
    public void containsTransactionTrueTest(String account) {
        assertTrue(bank.containsTransaction(account, new OutgoingTransfer("30-07-2020", 1890, "OutgoingTransfer to Hagen", account, "Hagen")));
        System.out.println("containsTransactionTrueTest in <" + account + "> is correct.");
    }

    @DisplayName("Contains a transaction is false")
    @ParameterizedTest
    @Order(13)
    @ValueSource(strings = {"Hagen", "Bob", "Narsha", "Dinesh", "Tim"})
    public void containsTransactionFalseTest(String account) {
        assertFalse(bank.containsTransaction(account, new OutgoingTransfer("30-07-2020", 1890, "OutgoingTransfer to Hagen", account, "Hagen")));
        System.out.println("containsTransactionFalseTest in <" + account + "> is correct.");
    }

    @DisplayName("Get account balance")
    @ParameterizedTest
    @Order(14)
    @CsvSource({"Klaus, 0", "Tim, 80", "Hagen, 903.75"})
    public void getAccountBalanceTest(String account, double balance) {
        System.out.println("Expected balance <" + balance + "> in account <" + account + ">");
        assertEquals(balance, bank.getAccountBalance(account));
    }

    @DisplayName("Get transactions list")
    @Test @Order(15)
    public void getTransactionTest() {
        List<Transaction> transactionList = List.of(
                new Payment("19-01-2011", -789, "Payment", 0.9, 0.25),
                new IncomingTransfer("30-07-2020", 1890, "OutgoingTransfer to Hagen", "Tom", "Hagen"));
        assertEquals(transactionList, bank.getTransactions("Hagen"));
        System.out.println("getTransactionTest in <Hagen> is correct.");
    }

    @DisplayName("Get transactions list by type")
    @Test @Order(16)
    public void getTransactionsByTypeTest() {
        List<Transaction> transactionList = List.of(
                new OutgoingTransfer("30-07-2020", 1890, "OutgoingTransfer to Hagen", "Klaus", "Hagen"));
        assertEquals(transactionList, bank.getTransactionsByType("Klaus", false));
        System.out.println("getTransactionByTypeTest in <Klaus> is correct.");
    }

    @DisplayName("Get sorted transactions list")
    @Test @Order(17)
        public void getTransactionsSortedTest() {
        assertEquals(List.of(
                        new IncomingTransfer("03-03-2000", 80, "IncomingTransfer from Adam to Tim; 80", "Adam", "Tim"))
                , bank.getTransactionsSorted("Tim", true));

    }

    @DisplayName("Get a list of all accounts")
    @Test @Order(18)
    public void getAllAccounts() {
        List<String> expected = new ArrayList<>();
        expected.add("Hagen");
        expected.add("Bob");
        expected.add("Dinesh");
        expected.add("Tim");
        expected.add("Harsh");
        expected.add("Narsha");
        expected.add("Klaus");
        expected.add("Rastla");

        assertEquals(expected, bank.getAllAccounts());
    }

    @ParameterizedTest
    @DisplayName("Delete a valid account")
    @Order(19)
    @ValueSource(strings = {"Bob", "Narsha", "Tim", "Hagen"})
    public void deleteValidAccount(String account) {
        assertDoesNotThrow(
                () -> bank.deleteAccount(account)
        );
    }

    @ParameterizedTest
    @DisplayName("Delete an invalid account")
    @Order(20)
    @ValueSource(strings = {"Gina", "Natasha", "Steve", ""})
    public void deleteInvalidAccount(String account) {
        Exception e = assertThrows(AccountDoesNotExistException.class,
                () -> bank.deleteAccount(account));
        System.out.print(e.getMessage());
    }

    @AfterAll
    public static void tearDown() throws IOException {
        final File folder = new File("src/main/resources/data/TestBank");
        if (folder.exists()) {
            System.out.println("Deleting <" + bank.getName() + "> after running tests");
            final File[] listOfFiles = folder.listFiles();
            assert listOfFiles != null;
            for (File file : listOfFiles)
                if (!file.delete())
                    System.out.println("=> ERROR! Some file cannot be deleted!");
            Files.deleteIfExists(Path.of("src/main/resources/data/TestBank"));
            System.out.println("=> SUCCESS!");
        }
    }
}