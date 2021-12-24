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
    requires com.google.gson;

    opens haypsilcn.bankappproject to javafx.fxml;
    opens bank;
    opens bank.exceptions;
    opens bank.exceptions.customDateFormat;

    exports bank;
    exports bank.exceptions;
    exports haypsilcn.bankappproject;
    exports bank.exceptions.customDateFormat;
}