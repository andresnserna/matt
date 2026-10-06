package edu.stedwards.matt.ui.interaction;

import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

public class VisualElements {

   /*
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

   public VBox mainSideBar() {
      VBox sideBar = new VBox();
      // Add sidebar components here
      return sideBar;
   }

   public BorderPane collapsableDrawer(String title, Node content){
      BorderPane drawer = new BorderPane();
      // add the elements inside the collapsable drawer here
      return drawer;
   }
   
   public TextFlow buildWizardScreenTitle(String title, String styleClass) {
      Text titleText = new Text(title);
      titleText.getStyleClass().add(styleClass);
      return buildWizardScreenTitle(titleText);
   }

   public TextFlow buildWizardScreenTitle(Text... titleParts) {
      TextFlow title = new TextFlow(titleParts);
      title.getStyleClass().add("welcome-title");
      title.setTextAlignment(TextAlignment.CENTER);
      return title;
   }
}
