package astrosim.view.nodes.inspector;

import javafx.scene.Group;

import java.util.function.Consumer;
import java.util.function.Function;

public class BooleanInspectorSetting extends InspectorSetting<Boolean> {
    protected BooleanInspectorSetting(String name, Boolean setting, Consumer<Boolean> onUpdate, Function<Boolean, Boolean> isValid) {
        super(name, setting, onUpdate, isValid);
    }
    // todo

    @Override
    public Group getToDisplay() {
        return null;
    }

    @Override
    public void addChangeListener(Runnable listener) {

    }
}
