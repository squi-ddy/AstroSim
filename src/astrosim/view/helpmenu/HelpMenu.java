package astrosim.view.helpmenu;

import astrosim.view.nodes.menu.Menu;
import astrosim.view.nodes.menu.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;

public class HelpMenu {
    public HelpMenu(StackPane root, HBox menuBar) {
        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem("Planet Settings", () -> new HelpPopup(root, getPlanetSettingsHelp()).show()));
        menuItems.add(new MenuItem("Show all Planets", () -> new HelpPopup(root, getSeeObjectsHelp()).show()));
        menuItems.add(new MenuItem("Scenario Settings", () -> new HelpPopup(root, getScenarioSettingsHelp()).show()));
        menuItems.add(new MenuItem("Global Settings", () -> new HelpPopup(root, getGlobalSettingsHelp()).show()));
        Menu helpMenu = new Menu("Help", menuItems, root);
        menuBar.getChildren().add(helpMenu);
    }

    private String getGlobalSettingsHelp() {
        return """
                The global settings are settings that will affect all simulations.
                These settings tend to correspond to performance settings.

                -Accuracy: Determines simulation accuracy. Accepts a value from 1 - 100.
                -Trail Length: Defines the maximum number of points on a trail. Accepts any value above 0.
                -Buffer Length: Defines the size of the buffers, which are populated with future positions. Accepts any value above 50.
                -Trail Gap: Defines how many internal simulated positions are combined into a line. Accepts any value above 0.
                -Burst Factor: Determine the proportion of the buffer that is filled by each simulation step. Accepts any value above 0.
                -Sensitivity: Determines the zoom change upon scroll. Accepts any value above 0.
                -Graphics: Determine how detailed the trails are. 'High' is recommended if you have a GPU.
                -Permanent Trails: Determines if permanent trails are rendered.
                -Dark Mode: Toggles light/dark mode.
                """;
    }

    private String getScenarioSettingsHelp() {
        return """
                The scenario settings are settings that only apply to a single scenario.
                These settings are saved with a scenario.
                
                -G: The value of G, the universal gravitational constant. Must be greater than zero.
                -Name: The name of the scenario.
                """;
    }

    private String getPlanetSettingsHelp() {
        return """
                The planet settings are settings that only apply to a single planet.
                These settings are saved with a scenario.
                
                -Name: The name of this planet.
                -Fill: The planet fill color. Accepts colors in web format (#ffffff, for example)
                -Trail: The planet's trail color. Accepts colors in web format.
                -Mass: The planet's mass. Must be greater than 0.
                -Radius: The planet's radius. Must be greater than zero.
                -Position: The planet's position.
                -Velocity: The planet's velocity.
                -Static: Whether the planet is allowed to move.
                -Show Trail: Whether this planet is allowed to have a trail.
                """;
    }

    private String getSeeObjectsHelp() {
        return """
                See all objects allows us to do interesting stuff with planets.
                
                -Centre on planet: centres the camera on a planet.
                -Delete planet: deletes the planet.
                -Follow: allows us to follow a planet. Also toggleable with TAB.
                """;
    }
}
