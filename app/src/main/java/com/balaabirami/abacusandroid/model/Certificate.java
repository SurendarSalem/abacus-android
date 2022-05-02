package com.balaabirami.abacusandroid.model;

public class Certificate {
    public static String CERT_MASTER = "M";
    public static String CERT_GRADUATE = "G";
    public static String CERT_NULL = "N";
    String name;
    boolean isChecked;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public Certificate(String name, boolean isChecked) {
        this.name = name;
        this.isChecked = isChecked;
    }

}
