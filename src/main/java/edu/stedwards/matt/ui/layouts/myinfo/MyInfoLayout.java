package edu.stedwards.matt.ui.layouts.myinfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import edu.stedwards.matt.controllers.OnboardingStepController;
import edu.stedwards.matt.controllers.ProfileController;
import edu.stedwards.matt.dao.MyInfoDAO;
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
import edu.stedwards.matt.ui.layouts.onboarding.OnboardingStepLayout;
import edu.stedwards.matt.ui.layouts.other.MainLayout;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

   private final ProfileController profileController = new ProfileController();
   private final MyInfoDAO myInfoDAO = new MyInfoDAO();

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

   /** Loads the signed-in user's profile from SQLite (or returns null if unavailable). */
   protected User loadProfile() {
      User profile = profileController.getCurrentUserProfile();
      return (profile != null && profile.getUserID() > 0) ? profile : null;
   }

   /**
    * Queries education, experience, projects, skills, awards, and websites for the signed-in user
    * and returns read-only cards with Edit actions wired to the corresponding onboarding step.
    */
   protected List<Node> loadViewOnlyCards() {
      User profile = loadProfile();
      if (profile == null || profile.getUserID() <= 0) {
         return List.of();
      }
      int userId = profile.getUserID();

      List<Education> education = myInfoDAO.getEducationForUser(userId);
      List<Experience> experiences = myInfoDAO.getExperienceForUser(userId);
      List<Projects> projects = myInfoDAO.getProjectsForUser(userId);
      List<Skill> skills = myInfoDAO.getSkillsForUser(userId);
      List<AwardsCerts> awards = myInfoDAO.getAwardsForUser(userId);
      List<Websites> websites = myInfoDAO.getWebsitesForUser(userId);

      return buildViewOnlyCards(userId, education, experiences, projects, skills, awards, websites);
   }

   /** Shared card-construction helper (backwards-compatible overload without edit actions). */
   protected List<Node> buildViewOnlyCards(List<Education> education, List<Experience> experiences,
         List<Projects> projects, List<Skill> skills, List<AwardsCerts> awards, List<Websites> websites) {
      User profile = loadProfile();
      int userId = (profile != null) ? profile.getUserID() : -1;
      return buildViewOnlyCards(userId, education, experiences, projects, skills, awards, websites);
   }

   private List<Node> buildViewOnlyCards(int userId, List<Education> education, List<Experience> experiences,
         List<Projects> projects, List<Skill> skills, List<AwardsCerts> awards, List<Websites> websites) {
      List<Node> cards = new ArrayList<>();

      Runnable editEducation = () -> {
         ObservableList<Education> list = FXCollections.observableArrayList(education);
         openEditorStep(
               step -> step.showEducationStep(list),
               () -> myInfoDAO.replaceEducationForUser(userId, list));
      };
      Runnable editExperiences = () -> {
         ObservableList<Experience> list = FXCollections.observableArrayList(experiences);
         openEditorStep(
               step -> step.showExperienceStep(list),
               () -> myInfoDAO.replaceExperienceForUser(userId, list));
      };
      Runnable editProjects = () -> {
         ObservableList<Projects> list = FXCollections.observableArrayList(projects);
         openEditorStep(
               step -> step.showProjectsStep(list),
               () -> myInfoDAO.replaceProjectsForUser(userId, list));
      };
      Runnable editSkills = () -> {
         ObservableList<Skill> list = FXCollections.observableArrayList(skills);
         openEditorStep(
               step -> step.showSkillsStep(list),
               () -> myInfoDAO.replaceSkillsForUser(userId, list));
      };
      Runnable editAwards = () -> {
         ObservableList<AwardsCerts> list = FXCollections.observableArrayList(awards);
         openEditorStep(
               step -> step.showAwardsStep(list),
               () -> myInfoDAO.replaceAwardsForUser(userId, list));
      };
      Runnable editWebsites = () -> {
         ObservableList<Websites> list = FXCollections.observableArrayList(websites);
         openEditorStep(
               step -> step.showWebsitesStep(list),
               () -> myInfoDAO.replaceWebsitesForUser(userId, list));
      };

      if (education != null) {
         for (int i = 0; i < education.size(); i++) {
            cards.add(OnboardingOverviewStep.buildEducationCardViewOnly(education.get(i), i + 1, editEducation));
         }
      }
      if (experiences != null) {
         for (int i = 0; i < experiences.size(); i++) {
            cards.add(OnboardingOverviewStep.buildExperienceCardViewOnly(experiences.get(i), i + 1, editExperiences));
         }
      }
      if (projects != null) {
         for (int i = 0; i < projects.size(); i++) {
            cards.add(OnboardingOverviewStep.buildProjectsCardViewOnly(projects.get(i), i + 1, editProjects));
         }
      }
      if (skills != null && !skills.isEmpty()) {
         cards.add(OnboardingOverviewStep.buildSkillsCardViewOnly(
               FXCollections.observableArrayList(skills), editSkills));
      }
      if (awards != null) {
         for (int i = 0; i < awards.size(); i++) {
            cards.add(OnboardingOverviewStep.buildAwardsCertsCardViewOnly(awards.get(i), i + 1, editAwards));
         }
      }
      if (websites != null && !websites.isEmpty()) {
         cards.add(OnboardingOverviewStep.buildWebsitesCardViewOnly(
               FXCollections.observableArrayList(websites), editWebsites));
      }
      return cards;
   }

   /** Opens a single onboarding step inside MainLayout and saves + returns to MyInfoLayout on Next. */
   private void openEditorStep(Consumer<OnboardingStepLayout> showSection, Runnable saveAction) {
      if (getScene() == null || !(getScene().getRoot() instanceof MainLayout mainLayout)) {
         return;
      }

      OnboardingStepLayout editor = new OnboardingStepLayout(new OnboardingStepController(), () -> {
         saveAction.run();
         mainLayout.showStep(new MyInfoLayout());
      });
      showSection.accept(editor);
      mainLayout.showStep(editor);
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
