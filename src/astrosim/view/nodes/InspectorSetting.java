package astrosim.view.nodes;

import javafx.scene.Group;

import java.util.function.Consumer;
import java.util.function.Function;

public class InspectorSetting<T> {
    private final String name;
    private T settingValue;
    private final Consumer<T> update;
    private final Function<T, Boolean> validityCheck;
    private final Group toDisplay;

    public InspectorSetting(String name, T setting, Consumer<T> update, Function<T, Boolean> validityCheck, Group toDisplay) {
        this.name = name;
        this.settingValue = setting;
        this.update = update;
        this.validityCheck = validityCheck;
        this.toDisplay = toDisplay;
    }

    public Group getToDisplay() {
        return toDisplay;
    }

    public T getSettingValue() {
        return settingValue;
    }

    public String getName() {
        return name;
    }

    public void setSettingValue(T settingValue) {
        this.settingValue = settingValue;
    }

    public void applyChange() {
        update.accept(this.settingValue);
    }

    public boolean checkValidity() {
        return validityCheck.apply(settingValue);
    }
}
