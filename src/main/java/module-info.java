module org.example.contractparser {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires org.apache.poi.ooxml;
    requires java.net.http;
    requires io.github.cdimascio.dotenv.java;
    requires software.amazon.awssdk.services.textract;
    requires software.amazon.awssdk.core;
    requires software.amazon.awssdk.regions;
    requires software.amazon.awssdk.auth;

    opens org.example.contractparser to javafx.fxml;
    exports org.example.contractparser;
}