package com.exemple.model;

import java.time.LocalDate;

public class CreditWithNames extends Credit {

    private String productName;
    private String clientName;

    public CreditWithNames(Credit credit, String nom, String clientNom) {
        super();
    }

    public CreditWithNames(String productName, String clientName, int qte, LocalDate date) {
        super(qte, date);
        this.productName = productName;
        this.clientName = clientName;
    }

    public CreditWithNames() {

    }

    public CreditWithNames(String nomClient, String nomProduit, int qt, LocalDate dat, String nomClientComplet) {
        super(nomClient, nomProduit, qt, dat);

    }

    public CreditWithNames(int qte, LocalDate date, String nom, String Produit) {
    }

    public CreditWithNames convertCreditToCreditWithNames(Credit credit) {
        CreditWithNames creditWithNames = new CreditWithNames();
        creditWithNames.setCredit_id(credit.getCredit_id());
        creditWithNames.setProduitId(credit.getProduit());
        creditWithNames.setClientId(credit.getClient());
        creditWithNames.setQte(credit.getQte());
        creditWithNames.setDate(credit.getDate());
        // Set other properties as needed
        return creditWithNames;
    }

    public CreditWithNames(Long credit_id, String productName, String clientName, int qte, LocalDate date) {
        super(credit_id, qte, date);
        this.productName = productName;
        this.clientName = clientName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    @Override
    public String toString() {
        return "CreditWithNames{" +
                "credit_id=" + this.getCredit_id() +
                ", productName='" + productName + '\'' +
                ", clientName='" + clientName + '\'' +
                ", qte=" + this.getQte() +
                ", date=" + this.getDate() +
                '}';
    }
}
