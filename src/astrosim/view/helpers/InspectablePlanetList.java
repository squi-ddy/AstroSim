package astrosim.view.helpers;

import astrosim.view.nodes.PlanetNode;
import astrosim.view.nodes.inspector.Inspectable;
import astrosim.view.nodes.inspector.InspectorSetting;
import astrosim.view.nodes.inspector.PlanetInspectorSetting;

import java.util.ArrayList;
import java.util.List;

public class InspectablePlanetList implements Inspectable {
    private final List<PlanetNode> planetList;

    public InspectablePlanetList() {
        planetList = new ArrayList<>();
    }

    public List<PlanetNode> getPlanetList() {
        return planetList;
    }

    @Override
    public List<InspectorSetting<?>> getSettings() {
        List<InspectorSetting<?>> settings = new ArrayList<>();
        planetList.forEach(p -> settings.add(new PlanetInspectorSetting(p.getPlanet().getName(), p.getPlanet())));
        return settings;
    }

    @Override
    public void onClose() {
        // we don't need to do anything here
    }
}
