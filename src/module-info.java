module AstroSim {
    opens astrosim.controller;
    opens astrosim.model.xml;
    opens astrosim.model.managers;
    opens astrosim.model.math;
    opens astrosim.model.simulation;
    opens astrosim.view.helpers;
    opens astrosim;
    opens astrosim.view.nodes.inspector;
    opens astrosim.view.nodes.menu;
    opens astrosim.view.nodes;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires org.jetbrains.annotations;
}