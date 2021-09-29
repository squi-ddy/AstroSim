package astrosim.model.managers;

import astrosim.controller.SimulatorGUIController;
import astrosim.view.nodes.inspector.InspectorPane;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class SimulatorGUIManager {
    private static final InspectorPane inspector;
    private static final DoubleProperty currentScale;
    private static SimulatorGUIController controller = null;

    private SimulatorGUIManager() {}

    static {
        inspector = new InspectorPane();
        currentScale = new SimpleDoubleProperty(1);
    }

    public static InspectorPane getInspector() {
        inspector.hidePane();
        return inspector;
    }

    public static InspectorPane getInspector(String name) {
        inspector.hidePane();
        inspector.setTitle(name);
        return inspector;
    }

    public static DoubleProperty scaleProperty() {
        return currentScale;
    }

    public static void setController(SimulatorGUIController controller) {
        SimulatorGUIManager.controller = controller;
    }

    public static SimulatorGUIController getController() {
        return controller;
    }
}
