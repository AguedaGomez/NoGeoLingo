package com.ssii.nogeolingo.Objects;

/**
 * Created by Ague on 08/08/2018.
 */

public class Concept {
    private String name;
    private String image;

    public Concept(String name, String image) {
        this.name = name;
        this.image = image;
}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

}
