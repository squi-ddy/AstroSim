package astrosim.view.helpers;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.List;

public class MenuRenderer {
    private MenuRenderer() {}

    public static void renderMenu(List<MenuItem> items, Label menuLabel, StackPane rootPane) {
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
            itemLabel.setFont(new Font(item.name() == null ? 4 : 16));
            root.getChildren().add(wrapper);
        }
        root.getStyleClass().add("simulator-menu-dropdown");
        Group wrapper = new Group(root);
        wrapper.setManaged(false);
        wrapper.setUserData("menu" + menuLabel.getText());
        addEventHandlers(root, menuLabel, wrapper, rootPane);
    }

    private static void addEventHandlers(VBox root, Label menuLabel, Group wrapper, StackPane rootPane) {
        menuLabel.setOnMouseClicked(e -> {
            Bounds boundsInScene = menuLabel.localToScene(menuLabel.getBoundsInLocal());
            wrapper.setLayoutX(boundsInScene.getMinX());
            wrapper.setLayoutY(boundsInScene.getMaxY());
            menuLabel.getStyleClass().replaceAll(s -> s.equals("simulator-menu") ? "simulator-menu-selected" : s);
            rootPane.getChildren().add(wrapper);
        });
        menuLabel.setOnMouseExited(e -> {
            if (e.getY() < menuLabel.getBoundsInLocal().getMaxY() - 2) {
                rootPane.getChildren().remove(wrapper);
                menuLabel.getStyleClass().replaceAll(s -> s.equals("simulator-menu-selected") ? "simulator-menu" : s);
            }
        });
        root.setOnMouseExited(e -> {
            rootPane.getChildren().remove(wrapper);
            menuLabel.getStyleClass().replaceAll(s -> s.equals("simulator-menu-selected") ? "simulator-menu" : s);
        });
    }
}
