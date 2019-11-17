package com.example.FZU;

public class Animal {
    private String aName;
    private String aSpeak;
    private String aIcon;
    private String bIcon;

    public Animal() {
    }

    public Animal(String aName, String aSpeak, String aIcon , String bIcon) {
        this.aName = aName;
        this.aSpeak = aSpeak;
        this.aIcon = aIcon;
        this.bIcon = bIcon;
    }

    public String getaName() {
        return aName;
    }

    public String getaSpeak() {
        return aSpeak;
    }

    public String getaIcon() {
        return aIcon;
    }

    public String getbIcon() { return bIcon; }

    public void setaName(String aName) {
        this.aName = aName;
    }

    public void setaSpeak(String aSpeak) {
        this.aSpeak = aSpeak;
    }

    public void setaIcon(String aIcon) {
        this.aIcon = aIcon;
    }

    public void setbIcon(String bIcon) { this.bIcon = bIcon;}
}
