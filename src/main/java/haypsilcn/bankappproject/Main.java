package haypsilcn.bankappproject;

import bank.Payment;
import bank.Transaction;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main {
    public static void main(String[] args) throws ParseException {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
//        String date = df.format(new Date("03-11-1990"));#

        Date date = df.parse("32-12-1990");
        System.out.println(df.format(date));


    }
}
