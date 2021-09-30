package astrosim.view.helpers;

import astrosim.Main;
import astrosim.model.managers.SettingsManager;
import javafx.css.CssParser;
import javafx.css.Declaration;
import javafx.css.Rule;
import javafx.css.Stylesheet;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class ThemeColors {
    private ThemeColors() {}

    public static Color getThemeColor(String name) {
        try {
            CssParser parser = new CssParser();
            Stylesheet css = parser.parse(Objects.requireNonNull(Main.class.getResource("/view/css/" + (SettingsManager.getGlobalSettings().isDarkMode() ? "dark.css" : "light.css"))));
            Rule rootRule = css.getRules().get(0);
            Optional<Declaration> declaration = rootRule.getDeclarations().stream().filter(d -> d.getProperty().equals(name)).findFirst();
            if (declaration.isPresent()) {
                Object parsed = declaration.get().getParsedValue().convert(null);
                if (parsed instanceof Color color) return color;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
