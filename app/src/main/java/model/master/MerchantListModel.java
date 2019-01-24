package model.master;

import java.math.BigDecimal;

public class MerchantListModel {

    private String id;
    private String nama;
    private Integer verificated;
    private String pathFoto;
    private BigDecimal rating;
    private Integer countUserRating;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public Integer getVerificated() {
        return verificated;
    }

    public void setVerificated(Integer verificated) {
        this.verificated = verificated;
    }

    public String getPathFoto() {
        return pathFoto;
    }

    public void setPathFoto(String pathFoto) {
        this.pathFoto = pathFoto;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public Integer getCountUserRating() {
        return countUserRating;
    }

    public void setCountUserRating(Integer countUserRating) {
        this.countUserRating = countUserRating;
    }
}
