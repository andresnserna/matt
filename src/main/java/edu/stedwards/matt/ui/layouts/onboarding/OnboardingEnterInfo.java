package edu.stedwards.matt.ui.layouts.onboarding;

import edu.stedwards.matt.ui.TextStyles;
import edu.stedwards.matt.ui.interaction.Buttons;
import edu.stedwards.matt.ui.interaction.VisualElements;
import edu.stedwards.matt.ui.labels.OnboardingScreenText;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

// onboarding - personal info

public class OnboardingEnterInfo extends BorderPane {

   public OnboardingEnterInfo(){
      getStyleClass().addAll("wizard-background", "screen-padding");
      
      TextFlow title = TextStyles.buildTitle(OnboardingScreenText.PI_TITLE, "onboarding-wizard-title");
      setTop(title);
      
      var scrollContent = VisualElements.wizardScrollable(new VBox());
      setCenter(scrollContent);
      BorderPane.setMargin(scrollContent, new Insets(0, 0, 24, 0));
      
      setBottom(new Buttons().buildNextButton());
   }
   
}
