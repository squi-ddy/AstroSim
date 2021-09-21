module AstroSim {
    opens AstroSim.controller;
    opens AstroSim.view;
    opens AstroSim.model;
    opens AstroSim.model.xml;
    opens AstroSim.model.simulation;
    requires javafx.base;
    requires javafx.controls;
    requires java.xml;
}