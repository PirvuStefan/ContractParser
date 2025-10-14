module org.example.contractparser {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires org.apache.poi.ooxml;

    opens org.example.contractparser to javafx.fxml;
    exports org.example.contractparser;
}