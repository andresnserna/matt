/**
 * Architecture layer: controller
 * Module wiring for the JavaFX application and exported packages.
 */
module edu.stedwards.matt {
    requires com.github.weisj.jsvg;
    requires com.github.weisj.jsvg.javafx;
    requires javafx.controls;
    requires transitive javafx.graphics;
    requires java.sql;
    exports edu.stedwards.matt;
}
