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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class MainController implements Initializable {

    private Stage stage;
    private Scene scene;
    private boolean success;
    private boolean ascOrder = true;
    private final AtomicReference<String> selectedAccount = new AtomicReference<>();

    private final ObservableList<String> accountObservableList = FXCollections.observableArrayList();
    private final Bank globalBank = new Bank("Global Bank", 0.12, 0.09);

    @FXML
    public MenuBar menuBar;
    @FXML
    private ListView<String> accountsListView;
    @FXML
    private Parent root;

    public void getSelectedAccount(String account) {
        accountsListView.getSelectionModel().select(account);
        selectedAccount.set(account);
    }


    private void updateListView(boolean ascending) {
        accountObservableList.clear();
        accountObservableList.addAll(globalBank.getAllAccounts());
        if (ascending)
            accountObservableList.sort(Comparator.naturalOrder());
        else 
            accountObservableList.sort(Comparator.reverseOrder());
        accountsListView.setItems(accountObservableList);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        updateListView(ascOrder);    // ListView sorted in ascending as default when first run program

        // when first run the program, first account in the list will be selected
        // after that, a correct account will be selected when returning from Account-View
        // i.e, returning from [Zack] Account-View so Zack will be automatic selected in Main-View
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
        backArrow.setDisable(true);     // go-back only enable in account view
        backArrow.setGraphic(goBack);

        Menu fileMenu = new Menu("File");
        MenuItem view = new MenuItem("View Account");
        MenuItem reload = new MenuItem("Reload");
        MenuItem exit = new MenuItem("Exit");

        fileMenu.getItems().addAll(view, reload, exit);

        view.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.ALT_DOWN));            // triggers account view when pressing the key combination ALT + V
        reload.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));      // triggers reload account list view when pressing key combination Ctrl + R
        exit.setAccelerator(new KeyCodeCombination(KeyCode.F4, KeyCombination.ALT_DOWN));           // exit program when pressing ALT + F4

        reload.setOnAction(event -> {
            updateListView(ascOrder);
            accountsListView.getSelectionModel().select(selectedAccount.toString());        // auto re-select same row after reloading list view
            System.out.println("ListView reloaded");
        });
        view.setOnAction(event -> {

            if (accountsListView.getSelectionModel().getSelectedIndex() != -1) {
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
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("There is no account to be viewed!");
                alert.showAndWait();
            }
        });
        exit.setOnAction(event -> {
            stage = (Stage) root.getScene().getWindow();
            stage.close();
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
            dialog.setTitle("New Account");
            dialog.setHeaderText("Add a new account to bank");
            dialog.getDialogPane().setMinWidth(300);


            Label nameLabel = new Label("Name: ");
            TextField nameTextFiel = new TextField();
            // TextField of name only accepts letters not numbers
            nameTextFiel.textProperty().addListener(((observableValue, oldValue, newValue) -> {
                if (!newValue.matches("[a-zA-Z]*"))
                    nameTextFiel.setText(oldValue);
            }));

            GridPane grid = new GridPane();
            grid.add(nameLabel, 1, 1);
            grid.add(nameTextFiel, 2, 1);
            dialog.getDialogPane().setContent(grid);
            dialog.setResizable(true);

            ButtonType buttonTypeOk = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

            dialog.show();
            nameTextFiel.requestFocus();    // TextFiled of Name will be autofocused after dialog shows up

            dialog.setResultConverter(buttonType -> {

                if (buttonType == buttonTypeOk) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);

                    if (nameTextFiel.getText().isEmpty()) {
                        alert.setContentText("Please insert valid value!");
                        alert.showAndWait();
                    } else {
                        // capitalize the first letter of account name
                        nameTextFiel.setText(nameTextFiel.getText().substring(0, 1).toUpperCase() + nameTextFiel.getText().substring(1));

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
                            updateListView(ascOrder);
                            accountsListView.getSelectionModel().select(nameTextFiel.getText());
                            selectedAccount.set(accountsListView.getSelectionModel().getSelectedItem());
                        } else
                            accountsListView.getSelectionModel().select(selectedAccount.toString());
                    }
                }
                return null;
            });
        });

        edit.setOnAction(event -> {
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Edit account");
            dialog.setHeaderText("Edit account [" + selectedAccount + "] to new name");
            dialog.getDialogPane().setMinWidth(300);


            Label nameLabel = new Label("New name: ");
            TextField nameTextFiel = new TextField();
            // TextField of name only accepts letters not numbers
            nameTextFiel.textProperty().addListener(((observableValue, oldValue, newValue) -> {
                if (!newValue.matches("[a-zA-Z]*"))
                    nameTextFiel.setText(oldValue);
            }));

            GridPane grid = new GridPane();
            grid.add(nameLabel, 1, 1);
            grid.add(nameTextFiel, 2, 1);
            dialog.getDialogPane().setContent(grid);
            dialog.setResizable(true);

            ButtonType buttonTypeOk = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

            dialog.show();
            nameTextFiel.requestFocus();

            dialog.setResultConverter(buttonType -> {
                if (buttonType == buttonTypeOk) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);

                    if (nameTextFiel.getText().isEmpty()) {
                        alert.setContentText("Please insert valid value!");
                        alert.showAndWait();
                    } else {
                        // capitalize the first letter of account name
                        nameTextFiel.setText(nameTextFiel.getText().substring(0, 1).toUpperCase() + nameTextFiel.getText().substring(1));

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
                            updateListView(ascOrder);
                            accountsListView.getSelectionModel().select(nameTextFiel.getText());
                            selectedAccount.set(nameTextFiel.getText());
                        } else
                            accountsListView.getSelectionModel().select(selectedAccount.toString());
                    }
                }
                return null;
            });
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

            // check whether list view is empty or not
            // if it is NOT empty then an element in list view can be deleted
            // an Alert of Confirmation will be shown in this case
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

                        updateListView(ascOrder);
                        if (selectedID == 0)    // if first element is deleted, then first new element will be selected
                            accountsListView.getSelectionModel().select(selectedID);
                        else        // after an element is deleted, then the element above deleted element will be selected
                            accountsListView.getSelectionModel().select(selectedID - 1);
                        selectedAccount.set(accountsListView.getSelectionModel().getSelectedItem());
                    }
                }
            }
            // in case list view is EMPTY then no element can be deleted
            // therefore an Alert of Error will be shown
            else {
                alert.setContentText("There is no account exist!");
                alert.showAndWait();
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

        ascending.setSelected(true);    // ascending sorting selected as default when first run program
        ascending.setOnAction(event -> {
            ascOrder = true;     // list view will keep ascending order after reloading, deleting or adding new account
            updateListView(true);
            accountsListView.getSelectionModel().select(selectedAccount.toString());        // auto re-select same item after changing sorting order of list view
        });
        descending.setOnAction(event -> {
            ascOrder = false;    // list view will keep descending order after reloading, deleting or adding new account
            updateListView(false);
            accountsListView.getSelectionModel().select(selectedAccount.toString());        // auto re-select same item after changing sorting order of list view
        });
        // disable positive, negative and default sorting
        // those only work in account view for sorting transactions
        positive.setDisable(true);
        negative.setDisable(true);
        default_.setDisable(true);

        // add all menus to MenuBar
        menuBar.getMenus().addAll(backArrow, fileMenu, editMenu, sorting);


        // when an account in list view is clicked
        accountsListView.setOnMouseClicked(mouseEvent -> {

            ContextMenu contextMenu = new ContextMenu();

            // only when list view is not empty, then list view can have multiple menuItem when right-clicked
            if (accountsListView.getSelectionModel().getSelectedIndex() != -1) {

                MenuItem viewAccount = new MenuItem("View Account");
                MenuItem editAccount = new MenuItem("Edit account");
                MenuItem deleteAccount = new MenuItem("Delete account");

                contextMenu.getItems().addAll(viewAccount, editAccount, deleteAccount);
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
                        stage.setTitle(selectedAccount.toString());
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


        // update selectedAccount when navigating list view elements by arrow keys
        accountsListView.addEventHandler(KeyEvent.KEY_RELEASED, keyEvent ->
           selectedAccount.set(accountsListView.getSelectionModel().getSelectedItem())
        );
    }
}
