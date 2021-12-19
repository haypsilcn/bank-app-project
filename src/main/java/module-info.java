module haypsilcn.bankappproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens haypsilcn.bankappproject to javafx.fxml;
    opens bank;
    exports bank;
    exports haypsilcn.bankappproject;
}