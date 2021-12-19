package bank;

public class IncomingTransfer extends Transfer {
    /**
     * @param newDate        needs to be in form "dd-mm-yyyy", otherwise will get error
     * @param newAmount
     * @param newDescription
     */
    public IncomingTransfer(String newDate, double newAmount, String newDescription) {
        super(newDate, newAmount, newDescription);
    }

    public IncomingTransfer(String newDate, double newAmount, String newDescription, String newSender, String newRecipient) {
        super(newDate, newAmount, newDescription, newSender, newRecipient);
    }
}
