package edu.stedwards.matt.ui.layouts.onboarding;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import edu.stedwards.matt.ui.interaction.Buttons;
import edu.stedwards.matt.ui.labels.OnboardingScreenText;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

public class ResumeUploadStep extends BorderPane {

   private TextFlow buildScreenTitle() {

   }

   private VBox buildUploadMessage() {

   }

   private VBox buildInstructionMessage1() {

   }

   private HBox buildUploadButtonGroup() {

   }

   private VBox buildInstructionMessage2() {

   }

   private HBox buildInputTextField() {

   }


   private HBox buildNextButton() {
      Button next = Buttons.button(OnboardingScreenText.BUTTON_NEXT, null, "onboarding-next-button");
      next.setGraphic(new Label("›"));
      next.setContentDisplay(ContentDisplay.RIGHT);
      next.setGraphicTextGap(8);

      HBox footer = new HBox(next);
      footer.setAlignment(Pos.CENTER_RIGHT);
      return footer;
   }

   public ResumeUploadStep() {
      getStyleClass().addAll("app-onboarding", "welcome-screen");
      setTop(buildScreenTitle());
      setCenter(buildUploadMessage());
      setCenter(buildInstructionMessage1());
      setCenter(buildUploadButtonGroup());
      setCenter(buildInstructionMessage2());
      setCenter(buildInputTextField());
      setBottom(buildNextButton());
      BorderPane.setAlignment(getCenter(), Pos.CENTER_LEFT);
   }

}
