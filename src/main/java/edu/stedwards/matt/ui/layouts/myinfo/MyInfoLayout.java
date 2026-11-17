package edu.stedwards.matt.ui.layouts.myinfo;

import java.util.ArrayList;
import java.util.List;

import edu.stedwards.matt.models.entity.AwardsCerts;
import edu.stedwards.matt.models.entity.Education;
import edu.stedwards.matt.models.entity.Experience;
import edu.stedwards.matt.models.entity.Projects;
import edu.stedwards.matt.models.entity.Skill;
import edu.stedwards.matt.models.entity.User;
import edu.stedwards.matt.models.entity.Websites;
import edu.stedwards.matt.controllers.ProfileController;
import edu.stedwards.matt.ui.TextStyles;
import edu.stedwards.matt.ui.interaction.VisualElements;
import edu.stedwards.matt.ui.labels.MyInfoScreenText;
import edu.stedwards.matt.ui.layouts.onboarding.OnboardingOverviewStep;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

/** Read-only profile and résumé information screen. */
public class MyInfoLayout extends BorderPane {

   public MyInfoLayout() {
      getStyleClass().addAll("screen-padding", "myinfo-screen");
      TextFlow title = TextStyles.buildTitle(MyInfoScreenText.TITLE, "myinfo-title");
      setTop(title);
      BorderPane.setMargin(title, new Insets(0, 0, 18, 0));

      VBox content = new VBox(18);
      content.getStyleClass().add("myinfo-content");
      content.setFillWidth(true);

      User profile = loadProfile();
      content.getChildren().add(buildPersonalInformation(profile));
      List<Node> cards = loadViewOnlyCards();
      if (cards == null || cards.isEmpty()) {
         Label emptyMessage = new Label(MyInfoScreenText.NO_VIEW_ONLY_CARDS_MESSAGE);
         emptyMessage.getStyleClass().add("myinfo-empty-message");
         content.getChildren().add(emptyMessage);
      } else {
         content.getChildren().addAll(cards);
      }

      setCenter(VisualElements.scrollable(content));
   }

   private VBox buildPersonalInformation(User profile) {
      String fullName = profile == null ? null
            : String.join(" ", nonBlank(profile.getFirstName()), nonBlank(profile.getLastName())).trim();
      String addressLine = profile == null ? null
            : joinNonBlank(", ",
                  profile.getAddressLine1(), profile.getAddressLine2());
      String cityLine = profile == null ? null : joinNonBlank(", ", profile.getCity(), profile.getState());
      if (profile != null && hasText(profile.getZip())) {
         cityLine = joinNonBlank("  ", cityLine, profile.getZip());
      }

      Label name = profileValue(fullName);
      Label phone = profileValue(profile == null ? null : profile.getPhoneNumber());
      Label address = profileValue(addressLine);
      Label email = profileValue(profile == null ? null : profile.getEmail());
      Label city = profileValue(cityLine);

      GridPane details = new GridPane();
      details.setHgap(24);
      details.setVgap(8);
      ColumnConstraintsHelper.setColumns(details);
      details.add(name, 0, 0);
      details.add(phone, 1, 0);
      details.add(address, 0, 1);
      details.add(email, 1, 1);
      details.add(city, 0, 2);
      GridPane.setHalignment(phone, javafx.geometry.HPos.RIGHT);
      GridPane.setHalignment(email, javafx.geometry.HPos.RIGHT);

      VBox personalInfo = new VBox(4, details);
      personalInfo.getStyleClass().add("myinfo-personal-info");
      return personalInfo;
   }

   private Label profileValue(String value) {
      Label label = new Label(hasText(value) ? value : MyInfoScreenText.UNAVAILABLE_LABEL);
      label.getStyleClass().add("myinfo-profile-value");
      label.setWrapText(true);
      return label;
   }

   private static String nonBlank(String value) {
      return value == null ? "" : value.trim();
   }

   private static boolean hasText(String value) {
      return value != null && !value.isBlank();
   }

   private static String joinNonBlank(String separator, String... values) {
      List<String> present = new ArrayList<>();
      for (String value : values) {
         if (hasText(value))
            present.add(value.trim());
      }
      return String.join(separator, present);
   }

   /**
    * TODO: javi implement. Called by the MyInfoLayout constructor when the My Info
    * screen opens.
    * Takes no arguments, returns the signed-in user's profile, and should query
    * the database for
    * their name, address, phone, and email. Return null when no profile is
    * available.
    */
   protected User loadProfile() {
      return null;
   }

   /**
    * TODO: javi implement. Called by the MyInfoLayout constructor after the
    * profile is loaded.
    * Takes no arguments and returns the read-only résumé cards for the signed-in
    * user, querying
    * education, experience, projects, skills, awards, and websites from the
    * database. Reuse the
    * onboarding overview card builders and return an empty list when the user has
    * no cards.
    */
   protected List<Node> loadViewOnlyCards() {
      return List.of();
      
   }

