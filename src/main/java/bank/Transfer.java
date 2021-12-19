package bank;

public abstract class Transfer extends Transaction {

    protected String sender;
    protected String recipient;

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    /**
     * @param newDate        needs to be in form "dd-mm-yyyy", otherwise will get error
     * @param newAmount
     * @param newDescription
     */
    public Transfer(String newDate, double newAmount, String newDescription) {
        super(newDate, newAmount, newDescription);
    }

    /**
     *
     * @param newDate
     * @param newAmount
     * @param newDescription
     * @param newSender set sender as unknown as default if newSender is empty
     * @param newRecipient set recipient as unknown as default if newRecipient is empty
     */
    public Transfer(String newDate, double newAmount, String newDescription, String newSender, String newRecipient)  {
        this(newDate, newAmount, newDescription);
        if (newSender.equals(""))
            this.sender = "unknown sender";
        else
            this.sender = newSender;
        if (newSender.equals(""))
            this.recipient = "unknown recipient";
        else
            this.recipient = newRecipient;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Transfer transfer)
            return (super.equals(transfer) && this.getSender().equals(transfer.getSender()) && this.getRecipient().equals(transfer.getRecipient()));
        return false;
    }

    public String toString() {
        return super.toString() + ", Sender: " + this.getSender() + ", Recipient: " + this.getRecipient();
    }
}
