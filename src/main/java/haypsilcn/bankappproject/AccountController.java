package haypsilcn.bankappproject;

import bank.*;
import bank.exceptions.AccountDoesNotExistException;
import bank.exceptions.TransactionAlreadyExistsException;
import bank.exceptions.TransactionDoesNotExistException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class AccountController /*implements Initializable*/ {

    private final ObservableList<Transaction> transactionsList = FXCollections.observableArrayList();
    private final AtomicReference<Transaction> selectedTransaction = new AtomicReference<>();
    private Bank bank;
    private Stage stage;
    private Scene scene;
    private boolean ascOrder;
    private boolean defaultOrder = true;

    @FXML
    public Text accountNameTextField;
    @FXML
    public MenuBar menuBar;
    @FXML
    public ListView<Transaction> transactionsListView;
    @FXML
    public AnchorPane root;

    private boolean checkValidDateFormat(TextField day, TextField month, TextField year) {
        // 1900 < year <= 2050
        if (Integer.parseInt(year.getText()) < 1900 || Integer.parseInt(year.getText()) > 2050)
            return false;
        else {
            // 1 <= month <= 12
            if (Integer.parseInt(month.getText()) < 1 || Integer.parseInt(month.getText()) > 12)
                return false;
            else {
              if (Integer.parseInt(month.getText()) == 1 ||
                        Integer.parseInt(month.getText()) == 3 ||
                        Integer.parseInt(month.getText()) == 5 ||
                        Integer.parseInt(month.getText()) == 7 ||
                        Integer.parseInt(month.getText()) == 8 ||
                        Integer.parseInt(month.getText()) == 10 ||
                        Integer.parseInt(month.getText()) == 12)
                    return Integer.parseInt(day.getText()) >= 1 && Integer.parseInt(day.getText()) <= 31;
              else if (Integer.parseInt(month.getText()) == 4 ||
                      Integer.parseInt(month.getText()) == 6 ||
                      Integer.parseInt(month.getText()) == 9 ||
                      Integer.parseInt(month.getText()) == 11)
                    return Integer.parseInt(day.getText()) >= 1 && Integer.parseInt(day.getText()) <= 30;
              else
                    return Integer.parseInt(day.getText()) >= 1 && Integer.parseInt(day.getText()) <= 28;
            }
        }
    }

    private void setUpDialogAddTransaction(MenuItem menuItem, String name) {
        Dialog<Transaction> dialog = new Dialog<>();
        dialog.getDialogPane().setMinWidth(300);
        dialog.getDialogPane().setMinHeight(250);


        Label date = new Label("Date: ");
        Label description = new Label("Description: ");
        Label amount = new Label("Amount: ");
        // will be shown as "incoming interest" and "outgoing interest" when adding new Payment
        // or as "sender" and "recipient" when adding new Transfer
        Label incomingInterest_sender = new Label();
        Label outgoingInterest_recipient = new Label();



        TextField dayText = new TextField();
        TextField monthText = new TextField();
        TextField yearText = new TextField();
        TextField descriptionText = new TextField();
        TextField amountText = new TextField();
        TextField incomingInterest_senderText = new TextField();
        TextField outgoingInterest_recipientText = new TextField();

        dayText.setPromptText("dd");
        monthText.setPromptText("mm");
        yearText.setPromptText("yyyy");

        dayText.setPrefWidth(50);
        monthText.setPrefWidth(50);
        yearText.setPrefWidth(50);
        description.setPrefWidth(100);
        amountText.setPrefWidth(100);
        incomingInterest_senderText.setPrefWidth(100);
        outgoingInterest_recipientText.setPrefWidth(100);

        // make all TextField for day, month and accept only integer 0-9 with length of 1 or 2 digits
        for (TextField textField : Arrays.asList(dayText, monthText)) {
            textField.textProperty().addListener(((observableValue, oldValue, newValue) -> {
                if (!newValue.matches("\\d{0,2}"))
                    textField.setText(oldValue);
            }));
        }
        // TextField of year only accepts integer 0-9 with length of up to 4 digits
        yearText.textProperty().addListener(((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,4}"))
                yearText.setText(oldValue);
        }));
        // TextField of amount accepts decimal number with max 2 decimal points allow
        amountText.textProperty().addListener(((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d{0,2})?"))
                amountText.setText(oldValue);
        }));

        GridPane gridPane = new GridPane();
        GridPane dateGridPane = new GridPane();

        dateGridPane.add(dayText, 1, 1);
        dateGridPane.add(monthText, 2, 1);
        dateGridPane.add(yearText, 3, 1);

        gridPane.add(description, 1, 1);
        gridPane.add(descriptionText, 2, 1);
        gridPane.add(date, 1, 2);
        gridPane.add(dateGridPane, 2, 2);
        gridPane.add(amount, 1, 3);
        gridPane.add(amountText, 2, 3);
        gridPane.add(incomingInterest_sender, 1, 4);
        gridPane.add(incomingInterest_senderText, 2, 4);
        gridPane.add(outgoingInterest_recipient, 1, 5);
        gridPane.add(outgoingInterest_recipientText, 2, 5);

        dialog.getDialogPane().setContent(gridPane);
        dialog.setResizable(true);

        ButtonType okButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, buttonTypeCancel);

        dialog.show();
        descriptionText.requestFocus();

        Alert invalid = new Alert(Alert.AlertType.ERROR);

        if (menuItem.getText().equals("Payment")) {
            // TextField of incoming and outgoing interest accept decimal number with max 2 decimal points allow
            for (TextField textField : Arrays.asList(incomingInterest_senderText, outgoingInterest_recipientText)) {
                textField.textProperty().addListener(((observableValue, oldValue, newValue) -> {
                    if (!newValue.matches("\\d*(\\.\\d{0,2})?"))
                        textField.setText(oldValue);
                }));
            }
            dialog.setTitle("New Payment");
            dialog.setHeaderText("Add new payment to account [" + name + "]");

            incomingInterest_sender.setText("Incoming Interest: ");
            outgoingInterest_recipient.setText("Outgoing Interest: ");

            dialog.setResultConverter(buttonType ->  {
                if (buttonType == okButton) {
                    if (dayText.getText().isEmpty() ||
                            monthText.getText().isEmpty() ||
                            yearText.getText().isEmpty() ||
                            amountText.getText().isEmpty()) {
                        invalid.setContentText("Please insert valid value!");
                        invalid.showAndWait();
                    } else {
                        if (!checkValidDateFormat(dayText, monthText, yearText)) {
                            invalid.setContentText("Please insert valid date!");
                            invalid.showAndWait();
                        }
                        else {
                            Payment payment = new Payment(dayText.getText() + "-" + monthText.getText() + "-" + yearText.getText(),
                                    Double.parseDouble(amountText.getText()),
                                    descriptionText.getText(),
                                    Double.parseDouble(incomingInterest_senderText.getText()),
                                    Double.parseDouble(outgoingInterest_recipientText.getText()));
                            try {
                                bank.addTransaction(name, payment);
                            } catch (TransactionAlreadyExistsException e) {
                                invalid.setContentText("Duplicated payment!");
                                invalid.showAndWait();
                                System.out.println(e.getMessage());
                            } catch (AccountDoesNotExistException e) {
                                System.out.println(e.getMessage());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            updateListView(bank.getTransactions(name));
                            accountNameTextField.setText(name + " [" + bank.getAccountBalance(name) + "€]");
                            transactionsListView.getSelectionModel().select(payment);
                            selectedTransaction.set(transactionsListView.getSelectionModel().getSelectedItem());
                        }
                    }
                }
                return null;
            });
        }  else {
            incomingInterest_sender.setText("Sender: ");
            outgoingInterest_recipient.setText("Recipient: ");

            // TextField of sender and recipient accept letters not numbers
            for (TextField textField : Arrays.asList(incomingInterest_senderText, outgoingInterest_recipientText)) {
                textField.textProperty().addListener(((observableValue, oldValue, newValue) -> {
                    if (!newValue.matches("[a-zA-Z]*"))
                        textField.setText(oldValue);
                }));
            }
            if (menuItem.getText().equals("Incoming Transfer")) {
                // for a new incoming transfer of an account X, X is the recipient
                // therefore the TextField of recipient should not be edited
                outgoingInterest_recipientText.setText(name);
                outgoingInterest_recipientText.setEditable(false);

                dialog.setTitle("New Incoming Transfer");
                dialog.setHeaderText("Add new incoming transfer to account [" + name + "]");

                dialog.setResultConverter(buttonType -> {
                    if (buttonType == okButton) {
                        if (dayText.getText().isEmpty() ||
                                monthText.getText().isEmpty() ||
                                yearText.getText().isEmpty() ||
                                amountText.getText().isEmpty() ||
                                incomingInterest_senderText.getText().isEmpty()) {
                            invalid.setContentText("Please insert valid value!");
                            invalid.showAndWait();
                        } else {
                            if (checkValidDateFormat(dayText, monthText, yearText)) {
                                invalid.setContentText("Please insert valid date!");
                                invalid.showAndWait();
                            }
                            else {
                                IncomingTransfer incomingTransfer = new IncomingTransfer(dayText.getText(),
                                        Double.parseDouble(amountText.getText()),
                                        descriptionText.getText(),
                                        incomingInterest_senderText.getText(),
                                        outgoingInterest_recipientText.getText());
                                try {
                                    bank.addTransaction(name, incomingTransfer);
                                } catch (TransactionAlreadyExistsException e) {
                                    invalid.setContentText("Duplicated incoming transfer!");
                                    invalid.showAndWait();
                                    System.out.println(e.getMessage());
                                } catch (AccountDoesNotExistException e) {
                                    System.out.println(e.getMessage());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                updateListView(bank.getTransactions(name));
                                accountNameTextField.setText(name + " [" + bank.getAccountBalance(name) + "€]");
                                transactionsListView.getSelectionModel().select(incomingTransfer);
                                selectedTransaction.set(transactionsListView.getSelectionModel().getSelectedItem());
                            }
                        }
                    }
                    return null;
                });
            } else  {
                // for a new outgoing transfer of an account X, X is the sender
                // therefore the TextField of sender should not be edited
                incomingInterest_senderText.setText(name);
                incomingInterest_senderText.setEditable(false);

                dialog.setTitle("New Outgoing Transfer");
                dialog.setHeaderText("Add new outgoing transfer to account [" + name + "]");

                dialog.setResultConverter(buttonType -> {
                    if (buttonType == okButton) {
                        if (dayText.getText().isEmpty() ||
                                monthText.getText().isEmpty() ||
                                yearText.getText().isEmpty() ||
                                amountText.getText().isEmpty() ||
                                incomingInterest_senderText.getText().isEmpty()) {
                            invalid.setContentText("Please insert valid value!");
                            invalid.showAndWait();
                        } else {
                            if (checkValidDateFormat(dayText, monthText, yearText)) {
                                invalid.setContentText("Please insert valid date!");
                                invalid.showAndWait();
                            }
                            else {
                                OutgoingTransfer outgoingTransfer = new OutgoingTransfer(dayText.getText(),
                                        Double.parseDouble(amountText.getText()),
                                        descriptionText.getText(),
                                        incomingInterest_senderText.getText(),
                                        outgoingInterest_recipientText.getText());
                                try {
                                    bank.addTransaction(name, outgoingTransfer);
                                } catch (TransactionAlreadyExistsException e) {
                                    invalid.setContentText("Duplicated outgoing transfer!");
                                    invalid.showAndWait();
                                    System.out.println(e.getMessage());
                                } catch (AccountDoesNotExistException e) {
                                    System.out.println(e.getMessage());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                updateListView(bank.getTransactions(name));
                                accountNameTextField.setText(name + " [" + bank.getAccountBalance(name) + "€]");
                                transactionsListView.getSelectionModel().select(outgoingTransfer);
                                selectedTransaction.set(transactionsListView.getSelectionModel().getSelectedItem());
                            }
                        }
                    }
                    return null;
                });
            }
        }
    }

    private void updateListView(List<Transaction> listTransaction) {
        transactionsList.clear();
        transactionsList.addAll(listTransaction);
        transactionsListView.setItems(transactionsList);
    }

    public void setUpData(Bank bankFromPreController, String account) {

        bank = bankFromPreController;
        accountNameTextField.setText(account + " [" + bank.getAccountBalance(account) + "€]");
        updateListView(bank.getTransactions(account));

        transactionsListView.getSelectionModel().selectFirst();
        selectedTransaction.set(transactionsListView.getSelectionModel().getSelectedItem());

        // setting menu bar
        ImageView goBackImg = new ImageView("file:src/main/resources/img/back-arrow.png");
        goBackImg.setFitHeight(15);
        goBackImg.setFitWidth(15);

        Label goBack = new Label();
        goBack.setOnMouseClicked(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("main-view.fxml")));
                root = loader.load();

                MainController mainController = loader.getController();
                mainController.getSelectedAccount(account);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("MainView");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        });
        goBack.setGraphic(goBackImg);
        Menu backArrow = new Menu();
        backArrow.setGraphic(goBack);

        Menu fileMenu = new Menu("File");
        MenuItem mainView = new MenuItem("Main View");
        MenuItem reload = new MenuItem("Reload");
        MenuItem exit = new MenuItem("Exit");

        fileMenu.getItems().addAll(mainView, reload, exit);

        mainView.setAccelerator(new KeyCodeCombination(KeyCode.BACK_SPACE));
        reload.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));      // triggers reload account list view when pressing key combination Ctrl + R
        exit.setAccelerator(new KeyCodeCombination(KeyCode.F4, KeyCombination.ALT_DOWN));           // exit program when pressing ALT + F4


        mainView.setOnAction(event -> {
            stage = (Stage) root.getScene().getWindow();
            try {
                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("main-view.fxml")));
                root = loader.load();

                MainController mainController = loader.getController();
                mainController.getSelectedAccount(account);
            } catch (IOException e) {
                e.printStackTrace();
            }

            scene = new Scene(root);
            stage.setTitle("Main View");
            stage.setScene(scene);
            stage.show();
        });
        reload.setOnAction(event -> {
            final int selectedID = transactionsListView.getSelectionModel().getSelectedIndex();
            updateListView(bank.getTransactions(account));
            transactionsListView.getSelectionModel().select(selectedID);
        });

        Menu editMenu = new Menu("Edit");
        Menu addMenu = new Menu("New Transaction");
        MenuItem delete = new MenuItem("Delete Transaction");
        MenuItem payment = new MenuItem("Payment");
        MenuItem incomingTransfer = new MenuItem("Incoming Transfer");
        MenuItem outgoingTransfer = new MenuItem("Outgoing Transfer");

        addMenu.getItems().addAll(payment, incomingTransfer, outgoingTransfer);
        editMenu.getItems().addAll(addMenu, delete);

        delete.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));

        payment.setOnAction(event -> setUpDialogAddTransaction(payment, account));
        incomingTransfer.setOnAction(event -> setUpDialogAddTransaction(incomingTransfer, account));
        outgoingTransfer.setOnAction(event -> setUpDialogAddTransaction(outgoingTransfer, account));
        delete.setOnAction(event -> {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Delete transaction confirmation");
            confirmation.setContentText("Do you wanna delete this transaction?");
            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    bank.removeTransaction(account, selectedTransaction.get());
                } catch (TransactionDoesNotExistException | IOException e) {
                    e.printStackTrace();
                }
                System.out.println("[" + selectedTransaction.toString().replace("\n", "]") + " is deleted");
                if (defaultOrder)
                    updateListView(bank.getTransactions(account));
                else {
                    if (ascOrder)
                        updateListView(bank.getTransactionsSorted(account, true));
                    else
                        updateListView(bank.getTransactionsSorted(account, false));
                }
                accountNameTextField.setText(account + " [" + bank.getAccountBalance(account) + "€]");
            }
        });

        Menu sorting = new Menu("Sorting");
        RadioMenuItem ascending = new RadioMenuItem ("Ascending");
        RadioMenuItem descending = new RadioMenuItem("Descending");
        RadioMenuItem positive = new RadioMenuItem("Positive");
        RadioMenuItem negative = new RadioMenuItem("Negative");
        RadioMenuItem default_ = new RadioMenuItem("Default");

        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(ascending, descending, positive, negative, default_);

        sorting.getItems().addAll(ascending, descending, positive, negative, default_);

        default_.setSelected(true);    // default sorting selected as default when first open account view
        ascending.setOnAction(event -> {
            selectedTransaction.set(transactionsListView.getSelectionModel().getSelectedItem());
            defaultOrder = false;
            ascOrder = true;     // list view will keep ascending order after reloading, deleting or adding new account
            updateListView(bank.getTransactionsSorted(account, true));
            transactionsListView.getSelectionModel().select(selectedTransaction.get());        // auto re-select same item after changing sorting order of list view
        });
        descending.setOnAction(event -> {
            selectedTransaction.set(transactionsListView.getSelectionModel().getSelectedItem());
            defaultOrder = false;
            ascOrder = false;    // list view will keep descending order after reloading, deleting or adding new account
            updateListView(bank.getTransactionsSorted(account, false));
            transactionsListView.getSelectionModel().select(selectedTransaction.get());        // auto re-select same item after changing sorting order of list view
        });
        positive.setOnAction(event -> {
            selectedTransaction.set(transactionsListView.getSelectionModel().getSelectedItem());
            updateListView(bank.getTransactionsByType(account, true));
            if (bank.getTransactionsByType(account, true).contains(selectedTransaction.get()))
                transactionsListView.getSelectionModel().select(selectedTransaction.get());
            else {
                transactionsListView.getSelectionModel().selectFirst();
                selectedTransaction.set(transactionsListView.getSelectionModel().getSelectedItem());
            }        });
        negative.setOnAction(event -> {
            selectedTransaction.set(transactionsListView.getSelectionModel().getSelectedItem());
            updateListView(bank.getTransactionsByType(account, false));
            if (bank.getTransactionsByType(account, false).contains(selectedTransaction.get()))
                transactionsListView.getSelectionModel().select(selectedTransaction.get());
            else {
                transactionsListView.getSelectionModel().selectFirst();
                selectedTransaction.set(transactionsListView.getSelectionModel().getSelectedItem());
            }
        });
        default_.setOnAction(event -> {
            selectedTransaction.set(transactionsListView.getSelectionModel().getSelectedItem());
            defaultOrder = true;
            ascOrder = false;
            updateListView(bank.getTransactions(account));
            transactionsListView.getSelectionModel().select(selectedTransaction.get());
        });


        menuBar.getMenus().addAll(backArrow, fileMenu, editMenu, sorting);
    }

    /*@Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // setting menu bar
        ImageView goBackImg = new ImageView("file:src/main/resources/img/back-arrow.png");
        goBackImg.setFitHeight(15);
        goBackImg.setFitWidth(15);

        Label goBack = new Label();
        goBack.setOnMouseClicked(event -> {
            try {
                root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main-view.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("MainView");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        });
        goBack.setGraphic(goBackImg);
        Menu backArrow = new Menu();
        backArrow.setAccelerator(new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_ANY));
        backArrow.setGraphic(goBack);

        menuBar.getMenus().addAll(backArrow);

    }*/
}
