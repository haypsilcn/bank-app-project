package haypsilcn.bankappproject;

import bank.Bank;
import bank.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class AccountController /*implements Initializable*/ {

    private final ObservableList<Transaction> transactionsList = FXCollections.observableArrayList();
    private final AtomicReference<Transaction> selectedTransaction = new AtomicReference<>();
    public Text accountNameTextField;
    private Bank bank;
    private Stage stage;
    private Scene scene;

    public MenuBar menuBar;
    public ListView<Transaction> transactionsListView;
    public AnchorPane root;

    private void updateListView(List<Transaction> listTransaction) {
        transactionsList.clear();
        transactionsList.addAll(listTransaction);
        transactionsListView.setItems(transactionsList);
    }

    public void setUpData(Bank bankFromPreController, String account) {

        bank = bankFromPreController;
        accountNameTextField.setText(account + "[" + bank.getAccountBalance(account) + "€]");
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

        Menu viewMenu = new Menu("View");
        MenuItem mainView = new MenuItem("Main View");
        MenuItem reload = new MenuItem("Reload");

        viewMenu.getItems().addAll(mainView, reload);

        mainView.setAccelerator(new KeyCodeCombination(KeyCode.BACK_SPACE));
        reload.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));      // triggers reload account list view when pressing key combination Ctrl + R

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
            stage.setTitle("MainView");
            stage.setScene(scene);
            stage.show();
        });
        reload.setOnAction(event -> {
            final int selectedID = transactionsListView.getSelectionModel().getSelectedIndex();
            updateListView(bank.getTransactions(account));
            transactionsListView.getSelectionModel().select(selectedID);
        });

        Menu editMenu = new Menu("Edit");
        MenuItem add = new MenuItem("New Transaction");
        MenuItem delete = new MenuItem("Delete Transaction");

        editMenu.getItems().addAll(add, delete);

        add.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));     // triggers reload account list view when pressing key combination Ctrl + N
        delete.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));

        menuBar.getMenus().addAll(backArrow, editMenu, viewMenu);
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
