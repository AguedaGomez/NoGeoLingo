package com.ssii.nogeolingo.Objects;

import android.support.annotation.NonNull;

/**
 * Created by Ague on 18/08/2018.
 */

public class OrderedConcept implements Comparable<OrderedConcept>{

    private String name;
    private int position;
    private int strength;
    private boolean shown;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public OrderedConcept(String name, int strength, int position, boolean shown) {
        this.name = name;
        this.strength = strength;
        this.position = position;
        this.shown = shown;
    }

    @Override
    public int compareTo(@NonNull OrderedConcept orderedConcept) {
        if (position < orderedConcept.getPosition())
            return -1;
        else if (position > orderedConcept.getPosition())
            return 1;
        return 0;
    }

    public boolean isShown() {
        return shown;
    }

    public void setShown(boolean shown) {
        this.shown = shown;
    }
}