   // /** Loads the user's personal info from OnboardingSession / SQLite into the form fields. */
   //  public void loadUserDataIntoFields() {
   //    currentUser = profileController.getCurrentUserProfile();
   //    if (currentUser == null) {
   //       currentUser = new User();
   //    }
   //    firstNameField.setText(Inputs.nullToEmpty(currentUser.getFirstName()));
   //    lastNameField.setText(Inputs.nullToEmpty(currentUser.getLastName()));
   //    emailField.setText(Inputs.nullToEmpty(currentUser.getEmail()));
   //    phoneField.setText(Inputs.nullToEmpty(currentUser.getPhoneNumber()));
   //    address1Field.setText(Inputs.nullToEmpty(currentUser.getAddressLine1()));
   //    address2Field.setText(Inputs.nullToEmpty(currentUser.getAddressLine2()));
   //    cityField.setText(Inputs.nullToEmpty(currentUser.getCity()));
   //    stateField.setText(Inputs.nullToEmpty(currentUser.getState()));
   //    zipField.setText(Inputs.nullToEmpty(currentUser.getZip()));
   //    apiKeyField.setText(Inputs.nullToEmpty(currentUser.getOllamaApiKey()));
   //    summaryArea.setText(Inputs.nullToEmpty(currentUser.getSummary()));
   //  }

   // /** Saves all edited fields back to SQLite and updates OnboardingSession. */
   // public void handleSaveProfile() {
   //    if (currentUser == null) {
   //       currentUser = new User();
   //    }
   //    currentUser.setFirstName(firstNameField.getText().trim());
   //    currentUser.setLastName(lastNameField.getText().trim());
   //    currentUser.setEmail(emailField.getText().trim());
   //    currentUser.setPhoneNumber(phoneField.getText().trim());
   //    currentUser.setAddressLine1(address1Field.getText().trim());
   //    currentUser.setAddressLine2(address2Field.getText().trim());
   //    currentUser.setCity(cityField.getText().trim());
   //    currentUser.setState(stateField.getText().trim());
   //    currentUser.setZip(zipField.getText().trim());
   //    currentUser.setOllamaApiKey(apiKeyField.getText().trim());
   //    currentUser.setSummary(summaryArea.getText().trim());
   //    currentUser.setOnboarded(true);

   //    boolean saved = profileController.saveUserProfile(currentUser);
   //    if (saved) {
   //       statusFeedbackLabel.setTextFill(Color.web("#2ecc71"));
   //       statusFeedbackLabel.setText("Profile saved successfully!");
   //    } else {
   //       statusFeedbackLabel.setTextFill(Color.web("#e74c3c"));
   //       statusFeedbackLabel.setText("Failed to save profile.");
   //    }
   // }

   /**
    * Shared card-construction helper for the backend implementation of
    * loadViewOnlyCards().
    */
   protected List<Node> buildViewOnlyCards(List<Education> education, List<Experience> experiences,
         List<Projects> projects, List<Skill> skills, List<AwardsCerts> awards, List<Websites> websites) {
      List<Node> cards = new ArrayList<>();
      if (education != null) {
         for (int i = 0; i < education.size(); i++) {
            cards.add(OnboardingOverviewStep.buildEducationCardViewOnly(education.get(i), i + 1, null));
         }
      }
      if (experiences != null) {
         for (int i = 0; i < experiences.size(); i++) {
            cards.add(OnboardingOverviewStep.buildExperienceCardViewOnly(experiences.get(i), i + 1, null));
         }
      }
      if (projects != null) {
         for (int i = 0; i < projects.size(); i++) {
            cards.add(OnboardingOverviewStep.buildProjectsCardViewOnly(projects.get(i), i + 1, null));
         }
      }
      if (skills != null && !skills.isEmpty()) {
         cards.add(OnboardingOverviewStep.buildSkillsCardViewOnly(
               javafx.collections.FXCollections.observableArrayList(skills), null));
      }
      if (awards != null) {
         for (int i = 0; i < awards.size(); i++) {
            cards.add(OnboardingOverviewStep.buildAwardsCertsCardViewOnly(awards.get(i), i + 1, null));
         }
      }
      if (websites != null && !websites.isEmpty()) {
         cards.add(OnboardingOverviewStep.buildWebsitesCardViewOnly(
               javafx.collections.FXCollections.observableArrayList(websites), null));
      }
      return cards;
   }

   private static final class ColumnConstraintsHelper {
      private static void setColumns(GridPane grid) {
         javafx.scene.layout.ColumnConstraints left = new javafx.scene.layout.ColumnConstraints();
         left.setHgrow(Priority.ALWAYS);
         left.setPercentWidth(50);
         javafx.scene.layout.ColumnConstraints right = new javafx.scene.layout.ColumnConstraints();
         right.setHgrow(Priority.ALWAYS);
         right.setPercentWidth(50);
         right.setHalignment(javafx.geometry.HPos.RIGHT);
         grid.getColumnConstraints().addAll(left, right);
      }
   }
}
