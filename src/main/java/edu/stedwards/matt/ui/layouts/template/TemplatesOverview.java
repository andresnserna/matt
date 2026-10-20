package edu.stedwards.matt.ui.layouts.template;

import edu.stedwards.matt.ui.TextStyles;
import edu.stedwards.matt.ui.interaction.Buttons;
import edu.stedwards.matt.ui.labels.TemplatesScreenText;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextFlow;

public class TemplatesOverview extends BorderPane {

   public TemplatesOverview(){
      getStyleClass().addAll("screen-padding");
      
      TextFlow title = TextStyles.buildTitle(TemplatesScreenText.PLACEHOLDER_TITLE, "screen-title");
      setTop(title);
      
      // setCenter(buildWelcomeMessage());
      
      setBottom(new Buttons().buildNextButton());
   }

}