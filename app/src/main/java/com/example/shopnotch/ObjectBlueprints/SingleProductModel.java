package com.example.shopnotch.ObjectBlueprints;

import java.io.Serializable;

public class SingleProductModel implements Serializable {

    private int prid,no_of_items;
    private String useremail,usermobile,prname,prprice,primage;


    public SingleProductModel(int prid, int no_of_items, String useremail, String usermobile, String prname, String prprice, String primage) {
        this.prid = prid;
        this.no_of_items = no_of_items;
        this.useremail = useremail;
        this.usermobile = usermobile;
        this.prname = prname;
        this.prprice = prprice;
        this.primage = primage;

    }
    public SingleProductModel(){

    }

    public String getUsermobile() {
        return usermobile;
    }

    public void setUsermobile(String usermobile) {
        this.usermobile = usermobile;
    }

    public int getNo_of_items() {
        return no_of_items;
    }

    public void setNo_of_items(int no_of_items) {
        this.no_of_items = no_of_items;
    }

    public int getPrid() {
        return prid;
    }

    public void setPrid(int prid) {
        this.prid = prid;
    }

    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }

    public String getPrname() {
        return prname;
    }

    public void setPrname(String prname) {
        this.prname = prname;
    }

    public String getPrprice() {
        return prprice;
    }

    public void setPrprice(String prprice) {
        this.prprice = prprice;
    }

    public String getPrimage() {
        return primage;
    }

    public void setPrimage(String primage) {
        this.primage = primage;
    }
}
