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
    //requires proto.google.cloud.vision.v1;
    //requires google.cloud.vision;
    requires protobuf.java;
    requires proto.google.cloud.vision.v1;
    requires google.cloud.vision;

    opens org.example.contractparser to javafx.fxml;
    exports org.example.contractparser;
}