package astrosim.view.nodes.menu;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;

@SuppressWarnings("java:S110")
public class Menu extends Label {
    private boolean isShowing;

    public Menu(String name, List<MenuItem> items, StackPane rootPane) {
        super(name);
        super.getStyleClass().add("simulator-menu");
        VBox root = new VBox();
        root.setFillWidth(true);
        for (MenuItem item : items) {
            Label itemLabel = new Label(item.name() == null ? "" : item.name());
            HBox wrapper = new HBox(itemLabel);
            if (item.name() != null) {
                wrapper.getStyleClass().add("simulator-menu-dropdown-label");
            } else {
                wrapper.getStyleClass().add("simulator-menu-dropdown-spacer");
            }
            wrapper.setOnMouseClicked(e -> item.doOnClick().run());
            root.getChildren().add(wrapper);
        }
        root.getStyleClass().add("simulator-menu-dropdown");
        Group wrapper = new Group(root);
        wrapper.setManaged(false);
        wrapper.setUserData("menu" + name);
        this.isShowing = false;
        addEventHandlers(root, wrapper, rootPane);
    }

    private void addEventHandlers(VBox root, Group wrapper, StackPane rootPane) {
        super.setOnMouseClicked(e -> {
            Bounds boundsInScene = super.localToScene(super.getBoundsInLocal());
            wrapper.setLayoutX(boundsInScene.getMinX());
            wrapper.setLayoutY(boundsInScene.getMaxY());
            super.getStyleClass().replaceAll(s -> s.equals("simulator-menu") ? "simulator-menu-selected" : s);
            if (!this.isShowing) {
                rootPane.getChildren().add(wrapper);
                this.isShowing = true;
            }
        });
        super.setOnMouseExited(e -> {
            if (e.getY() < super.getBoundsInLocal().getMaxY() - 2) {
                rootPane.getChildren().remove(wrapper);
                this.isShowing = false;
                super.getStyleClass().replaceAll(s -> s.equals("simulator-menu-selected") ? "simulator-menu" : s);
            }
        });
        root.setOnMouseExited(e -> {
            rootPane.getChildren().remove(wrapper);
            this.isShowing = false;
            super.getStyleClass().replaceAll(s -> s.equals("simulator-menu-selected") ? "simulator-menu" : s);
        });
    }
}
