package edu.stedwards.matt.ui.interaction;

import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.parser.SVGLoader;
import com.github.weisj.jsvg.ui.jfx.FXSVGCanvas;
import javafx.scene.control.ProgressBar;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import java.net.URL;



public class VisualElements {
   
   /**
    * a reusable scroll pane that can be used anywhere in the app, with a styled scrollbar and resizable content.
    */
   public static ScrollPane scrollable(Node content) {
      ScrollPane scrollPane = new ScrollPane(content);
      scrollPane.setFitToWidth(true); // Keep resizable content, like a VBox, the viewport's width
      scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
      scrollPane.getStyleClass().add("content-scroll"); //a styled scrollbar reused across the app
      return scrollPane;
   }

   /**
    * a container box that has information printed in it, not editable, just
    * presented in a concise and uniform way, but a button in the heading bar can
    * take you back to the screen that has this container's information came from
    */   
   public VBox infoContainer(double width, double height, String headingTitle, int headingTitleNum, Node content) {
      Label heading = new Label(headingTitleNum + ". " + headingTitle);
      HBox headingBar = new HBox(heading);
      headingBar.setAlignment(Pos.CENTER_LEFT);

      VBox container = new VBox(headingBar, content);
      container.getStyleClass().add("info-container");
      container.setPrefSize(width, height);
      return container;
   }

   /**
    * this sidebar sticks to the left side of the app at all times and is the main navigation interface for the app.
    */
   public VBox mainSideBar() {
      VBox sideBar = new VBox();
      // Add sidebar components here
      return sideBar;
   }

   /**
    * ??
    */
   public BorderPane collapsableDrawer(String title, Node content){
      BorderPane drawer = new BorderPane();
      // add the elements inside the collapsable drawer here
      return drawer;
   }

   /**
    * ?
    */
   public static FXSVGCanvas startScreenMattLogo() {
      URL logoResource = VisualElements.class.getResource("/images/matt-logo-1.svg");
      if (logoResource == null) {
         System.err.println("[StartLayout] ERROR: /images/matt-logo-1.svg was not found on the classpath.");
         throw new IllegalStateException("Matt logo resource not found");
      }

      SVGDocument document;
      try {
         document = new SVGLoader().load(logoResource);
      } catch (RuntimeException exception) {
         System.err.println("[StartLayout] ERROR: JSVG failed to parse the Matt logo.");
         exception.printStackTrace();
         throw exception;
      }
      if (document == null) {
         System.err.println("[StartLayout] ERROR: JSVG returned no document for the Matt logo.");
         throw new IllegalStateException("JSVG returned no document for the Matt logo");
      }

      FXSVGCanvas logo = new FXSVGCanvas();
      logo.setRenderBackend(FXSVGCanvas.RenderBackend.AWT);
      logo.setDocument(document);
      logo.setShowTransparentPattern(false);
      logo.getStyleClass().add("start-screen-logo");
      logo.setMinSize(220, 157);
      logo.setPrefSize(220, 157);
      logo.setMaxSize(220, 157);
      // System.out.println("[StartLayout] JSVG parsed the Matt logo and attached it to the AWT renderer.");
      // reportLogoRender(logo);

      return logo;
   }
   
   /**
    * ?
    */
   public static HBox buildLoadingBar(ProgressBar loadingProgress, Label loadingPercentage) {
      HBox row = new HBox(10);
      row.setAlignment(Pos.CENTER);

      loadingProgress.setPrefWidth(260);
      loadingProgress.getStyleClass().add("start-screen-progress");
      loadingPercentage.getStyleClass().add("start-screen-loading-percentage");
      loadingProgress.progressProperty()
         .addListener((observable, oldProgress, newProgress) ->
               loadingPercentage.setText(Math.round(newProgress.doubleValue() * 100) + "%"));

      row.getChildren().addAll(loadingProgress, loadingPercentage);

      return row;
   }
}
