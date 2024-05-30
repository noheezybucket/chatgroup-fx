module sn.dev.chatgroup {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.persistence;
    requires org.hibernate.orm.core;

    opens sn.dev.chatgroup.controller to javafx.fxml;
    opens sn.dev.chatgroup.entity; // Open the package containing entities to Hibernate

    exports sn.dev.chatgroup.controller;
    exports sn.dev.chatgroup;
    exports sn.dev.chatgroup.entity;




}