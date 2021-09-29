package astrosim.view.nodes.inspector;

import astrosim.model.managers.ScenarioManager;
import astrosim.model.managers.Settings;
import astrosim.model.managers.SimulatorGUIManager;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("java:S110")
public class InspectorPane extends BorderPane {
    private final List<InspectorSetting<?>> settings;
    private final GridPane gridPane;
    private final Label titleLabel;
    private Inspectable inspecting;

    public InspectorPane() {
        super();
        super.setPrefWidth(300);
        super.getStyleClass().add("inspector-pane-base");
        this.gridPane = new GridPane();
        this.settings = new ArrayList<>();
        gridPane.setHgap(10);
        gridPane.setVgap(5);
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(25);
        column1.setHalignment(HPos.RIGHT);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setHalignment(HPos.CENTER);
        column2.setPercentWidth(75);
        gridPane.getColumnConstraints().addAll(column1, column2);
        ScrollPane centreWrapper = new ScrollPane(gridPane);
        centreWrapper.getStyleClass().add("inspector-pane-centre");
        centreWrapper.setFitToWidth(true);
        super.setCenter(centreWrapper);
        SVGPath icon = new SVGPath();
        icon.setContent("M 1.6 1 L 4 5 L 9.4 -4 L 4 5 Z");
        icon.setStyle("-fx-stroke-width: 2.5; -fx-stroke-line-cap: round;");
        Button applyButton = new Button();
        applyButton.setGraphic(icon);
        applyButton.getStyleClass().add("themed-button");
        applyButton.setOnAction(e -> {
            settings.forEach(InspectorSetting::applyChange);
            hidePane();
        });
        HBox.setMargin(applyButton, new Insets(1));
        HBox wrapper = new HBox(applyButton);
        wrapper.setAlignment(Pos.CENTER_RIGHT);
        wrapper.getStyleClass().add("inspector-pane-bottom");
        super.setBottom(wrapper);
        titleLabel = new Label();
        titleLabel.setFont(new Font(16));
        titleLabel.getStyleClass().add("inspector-title");
        HBox.setMargin(titleLabel, new Insets(1));
        Button closeInspector = new Button();
        SVGPath xSymbolShape = new SVGPath();
        xSymbolShape.setContent("M 5 5 L -5 -5 M -5 5 L 5 -5");
        xSymbolShape.setStyle("-fx-stroke-width: 2.5; -fx-stroke-line-cap: round;");
        closeInspector.setGraphic(xSymbolShape);
        closeInspector.getStyleClass().add("themed-button");
        closeInspector.setOnAction(e -> hidePane());
        HBox.setMargin(closeInspector, new Insets(1));
        StackPane topWrapper = new StackPane();
        HBox titleWrapper = new HBox(titleLabel);
        titleWrapper.setAlignment(Pos.CENTER);
        HBox closeButtonWrapper = new HBox(closeInspector);
        closeButtonWrapper.setAlignment(Pos.CENTER_RIGHT);
        topWrapper.getChildren().addAll(titleWrapper, closeButtonWrapper);
        topWrapper.getStyleClass().add("inspector-pane-top");
        super.setTop(topWrapper);
        hidePane();
    }

    private void addSetting(InspectorSetting<?> setting) {
        settings.add(setting);
        int row = gridPane.getRowCount();
        Label propertyLabel = new Label(setting.getName());
        propertyLabel.getStyleClass().add("inspector-property-label");
        gridPane.add(propertyLabel, 0, row);
        gridPane.add(setting.getToDisplay(), 1, row);
        setting.addChangeListener(() -> {
            propertyLabel.getStyleClass().remove("inspector-property-label-invalid");
            propertyLabel.getStyleClass().remove("inspector-property-label-changed");
            if (!setting.isValid()) {
                propertyLabel.getStyleClass().add("inspector-property-label-invalid");
            }
            else if (!setting.isOriginal()) {
                propertyLabel.getStyleClass().add("inspector-property-label-changed");
            }
        });
    }

    public void loadSettings(Inspectable object) {
        ScenarioManager.getScenario().stopThread();
        Settings.setSpeed((short) 0);
        SimulatorGUIManager.getController().syncSpeedButtons();
        this.inspecting = object;
        settings.clear();
        gridPane.getChildren().clear();
        for (InspectorSetting<?> setting : object.getSettings()) {
            addSetting(setting);
        }
        showPane();
    }

    public void showPane() {
        super.setManaged(true);
        super.setVisible(true);
    }

    public void hidePane() {
        super.setManaged(false);
        super.setVisible(false);
        if (this.inspecting != null) {
            this.inspecting.onClose();
            this.inspecting = null;
        }
        ScenarioManager.getScenario().startThread();
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }
}
