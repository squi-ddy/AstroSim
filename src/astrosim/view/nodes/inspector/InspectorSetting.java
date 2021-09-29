package astrosim.view.nodes.inspector;

import javafx.scene.Group;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class InspectorSetting<T> {
    private final String name;
    private T newValue;
    private final T originalValue;
    private final Consumer<T> onUpdate;
    private final Function<T, Boolean> isValid;

    protected InspectorSetting(String name, T setting, Consumer<T> onUpdate, Function<T, Boolean> isValid) {
        this.name = name;
        this.newValue = setting;
        this.originalValue = setting;
        this.onUpdate = onUpdate;
        this.isValid = isValid;
    }

    public String getName() {
        return name;
    }

    public void setNewValue(T newValue) {
        this.newValue = newValue;
    }

    public T getNewValue() {
        return newValue;
    }

    public void applyChange() {
        if (isValid()) {
            onUpdate.accept(newValue);
        }
    }

    public boolean isOriginal() {
        return originalValue.equals(newValue);
    }

    public boolean isValid() {
        return isValid.apply(newValue);
    }

    public abstract Group getToDisplay();
    public abstract void addChangeListener(Runnable listener);
}
