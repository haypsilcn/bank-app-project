package bank;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

public class BankTest {

    static Bank bank;

    @BeforeAll
    public static void setUp() {
        bank = new Bank("TestBank", 0, 0.12);
        bank.setDirectory("");
    }

    @AfterEach
    void tearDown() {
    }
}