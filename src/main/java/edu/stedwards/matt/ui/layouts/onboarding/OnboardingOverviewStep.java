package edu.stedwards.matt.ui.layouts.onboarding;

import edu.stedwards.matt.ui.TextStyles;
import edu.stedwards.matt.ui.interaction.Buttons;
import edu.stedwards.matt.ui.labels.OnboardingScreenText;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

public class OnboardingOverviewStep extends BorderPane {

   private VBox buildAllInfoContainers(){
      // this method will create all the containers to hold the info based on what the user put in during onboarding, 
      // will need to make sure all that info is still in scope (because the DB write will happen after the user 
      // confirms during the end of this step) so just loops through all the info and creates a container for each section 
      // of info, then adds them to a VBox and returns it. also this is a scrollable area up and down, with the bottom 
      // section having a transparency blur just before getting to the height of the next button
      
      VBox allInfoContainers = new VBox();
      // populate allInfoContainers with the info sections
      return allInfoContainers;
   }

   public OnboardingOverviewStep(){
      getStyleClass().addAll("wizard-background", "screen-padding");
      
      TextFlow title = TextStyles.buildTitle(OnboardingScreenText.FIN_TITLE, "wizard-title");
      // ^^this title will change with the specific step you're on, so this impl rn is temporary
      setTop(title);
      
      // setCenter(buildWelcomeMessage());
      
      setBottom(new Buttons().buildNextButton());
   }

}
