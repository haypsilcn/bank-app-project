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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class AccountController /*implements Initializable*/ {

    private final ObservableList<Transaction> transactionsList = FXCollections.observableArrayList();
    private final AtomicReference<Transaction> selectedTransaction = new AtomicReference<>();
    private Bank bank;
    private Stage stage;
    private Scene scene;

    @FXML
    public Text accountNameTextField;
    @FXML
    public MenuBar menuBar;
    @FXML
    public ListView<Transaction> transactionsListView;
    @FXML
    public AnchorPane root;

    private void setUpDialogAddTransaction(MenuItem menuItem, String name) {
        Dialog<Transaction> dialog = new Dialog<>();
        dialog.getDialogPane().setMinWidth(350);
        dialog.getDialogPane().setMinHeight(250);
        GridPane gridPane = new GridPane();

        Label date = new Label("Date: ");
        Label description = new Label("Description: ");
        Label amount = new Label("Amount: ");
        Label incomingInterest_sender = new Label();
        Label outgoingInterest_recipient = new Label();

        TextField dateText = new TextField();
        TextField descriptionText = new TextField();
        TextField amountText = new TextField();
        TextField incomingInterest_senderText = new TextField();
        TextField outgoingInterest_recipientText = new TextField();

        gridPane.add(date, 1, 1);
        gridPane.add(dateText, 2, 1);
        gridPane.add(description, 1, 2);
        gridPane.add(descriptionText, 2, 2);
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
        dateText.requestFocus();

        Alert invalid = new Alert(Alert.AlertType.ERROR);

        if (menuItem.getText().equals("Payment")) {
            dialog.setTitle("New Payment");
            dialog.setHeaderText("Add new payment to account [" + name + "]");

            incomingInterest_sender.setText("Incoming Interest: ");
            outgoingInterest_recipient.setText("Outgoing Interest: ");

            dialog.setResultConverter(buttonType ->  {
                if (buttonType == okButton) {
                    if (Objects.equals(dateText.getText(), "") ||
                            Objects.equals(descriptionText.getText(),"") ||
                            Objects.equals(amountText.getText(), "") ||
                            Objects.equals(incomingInterest_senderText.getText(), "") ||
                            Objects.equals(outgoingInterest_recipientText.getText(), "")) {
                        invalid.setContentText("Please insert valid value!");
                        invalid.showAndWait();
                    } else {
                        Payment payment = new Payment(dateText.getText(),
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
                return null;
            });
        }  else {
            incomingInterest_sender.setText("Sender: ");
            outgoingInterest_recipient.setText("Recipient: ");
            if (menuItem.getText().equals("Incoming Transfer")) {

                dialog.setTitle("New Incoming Transfer");
                dialog.setHeaderText("Add new incoming transfer to account [" + name + "]");

                dialog.setResultConverter(buttonType -> {
                    if (buttonType == okButton) {
                        if (Objects.equals(dateText.getText(), "") ||
                                Objects.equals(descriptionText.getText(),"") ||
                                Objects.equals(amountText.getText(), "") ||
                                Objects.equals(incomingInterest_senderText.getText(), "") ||
                                Objects.equals(outgoingInterest_recipientText.getText(), "")) {
                            invalid.setContentText("Please insert valid value!");
                            invalid.showAndWait();
                        } else {
                            IncomingTransfer incomingTransfer = new IncomingTransfer(dateText.getText(),
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
                    return null;
                });
            } else  {
                dialog.setTitle("New Outgoing Transfer");
                dialog.setHeaderText("Add new outgoing transfer to account [" + name + "]");

                dialog.setResultConverter(buttonType -> {
                    if (buttonType == okButton) {
                        if (Objects.equals(dateText.getText(), "") ||
                                Objects.equals(descriptionText.getText(),"") ||
                                Objects.equals(amountText.getText(), "") ||
                                Objects.equals(incomingInterest_senderText.getText(), "") ||
                                Objects.equals(outgoingInterest_recipientText.getText(), "")) {
                            invalid.setContentText("Please insert valid value!");
                            invalid.showAndWait();
                        } else {
                            OutgoingTransfer outgoingTransfer = new OutgoingTransfer(dateText.getText(),
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
                updateListView(bank.getTransactions(account));
                accountNameTextField.setText(account + " [" + bank.getAccountBalance(account) + "€]");
            }
        });

        menuBar.getMenus().addAll(backArrow, fileMenu, editMenu);
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
