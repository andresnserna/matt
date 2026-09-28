package edu.stedwards.matt.ui.labels;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class TextStyles {
   private TextStyles() {
   }

   // public static Label heading(String text) {
   //    Label label = new Label(text);
   //    label.getStyleClass().add("window-heading-1");
   //    return label;
   // }

   // public static Label instruction(String text) {
   //    Label label = new Label(text);
   //    label.getStyleClass().add("instruction-text");
   //    return label;
   // }
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
