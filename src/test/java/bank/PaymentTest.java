package bank;

import bank.exceptions.customDateFormat.DayFormatInvalidException;
import bank.exceptions.customDateFormat.MonthFormatInvalidException;
import bank.exceptions.customDateFormat.YearFormatInvalidException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    static Payment payment1;
    static Payment payment2;

    @BeforeAll
    public static void setUp() throws DayFormatInvalidException, MonthFormatInvalidException, YearFormatInvalidException {
        System.out.println("Set up for Payment objects");
        payment1 = new Payment("12-03-2008", 321, "Payment 01");
        payment2 = new Payment("23-09-1897", -2500,"Payment 02",  0.8, 0.5);
    }

    @Test
    public void threeAttributesConstructorTest() {
        assertEquals("12-03-2008", payment1.getDate());
        assertEquals("Payment 01", payment1.getDescription());
        assertEquals(321, payment1.getAmount());
    }

    @Test
    public void allAttributesConstructorTest() {
        assertEquals("23-09-1897", payment2.getDate());
        assertEquals("Payment 02", payment2.getDescription());
        assertEquals(-2500, payment2.getAmount());
        assertEquals(0.8, payment2.getIncomingInterest());
        assertEquals(0.5, payment2.getOutgoingInterest());
    }


    @Test
    public void calculateIncomingInterestTest() {
        double expected = payment1.getAmount() - payment1.getIncomingInterest() * payment1.getAmount();
        assertTrue(payment1.getAmount() >= 0);
        assertEquals(expected, payment1.calculate());
    }

    @Test
    public void calculateOutgoingInterestTest() {
        double expected = payment2.getAmount() + payment2.getOutgoingInterest() * payment2.getAmount();
        assertTrue(payment2.getAmount() < 0);
        assertEquals(expected, payment2.calculate());
    }

    @Test
    public void equalsTrueTest() {
        assertEquals(payment2, payment2);
    }

    @Test
    public void equalsFalseTest() {
        assertNotEquals(payment1, payment2);
    }

    @Test
    public void toStringTester() {
        String string = "CustomDateFormat: 23-09-1897, Amount: -3750.0 €, Description: Payment 02, Incoming interest: 0.8, Outgoing interest: 0.5\n";
        assertEquals(string, payment2.toString());
    }
}