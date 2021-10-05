package astrosim.model.simulation;

import astrosim.model.managers.ScenarioManager;
import astrosim.model.managers.SimulatorGUIManager;
import astrosim.model.xml.XMLHashable;
import astrosim.model.xml.XMLNodeInfo;
import astrosim.model.xml.XMLParseException;
import astrosim.view.nodes.Trail;
import astrosim.view.nodes.inspector.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Settings implements XMLHashable, Inspectable {
    private int accuracy; // global simulation accuracy (a number between 1 - 10) -> determines time step
    private String lastSave; // file path to last save; provides smoothness
    private int speed = 0; // The speed of the simulation
    private int maxPointsInTrail;
    private int maxBufferInTrail;
    private int positionGapInTrail;
    private boolean darkMode;
    private double burstFactor;
    private int sensitivity;

    @SuppressWarnings("java:S107")
    public Settings(int accuracy, String lastSave, int maxPointsInTrail, int maxBufferInTrail, int positionGapInTrail, boolean darkMode, double burstFactor, int sensitivity) {
        this.accuracy = accuracy;
        this.lastSave = lastSave;
        this.maxPointsInTrail = maxPointsInTrail;
        this.maxBufferInTrail = maxBufferInTrail;
        this.positionGapInTrail = positionGapInTrail;
        this.darkMode = darkMode;
        this.burstFactor = burstFactor;
        this.sensitivity = sensitivity;
        setOrbitalPathConstants();
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
        SimulatorGUIManager.getController().setDarkMode(darkMode);
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public void setMaxBufferInTrail(int maxBufferInTrail) {
        this.maxBufferInTrail = maxBufferInTrail;
        setOrbitalPathConstants();
        SimulatorGUIManager.getController().setBurstFactor(burstFactor);
    }

    public void setMaxPointsInTrail(int maxPointsInTrail) {
        this.maxPointsInTrail = maxPointsInTrail;
        setOrbitalPathConstants();
    }

    public void setPositionGapInTrail(int positionGapInTrail) {
        this.positionGapInTrail = positionGapInTrail;
        setOrbitalPathConstants();
    }

    public void setOrbitalPathConstants() {
        OrbitalPath.setMaxBufferLength(maxBufferInTrail / (101 - accuracy));
        OrbitalPath.setMaxLength(maxPointsInTrail);
        OrbitalPath.setPositionGap(positionGapInTrail / (101 - accuracy));
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
        setOrbitalPathConstants();
        SimulatorGUIManager.getController().setBurstFactor(burstFactor);
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setSensitivity(int sensitivity) {
        this.sensitivity = sensitivity;
    }

    public int getSensitivity() {
        return sensitivity;
    }

    public int getSpeed() {
        return speed;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public String getLastSave() {
        return lastSave;
    }

    public double getBurstFactor() {
        return burstFactor;
    }

    public void setBurstFactor(double burstFactor) {
        this.burstFactor = burstFactor;
        setOrbitalPathConstants();
        SimulatorGUIManager.getController().setBurstFactor(burstFactor);
    }

    public void setLastSave(String lastSave) {
        this.lastSave = lastSave;
    }

    public static Settings fromXML(XMLNodeInfo info) throws XMLParseException {
        try {
            Map<String, XMLNodeInfo> settings = info.getDataTable();
            short accuracy = Short.parseShort(settings.get("accuracy").getValue());
            String lastSave = (settings.get("lastSave").getValue().equals("null") ? null : settings.get("lastSave").getValue());
            int maxBufferInTrail = Integer.parseInt(settings.get("bufferLen").getValue());
            int maxPointsInTrail = Integer.parseInt(settings.get("trailLen").getValue());
            boolean darkMode = Boolean.parseBoolean(settings.get("dark").getValue());
            int sensitivity = Integer.parseInt(settings.get("sensitivity").getValue());
            int positionGapInTrail = Integer.parseInt(settings.get("positionGap").getValue());
            double burstFactor = Double.parseDouble(settings.get("burstFactor").getValue());
            Trail.setGraphicMode(Integer.parseInt(settings.get("renderQuality").getValue()));
            Trail.setShowingPermanent(Boolean.parseBoolean(settings.get("permanentTrail").getValue()));
            return new Settings(accuracy, lastSave, maxPointsInTrail, maxBufferInTrail, positionGapInTrail, darkMode, burstFactor, sensitivity);
        } catch (XMLParseException | NumberFormatException | NullPointerException e) {
            throw new XMLParseException(XMLParseException.Type.XML_ERROR);
        }
    }

    @Override
    public XMLNodeInfo hashed() {
        Map<String, XMLNodeInfo> hashed = new HashMap<>();
        hashed.put("accuracy", new XMLNodeInfo(String.valueOf(accuracy)));
        hashed.put("lastSave", new XMLNodeInfo(lastSave == null ? "null" : lastSave));
        hashed.put("trailLen", new XMLNodeInfo(String.valueOf(maxPointsInTrail)));
        hashed.put("bufferLen", new XMLNodeInfo(String.valueOf(maxBufferInTrail)));
        hashed.put("dark", new XMLNodeInfo(String.valueOf(darkMode)));
        hashed.put("sensitivity", new XMLNodeInfo(String.valueOf(sensitivity)));
        hashed.put("positionGap", new XMLNodeInfo(String.valueOf(positionGapInTrail)));
        hashed.put("burstFactor", new XMLNodeInfo(String.valueOf(burstFactor)));
        hashed.put("renderQuality", new XMLNodeInfo(String.valueOf(Trail.getGraphicMode())));
        hashed.put("permanentTrail", new XMLNodeInfo(String.valueOf(Trail.isShowingPermanent())));
        return new XMLNodeInfo(hashed);
    }

    @Override
    public List<InspectorSetting<?>> getSettings() {
        List<InspectorSetting<?>> settings = new ArrayList<>();
        settings.add(new IntegerInspectorSetting("Accuracy", accuracy, this::setAccuracy, a -> a != null && 0 < a && a <= 100));
        settings.add(new IntegerInspectorSetting("Trail Length", maxPointsInTrail, this::setMaxPointsInTrail, a -> a != null && a >= 0));
        settings.add(new IntegerInspectorSetting("Buffer Length", maxBufferInTrail, this::setMaxBufferInTrail, a -> a != null && a >= 50));
        settings.add(new IntegerInspectorSetting("Trail Gap", positionGapInTrail, this::setPositionGapInTrail, a -> a != null && a >= 0));
        settings.add(new DoubleInspectorSetting("Burst Factor", burstFactor, this::setBurstFactor, f -> f != null && f > 0));
        settings.add(new IntegerInspectorSetting("Sensitivity", sensitivity, this::setSensitivity, s -> s != null && s > 0));
        settings.add(new ListInspectorSetting("Graphics", Trail.getGraphicMode(), Trail::setGraphicMode, List.of("Low", "Medium", "High")));
        settings.add(new BooleanInspectorSetting("Permanent Trails", Trail.isShowingPermanent(), Trail::setShowingPermanent));
        settings.add(new BooleanInspectorSetting("Dark Mode", darkMode, this::setDarkMode));
        return settings;
    }

    @Override
    public void onClose() {
        ScenarioManager.getScenario().getPlanets().forEach(p -> p.getPath().getTrail().updateGlobalSettings());
    }
}
