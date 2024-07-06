package fr.actia.teledist.evol.treeviewer;

import javafx.scene.control.CheckBox;

public class CheckboxesUsines extends CheckBox {
    private int id;

    public CheckboxesUsines(String text, int id) {
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
