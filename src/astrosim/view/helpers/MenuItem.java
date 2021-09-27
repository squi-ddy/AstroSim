package astrosim.view.helpers;

public record MenuItem(String name, Runnable doOnClick) {
    public static MenuItem SPACING = new MenuItem(null, () -> {});
}
