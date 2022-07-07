package com.example.shopnotch.ObjectBlueprints;

import java.io.Serializable;

public class GenericProductModel implements Serializable {

    public int modelid;
    public String modelname;
    public String modelimage1;
    public String modelimage2;
    public float modelprice;

    public GenericProductModel(int modelid, String modelname, String modelimage1, String modelimage2, float modelprice) {
        this.modelid = modelid;
        this.modelname = modelname;
        this.modelimage1 = modelimage1;
        this.modelimage2 = modelimage2;
        this.modelprice = modelprice;
    }

    public GenericProductModel(){

    }

    public String getCardname() {
        return modelname;
    }

    public void setCardname(String modelname) {
        this.modelname = modelname;
    }

    public String getCardimage() {
        return modelimage1;
    }

    public String getCardimage2() {
        return modelimage2;
    }

    public void setCardimage(String modelimage1) {
        this.modelimage1 = modelimage1;
    }
//    public void setCardimage2(String modelimage2) {
//        this.modelimage2 = modelimage2;
//    }
    public float getCardprice() {
        return modelprice;
    }

    public void setCardprice(float modelprice) {
        this.modelprice = modelprice;
    }

    public int getCardid() {
        return modelid;
    }

    public void setCardid(int modelid) {
        this.modelid = modelid;
    }
}