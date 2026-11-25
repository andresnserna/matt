package edu.stedwards.matt.ui.layouts.onboarding;

import edu.stedwards.matt.models.OnboardingSession;
import edu.stedwards.matt.models.entity.User;
import edu.stedwards.matt.services.ValidationService;
import edu.stedwards.matt.ui.TextStyles;
import edu.stedwards.matt.ui.interaction.Buttons;
import edu.stedwards.matt.ui.interaction.Inputs;
import edu.stedwards.matt.ui.interaction.VisualElements;
import edu.stedwards.matt.ui.labels.OnboardingScreenText;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
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

   // 1. Promoted to instance variables to capture user input
   private TextField firstNameField;
   private TextField lastNameField;
   private TextField stateField;
   private TextField zipField;
   private TextField addy1Field;
   private TextField addy2Field;
   private TextField cityField;
   private TextField phoneField;
   private TextField emailField;

   private final Label errorLabel = new Label();
   private final Runnable onNext;

   // Default constructor for Matt.java preview compatibility
   public OnboardingEnterInfo() {
      this(() -> System.out.println("Next clicked in preview mode."));
   }

   // The MVC constructor that takes the navigation logic from the outside
   public OnboardingEnterInfo(Runnable onNext) {
      this.onNext = onNext;
      getStyleClass().addAll("wizard-background", "screen-padding");

      TextFlow title = TextStyles.buildTitle(OnboardingScreenText.PI_TITLE, "onboarding-wizard-title");
      setTop(title);

      var scrollContent = VisualElements.wizardScrollable(buildPersonalInfoContent());
      setCenter(scrollContent);
      BorderPane.setMargin(scrollContent, new Insets(0, 0, 24, 0));

      HBox bottomRow = new Buttons().buildNextButton();
      if (!bottomRow.getChildren().isEmpty() && bottomRow.getChildren().get(0) instanceof Button nextBtn) {
         nextBtn.setOnAction(e -> handleNext());
      }
      setBottom(bottomRow);
   }

   private TextField buildPersonalField(String placeholder) {
      return Inputs.inputField(placeholder, PERSONAL_FIELD_WIDTH, PERSONAL_FIELD_WIDTH);
   }

   // Modified to accept a TextField object directly so we can store it in our instance variables
   private VBox buildPersonalFieldGroup(String labelText, TextField field) {
      VBox group = Inputs.buildInputGroup(
            TextStyles.buildLabel(labelText, "input-label"), field);
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

      errorLabel.setStyle("-fx-text-fill: #c0392b; -fx-font-weight: bold;");
      errorLabel.setWrapText(true);
      errorLabel.setMaxWidth(Double.MAX_VALUE);

      firstNameField = buildPersonalField(OnboardingScreenText.PI_EXAMPLE_FIRST);
      lastNameField = buildPersonalField(OnboardingScreenText.PI_EXAMPLE_LAST);
      stateField = buildPersonalField(OnboardingScreenText.PI_EXAMPLE_STATE);
      zipField = buildPersonalField(OnboardingScreenText.LABEL_ZIP_PLACEHOLDER);
      addy1Field = buildPersonalField(OnboardingScreenText.LABEL_ADDY1_PLACEHOLDER);
      addy2Field = buildPersonalField(OnboardingScreenText.LABEL_ADDY2_PLACEHOLDER);
      cityField = buildPersonalField(OnboardingScreenText.LABEL_CITY_PLACEHOLDER);
      phoneField = buildPersonalField(OnboardingScreenText.LABEL_PHONE_PLACEHOLDER);
      emailField = buildPersonalField(OnboardingScreenText.LABEL_EMAIL_PLACEHOLDER);

      VBox nameColumn = buildPersonalColumn(
            buildPersonalFieldGroup(OnboardingScreenText.PI_LABEL_FIRST, firstNameField),
            buildPersonalFieldGroup(OnboardingScreenText.PI_LABEL_LAST, lastNameField));

      VBox stateGroup = buildPersonalFieldGroup(OnboardingScreenText.PI_LABEL_STATE, stateField);
      VBox zipGroup = buildPersonalFieldGroup(OnboardingScreenText.PI_LABEL_ZIP, zipField);
      HBox stateAndZip = new HBox(12, stateGroup, zipGroup);
      stateAndZip.setPrefWidth(PERSONAL_FIELD_WIDTH);
      stateAndZip.setMaxWidth(PERSONAL_FIELD_WIDTH);
      stateGroup.setMaxWidth(Double.MAX_VALUE);
      zipGroup.setMaxWidth(Double.MAX_VALUE);
      HBox.setHgrow(stateGroup, Priority.ALWAYS);
      HBox.setHgrow(zipGroup, Priority.ALWAYS);

      VBox locationColumn = buildPersonalColumn(
            buildPersonalFieldGroup(OnboardingScreenText.PI_LABEL_ADDY1, addy1Field),
            buildPersonalFieldGroup(OnboardingScreenText.PI_LABEL_ADDY2, addy2Field),
            buildPersonalFieldGroup(OnboardingScreenText.PI_LABEL_CITY, cityField), stateAndZip);

      VBox contactColumn = buildPersonalColumn(
            buildPersonalFieldGroup(OnboardingScreenText.PI_LABEL_PHONE, phoneField),
            buildPersonalFieldGroup(OnboardingScreenText.PI_LABEL_EMAIL, emailField));

      HBox columns = new HBox(36, nameColumn, locationColumn, contactColumn);
      columns.setAlignment(Pos.TOP_CENTER);
      columns.setFillHeight(true);
      columns.setMaxWidth(Double.MAX_VALUE);
      for (VBox column : new VBox[] { nameColumn, locationColumn, contactColumn }) {
         HBox.setHgrow(column, Priority.ALWAYS);
      }

      VBox content = new VBox(24, instruction, columns, errorLabel);
      content.setFillWidth(true);
      content.setMaxWidth(Double.MAX_VALUE);
      content.setPadding(new Insets(24, 0, 24, 0));
      return content;
   }

   /** Validate email, append all personal-info fields to OnboardingSession, and navigate to Education. */
   public void handleNext() {
      String email = emailField.getText() == null ? "" : emailField.getText().trim();
      if (!ValidationService.isValidEmail(email)) {
         errorLabel.setText("Please enter a valid email address (e.g., name@domain.com).");
         return;
      }
      errorLabel.setText("");

      User user = OnboardingSession.getInstance().getUserProfile();
      if (user == null) {
         user = new User();
         OnboardingSession.getInstance().setUserProfile(user);
      }

      user.setFirstName(trimToEmpty(firstNameField.getText()));
      user.setLastName(trimToEmpty(lastNameField.getText()));
      user.setPhoneNumber(trimToEmpty(phoneField.getText()));
      user.setEmail(email);
      user.setAddressLine1(trimToEmpty(addy1Field.getText()));
      user.setAddressLine2(trimToEmpty(addy2Field.getText()));
      user.setCity(trimToEmpty(cityField.getText()));
      user.setState(trimToEmpty(stateField.getText()));
      user.setZip(trimToEmpty(zipField.getText()));

      if (onNext != null) {
         onNext.run();
      }
   }

   /** Populate personal-info fields with values from the active session / résumé parser. */
   public void populateFromParsedData() {
      // Grab the user profile from the session (which the parser should have filled)
      User user = OnboardingSession.getInstance().getUserProfile();
      if (user == null) {
         return;
      }

      if (user.getFirstName() != null) firstNameField.setText(user.getFirstName());
      if (user.getLastName() != null) lastNameField.setText(user.getLastName());
      if (user.getPhoneNumber() != null) phoneField.setText(user.getPhoneNumber());
      if (user.getEmail() != null) emailField.setText(user.getEmail());
      if (user.getAddressLine1() != null) addy1Field.setText(user.getAddressLine1());
      if (user.getAddressLine2() != null) addy2Field.setText(user.getAddressLine2());
      if (user.getCity() != null) cityField.setText(user.getCity());
      if (user.getState() != null) stateField.setText(user.getState());
      if (user.getZip() != null) zipField.setText(user.getZip());
   }

   private static String trimToEmpty(String value) {
      return value == null ? "" : value.trim();
   }
}