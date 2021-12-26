package haypsilcn.bankappproject;

import bank.Bank;
import bank.exceptions.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class MainController implements Initializable {

    private Stage stage;
    private Scene scene;
    private boolean success;
    private final AtomicReference<String> selectedAccount = new AtomicReference<>();

    private final ObservableList<String> accountList = FXCollections.observableArrayList();
    private final Bank globalBank = new Bank("Global Bank", 0.12, 0.09);

    @FXML
    public MenuBar menuBar;
    @FXML
    private Text text;
    @FXML
    private ListView<String> accountsListView;
    @FXML
    private Parent root;

    public void getSelectedAccount(String account) {
        accountsListView.getSelectionModel().select(account);
        selectedAccount.set(account);
    }


    private void updateListView() {
        accountList.clear();
        accountList.addAll(globalBank.getAllAccounts());
        accountList.sort(Comparator.naturalOrder());
        accountsListView.setItems(accountList);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        updateListView();

        // when first run the program, first account in the list will be selected
        // after that, a correct account will be selected when returning from Account-View
        // i.e, returning from [Zack] Account-View so Zack will be automatic selected
        if (accountsListView.getSelectionModel().getSelectedIndex() == -1) {
            accountsListView.getSelectionModel().selectFirst();
            selectedAccount.set(accountsListView.getSelectionModel().getSelectedItem());
        }

        // setting menu bar
        ImageView goBackImg = new ImageView("file:src/main/resources/img/back-arrow.png");
        goBackImg.setFitHeight(15);
        goBackImg.setFitWidth(15);

        Label goBack = new Label();
        goBack.setGraphic(goBackImg);
        Menu backArrow = new Menu();
        backArrow.setGraphic(goBack);

        Menu viewMenu = new Menu("View");
        MenuItem view = new MenuItem("View Account");
        MenuItem reload = new MenuItem("Reload");

        viewMenu.getItems().addAll(view, reload);

        view.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.ALT_DOWN));    // triggers account view when pressing the key combination ALT + V
        reload.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));      // triggers reload account list view when pressing key combination Ctrl + R

        reload.setOnAction(event -> {
            final int selectedID = accountsListView.getSelectionModel().getSelectedIndex();
            updateListView();
            accountsListView.getSelectionModel().select(selectedID);        // auto re-select same row after reloading list view

        });

        view.setOnAction(event -> {
            stage = (Stage) root.getScene().getWindow();

            try {
                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("account-view.fxml")));
                root = loader.load();

                AccountController accountController = loader.getController();
                accountController.setUpData(globalBank, selectedAccount.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }


            scene = new Scene(root);
            stage.setTitle(selectedAccount.toString());
            stage.setScene(scene);
            stage.show();
        });

        Menu editMenu = new Menu("Edit");
        MenuItem add = new MenuItem("New Account");
        MenuItem edit = new MenuItem("Edit Account");
        MenuItem delete = new MenuItem("Delete Account");

        editMenu.getItems().addAll(add, edit, delete);

        add.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));     // triggers reload account list view when pressing key combination Ctrl + N
        edit.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));       // triggers reload account list view when pressing key combination Ctrl + E
        delete.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));


        add.setOnAction(event -> {
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Add new account");
            dialog.setHeaderText("Add a new account to bank");
            dialog.getDialogPane().setMinWidth(300);


            Label nameLabel = new Label("Name: ");
            TextField nameTextFiel = new TextField();

            GridPane grid = new GridPane();
            grid.add(nameLabel, 1, 1);
            grid.add(nameTextFiel, 2, 1);
            dialog.getDialogPane().setContent(grid);
            dialog.setResizable(true);

            ButtonType buttonTypeOk = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

            dialog.setResultConverter(buttonType -> {
                if (buttonType == buttonTypeOk) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);

                    try {
                        globalBank.createAccount(nameTextFiel.getText());
                        success = true;
                    } catch (AccountAlreadyExistsException e) {
                        alert.setContentText("Duplicated account!");
                        alert.showAndWait();
                        success = false;
                        System.out.println(e.getMessage());
                    } catch (AccountInvalidException e) {
                        alert.setContentText("Please insert a valid name!");
                        alert.showAndWait();
                        success = false;
                        System.out.println(e.getMessage());
                    } catch (IOException e) {
                        success = false;
                        e.printStackTrace();
                    }

                    if (success) {
                        updateListView();
                        accountsListView.getSelectionModel().select(nameTextFiel.getText());
                        System.out.println(accountsListView.getSelectionModel().getSelectedItem());
                        selectedAccount.set(accountsListView.getSelectionModel().getSelectedItem());
                    } else
                        accountsListView.getSelectionModel().select(selectedAccount.toString());
                }
                return null;
            });

            dialog.show();
        });

        edit.setOnAction(event -> {
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Edit account");
            dialog.setHeaderText("Edit account [" + selectedAccount + "] to new name");
            dialog.getDialogPane().setMinWidth(300);


            Label nameLabel = new Label("New name: ");
            TextField nameTextFiel = new TextField();

            GridPane grid = new GridPane();
            grid.add(nameLabel, 1, 1);
            grid.add(nameTextFiel, 2, 1);
            dialog.getDialogPane().setContent(grid);
            dialog.setResizable(true);

            ButtonType buttonTypeOk = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

            dialog.setResultConverter(buttonType -> {
                if (buttonType == buttonTypeOk) {

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    try {
                        globalBank.editAccount(selectedAccount.toString(),nameTextFiel.getText());
                        success = true;
                    } catch (AccountAlreadyExistsException e) {
                        alert.setContentText("New account name is duplicated!");
                        alert.showAndWait();
                        success = false;
                        System.out.println(e.getMessage());
                    } catch (AccountInvalidException e) {
                        alert.setContentText("Please insert a valid name!");
                        alert.showAndWait();
                        success = false;
                        System.out.println(e.getMessage());
                    } catch (AccountDoesNotExistException e) {
                        alert.setContentText("Account is not exist to be edited!");
                        alert.showAndWait();
                        success = false;
                        System.out.println(e.getMessage());
                    } catch (IOException e) {
                        success = false;
                        e.printStackTrace();
                    }

                    if (success) {
                        updateListView();
                        accountsListView.getSelectionModel().select(nameTextFiel.getText());
                        selectedAccount.set(nameTextFiel.getText());
                    } else
                        accountsListView.getSelectionModel().select(selectedAccount.toString());
                }
                return null;
            });
            dialog.show();
        });

        delete.setOnAction(event -> {

            ButtonType confirm = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "", confirm, cancel );
            Alert alert = new Alert(Alert.AlertType.ERROR);

            confirmation.setTitle("Delete account confirmation");
            confirmation.setHeaderText("Account [" + selectedAccount + "] will be deleted");
            confirmation.setContentText("Please confirm");

            final int selectedID = accountsListView.getSelectionModel().getSelectedIndex();

            if (accountsListView.getSelectionModel().getSelectedIndex() != -1) {
                Optional<ButtonType> result = confirmation.showAndWait();
                if (result.isPresent() && result.get() == confirm) {
                    try {
                        globalBank.deleteAccount(selectedAccount.toString());
                        success = true;
                    } catch (AccountDoesNotExistException e) {
                        success = false;
                        System.out.println(e.getMessage());
                    } catch (IOException e) {
                        success = false;
                        e.printStackTrace();
                    }


                    if (success) {
                        System.out.println(selectedAccount + " is deleted");

                        updateListView();
                        if (selectedID == 0)
                            accountsListView.getSelectionModel().select(selectedID);
                        else
                            accountsListView.getSelectionModel().select(selectedID - 1);
                        selectedAccount.set(accountsListView.getSelectionModel().getSelectedItem());
                    }
                }
            } else {
                alert.setContentText("There is no account exist!");
                alert.showAndWait();
            }
        });



        menuBar.getMenus().addAll(backArrow, editMenu, viewMenu);


        // when an account in list view is clicked
        accountsListView.setOnMouseClicked(mouseEvent -> {

            ContextMenu contextMenu = new ContextMenu();

            // only when list view is not empty, then list view can have multiple menuItem when right-clicked
            if (accountsListView.getSelectionModel().getSelectedIndex() != -1) {

                MenuItem viewAccount = new MenuItem("View Account");
                MenuItem editAccount = new MenuItem("Edit account");
                MenuItem deleteAccount = new MenuItem("Delete account");

                contextMenu.getItems().addAll(viewAccount,editAccount, deleteAccount);
                accountsListView.setContextMenu(contextMenu);
                selectedAccount.set(accountsListView.getSelectionModel().getSelectedItem());

                System.out.println(accountsListView.getSelectionModel().getSelectedItem() + " is selected");

                // goes to AccountView if double-click on item
                if (mouseEvent.getClickCount() == 2)
                    try {
                        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("account-view.fxml")));
                        root = loader.load();

                        AccountController accountController = loader.getController();
                        accountController.setUpData(globalBank, selectedAccount.toString());

                        stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
                        scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                editAccount.setOnAction(edit.getOnAction());
                deleteAccount.setOnAction(delete.getOnAction());
                viewAccount.setOnAction(event -> {
                    stage = (Stage) root.getScene().getWindow();

                    try {
                        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("account-view.fxml")));
                        root = loader.load();

                        AccountController accountController = loader.getController();
                        accountController.setUpData(globalBank, selectedAccount.toString().replace("[", "").replace("]", ""));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    scene = new Scene(root);
                    stage.setTitle(selectedAccount.toString());
                    stage.setScene(scene);
                    stage.show();
                });
            }
            // if list view is empty then it can only have one option "add new account" when right-clicked
            else {
                MenuItem addAccount = new MenuItem("New Account");
                contextMenu.getItems().addAll(addAccount);
                accountsListView.setContextMenu(contextMenu);

                addAccount.setOnAction(add.getOnAction());
            }


        });

    }
}
