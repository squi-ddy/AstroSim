package astrosim.view.nodes;

import astrosim.model.math.Vec2;
import astrosim.model.simulation.OrbitalPath;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;

import java.util.ArrayDeque;
import java.util.Deque;

public class Trail extends Group {
    private final Polyline permanentTrail;
    private final Deque<Shape> trailLines;
    private final Group dynamicTrailWrapper;
    private final OrbitalPath path;
    private Color color;
    private double trailLength;
    private int numPoints;
    private boolean showing;

    private static boolean isPolyline = false;
    private static boolean stepwise = false;
    private static boolean showingPermanent = false;

    public static void setShowingPermanent(boolean showingPermanent) {
        Trail.showingPermanent = showingPermanent;
    }

    public static boolean isShowingPermanent() {
        return showingPermanent;
    }

    public static void setGraphicMode(int renderQuality) {
        if (renderQuality == 0) {
            isPolyline = true;
            stepwise = false;
        } else if (renderQuality == 1) {
            isPolyline = false;
            stepwise = true;
        } else {
            isPolyline = false;
            stepwise = false;
        }
    }

    public static int getGraphicMode() {
        if (isPolyline) return 0;
        else if (stepwise) return 1;
        return 2;
    }

    public Trail(Vec2 position, OrbitalPath path, boolean showing) {
        this.showing = showing;
        this.path = path;
        numPoints = 1;
        trailLength = 0;
        permanentTrail = new Polyline();
        permanentTrail.setStrokeLineCap(StrokeLineCap.BUTT);
        permanentTrail.setStrokeWidth(10);
        permanentTrail.getPoints().addAll(position.getX(), position.getY(), position.getX(), position.getY());
        trailLines = new ArrayDeque<>();
        if (!isPolyline) {
            Line line = new Line(position.getX(), position.getY(), position.getX(), position.getY());
            line.setStrokeLineCap(StrokeLineCap.BUTT);
            line.setStrokeWidth(10);
            trailLines.add(line);
        }
        else {
            Polyline polyline = new Polyline();
            polyline.setStrokeWidth(10);
            polyline.setStrokeLineCap(StrokeLineCap.BUTT);
            polyline.getPoints().addAll(position.getX(), position.getY(), position.getX(), position.getY());
            trailLines.add(polyline);
        }
        this.dynamicTrailWrapper = new Group();
        dynamicTrailWrapper.getChildren().setAll(trailLines);
        super.getChildren().addAll(permanentTrail, dynamicTrailWrapper);
        manageShowing();
    }

    private void manageShowing() {
        clearTrail();
        if (!showing) {
            dynamicTrailWrapper.setManaged(false);
            dynamicTrailWrapper.setVisible(false);
            permanentTrail.setVisible(false);
            permanentTrail.setManaged(false);
        }
        else if (showingPermanent) {
            dynamicTrailWrapper.setManaged(false);
            dynamicTrailWrapper.setVisible(false);
            permanentTrail.setManaged(true);
            permanentTrail.setVisible(true);
        } else {
            permanentTrail.setManaged(false);
            permanentTrail.setVisible(false);
            dynamicTrailWrapper.setManaged(true);
            dynamicTrailWrapper.setVisible(true);
        }
    }

    private void setupTrailLines(Vec2 position) {
        trailLength = 0;
        numPoints = 1;
        trailLines.clear();
        if (!isPolyline) {
            Line line = new Line(position.getX(), position.getY(), position.getX(), position.getY());
            line.setStrokeLineCap(StrokeLineCap.BUTT);
            line.setStrokeWidth(10);
            trailLines.add(line);
        }
        else {
            Polyline polyline = new Polyline();
            polyline.setStrokeWidth(10);
            polyline.setStrokeLineCap(StrokeLineCap.BUTT);
            polyline.getPoints().addAll(position.getX(), position.getY(), position.getX(), position.getY());
            trailLines.add(polyline);
        }
        dynamicTrailWrapper.getChildren().setAll(trailLines);
    }

    public void updateGlobalSettings() {
        manageShowing();
        setupTrailLines(path.getPosition());
        if (color != null) setColor(color);
    }

    public void setColor(Color color) {
        this.color = color;
        permanentTrail.setStroke(color);
        if (isPolyline) {
            trailLines.getLast().setStroke(color);
        }
    }

    public void clearPermanentTrail() {
        double lastX = permanentTrail.getPoints().get(permanentTrail.getPoints().size() - 2);
        double lastY = permanentTrail.getPoints().get(permanentTrail.getPoints().size() - 1);
        permanentTrail.getPoints().clear();
        permanentTrail.getPoints().addAll(lastX, lastY, lastX, lastY);
    }

