package com.ssii.nogeolingo.Objects;

/**
 * Created by Ague on 16/08/2018.
 */

public class ShownConcept {

    private String appearanceTime;
    private String showTextTime;
    private String nameConcept;
    private int error;


    public ShownConcept(String appearanceTime, String showTextTime, String nameConcept) {
        this.appearanceTime = appearanceTime;
        this.showTextTime = showTextTime;
        this.nameConcept = nameConcept;
        this.error = -1;
    }

    public String getAppearanceTime() {
        return appearanceTime;
    }

    public void setAppearanceTime(String appearanceTime) {
        this.appearanceTime = appearanceTime;
    }

    public String getShowTextTime() {
        return showTextTime;
    }

    public void setShowTextTime(String showTextTime) {
        this.showTextTime = showTextTime;
    }

    public String getNameConcept() {
        return nameConcept;
    }

    public void setNameConcept(String nameConcept) {
        this.nameConcept = nameConcept;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }
}
