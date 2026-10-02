package edu.stedwards.matt.ui.layouts.onboarding;

import edu.stedwards.matt.ui.interaction.VisualElements;
import edu.stedwards.matt.ui.interaction.Buttons;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

public class FinalOverviewStep extends BorderPane{

   private VBox buildOverviewMessage() {
      VBox message = new VBox();
      // add content
      return message;
   }

   private VBox buildAllInfoContainers() {
      // this method will create all the containers to hold the info based on what the user put in during onboarding, 
      // will need to make sure all that info is still in scope (because the DB write will happen after the user 
      // confirms during the end of this step) so just loops through all the info and creates a container for each section 
      // of info, then adds them to a VBox and returns it. also this is a scrollable area up and down, with the bottom 
      // section having a transparency blur just before getting to the height of the next button
   }

   public FinalOverviewStep() {
      getStyleClass().addAll("app-onboarding", "welcome-screen");
      setTop(new VisualElements().buildWizardScreenTitle());
      setCenter(buildOverviewMessage());
      setBottom(new Buttons().buildNextButton());
      BorderPane.setAlignment(getCenter(), Pos.CENTER_LEFT);
   }

}
