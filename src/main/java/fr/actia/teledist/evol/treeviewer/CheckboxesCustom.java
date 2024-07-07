package fr.actia.teledist.evol.treeviewer;

import javafx.scene.control.CheckBox;

public class CheckboxesCustom extends CheckBox {
    private int id;

    public CheckboxesCustom() {
    }
    public CheckboxesCustom(String text, int id) {
        super(text);
        this.id = id;
    }

    public int getIdUsine() {
        return this.id;
    }

    public void setIdUsine(int id) {
        this.id = id;
    }
}
