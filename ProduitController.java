package com.example.esalaf;

import com.exemple.model.Produit;
import com.exemple.model.ProduitDAO;
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
public class ProduitController implements Initializable{

    @FXML
    private TextField nom;

    @FXML
    private TextField prix;

    @FXML
    private TableView<Produit> prod_tab;

    @FXML
    private TableColumn<Produit, Long> col_id;

    @FXML
    private TableColumn<Produit, String> col_name;

    @FXML
    private TableColumn<Produit, Long> col_price;

    public void UpdateTable(){
        col_id.setCellValueFactory(new PropertyValueFactory<Produit,Long>("id"));
        col_name.setCellValueFactory(new PropertyValueFactory<Produit,String>("nom"));
        col_price.setCellValueFactory(new PropertyValueFactory<Produit,Long>("prix"));


        prod_tab.setItems(getDataProducts());
    }
    @FXML
    protected void onAddButtonClick(){
        Produit prod = new Produit(  nom.getText() , Double.parseDouble(prix.getText()));
        prod.setId((long) (prod_tab.getItems().size() + 1));
        try {
            ProduitDAO prodao = new ProduitDAO();
            prodao.save(prod);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        UpdateTable();
    }

    @FXML
    protected void onRemoveButtonClick() {
        Produit selectedProduit = prod_tab.getSelectionModel().getSelectedItem();
        if (selectedProduit != null) {
            try {
                ProduitDAO produitDAO = new ProduitDAO();
                produitDAO.delete(selectedProduit);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            UpdateTable();
        }
    }

    //modifier produits
    @FXML

    protected void onUpdateProduitButtonClick() {
        Produit selectedProduit = prod_tab.getSelectionModel().getSelectedItem();
        if (selectedProduit == null) {
            return;
        }
        Dialog<Produit> dialog = new Dialog<>();
        dialog.setTitle("Modifier un produit");
        // Créer les champs de saisie
        TextField nomField = new TextField(selectedProduit.getNom());
        TextField prixField = new TextField(Double.toString(selectedProduit.getPrix()));
        // Ajouter les champs de saisie à la boîte de dialogue
        GridPane grid = new GridPane();
        grid.add(new Label("Nom :"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Prix :"), 0, 1);
        grid.add(prixField, 1, 1);
        dialog.getDialogPane().setContent(grid);
       // Ajouter les boutons de confirmation et d'annulation
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
         // Attendre que l'utilisateur clique sur un bouton
        Optional<Produit> result = dialog.showAndWait();
        if (result.isPresent()) {
            // Récupérer les nouvelles informations du produit
            String newNom = nomField.getText();
            double newPrix = Double.parseDouble(prixField.getText());
            // Mettre à jour le produit dans la base de données
            try {
                ProduitDAO produitDAO = new ProduitDAO();
                selectedProduit.setNom(newNom);
                selectedProduit.setPrix(newPrix);
                produitDAO.update(selectedProduit);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            UpdateTable();
        }
    }
    public static ObservableList<Produit> getDataProducts(){

        ProduitDAO produitDAO = null;

        ObservableList<Produit> listfx = FXCollections.observableArrayList();

        try {
            produitDAO = new ProduitDAO();
            for(Produit ettemp : produitDAO.getAll())
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
