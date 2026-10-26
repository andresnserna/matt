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
    requires org.jsoup;
    requires org.apache.pdfbox;
    requires java.net.http;
    requires fr.brouillard.oss.cssfx;
    requires javafx.web;
    exports edu.stedwards.matt;

    

}
