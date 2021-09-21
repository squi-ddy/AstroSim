module AstroSim {
    opens AstroSim.controller;
    opens AstroSim.view;
    opens AstroSim.model;
    opens AstroSim;
    requires javafx.base;
    requires javafx.controls;
    requires java.xml;
}