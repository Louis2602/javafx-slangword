module fitus.clc.java.javafxslangword {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens fitus.clc.java.javafxslangword to javafx.fxml;
    exports fitus.clc.java.javafxslangword;
}