    public void addPointToTrail(Vec2 end) {
        permanentTrail.getPoints().set(permanentTrail.getPoints().size() - 2, end.getX());
        permanentTrail.getPoints().set(permanentTrail.getPoints().size() - 1, end.getY());
        permanentTrail.getPoints().addAll(end.getX(), end.getY());
        if (showingPermanent || !showing) return;
        numPoints++;
        if (isPolyline) {
            if (!(trailLines.getLast() instanceof Polyline)) clearTrail();
            Polyline line = (Polyline) trailLines.getLast();
            line.getPoints().set(line.getPoints().size() - 2, end.getX());
            line.getPoints().set(line.getPoints().size() - 1, end.getY());
            line.getPoints().addAll(end.getX(), end.getY());
            return;
        }
        if (!(trailLines.getLast() instanceof Line)) clearTrail();
        Line lastLine = (Line) trailLines.getLast();
        lastLine.setEndX(end.getX());
        lastLine.setEndY(end.getY());
        trailLength += new Vec2(lastLine.getStartX(), lastLine.getStartY()).sub(new Vec2(lastLine.getEndX(), lastLine.getEndY())).magnitude();
        Line line = new Line(end.getX(), end.getY(), end.getX(), end.getY());
        line.setStrokeLineCap(StrokeLineCap.BUTT);
        line.setStrokeWidth(10);
        line.setStroke(color);
        trailLines.add(line);
        double currTrailLength = 0;
        for (Shape node : trailLines) {
            Line currLine = (Line) node;
            double extraLength = new Vec2(currLine.getStartX(), currLine.getStartY()).sub(new Vec2(currLine.getEndX(), currLine.getEndY())).magnitude();
            if (!stepwise) {
                currLine.setStroke(new LinearGradient(currLine.getStartX(), currLine.getStartY(), currLine.getEndX(), currLine.getEndY(), false, CycleMethod.NO_CYCLE,
                        new Stop(0, color.deriveColor(1, 1, 1, getTransparency(currTrailLength / trailLength))),
                        new Stop(1, color.deriveColor(1, 1, 1, getTransparency((currTrailLength + extraLength) / trailLength)))
                ));
            } else {
                currLine.setStroke(color.deriveColor(1, 1, 1, getTransparency(currTrailLength / trailLength)));
            }
            currTrailLength += extraLength;
        }
        dynamicTrailWrapper.getChildren().setAll(trailLines);
    }

    public void changePoint(Vec2 end) {
        if (!showing) return;
        permanentTrail.getPoints().set(permanentTrail.getPoints().size() - 2, end.getX());
        permanentTrail.getPoints().set(permanentTrail.getPoints().size() - 1, end.getY());
        if (showingPermanent) return;
        if (isPolyline) {
            if (!(trailLines.getLast() instanceof Polyline)) clearTrail();
            Polyline line = (Polyline) trailLines.getLast();
            line.getPoints().set(line.getPoints().size() - 2, end.getX());
            line.getPoints().set(line.getPoints().size() - 1, end.getY());
            return;
        }
        if (!(trailLines.getLast() instanceof Line)) clearTrail();
        Line lastLine = (Line) trailLines.getLast();
        lastLine.setEndX(end.getX());
        lastLine.setEndY(end.getY());
    }

    public void deletePointFromTrail() {
        if (showingPermanent || !showing) return;
        numPoints--;
        if (isPolyline) {
            if (!(trailLines.getLast() instanceof Polyline)) clearTrail();
            Polyline line = (Polyline) trailLines.getLast();
            line.getPoints().remove(0, 2);
            return;
        }
        if (!(trailLines.getLast() instanceof Line)) clearTrail();
        Line line = (Line) trailLines.removeFirst();
        trailLength -= new Vec2(line.getStartX(), line.getStartY()).sub(new Vec2(line.getEndX(), line.getEndY())).magnitude();
        dynamicTrailWrapper.getChildren().remove(0);
    }

    private double getTransparency(double interpolate) {
        return interpolate * interpolate;
    }

    public void clearTrail() {
        trailLength = 0;
        numPoints = 1;
        if (isPolyline) {
            if (!(trailLines.getLast() instanceof Polyline)) setupTrailLines(path.getPosition());
            Polyline line = (Polyline) trailLines.getLast();
            double lastX = path.getPosition().getX();
            double lastY = path.getPosition().getY();
            line.getPoints().clear();
            line.getPoints().addAll(lastX, lastY, lastX, lastY);
        } else {
            trailLines.clear();
            trailLines.add(new Line(path.getPosition().getX(), path.getPosition().getY(), path.getPosition().getX(), path.getPosition().getY()));
        }
        dynamicTrailWrapper.getChildren().setAll(trailLines);
    }

    public int getNumPoints() {
        return numPoints;
    }

    public void setShowing(boolean showing) {
        this.showing = showing;
        manageShowing();
    }

    public boolean isShowing() {
        return showing;
    }
}
