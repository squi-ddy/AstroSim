package astrosim.view.nodes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

public class InspectorPane extends BorderPane {
    private final List<InspectorSetting<?>> settings;
    private final GridPane gridPane;

    public InspectorPane() {
        super();
        super.setPrefWidth(300);
        this.gridPane = new GridPane();
        this.settings = new ArrayList<>();
        gridPane.setHgap(5);
        gridPane.setVgap(1);
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(0.3);
        gridPane.getColumnConstraints().add(column1);
        super.setCenter(gridPane);
        SVGPath icon = new SVGPath();
        icon.setContent("M 1.6 1 L 4 5 L 9.4 -4 L 4 5 Z");
        icon.setStyle("-fx-stroke-width: 2.5; -fx-stroke-line-cap: round;");
        Button applyButton = new Button();
        applyButton.setGraphic(icon);
        applyButton.getStyleClass().add("themed-button");
        HBox.setMargin(applyButton, new Insets(1));
        HBox wrapper = new HBox(applyButton);
        wrapper.setAlignment(Pos.CENTER_RIGHT);
        super.setBottom(wrapper);
        Label title = new Label("Properties");
        title.setFont(new Font(16));
        HBox.setMargin(title, new Insets(1));
        Button closeInspector = new Button();
        SVGPath xSymbolShape = new SVGPath();
        xSymbolShape.setContent("M 5 5 L -5 -5 M -5 5 L 5 -5");
        xSymbolShape.setStyle("-fx-stroke-width: 2.5; -fx-stroke-line-cap: round;");
        closeInspector.setGraphic(xSymbolShape);
        closeInspector.getStyleClass().add("themed-button");
        closeInspector.setOnAction(e -> {
            hidePane();
        });
        HBox.setMargin(closeInspector, new Insets(1));
        StackPane topWrapper = new StackPane();
        HBox titleWrapper = new HBox(title);
        titleWrapper.setAlignment(Pos.CENTER);
        HBox closeButtonWrapper = new HBox(closeInspector);
        closeButtonWrapper.setAlignment(Pos.CENTER_RIGHT);
        topWrapper.getChildren().addAll(titleWrapper, closeButtonWrapper);
        super.setTop(topWrapper);
    }

    private void addSetting(InspectorSetting<?> setting) {
        settings.add(setting);
        int column = gridPane.getColumnCount();
        gridPane.add(new Label(setting.getName()), 0, column);
        gridPane.add(setting.getToDisplay(), 1, column);
    }

    public void showPane() {
        super.setManaged(true);
        super.setVisible(true);
    }

    public void hidePane() {
        super.setManaged(false);
        super.setVisible(false);
    }
}
