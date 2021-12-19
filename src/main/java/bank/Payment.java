package bank;

public class Payment extends Transaction {

    private double incomingInterest;
    private double outgoingInterest;

    public double getIncomingInterest() {
        return incomingInterest;
    }

    public double getOutgoingInterest() {
        return outgoingInterest;
    }

    public void setIncomingInterest(double incomingInterest) {
        this.incomingInterest = incomingInterest;
    }

    public void setOutgoingInterest(double outgoingInterest) {
        this.outgoingInterest = outgoingInterest;
    }

    public Payment(String newDate, double newAmount, String newDescription) {
        super(newDate, newAmount, newDescription);
    }

    /**
     *
     * @param newDate
     * @param newAmount
     * @param newDescription
     * @param newIncomingInterest incoming interest needs to be in between [0,1), otherwise will be set as 0 as default
     * @param newOutgoingInterest outgoing interest needs to be in between [0,1), otherwise will be set as 0 as default
     */
    public Payment(String newDate, double newAmount, String newDescription, double newIncomingInterest, double newOutgoingInterest) {
        this(newDate, newAmount, newDescription);
        if (0 <= newIncomingInterest && newIncomingInterest <= 1)
            this.incomingInterest = newIncomingInterest;
        else
            this.incomingInterest = 0.5;
        if (0 <= newOutgoingInterest && newOutgoingInterest <= 1)
            this.outgoingInterest = newOutgoingInterest;
        else
            this.outgoingInterest = 0.5;
    }

    /**
     *
     * @return the final amount after rounding and take the value of two number after comma
     */
    @Override
    public double calculate() {
        double result;
        if (this.getAmount() >= 0)
            result = this.getAmount() - this.getIncomingInterest() * this.getAmount();
        else
            result = this.getAmount() + this.getOutgoingInterest() * this.getAmount();
        return (double) Math.round(result * 100) / 100;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Payment payment)
            return (super.equals(payment) && this.getIncomingInterest() == payment.getIncomingInterest() && this.getOutgoingInterest() == payment.getOutgoingInterest());
        return false;
    }

    public String toString() {
        return super.toString() + ", Incoming interest: " + this.getIncomingInterest() + ", Outgoing interest: " + this.getOutgoingInterest();
    }
}
