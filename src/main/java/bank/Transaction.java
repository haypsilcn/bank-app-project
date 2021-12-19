package bank;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Transaction {

    protected double amount;
    protected String description;
    protected String date;

    public double getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @param newDate needs to be in form "dd-mm-yyyy", otherwise will get error
     * @param newAmount
     * @param newDescription
     */
    public Transaction(String newDate, double newAmount, String newDescription) {
        if (newDate.equals(""))
            this.date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        else {
            this.date = newDate;
        }

        if (newDescription.equals(""))
            this.description = "no description";
        else
            this.description = newDescription;
        this.amount = newAmount;
    }

    public double calculate() {
        return this.getAmount();
    }

    public boolean equals(Object obj) {
        if (obj instanceof Transaction transaction)
            return (this.getDate().equals(transaction.date) && this.getDescription().equals(transaction.getDescription()) && getAmount() == transaction.getAmount());
        return false;
    }

    /**
     *  format String date to correct form
     * @return
     */
    public String toString() {
        DateFormat pattern = new SimpleDateFormat("dd-MM-yyyy");
        Date formatDate = null;
        try {
            formatDate = pattern.parse(getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "Date: " + pattern.format(formatDate) + ", Amount: " + this.calculate() + " €, Description: " + this.getDescription();
    }
}
