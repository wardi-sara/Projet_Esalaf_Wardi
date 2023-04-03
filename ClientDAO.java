package com.exemple.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO extends  BaseDAO<Client> {
    public ClientDAO() throws SQLException {

        super();
    }


    // mapping objet --> relation
    @Override
    public void save(Client object) throws SQLException {

        String req = "insert into client (nom , telephone) values (? , ?) ;";


        this.preparedStatement = this.connection.prepareStatement(req);

        this.preparedStatement.setString(1 , object.getNom());
        this.preparedStatement.setString(2 , object.getTelepehone());


        this.preparedStatement.execute();

    }


    @Override
    public void update(Client object) throws SQLException {
        String req = "UPDATE client SET nom = ?, telephone = ? WHERE id_client = ?;";
        this.preparedStatement = this.connection.prepareStatement(req);
        this.preparedStatement.setString(1, object.getNom());
        this.preparedStatement.setString(2, object.getTelepehone());
        this.preparedStatement.setLong(3, object.getId_client());
        this.preparedStatement.executeUpdate();
    }

    @Override
   /* public void delete(Client object) throws SQLException {

    }
    */
    public void delete(Client client) throws SQLException {
        // Vérifier si le client a des crédits associés
        CreditDAO creditDAO = new CreditDAO();
        List<Credit> credits = creditDAO.getAll();

        if (!credits.isEmpty()) {
            // Supprimer les crédits associés avant de supprimer le client
            for (Credit credit : credits) {
                creditDAO.delete(credit);
            }
        }

        // Supprimer le client
        String req = "DELETE FROM client WHERE id_client = ?;";
        this.preparedStatement = this.connection.prepareStatement(req);
        this.preparedStatement.setLong(1, client.getId_client());
        this.preparedStatement.executeUpdate();
    }

    @Override
    public Client getOne(Long id) throws SQLException {
        return null;
    }


    // mapping relation --> objet
    @Override
    public List<Client> getAll() throws SQLException{

        List<Client> mylist = new ArrayList<Client>();
        String req = " select * from client" ;


        this.statement = this.connection.createStatement();

        this.resultSet =  this.statement.executeQuery(req);

        while (this.resultSet.next()){

            mylist.add( new Client(this.resultSet.getLong(1) , this.resultSet.getString(2),
                    this.resultSet.getString(3)));


        }

        return mylist;
    }

    public List<String> getAllNames() throws SQLException {
        List<Client> clients = getAll();
        List<String> names = new ArrayList<String>();
        for (Client client : clients) {
            names.add(client.getNom());
        }
        return names;
    }
    public String getClientName(Long id) throws SQLException {
        String req = "SELECT nom FROM client WHERE id_client = ?;";
        this.preparedStatement = this.connection.prepareStatement(req);
        this.preparedStatement.setLong(1, id);
        this.resultSet = this.preparedStatement.executeQuery();
        if (this.resultSet.next()) {
            return this.resultSet.getString(1);
        }
        return null;
    }
}
