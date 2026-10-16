package edu.stedwards.matt.ui.layouts.onboarding;

import edu.stedwards.matt.ui.TextStyles;
import edu.stedwards.matt.ui.interaction.Buttons;
import edu.stedwards.matt.ui.labels.OnboardingScreenText;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextFlow;

// onboarding - personal info
// onboarding - education
// onboarding - experiance
// onboarding - projects
// onboarding - skills
// onboarding - awards/certs
// onboarding - websites

public class OnboardingEnterInfo extends BorderPane {

   public OnboardingEnterInfo(){
      getStyleClass().addAll("wizard-background", "screen-padding");
      
      TextFlow title = TextStyles.buildTitle(OnboardingScreenText.PI_TITLE, "wizard-title");
      setTop(title);
      
      // setCenter(buildWelcomeMessage());
      
      setBottom(new Buttons().buildNextButton());
   }
   
}
