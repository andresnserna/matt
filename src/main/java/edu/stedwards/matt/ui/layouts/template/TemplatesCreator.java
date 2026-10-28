package edu.stedwards.matt.ui.layouts.template;

import edu.stedwards.matt.ui.TextStyles;
import edu.stedwards.matt.ui.interaction.Buttons;
import edu.stedwards.matt.ui.labels.TemplatesScreenText;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextFlow;
import javafx.scene.web.HTMLEditor;
import javafx.geometry.Insets;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class TemplatesCreator extends BorderPane {

   public TemplatesCreator(){
      getStyleClass().addAll("screen-padding");
      
      TextFlow title = TextStyles.buildTitle(TemplatesScreenText.PLACEHOLDER_TITLE, "screen-title");
      setTop(title);
      
      // setCenter(buildWelcomeMessage());
      HTMLEditor htmlEditor = new HTMLEditor();
      htmlEditor.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
      setCenter(htmlEditor);
      BorderPane.setMargin(htmlEditor, new Insets(0, 0, 24, 0));
      
      setBottom(new Buttons().buildNextButton());
   }
}