package astrosim.view.nodes.inspector;

import java.util.List;

public interface Inspectable {
    List<InspectorSetting<?>> getSettings();
    void onClose();
}
