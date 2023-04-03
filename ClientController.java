package com.example.esalaf;

import com.exemple.model.Client;
import com.exemple.model.ClientDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    @FXML
    private TextField nom;

    @FXML
    private TextField tele;


    @FXML
    private TableView<Client> mytab;

    @FXML
    private TableColumn<Client, Long> col_id;

    @FXML
    private TableColumn<Client, String> col_nom;

    @FXML
    private TableColumn<Client, String> col_tele;


    @FXML
    protected void onSaveButtonClick(){

        Client cli = new Client(0l , nom.getText() , tele.getText());

        try {
            ClientDAO clidao = new ClientDAO();

            clidao.save(cli);



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        UpdateTable();

    }

   /* @FXML
    protected void onEditButtonClick() {
        // Récupérer le client sélectionné dans la table
        Client selectedClient = mytab.getSelectionModel().getSelectedItem();

        if (selectedClient == null) {
            // Le client n'a pas été sélectionné, afficher un message d'erreur
            // ou simplement sortir de la méthode sans rien faire.
            return;
        }

        // Afficher les informations du client dans les champs de texte
        nom.setText(selectedClient.getNom());
        tele.setText(selectedClient.getTelepehone());

        // Modifier le client lorsque le bouton "Modifier" est cliqué
        ClientDAO clientDAO = null;

        try {
            clientDAO = new ClientDAO();

            // Supprimer le client existant
            clientDAO.delete(selectedClient);

            // Créer un nouveau client avec les informations modifiées
            Client updatedClient = new Client(selectedClient.getId_client(), nom.getText(), tele.getText());

            // Enregistrer le nouveau client dans la base de données
            clientDAO.save(updatedClient);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Mettre à jour la table pour afficher les modifications
        UpdateTable();
    }*/


    @FXML
    protected void onUpdateButtonClick() {
        Client selectedClient = mytab.getSelectionModel().getSelectedItem();

        if (selectedClient == null) {
            // Le client n'a pas été sélectionné, afficher un message d'erreur
            // ou simplement sortir de la méthode sans rien faire.
            return;
        }

        // Afficher un formulaire de modification avec les informations du client sélectionné
        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle("Modifier un client");

        // Créer les champs de saisie
        TextField nomField = new TextField(selectedClient.getNom());
        TextField teleField = new TextField(selectedClient.getTelepehone());

        // Ajouter les champs de saisie à la boîte de dialogue
        GridPane grid = new GridPane();
        grid.add(new Label("Nom :"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Téléphone :"), 0, 1);
        grid.add(teleField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Ajouter les boutons de confirmation et d'annulation
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Attendre que l'utilisateur clique sur un bouton
        Optional<Client> result = dialog.showAndWait();

        if (result.isPresent()) {
            // Récupérer les nouvelles informations du client
            String newNom = nomField.getText();
            String newTele = teleField.getText();

            // Mettre à jour le client dans la base de données
            try {
                ClientDAO clientDAO = new ClientDAO();
                selectedClient.setNom(newNom);
                selectedClient.setTelepehone(newTele);
                clientDAO.update(selectedClient);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            // Mettre à jour la TableView
            UpdateTable();
        }
    }
   /* @FXML
    protected void onEditButtonClick() {
        // Récupérer le client sélectionné dans la table
        Client selectedClient = mytab.getSelectionModel().getSelectedItem();

        if (selectedClient == null) {
            // Le client n'a pas été sélectionné, afficher un message d'erreur
            // ou simplement sortir de la méthode sans rien faire.
            return;
        }

        // Afficher les informations du client dans les champs de texte
        nom.setText(selectedClient.getNom());
        tele.setText(selectedClient.getTelepehone());

        // Modifier le client lorsque le bouton "Modifier" est cliqué
        ClientDAO clientDAO = null;

        try {
            clientDAO = new ClientDAO();

            // Supprimer le client existant
            clientDAO.delete(selectedClient);

            // Créer un nouveau client avec les informations modifiées
            Client updatedClient = new Client(selectedClient.getId_client(), nom.getText(), tele.getText());

            // Enregistrer le nouveau client dans la base de données
            clientDAO.save(updatedClient);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        UpdateTable();
    }*/


    @FXML
    protected void onDeleteButtonClick() {
        Client selectedClient = mytab.getSelectionModel().getSelectedItem();
        if (selectedClient == null) {
            return;
        }
        try {
            ClientDAO clientDAO = new ClientDAO();
            clientDAO.delete(selectedClient);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        UpdateTable();
    }




    public void UpdateTable(){
        col_id.setCellValueFactory(new PropertyValueFactory<Client,Long>("id_client"));
        col_nom.setCellValueFactory(new PropertyValueFactory<Client,String>("nom"));

        col_tele.setCellValueFactory(new PropertyValueFactory<Client,String>("telepehone"));


        mytab.setItems(getDataClients());
    }

    public static ObservableList<Client> getDataClients(){

        ClientDAO clidao = null;

        ObservableList<Client> listfx = FXCollections.observableArrayList();

        try {
            clidao = new ClientDAO();
            for(Client ettemp : clidao.getAll())
                listfx.add(ettemp);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return listfx ;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UpdateTable();
    }
}