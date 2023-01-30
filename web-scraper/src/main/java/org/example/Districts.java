package org.example;

public class Districts {
    private String congressional;
    private String senate;
    private String house;

    public Districts(String cong, String sen, String h) {
        this.congressional = cong;
        this.senate = sen;
        this.house = h;
    }

    public String getCongressional() {
        return congressional;
    }

    public String getSenate() {
        return senate;
    }

    public String getHouse() {
        return house;
    }
}
