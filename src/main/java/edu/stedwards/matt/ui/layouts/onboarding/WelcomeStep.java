/**
 * Architecture layer: view
 * JavaFX layout for the Start screen.
 */

package edu.stedwards.matt.ui.layouts.onboarding;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import edu.stedwards.matt.ui.interaction.Buttons;

/**
 * TODO: (LONG description of this class)
 */

public class WelcomeStep extends HBox {

   private HBox buildScreenTitle() {
      HBox row = new HBox();
      // row.setAlignment(Pos.CENTER);

      // row.getChildren().add(logo);

      return row;
   }

   private HBox buildInstructionText1() {
      HBox row = new HBox();
      // row.setAlignment(Pos.CENTER);

      // row.getChildren().add(logo);

      return row;
   }

   /**
    * this might be a more universal layout method, since i'll be building the horizontal button group at least twice more in the job upload and template upload
    */
   private HBox buildUploadButtonGroupH() {
      HBox row = new HBox();
      // row.setAlignment(Pos.CENTER);

      // row.getChildren().add(logo);

      return row;
   }
   
   private HBox buildInstructionText2() {
      HBox row = new HBox();
      // row.setAlignment(Pos.CENTER);

      // row.getChildren().add(logo);

      return row;
   }

   /**
    * this might be a more universal layout method, since i'll be building input fields alot
    */
   private HBox buildInputField() {

      HBox row = new HBox();
      // row.setAlignment(Pos.CENTER);

      // row.getChildren().add(logo);

      return row;
   }

   private HBox buildNextButton() {
      HBox row = new HBox();
      // row.setAlignment(Pos.CENTER);

      // String text = "";
      // EventHandler<ActionEvent> onAction = null;
      // String styleClasses = "";
      // Button nextButton = new Button(text, onAction, styleClasses)

      // row.getChildren().add(nextButton);

      return row;
   }

   public WelcomeStep() {
        getChildren().addAll(
            // h1 “Upload Your Resume“ screen title
            buildScreenTitle(),
            // h2 instruction text
            buildInstructionText1(),
            // upload button group - horizontal
            buildUploadButtonGroupH(),
            // h2 instruction text
            buildInstructionText2(),
            // input field (design has height 40.. see how that translates to window)
            buildInputField(),
            // input field (design has height 40.. see how that translates to window)
            buildNextButton()
        );
    }

}
