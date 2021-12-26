package haypsilcn.bankappproject;

import bank.*;
import bank.exceptions.*;
import bank.exceptions.customDateFormat.DayFormatInvalidException;
import bank.exceptions.customDateFormat.MonthFormatInvalidException;
import bank.exceptions.customDateFormat.YearFormatInvalidException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) throws AccountInvalidException, DayFormatInvalidException, MonthFormatInvalidException, YearFormatInvalidException {

        Bank bank = new Bank("", -1, 2);
        /*try {
            bank.getAccountBalance("");
        } catch (AccountInvalidException e) {
            System.out.println(e.getMessage());
        }*/

      /*  try {
            bank.createAccount("Gin");
        } catch (AccountInvalidException | AccountAlreadyExistsException | IOException e) {
            System.out.println(e.getMessage());
        }*/
       /* try {
            Payment payment = new Payment(new CustomDateFormat(1, 1, 1990), 90, "");
            System.out.println(payment.getDate());

        } catch (DayFormatInvalidException e)
        {
            e.printStackTrace();
        }*/

        Payment payment = new Payment("1-1-1990", 90, "");
        IncomingTransfer outgoingTransfer = new IncomingTransfer("23-8-2009", 890, "", "", "");

       /* try {
            bank.addTransaction("Gin", payment);
        } catch (AccountDoesNotExistException | IOException | TransactionAlreadyExistsException e) {
            System.out.println(e.getMessage());
        }*/



        /*try {
            bank.createAccount("Tom");
        } catch (AccountInvalidException | AccountAlreadyExistsException | IOException e) {
            System.out.println(e.getMessage());
        }
        try {
            bank.addTransaction("Tom",payment);
        } catch (AccountDoesNotExistException | TransactionAlreadyExistsException| IOException e) {
            System.out.println(e.getMessage());
        }

        try {
            bank.createAccount("Tom");
        } catch (AccountInvalidException | AccountAlreadyExistsException | IOException e) {
            System.out.println(e.getMessage());
        }*/

        try {
            bank.editAccount("Tim", "");
        } catch (AccountInvalidException | AccountAlreadyExistsException | IOException | AccountDoesNotExistException e) {
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
