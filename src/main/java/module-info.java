/**
 * Architecture layer: controller
 * Module wiring for the JavaFX application and exported packages.
 */
module edu.stedwards.matt {
    requires com.github.weisj.jsvg;
    requires com.github.weisj.jsvg.javafx;
    requires javafx.controls;
    requires transitive javafx.graphics;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires java.sql;
    requires java.net.http;
    exports edu.stedwards.matt;

}
