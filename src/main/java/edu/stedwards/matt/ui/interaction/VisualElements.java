package edu.stedwards.matt.ui.interaction;

import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.control.Button;
import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.parser.SVGLoader;
import com.github.weisj.jsvg.ui.jfx.FXSVGCanvas;

import edu.stedwards.matt.ui.labels.MainScreenText;
import edu.stedwards.matt.ui.labels.OtherScreenText;
import javafx.scene.control.ProgressBar;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import java.net.URL;
import java.util.Objects;
import edu.stedwards.matt.ui.interaction.Buttons;


public class VisualElements {
   private static final double COLLAPSED_SIDEBAR_WIDTH = 64;
   private static final double DRAWER_SIDE_INSET = 8;
   private double sidebarWidth = 240;

   /**
    * ?
    */
   public double getSidebarWidth() {
      return sidebarWidth;
   }

   /**
    * ?
    */
   public void setSidebarWidth(double sidebarWidth) {
      if (!Double.isFinite(sidebarWidth) || sidebarWidth <= 2 * DRAWER_SIDE_INSET) {
         throw new IllegalArgumentException("Sidebar width must be greater than 16 pixels");
      }
      this.sidebarWidth = sidebarWidth;
   }
   
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
      sideBar.getStyleClass().add("sidebar");
      sideBar.setMinWidth(COLLAPSED_SIDEBAR_WIDTH);
      sideBar.setPrefWidth(getSidebarWidth());
      double buttonWidth = getSidebarWidth() - (2 * DRAWER_SIDE_INSET) - 4;

      VBox menuButtons = new VBox();
      // make the buttons + add the unpressed style class
      Button homeButton = Buttons.button(MainScreenText.BUTTON_HOME, null, buttonWidth, "sidebar-button");
      Button templatesButton = Buttons.button(MainScreenText.BUTTON_TEMPLATES, null, buttonWidth, "sidebar-button");
      Button applicationsButton = Buttons.button(MainScreenText.BUTTON_APPLICATIONS, null, buttonWidth, "sidebar-button");
      Button myInfoButton = Buttons.button(MainScreenText.BUTTON_MY_INFO, null, buttonWidth, "sidebar-button");
      Button settingsButton = Buttons.button(MainScreenText.BUTTON_SETTINGS, null, buttonWidth, "sidebar-button");

      // add conditional code for when button pressed, change the styleclass

      // ok now add em all to the vbox
      menuButtons.getChildren().addAll(homeButton, templatesButton, applicationsButton, myInfoButton, settingsButton);

      VBox recentApplicationsContent = new VBox();
         //add the recent items from db
      VBox recentApplicationsDrawer = collapsableDrawer(
            OtherScreenText.PLACEHOLDER_TITLE,
            recentApplicationsContent,
            "sidebar-recent-items",
            getSidebarWidth());

      VBox recentMaterialsContent = new VBox();
         // add the recent materials from db
      VBox recentMaterialsDrawer = collapsableDrawer(
            OtherScreenText.PLACEHOLDER_TITLE,
            recentMaterialsContent,
            "sidebar-menu-items",
            getSidebarWidth());

      Region spacer = new Region();
      VBox.setVgrow(spacer, Priority.ALWAYS);

      HBox footer = new HBox();
      footer.setAlignment(Pos.CENTER_LEFT);

      Label versionLabel = new Label(OtherScreenText.APP_VERSION);
      versionLabel.getStyleClass().add("sidebar-version-label");

      Region footerSpacer = new Region();
      HBox.setHgrow(footerSpacer, Priority.ALWAYS);

      Button toggle = new Button("‹");
      toggle.getStyleClass().add("sidebar-toggle-button");
      toggle.setAccessibleText("Collapse sidebar");
      footer.getChildren().addAll(versionLabel, footerSpacer, toggle);
      VBox.setVgrow(footer, Priority.NEVER);

      //build the side bar
      sideBar.getChildren().addAll(
         menuButtons, 
         recentMaterialsDrawer, recentApplicationsDrawer, 
         spacer, 
         footer
      );

      toggle.setOnAction(event -> {
         boolean collapsing = sideBar.getPrefWidth() > COLLAPSED_SIDEBAR_WIDTH;
         sideBar.setPrefWidth(collapsing ? COLLAPSED_SIDEBAR_WIDTH : getSidebarWidth());
         sideBar.getStyleClass().remove("sidebar-collapsed");

         if (collapsing) {
            sideBar.getStyleClass().add("sidebar-collapsed");
            toggle.setText("›");
            toggle.setAccessibleText("Expand sidebar");
            setMenuBarItems(false, versionLabel, recentMaterialsDrawer, recentApplicationsDrawer);
            //TODO: ^this might need to be able to accept a list of nodes of the specified types, since the main sidebar is gonna hold buttons and drawer content and other stuff too
         } else {
            toggle.setText("‹");
            toggle.setAccessibleText("Collapse sidebar");
            setMenuBarItems(true, versionLabel, recentMaterialsDrawer, recentApplicationsDrawer);
            versionLabel.setVisible(true);
            versionLabel.setManaged(true);
         }
      });

      return sideBar;
   }

   /**
    * ?
    */
   private void setMenuBarItems(boolean visible, Label versionLabel, VBox recentMaterialsDrawer, VBox recentApplicationsDrawer) {
      versionLabel.setVisible(visible);
      versionLabel.setManaged(visible);
      recentMaterialsDrawer.setVisible(visible);
      recentMaterialsDrawer.setManaged(visible);
      recentApplicationsDrawer.setVisible(visible);
      recentApplicationsDrawer.setManaged(visible);
   }

   /**
    * Creates an expanded-by-default drawer that collapses vertically.
    * Place it in a VBox to have following content move when the drawer collapses.
    */
   public VBox collapsableDrawer(String title, Node content, String styleClass, double parentContainerWidth){
      Objects.requireNonNull(title, "title");
      Objects.requireNonNull(content, "content");
      Objects.requireNonNull(styleClass, "styleClass");
      double width = parentContainerWidth - 2 * DRAWER_SIDE_INSET;

      Label heading = new Label(title);
      heading.getStyleClass().add("drawer-title");

      Region headerSpacer = new Region();
      HBox.setHgrow(headerSpacer, Priority.ALWAYS);

      Button toggle = new Button("⌃");
      toggle.getStyleClass().add("drawer-toggle");
      toggle.setAccessibleText("Collapse " + title);

      // the line that goes below the drawer
      Line drawerBar = new Line(0, 0, width - 8, 0);
      drawerBar.getStyleClass().add("drawer-bar");

      HBox header = new HBox(heading, headerSpacer, toggle);
      header.getStyleClass().add("drawer-header");
      header.setAlignment(Pos.CENTER_LEFT);

      VBox drawer = new VBox(header, drawerBar, content);
      drawer.getStyleClass().addAll("drawer", styleClass);
      drawer.setMinWidth(width);
      drawer.setPrefWidth(width);
      drawer.setMaxWidth(width);
      content.getStyleClass().add("drawer-content");

      toggle.setOnAction(event -> {
         boolean collapsing = content.isManaged();
         content.setVisible(!collapsing);
         content.setManaged(!collapsing);
         toggle.setText(collapsing ? "⌄" : "⌃");
         toggle.setAccessibleText((collapsing ? "Expand " : "Collapse ") + title);
      });

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
