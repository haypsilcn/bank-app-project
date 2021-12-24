package bank;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class TransferTest {

    static Transfer incomingTransfer;
    static Transfer outgoingTransfer;
    static Transfer copyTransfer;

    @BeforeAll
    public static void setUp() {
        incomingTransfer = new IncomingTransfer("03-03-2000", 80, "IncomingTransfer from Molziles to Elixir; 80");
        outgoingTransfer = new OutgoingTransfer("30-07-2020", 1890,"OutgoingTransfer to Hagen",  "Elixir", "Hagen");
    }


    @Test
    public void threeAttributesConstructorTest() {
        assertEquals("03-03-2000", incomingTransfer.getDate());
        assertEquals("IncomingTransfer from Molziles to Elixir; 80", incomingTransfer.getDescription());
        assertEquals(80, incomingTransfer.getAmount());

    }

    @Test
    public void allAttributesConstructorTest() {
        assertEquals("30-07-2020", outgoingTransfer.getDate());
        assertEquals("OutgoingTransfer to Hagen", outgoingTransfer.getDescription());
        assertEquals(1890, outgoingTransfer.getAmount());
        assertEquals("Elixir", outgoingTransfer.getSender());
        assertEquals("Hagen", outgoingTransfer.getRecipient());
    }

    @Test
    public void calculateIncomingTransferTest() {
        assertInstanceOf(IncomingTransfer.class, incomingTransfer);
        assertEquals(incomingTransfer.getAmount(), incomingTransfer.calculate());
    }

    @Test
    public void calculateOutgoingTransferTest() {
        assertInstanceOf(OutgoingTransfer.class, outgoingTransfer);
        assertEquals(-outgoingTransfer.getAmount(), outgoingTransfer.calculate());
    }

    @Test
    public void equalsTrueTest() {
        assertEquals(outgoingTransfer, outgoingTransfer);
    }

    @Test
    public void equalsFalseTest() {
        assertNotEquals(incomingTransfer, outgoingTransfer);
    }

    @Test
    public void toStringTester() {
        assertEquals("CustomDateFormat: 30-07-2020, Amount: -1890.0 €, Description: OutgoingTransfer to Hagen, Sender: Elixir, Recipient: Hagen\n", outgoingTransfer.toString());
    }
}
