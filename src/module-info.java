module ClienteCorreo {

    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.mail;
    requires java.base;
    requires org.controlsfx.controls;
    requires commons.email;
    requires ComponenteReloj;

    exports emma.logic;
    exports emma.models;
    exports emma.views;
    exports emma;

    opens emma.views to javafx.fxml;

}