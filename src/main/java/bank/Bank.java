package bank;

import bank.exceptions.*;
import com.google.gson.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Bank {

    /**
     * name of Bank
     */
    private String name;
    private String directory = "data";
    private double incomingInterest;
    private double outgoingInterest;
    private Map<String, List<Transaction>> accountsToTransactions = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirectory() {
        return "src/main/resources/" + directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public double getIncomingInterest() {
        return incomingInterest;
    }

    public void setIncomingInterest(double incomingInterest) {
        this.incomingInterest = incomingInterest;
    }

    public double getOutgoingInterest() {
        return outgoingInterest;
    }

    public void setOutgoingInterest(double outgoingInterest) {
        this.outgoingInterest = outgoingInterest;
    }

    private void writeAccount(String account) throws IOException {
        FileWriter fileWriter = new FileWriter(this.getDirectory() + "/" + account + ".json");
        try {
            fileWriter.write("[");
            for (Transaction transaction : accountsToTransactions.get(account)) {
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(transaction.getClass(), new CustomJsonSerialization())
                        .setPrettyPrinting()
                        .create();

                String json = gson.toJson(transaction);
                if (accountsToTransactions.get(account).indexOf(transaction) != 0)
                    fileWriter.write(",");
                fileWriter.write(json);
            }
            fileWriter.write("]");

        } catch (IOException e) {
            e.printStackTrace();
        }
        // have to close file, otherwise file will be empty and nothing is written to file
        fileWriter.close();
    }

    private void readAccounts() throws AccountAlreadyExistsException, IOException, AccountInvalidException {
        File folder = new File(this.getDirectory());
        File[] listOfFiles = folder.listFiles();

        assert listOfFiles != null;
        for (File file : listOfFiles) {
            String accountName = file.getName().replace(".json", "");
            String accountNameFile = file.getName();
            this.createAccount(accountName);

            try {

                Reader reader = new FileReader(this.getDirectory() + "/" + accountNameFile);
                JsonArray parser = JsonParser.parseReader(reader).getAsJsonArray();
                for (JsonElement jsonElement : parser.getAsJsonArray()) {

                    JsonObject jsonObject = jsonElement.getAsJsonObject();

                    Gson customGson = new GsonBuilder()
                            .registerTypeAdapter(Transaction.class, new CustomJsonSerialization())
                            .create();

                    String str = customGson.toJson(jsonObject.get("INSTANCE"));

                    if (jsonObject.get("CLASSNAME").getAsString().equals("Payment")) {
                        Payment payment = customGson.fromJson(str, Payment.class);
                        this.addTransaction(accountName, payment);
                    }
                    else if (jsonObject.get("CLASSNAME").getAsString().equals("IncomingTransfer")) {
                        IncomingTransfer incomingTransfer = customGson.fromJson(str, IncomingTransfer.class);
                        this.addTransaction(accountName, incomingTransfer);
                    }
                    else {
                        OutgoingTransfer outgoingTransfer = customGson.fromJson(str, OutgoingTransfer.class);
                        this.addTransaction(accountName, outgoingTransfer);
                    }

                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (AccountDoesNotExistException | TransactionAlreadyExistsException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public Bank(String newName, double newIncomingInterest, double newOutgoingInterest) {
        if (newName.equals(""))
            this.name = "unnamed bank";
        else
            this.name = newName;
        if (0 <= newIncomingInterest && newIncomingInterest <= 1)
            this.incomingInterest = newIncomingInterest;
        else
            this.incomingInterest = 0.5;
        if (0 <= newOutgoingInterest && newOutgoingInterest <= 1)
            this.outgoingInterest = newOutgoingInterest;
        else
            this.outgoingInterest = 0.5;

        try {
            Path path = Paths.get(this.getDirectory());

            if (Files.notExists(path)) {
                Files.createDirectories(path);
                System.out.println("\nDirectory for " + this.getName() + " is created!");
            }
            else {
                System.out.println("\nDirectory for " + this.getName() + " is already exist!");
                System.out.println("=> Start reading account(s) from directory to " + this.getName() + ":");
                readAccounts();
                System.out.println("FINISHED reading account(s) for " + this.getName() + "\n");
            }
        } catch (AccountInvalidException | AccountAlreadyExistsException | IOException e) {
            System.out.println("Failed to create directory for " + this.getName() + "!");
        }
    }

    public void createAccount(String account) throws AccountAlreadyExistsException, IOException, AccountInvalidException {

        Path path = Path.of(this.getDirectory() + "/" + account + ".json");

        if (Files.exists(path)) {
            System.out.print("\nAdding <" + account + "> from the data system to bank <" + this.getName() + "> " );
            if (accountsToTransactions.containsKey(account))
                throw new AccountAlreadyExistsException("=> FAILED! ACCOUNT <" + account + "> ALREADY EXISTS!\n");
            else {
                accountsToTransactions.put(account, List.of());
                System.out.println("=> SUCCESS!");
            }
        }
        else {
            System.out.print("\nCreating new account <" + account + "> to bank <" + this.getName() + "> ");
            if (account.equals(""))
                throw new AccountInvalidException("=> FAILED! ACCOUNT NAME CANNOT BE EMPTY!");
            accountsToTransactions.put(account, List.of());
            writeAccount(account);
            System.out.println("=> SUCCESS!");
        }
    }

    public void createAccount(String account, List<Transaction> transactionsList) throws AccountAlreadyExistsException, IOException, AccountInvalidException {
        Path path = Path.of(this.getDirectory() + "/" + account + ".json");
        String transactionsString = transactionsList.toString().replaceAll("[]]|[\\[]", "").replace("\n, ", "\n\t\t");

        if (Files.exists(path)) {
            System.out.print("\nAdding <" + account + "> from the data system to bank <" + this.getName() + "> with transactions list: \n\t\t" + transactionsString);
            if (accountsToTransactions.containsKey(account))
                throw new AccountAlreadyExistsException("=> FAILED! ACCOUNT <" + account + "> ALREADY EXISTS!\n");
            else {
                for (Transaction transaction : transactionsList)
                    setupTransaction(account, transaction);
                accountsToTransactions.put(account, transactionsList);
                System.out.println("=> SUCCESS!");
            }
        } else {
            System.out.print("\nCreating new account <" + account + "> to bank <" + this.getName() + "> with transactions list: \n\t\t" + transactionsString);
            if (account.equals(""))
                throw new AccountInvalidException("=> FAILED! ACCOUNT NAME CANNOT BE EMPTY!");
            for (Transaction transaction : transactionsList)
                setupTransaction(account, transaction);
            accountsToTransactions.put(account, transactionsList);
            writeAccount(account);
            System.out.println("=> SUCCESS!");
        }
    }

    public void deleteAccount(String account) throws AccountDoesNotExistException, IOException {
        System.out.print("\nDelete account <" + account + "> from bank <" + this.getName() + "> ");
        if (!accountsToTransactions.containsKey(account))
            throw new AccountDoesNotExistException("=> FAILED! ACCOUNT <" + account + "> DOES NOT EXISTS!\n");
        else {
            accountsToTransactions.remove(account);
            Path path = Path.of(this.getDirectory() + "/" + account + ".json");
            Files.deleteIfExists(path);
            System.out.println("=> SUCCESS!");
        }
    }

    public void addTransaction(String account, Transaction transaction) throws AccountDoesNotExistException, IOException, TransactionAlreadyExistsException {
        System.out.println("Adding new transaction <" + transaction.toString().replace("\n", "") + "> to account <" + account + "> in bank <" + this.getName() + ">");
        if (!accountsToTransactions.containsKey(account))
            throw new AccountDoesNotExistException("=> FAILED! ACCOUNT <" + account + "> DOES NOT EXISTS!\n");
        else {
            if (containsTransaction(account, transaction))
                throw new TransactionAlreadyExistsException("=> FAILED! THIS TRANSACTION ALREADY EXISTS!\n");
            else {
                List<Transaction> transactionsList = new ArrayList<>(accountsToTransactions.get(account));
                transactionsList.add(transaction);
                accountsToTransactions.put(account, transactionsList);
                writeAccount(account);
                System.out.println("=> SUCCESS!");
            }
        }
    }

    public void removeTransaction(String account, Transaction transaction) throws TransactionDoesNotExistException, IOException {
        System.out.println("Removing transaction <" + transaction.toString().replace("\n", "") + "> from account <" + account + "> in bank <" + this.getName() + ">");
        if (!containsTransaction(account, transaction))
            throw new TransactionDoesNotExistException("=> FAILED! THIS TRANSACTION DOES NOT EXISTS!\n");
        else {
            List<Transaction> transactionsList = new ArrayList<>(accountsToTransactions.get(account));
            transactionsList.remove(transaction);
            accountsToTransactions.put(account, transactionsList);
            writeAccount(account);
            System.out.println("=> SUCCESS!\n");
        }
    }

    public boolean containsTransaction(String account, Transaction transaction) {
        setupTransaction(account, transaction);
        return accountsToTransactions.get(account).contains(transaction);
    }

    private void setupTransaction(String account, Transaction transaction) {
        if (transaction instanceof Payment payment) {
            if (payment.getIncomingInterest() == 0)
                payment.setIncomingInterest(this.getIncomingInterest());
            if (payment.getOutgoingInterest() == 0)
                payment.setOutgoingInterest(this.getOutgoingInterest());
        } else if (transaction instanceof IncomingTransfer incomingTransfer)
            incomingTransfer.setRecipient(account);
        else if (transaction instanceof OutgoingTransfer outgoingTransfer)
            outgoingTransfer.setSender(account);
    }

    public List<String> getAllAccounts() {
        Set<String> setKey = accountsToTransactions.keySet();
        return new ArrayList<>(setKey);
    }


    public double getAccountBalance(String account) throws AccountInvalidException {
        if (account.equals(""))
            throw new AccountInvalidException("=> FAILED! THIS TRANSACTION DOES NOT EXISTS!\n");
        double balance = 0;
        for (Transaction transaction : accountsToTransactions.get(account))
            balance = balance + transaction.calculate();
        System.out.println("Balance of account <" + account + "> in bank <" + this.getName() + "> : " + (double) Math.round(balance * 100) / 100 + "\n");
        return (double) Math.round(balance * 100) / 100;
    }

    public List<Transaction> getTransactions(String account) {
        return accountsToTransactions.get(account);
    }

    public List<Transaction> getTransactionsByType(String account, boolean positive) {
        List<Transaction> transactionsListByType = new ArrayList<>();
        if (positive)
            System.out.println("List of POSITIVE transactions of account <" + account + "> :");
        else
            System.out.println("List of NEGATIVE transactions of account <" + account + "> :");
        for (Transaction transaction : accountsToTransactions.get(account)) {
            if (positive && transaction.calculate() >= 0)
                transactionsListByType.add(transaction);
            else if (!positive && transaction.calculate() < 0)
                transactionsListByType.add(transaction);
        }
        System.out.println(transactionsListByType.toString().replace("[", "\t\t").replace("]", "").replace("\n, ", "\n\t\t"));

        return transactionsListByType;
    }

    public List<Transaction> getTransactionsSorted(String account, boolean asc) {
        // create new list to store sorted list without affecting original list
        List<Transaction> sortedTransactionsList = new ArrayList<>(accountsToTransactions.get(account));
        if (asc) {
            sortedTransactionsList.sort(Comparator.comparingDouble(Transaction::calculate));
            System.out.println("Sorting transactions of account <" + account + "> by calculated amounts in ASCENDING order:\n" + sortedTransactionsList.toString().replace("[", "\t\t").replace("]", "").replace("\n, ", "\n\t\t"));
        } else {
            sortedTransactionsList.sort(Comparator.comparingDouble(Transaction::calculate).reversed());
            System.out.println("Sorting transactions of account <" + account + "> by calculated amounts in DESCENDING order:\n" + sortedTransactionsList.toString().replace("[", "\t\t").replace("]", "").replace("\n, ", "\n\t\t"));
        }
        return sortedTransactionsList;
    }


    public boolean equals(Object obj) {
        if (obj instanceof Bank bank)
            return (this.getName().equals(bank.getName()) && this.getIncomingInterest() == bank.getIncomingInterest() && this.getOutgoingInterest() == bank.getOutgoingInterest() && this.accountsToTransactions.equals(bank.accountsToTransactions));
        return false;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        Set<String> setKey = accountsToTransactions.keySet();
        for (String key : setKey) {
            str.append(key).append(" => \n");
            List<Transaction> transactionsList = accountsToTransactions.get(key);
            for (Transaction transaction : transactionsList)
                str.append("\t\t").append(transaction);
        }
        return "Name: " + name + "\nIncoming Interest: " + incomingInterest + "\nOutgoing Interest: " + outgoingInterest + "\n" + str;
    }

}
