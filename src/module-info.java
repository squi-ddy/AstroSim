module AstroSim {
    opens astrosim.controller;
    opens astrosim.view.fxml;
    opens astrosim.model;
    opens astrosim;
    requires javafx.base;
    requires javafx.controls;
    requires java.xml;
}