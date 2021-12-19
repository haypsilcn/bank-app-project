package haypsilcn.bankappproject;

import bank.*;
import bank.exceptions.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
    public static void main(String[] args) {

        Bank bank = new Bank("", -1, 2);
        /*try {
            bank.getAccountBalance("");
        } catch (AccountInvalidException e) {
            System.out.println(e.getMessage());
        }*/

        try {
            bank.createAccount("Gin");
        } catch (AccountInvalidException | AccountAlreadyExistsException | IOException e) {
            System.out.println(e.getMessage());
        }

        /*try {
            bank.addTransaction("", new Payment("", 890, ""));
        } catch (AccountDoesNotExistException | IOException | TransactionAlreadyExistsException e) {
            System.out.println(e.getMessage());
        }*/
    }
}
