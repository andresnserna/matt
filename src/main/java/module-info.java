/**
 * Architecture layer: controller
 * Module wiring for the JavaFX application and exported packages.
 */
module edu.stedwards.matt {
    requires javafx.controls;
    requires transitive javafx.graphics;
    exports edu.stedwards.matt;
}
