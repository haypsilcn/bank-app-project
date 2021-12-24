package bank;

import bank.exceptions.customDateFormat.DayFormatInvalidException;
import bank.exceptions.customDateFormat.MonthFormatInvalidException;
import bank.exceptions.customDateFormat.YearFormatInvalidException;
import com.google.gson.*;

import java.lang.reflect.Type;

public class CustomJsonSerialization implements JsonSerializer<Transaction>, JsonDeserializer<Transaction> {
    @Override
    public Transaction deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        /*CustomDateFormat date = null;
        try {
            date = new CustomDateFormat(
                    jsonObject.get("day").getAsInt(),
                    jsonObject.get("month").getAsInt(),
                    jsonObject.get("year").getAsInt()        );
        } catch (DayFormatInvalidException | MonthFormatInvalidException | YearFormatInvalidException e) {
            e.printStackTrace();
        }*/

        String dateString = jsonObject.get("date").getAsString();
        StringBuilder day =  new StringBuilder();
        StringBuilder month =  new StringBuilder();
        StringBuilder year = new StringBuilder();

        for (int i = 0, separator = 0; i < dateString.length(); i++) {
            if (separator == 0) {
                if (dateString.charAt(i) == '-')
                    separator++;
                else
                    day.append(dateString.charAt(i));
            } else if (separator == 1) {
                if (dateString.charAt(i+1) == '-')
                    separator++;
                else month.append(dateString.charAt(i+1));
            } else if (separator == 2)
                if (dateString.charAt(i) != '-')
                    year.append(dateString.charAt(i));
        }

        CustomDateFormat date = null;
        try {
            date = new CustomDateFormat(Integer.parseInt(day.toString()), Integer.parseInt(month.toString()), Integer.parseInt(year.toString()));
            System.out.println(date);

        } catch (DayFormatInvalidException | MonthFormatInvalidException | YearFormatInvalidException e) {
            System.out.println(e.getMessage());
        }



        if (jsonObject.get("CLASSNAME").getAsString().equals("Payment")) {
            return new Payment(
                    jsonObject.get("date").getAsString(),
                    jsonObject.get("amount").getAsDouble(),
                    jsonObject.get("description").getAsString(),
                    jsonObject.get("incomingInterest").getAsDouble(),
                    jsonObject.get("outgoingInterest").getAsDouble());

        }

        else if (jsonObject.get("CLASSNAME").getAsString().equals("IncomingTransfer")) {
            return new IncomingTransfer(
                    jsonObject.get("date").getAsString(),
                    jsonObject.get("amount").getAsDouble(),
                    jsonObject.get("description").getAsString(),
                    jsonObject.get("sender").getAsString(),
                    jsonObject.get("recipient").getAsString()
            );
        }
        else {
            return new OutgoingTransfer(
                    jsonObject.get("date").getAsString(),
                    jsonObject.get("amount").getAsDouble(),
                    jsonObject.get("description").getAsString(),
                    jsonObject.get("sender").getAsString(),
                    jsonObject.get("recipient").getAsString()
            );
        }

    }

    @Override
    public JsonElement serialize(Transaction transaction, Type type, JsonSerializationContext jsonSerializationContext) {

        JsonObject jsonTransaction = new JsonObject();
        JsonObject jsonAccount = new JsonObject();
        JsonObject jsonDate = new JsonObject();

        /*jsonDate.addProperty("day", transaction.getDate().getDay());
        jsonDate.addProperty("month", transaction.getDate().getMonth());
        jsonDate.addProperty("year ", transaction.getDate().getYear());*/


        if (transaction instanceof Payment payment) {
            jsonTransaction.addProperty("incomingInterest", payment.getIncomingInterest());
            jsonTransaction.addProperty("outgoingInterest", payment.getOutgoingInterest());
        }
        else if (transaction instanceof Transfer transfer) {
            jsonTransaction.addProperty("sender", transfer.getSender());
            jsonTransaction.addProperty("recipient", transfer.getRecipient());
        }
        jsonTransaction.addProperty("date", transaction.getDate());
//        jsonTransaction.add("DATE", jsonDate);

        /*jsonTransaction.addProperty("day", transaction.getDate().getDay());
        jsonTransaction.addProperty("month", transaction.getDate().getMonth());
        jsonTransaction.addProperty("year", transaction.getDate().getYear());*/

        jsonTransaction.addProperty("amount", transaction.getAmount());
        jsonTransaction.addProperty("description", transaction.getDescription());

        jsonAccount.addProperty("CLASSNAME", transaction.getClass().getSimpleName());
        jsonAccount.add("INSTANCE", jsonTransaction);

        return jsonAccount;
    }
}
