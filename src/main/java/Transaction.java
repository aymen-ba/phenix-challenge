public class Transaction {

    private String datetime;
    private Long produit;
    private Long quantite;
    private Double prixTotal;


    public Transaction(String datetime, Long produit, Long quantite) {
        this.datetime = datetime;
        this.produit = produit;
        this.quantite = quantite;
    }


    public Transaction(String datetime, Long produit, Long quantite, Double prixTotal) {
        this.datetime = datetime;
        this.produit = produit;
        this.quantite = quantite;
        this.prixTotal = prixTotal;
    }

    public Double getPrix() {
        return prixTotal;
    }

    public void setPrix(Double prixTotal) {
        this.prixTotal = prixTotal;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public Long getProduit() {
        return produit;
    }

    public void setProduit(Long produit) {
        this.produit = produit;
    }

    public Long getQuantite() {
        return quantite;
    }

    public void setQuantite(Long quantite) {
        this.quantite = quantite;
    }
}