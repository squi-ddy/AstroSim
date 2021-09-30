package astrosim.view.nodes.inspector;

import javafx.scene.Group;
import javafx.scene.control.CheckBox;

import java.util.function.Consumer;

public class BooleanInspectorSetting extends InspectorSetting<Boolean> {
    private final Group wrapper;
    private final CheckBox checkBox;

    public BooleanInspectorSetting(String name, Boolean setting, Consumer<Boolean> onUpdate) {
        super(name, setting, onUpdate, b -> true);
        checkBox = new CheckBox();
        checkBox.getStyleClass().add("themed-check-box");
        checkBox.setSelected(setting);
        wrapper = new Group(checkBox);
    }

    @Override
    public Group getToDisplay() {
        return wrapper;
    }

    @Override
    public void addChangeListener(Runnable listener) {
        checkBox.setOnAction(e -> {
            super.setNewValue(checkBox.isSelected());
            listener.run();
        });
    }
}
