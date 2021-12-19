package bank;

public class OutgoingTransfer extends Transfer {
    /**
     * @param newDate        needs to be in form "dd-mm-yyyy", otherwise will get error
     * @param newAmount
     * @param newDescription
     */
    public OutgoingTransfer(String newDate, double newAmount, String newDescription) {
        super(newDate, newAmount, newDescription);
    }

    /**
     * @param newDate
     * @param newAmount
     * @param newDescription
     * @param newSender      set sender as unknown as default if newSender is empty
     * @param newRecipient   set recipient as unknown as default if newRecipient is empty
     */
    public OutgoingTransfer(String newDate, double newAmount, String newDescription, String newSender, String newRecipient) {
        super(newDate, newAmount, newDescription, newSender, newRecipient);
    }

    @Override
    public double calculate() {
        return -this.getAmount();
    }
}
