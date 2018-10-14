package ar.com.tamborindeguy.client.ui;

import ar.com.tamborindeguy.client.managers.AOInputProcessor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class GUI {

    private static Inventory inventory;
    private static DialogText dialog;
    private Stage stage;

    public GUI() {
        this.stage = new AOInputProcessor();
    }

    public void initialize() {
        stage.addActor(createDialogContainer());
        stage.addActor(createInventory());
        Gdx.input.setInputProcessor(stage);
    }

    private Container<Table> createInventory() {
        Container<Table> dialogContainer = new Container<>();
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float containerW = Inventory.COLUMNS * Slot.SIZE * Inventory.ZOOM;
        dialogContainer.setWidth(containerW);
        dialogContainer.setHeight(Inventory.ROWS * Slot.SIZE * Inventory.ZOOM);
        dialogContainer.setPosition((screenW - dialogContainer.getWidth()) - 10, (screenH - dialogContainer.getHeight() - 20) / 2);
        inventory = new Inventory();
        dialogContainer.setActor(inventory);
        return dialogContainer;
    }

    private Container<Table> createDialogContainer() {
        Container<Table> dialogContainer = new Container<>();
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float containerW = screenW * 0.8f;
        dialogContainer.setWidth(containerW);
        dialogContainer.setPosition((screenW - containerW) / 2.0f, screenH * 0.25f);
        dialogContainer.fillX();
        dialog = new DialogText();
        dialogContainer.setActor(dialog);
        return dialogContainer;
    }

    public static Inventory getInventory() {
        return inventory;
    }

    public static DialogText getDialog() {
        return dialog;
    }

    public void draw() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
    }

}