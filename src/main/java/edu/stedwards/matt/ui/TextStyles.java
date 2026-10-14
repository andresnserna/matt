/**
 * Architecture layer: view
 * TODO: (SHORT description of class here)
 */

package edu.stedwards.matt.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.scene.layout.BorderPane;
import javafx.scene.Node;
import javafx.scene.text.Text;

/**
 * TODO: (LONG description of class here)
 */

public class TextStyles {

   private TextStyles() {
   }
   
   /**
    * Builds a field label that will be passed into buildInputGroup(). or also used for loading item
    */
   public static Label buildLabel(String label, String styleClass) {
      Label fieldLabel = new Label(label);
      fieldLabel.getStyleClass().add(styleClass);
      return fieldLabel;
   }

   /**
    * Builds a piece of text that can be used anywhere in the app, with support for multiple text parts. 
    * This will be used for screen titles, headings, sub headings, blocks of instructions, anywhere where text is generally needed.
    * the style of the text passed in here is determined by the style class passed in, as well as the alignment.
    */
   public static TextFlow buildTitle(String title, String styleClass, TextAlignment alignment) {
      Text titleText = new Text(title);
      titleText.getStyleClass().add(styleClass);
      titleText.setTextAlignment(alignment);
      return buildTitle(titleText);
   }

   public static TextFlow buildTitle(String title, String styleClass) {
      Text titleText = new Text(title);
      titleText.getStyleClass().add(styleClass);
      return buildTitle(titleText);
   }
   
   /**
    * Builds a piece of text with the passed in TextFlow object(s). this is a
    * helper func to the buildTitle(string, string, TextAlignment) method.
    */
   public static TextFlow buildTitle(Text... titleParts) {
      TextFlow title = new TextFlow(titleParts);
      title.getStyleClass().add("welcome-title");
      // title.setTextAlignment(alignment);
      return title;
   }

   /**
    * ?
    */
   public static HBox loadingStatus(String loadingItem) {
      HBox row = new HBox();
      row.setAlignment(Pos.CENTER);

      Label prefix = new Label("Loading ");
      prefix.getStyleClass().add("start-screen-loading-prefix");

      Label item = new Label(loadingItem);
      item.getStyleClass().add("start-screen-loading-item");

      row.getChildren().addAll(prefix, item);
      return row;
   }
   
}
