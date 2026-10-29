package edu.stedwards.matt.ui.layouts.onboarding;

import edu.stedwards.matt.ui.TextStyles;
import edu.stedwards.matt.ui.interaction.Buttons;
import edu.stedwards.matt.ui.interaction.Inputs;
import edu.stedwards.matt.ui.interaction.VisualElements;
import edu.stedwards.matt.ui.labels.OnboardingScreenText;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

// onboarding - personal info

public class OnboardingEnterInfo extends BorderPane {
   private static final double PERSONAL_FIELD_WIDTH = 260;

   private TextField buildPersonalField(String placeholder) {
      TextField field = Inputs.inputField(placeholder, PERSONAL_FIELD_WIDTH, PERSONAL_FIELD_WIDTH);
      return field;
   }

   private VBox buildPersonalFieldGroup(String labelText, String placeholder) {
      VBox group = Inputs.buildInputGroup(
            TextStyles.buildLabel(labelText, "input-label"), buildPersonalField(placeholder));
      group.setMaxWidth(Double.MAX_VALUE);
      return group;
   }

   private VBox buildPersonalColumn(Node... fields) {
      VBox column = new VBox(16);
      column.setFillWidth(true);
      column.setMaxWidth(Double.MAX_VALUE);
      column.getChildren().addAll(fields);
      return column;
   }

   private VBox buildPersonalInfoContent() {
      Label instruction = new Label(OnboardingScreenText.CONFIRM_MESSAGE_1);
      instruction.getStyleClass().add("onboarding-wizard-subtitle");
      instruction.setWrapText(true);
      instruction.setMaxWidth(Double.MAX_VALUE);

      VBox nameColumn = buildPersonalColumn(
            buildPersonalFieldGroup(OnboardingScreenText.PI_LABEL_FIRST, OnboardingScreenText.PI_EXAMPLE_FIRST),
            buildPersonalFieldGroup(OnboardingScreenText.PI_LABEL_LAST, OnboardingScreenText.PI_EXAMPLE_LAST));

      VBox stateGroup = buildPersonalFieldGroup(OnboardingScreenText.PI_LABEL_STATE,
            OnboardingScreenText.PI_EXAMPLE_STATE);
      VBox zipGroup = buildPersonalFieldGroup(OnboardingScreenText.PI_LABEL_ZIP,
            OnboardingScreenText.LABEL_ZIP_PLACEHOLDER);
      HBox stateAndZip = new HBox(12, stateGroup, zipGroup);
      stateAndZip.setPrefWidth(PERSONAL_FIELD_WIDTH);
      stateAndZip.setMaxWidth(PERSONAL_FIELD_WIDTH);
      stateGroup.setMaxWidth(Double.MAX_VALUE);
      zipGroup.setMaxWidth(Double.MAX_VALUE);
      HBox.setHgrow(stateGroup, Priority.ALWAYS);
      HBox.setHgrow(zipGroup, Priority.ALWAYS);

      VBox locationColumn = buildPersonalColumn(
            buildPersonalFieldGroup(OnboardingScreenText.PI_LABEL_ADDY1,
                  OnboardingScreenText.LABEL_ADDY1_PLACEHOLDER),
            buildPersonalFieldGroup(OnboardingScreenText.PI_LABEL_ADDY2,
                  OnboardingScreenText.LABEL_ADDY2_PLACEHOLDER),
            buildPersonalFieldGroup(OnboardingScreenText.PI_LABEL_CITY,
                  OnboardingScreenText.LABEL_CITY_PLACEHOLDER), stateAndZip);

      VBox contactColumn = buildPersonalColumn(
            buildPersonalFieldGroup(OnboardingScreenText.PI_LABEL_PHONE,
                  OnboardingScreenText.LABEL_PHONE_PLACEHOLDER),
            buildPersonalFieldGroup(OnboardingScreenText.PI_LABEL_EMAIL,
                  OnboardingScreenText.LABEL_EMAIL_PLACEHOLDER));

      HBox columns = new HBox(36, nameColumn, locationColumn, contactColumn);
      columns.setAlignment(Pos.TOP_CENTER);
      columns.setFillHeight(true);
      columns.setMaxWidth(Double.MAX_VALUE);
      for (VBox column : new VBox[] { nameColumn, locationColumn, contactColumn }) {
         HBox.setHgrow(column, Priority.ALWAYS);
      }

      VBox content = new VBox(36, instruction, columns);
      content.setFillWidth(true);
      content.setMaxWidth(Double.MAX_VALUE);
      content.setPadding(new Insets(24, 0, 24, 0));
      return content;
   }

   public OnboardingEnterInfo(){
      getStyleClass().addAll("wizard-background", "screen-padding");
      
      TextFlow title = TextStyles.buildTitle(OnboardingScreenText.PI_TITLE, "onboarding-wizard-title");
      setTop(title);
      
      var scrollContent = VisualElements.wizardScrollable(buildPersonalInfoContent());
      setCenter(scrollContent);
      BorderPane.setMargin(scrollContent, new Insets(0, 0, 24, 0));
      
      setBottom(new Buttons().buildNextButton());
   }
   
}
