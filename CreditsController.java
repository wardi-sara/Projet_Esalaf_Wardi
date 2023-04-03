package com.example.esalaf;

import com.exemple.model.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;


public class CreditsController implements Initializable {


    @FXML
    private TableView<CreditWithNames> creditsTable;


    @FXML
    private TableColumn<CreditWithNames, String> clientNameColumn;

    @FXML
    private TableColumn<CreditWithNames, String> productNameColumn;

    @FXML
    private TableColumn<CreditWithNames, Integer> creditAmountColumn;

    @FXML
    private TableColumn<CreditWithNames, LocalDate> creditDateColumn;

    @FXML
    private ChoiceBox nomclient;
    @FXML
    private ChoiceBox nomproduit;
    @FXML
    private TextField qte;
    @FXML
    private DatePicker date;
    @FXML
    private TextField montant;
    private Connection connection;

    private Button deleteCreditButton;

    public CreditsController() throws SQLException {
    }

    public String getProductName(Long id) {
        ProduitDAO produitDAO;
        try {
            produitDAO = new ProduitDAO();
            return produitDAO.getProductName(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getClientName(Long id) {
        ClientDAO clientDAO;
        try {
            clientDAO = new ClientDAO();
            return clientDAO.getClientName(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    ClientDAO clientDAO = new ClientDAO();
    ProduitDAO produitDAO = new ProduitDAO();


    public void updateTable() {
        try {
            // Update client name column
            clientNameColumn.setCellValueFactory(cellData -> {
                CreditWithNames credit = cellData.getValue();
                String clientName = credit.getClientName();
                return new SimpleStringProperty(String.valueOf(clientName));
            });

            // Update product name column
            productNameColumn.setCellValueFactory(cellData -> {
                CreditWithNames credit = cellData.getValue();
                String productName = credit.getProductName();
                return new SimpleStringProperty(String.valueOf(productName));
            });

            // Update credit amount and date columns
            creditAmountColumn.setCellValueFactory(new PropertyValueFactory<CreditWithNames, Integer>("qte"));
            creditDateColumn.setCellValueFactory(new PropertyValueFactory<CreditWithNames, LocalDate>("date"));

            // Set table data
            creditsTable.setItems(getCreditData());
        } catch (NullPointerException e) {
            // Handle exception
            System.err.println("Error updating table: " + e.getMessage());
            // Display error message to user
            // You could use an Alert dialog or some other means of displaying the error message
        }
    }

    public static ObservableList<CreditWithNames> getCreditData() {
        CreditDAO creditDAO = null;
        ObservableList<CreditWithNames> credits = FXCollections.observableArrayList();
        try {
            creditDAO = new CreditDAO();
            for (CreditWithNames credit : creditDAO.getAllWithNames()) {
                credits.add(credit);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return credits;
    }
    @FXML
    public void onUpdateCreditButtonClick() {
        CreditWithNames selectedCredit = creditsTable.getSelectionModel().getSelectedItem();
        if (selectedCredit != null) {
            // Create a new dialog to modify the credit
            Dialog<CreditWithNames> dialog = new Dialog<>();
            dialog.setTitle("Modifier crédit");

            // Set the button types (OK and Cancel)
            ButtonType okButtonType = new ButtonType("Modifier", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

            // Create the layout for the dialog
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            // Add the fields to the layout
            ChoiceBox<Client> nomclientBox = new ChoiceBox<>();
            try {
                nomclientBox.setItems(FXCollections.observableArrayList(clientDAO.getAll()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            nomclientBox.setValue(selectedCredit.getClient());
            grid.add(new Label("Nom du client:"), 0, 0);
            grid.add(nomclientBox, 1, 0);

            ChoiceBox<Produit> nomproduitBox = new ChoiceBox<>();
            try {
                nomproduitBox.setItems(FXCollections.observableArrayList(produitDAO.getAll()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            nomproduitBox.setValue(selectedCredit.getProduit());
            grid.add(new Label("Nom du produit:"), 0, 1);
            grid.add(nomproduitBox, 1, 1);

            TextField qteField = new TextField();
            qteField.setText(String.valueOf(selectedCredit.getQte()));
            grid.add(new Label("Quantité:"), 0, 2);
            grid.add(qteField, 1, 2);

            DatePicker dateField = new DatePicker();
            dateField.setValue(selectedCredit.getDate());
            grid.add(new Label("Date:"), 0, 3);
            grid.add(dateField, 1, 3);

            TextField montantField = new TextField();
            montantField.setText(String.valueOf(selectedCredit.getQte()));
            grid.add(new Label("Montant:"), 0, 4);
            grid.add(montantField, 1, 4);

            // Add the layout to the dialog pane
            dialog.getDialogPane().setContent(grid);

            // Request focus on the nomclient field by default
            Platform.runLater(() -> nomclientBox.requestFocus());

            // Convert the result to a CreditWithNames object when the OK button is clicked
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == okButtonType) {
                    // Update the selected credit with the new values
                    selectedCredit.setClientId(nomclientBox.getValue());
                    selectedCredit.setProduitId(nomproduitBox.getValue());
                    selectedCredit.setQte(Integer.parseInt(qteField.getText()));
                    selectedCredit.setDate(dateField.getValue());
                    selectedCredit.setQte((int) Double.parseDouble(montantField.getText()));
                    return selectedCredit;
                }
                return null;
            });

            // Show the dialog and wait for the user to close it
            Optional<CreditWithNames> result = dialog.showAndWait();

            // Update the table if the user clicked OK
            result.ifPresent(credit -> {
                try {
                    CreditDAO creditDAO = new CreditDAO();
                    creditDAO.update(selectedCredit);
                    updateTable();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } }


    @FXML
    public void deleteButtonClick() throws SQLException {
        // utiliser la variable selectedCreditId pour supprimer le crédit sélectionné
        if (selectedCreditId ==null) {
             CreditDAO creditDAO = new CreditDAO();
            creditsTable.getItems().removeIf(credit -> credit.getCredit_id().equals(selectedCreditId));
            // afficher un message d'erreur ou gérer le cas où aucun crédit n'est sélectionné
            return;
        }

        try {
            CreditDAO creditDAO = new CreditDAO();
            //creditDAO.deleteCreditWithForeignKeys(selectedCreditId);
            // actualiser la table pour refléter la suppression
            creditsTable.getItems().removeIf(credit -> credit.getCredit_id().equals(selectedCreditId));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }




    List<Client> clients = new ArrayList<>();

   public void setupChoiceBoxes() {
       try {
           // Get client names
           List<String> clientNames = clientDAO.getAllNames();
           ObservableList<String> clientNamesList = FXCollections.observableArrayList(clientNames);
           nomclient.setItems(clientNamesList);

           // Get product names
           List<String> productNames = produitDAO.getAllNames();
           ObservableList<String> productNamesList = FXCollections.observableArrayList(productNames);
           nomproduit.setItems(productNamesList);
       } catch (SQLException e) {
           throw new RuntimeException(e);
       }
   }
    public void ajouterCredit() {
        String nomClient = (String) nomclient.getValue();
        String nomProduit = (String) nomproduit.getValue();
        int qt = Integer.parseInt(qte.getText());
        LocalDate dat = date.getValue();
        // Récupération de l'id_client correspondant au nomClient
        int idClient = 0;
        try {
            idClient = getIdClient(nomClient);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // Récupération de l'id_produit correspondant au nomProduit
        int idProduit = 0;
        try {
            idProduit = getIdProduit(nomProduit);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Insertion des données dans la base de données
        String query = "INSERT INTO credit (id_client, id_produit, qte, date) " +
                "VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idClient);
            stmt.setInt(2, idProduit);
            stmt.setInt(3, qt);
            stmt.setDate(4, Date.valueOf(dat));
            stmt.executeUpdate();
            // Ajout des données dans le TableView
            Credit credit = new Credit(nomClient, nomProduit, qt, dat);
            if (credit instanceof CreditWithNames) {
                // Conversion n'est pas justifiée, traitement alternatif
            }
            else {
                // Conversion justifiée, initialisation des propriétés nécessaires
                CreditWithNames creditWithNames = new CreditWithNames(
                        credit.getQte(),
                        credit.getDate(),
                        "Nom",
                        "Produit");
                // Utilisation de l'instance de CreditWithNames
                creditsTable.getItems().add((CreditWithNames) credit);
            }
            //CreditWithNames creditWithNames = (CreditWithNames) credit;
          //  creditsTable.getItems().add((CreditWithNames) credit);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private int getIdClient(String nomClient)throws SQLException{

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/esalaf", "root", "");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String query = "SELECT id_client FROM client WHERE nom=?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, nomClient);
        ResultSet rs = ps.executeQuery();
        int id_client = 0;
        if (rs.next()) {
            id_client = rs.getInt("id_client");
        }

        return id_client;
    }


    public int getIdProduit(String nomProduit) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/esalaf", "root", "");
        String query = "SELECT id_produit FROM produit WHERE nom=?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, nomProduit);
        ResultSet rs = ps.executeQuery();
        int id_produit = 0;
        if (rs.next()) {
            id_produit = rs.getInt("id_produit");
        }
        return id_produit;
    }



   private Long selectedCreditId;

    @Override
        public void initialize(URL location, ResourceBundle resources) {
        // ajouter un écouteur de sélection de ligne à la table
        creditsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // extraire l'ID du crédit à partir de la ligne sélectionnée
                Long credit_id = newSelection.getCredit_id();
                // stocker l'ID pour une utilisation ultérieure
                selectedCreditId = credit_id;
            }
        });
        setupChoiceBoxes();

        updateTable();
    }}

