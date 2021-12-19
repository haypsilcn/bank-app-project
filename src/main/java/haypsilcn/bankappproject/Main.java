package haypsilcn.bankappproject;

import bank.*;
import bank.exceptions.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) throws AccountInvalidException {

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

        Payment payment = new Payment("1-1-1990", 90, "");
        IncomingTransfer outgoingTransfer = new IncomingTransfer("", 890, "", "", "");

       /* try {
            bank.addTransaction("Gin", payment);
        } catch (AccountDoesNotExistException | IOException | TransactionAlreadyExistsException e) {
            System.out.println(e.getMessage());
        }*/

        try {
            bank.createAccount("Tom", List.of(
                    payment,
                    outgoingTransfer
            ));
        } catch (AccountInvalidException | AccountAlreadyExistsException | IOException e) {
            System.out.println(e.getMessage());
        }

        System.out.println(bank);




        /*try {
            bank.addTransaction("", new Payment("", 890, ""));
        } catch (AccountDoesNotExistException | IOException | TransactionAlreadyExistsException e) {
            System.out.println(e.getMessage());
        }*/
    }
}
