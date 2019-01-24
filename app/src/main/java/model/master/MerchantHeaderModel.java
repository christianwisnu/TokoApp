package model.master;

import java.io.Serializable;

public class MerchantHeaderModel implements Serializable {

    private String id;
    private String nama;
    private String alamat;
    private String idCountry;
    private String idKabKota;
    private String idKec;
    private String kodePos;
    private String email;
    private String noFax;
    private String telp1;
    private String telp2;
    private String slogan;
    private String note;
    private String deskripsi;
    private double latitude;
    private double longtitude;
    private Integer verificated;
    private Integer stayed;
    private Integer active;
    private String facebook;
    private String twitter;
    private String instagram;


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

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getIdCountry() {
        return idCountry;
    }

    public void setIdCountry(String idCountry) {
        this.idCountry = idCountry;
    }

    public String getIdKabKota() {
        return idKabKota;
    }

    public void setIdKabKota(String idKabKota) {
        this.idKabKota = idKabKota;
    }

    public String getIdKec() {
        return idKec;
    }

    public void setIdKec(String idKec) {
        this.idKec = idKec;
    }

    public String getKodePos() {
        return kodePos;
    }

    public void setKodePos(String kodePos) {
        this.kodePos = kodePos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNoFax() {
        return noFax;
    }

    public void setNoFax(String noFax) {
        this.noFax = noFax;
    }

    public String getTelp1() {
        return telp1;
    }

    public void setTelp1(String telp1) {
        this.telp1 = telp1;
    }

    public String getTelp2() {
        return telp2;
    }

    public void setTelp2(String telp2) {
        this.telp2 = telp2;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public Integer getVerificated() {
        return verificated;
    }

    public void setVerificated(Integer verificated) {
        this.verificated = verificated;
    }

    public Integer getStayed() {
        return stayed;
    }

    public void setStayed(Integer stayed) {
        this.stayed = stayed;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }
}
