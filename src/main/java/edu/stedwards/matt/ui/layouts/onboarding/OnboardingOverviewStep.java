package edu.stedwards.matt.ui.layouts.onboarding;

import java.util.List;

import edu.stedwards.matt.models.entity.AwardsCerts;
import edu.stedwards.matt.models.entity.Education;
import edu.stedwards.matt.models.entity.Experience;
import edu.stedwards.matt.models.entity.Projects;
import edu.stedwards.matt.models.entity.Skill;
import edu.stedwards.matt.models.entity.Websites;
import edu.stedwards.matt.ui.TextStyles;
import edu.stedwards.matt.ui.interaction.Buttons;
import edu.stedwards.matt.ui.interaction.Inputs;
import edu.stedwards.matt.ui.interaction.VisualElements;
import edu.stedwards.matt.ui.labels.OnboardingScreenText;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

/** Read-only overview of the data collected in the editable onboarding steps. */
public class OnboardingOverviewStep extends BorderPane {
   public OnboardingOverviewStep() {
      this(null, null, null, null, null, null, null, null, null, null, null, null);
   }

   public OnboardingOverviewStep(
         ObservableList<Education> education,
         ObservableList<Experience> experiences,
         ObservableList<Projects> projects,
         ObservableList<Skill> skills,
         ObservableList<AwardsCerts> awards,
         ObservableList<Websites> websites,
         Runnable editEducation,
         Runnable editExperiences,
         Runnable editProjects,
         Runnable editSkills,
         Runnable editAwards,
         Runnable editWebsites) {
      getStyleClass().addAll("wizard-background", "screen-padding");
      TextFlow title = TextStyles.buildTitle(OnboardingScreenText.FIN_TITLE, "onboarding-wizard-title");
      setTop(title);

      Node scrollContent = VisualElements.wizardScrollable(buildAllInfoContainers(
            education, experiences, projects, skills, awards, websites,
            editEducation, editExperiences, editProjects, editSkills, editAwards, editWebsites));
      setCenter(scrollContent);
      BorderPane.setMargin(scrollContent, new Insets(0, 0, 24, 0));
      setBottom(new Buttons().buildNextButton());
   }

   private VBox buildAllInfoContainers(
         ObservableList<Education> education,
         ObservableList<Experience> experiences,
         ObservableList<Projects> projects,
         ObservableList<Skill> skills,
         ObservableList<AwardsCerts> awards,
         ObservableList<Websites> websites,
         Runnable editEducation,
         Runnable editExperiences,
         Runnable editProjects,
         Runnable editSkills,
         Runnable editAwards,
         Runnable editWebsites) {
      VBox allInfoContainers = new VBox(20);
      if (education != null) {
         allInfoContainers.getChildren().addAll(buildViewOnlySections(education,
               (item, number) -> buildEducationCardViewOnly(item, number, editEducation)).getChildren());
      }
      if (experiences != null) {
         allInfoContainers.getChildren().addAll(buildViewOnlySections(experiences,
               (item, number) -> buildExperienceCardViewOnly(item, number, editExperiences)).getChildren());
      }
      if (projects != null) {
         allInfoContainers.getChildren().addAll(buildViewOnlySections(projects,
               (item, number) -> buildProjectsCardViewOnly(item, number, editProjects)).getChildren());
      }
      if (skills != null && !skills.isEmpty()) {
         allInfoContainers.getChildren().add(buildSkillsCardViewOnly(skills, editSkills));
      }
      if (awards != null) {
         allInfoContainers.getChildren().addAll(buildViewOnlySections(awards,
               (item, number) -> buildAwardsCertsCardViewOnly(item, number, editAwards)).getChildren());
      }
      if (websites != null && !websites.isEmpty()) {
         allInfoContainers.getChildren().add(buildWebsitesCardViewOnly(websites, editWebsites));
      }
      return allInfoContainers;
   }

   private <T> VBox buildViewOnlySections(ObservableList<T> items, ViewOnlySectionFactory<T> cardFactory) {
      VBox cards = new VBox(16);
      cards.setFillWidth(true);
      for (int i = 0; i < items.size(); i++) {
         cards.getChildren().add(cardFactory.build(items.get(i), i + 1));
      }
      return cards;
   }

   @FunctionalInterface
   private interface ViewOnlySectionFactory<T> {
      VBox build(T item, int sectionNumber);
   }

   private static VBox buildViewOnlySectionCard(String heading, Node fields, Runnable onEdit) {
      Button edit = Buttons.button(OnboardingScreenText.CONTENT_CARD_EDIT_ICON, event -> {
         if (onEdit != null) {
            onEdit.run();
         }
      }, "experience-icon-button");
      edit.setAccessibleText("Edit " + heading);
      return VisualElements.buildParsedInfoCard(heading, List.of(fields), edit);
   }

   public static VBox buildExperienceCardViewOnly(Experience experience, int sectionNumber, Runnable onEdit) {
      Region spacer = new Region();
      HBox dateRange = new HBox(8,
            value(experience.getStartDate()), new Label("–"), value(experience.getEndDate()));
      HBox firstRow = new HBox(16,
            value(experience.getJobTitle()), value(experience.getCompanyName()), spacer, dateRange);
      firstRow.setAlignment(Pos.CENTER_LEFT);
      HBox.setHgrow(spacer, Priority.ALWAYS);
      VBox content = new VBox(16, firstRow, value(experience.getDescription()));
      return buildViewOnlySectionCard(OnboardingScreenText.EXP_CARD_PREFIX + sectionNumber, content, onEdit);
   }

   public static VBox buildProjectsCardViewOnly(Projects project, int sectionNumber, Runnable onEdit) {
      Region firstSpacer = new Region();
      HBox dates = new HBox(8,
            value(project.getStartDate()), new Label("–"), value(project.getEndDate()));
      HBox firstRow = new HBox(16,
            value(project.getProjectName()), value(project.getRole()), firstSpacer, dates);
      firstRow.setAlignment(Pos.CENTER_LEFT);
      HBox.setHgrow(firstSpacer, Priority.ALWAYS);

      Region secondSpacer = new Region();
      HBox secondRow = new HBox(16, secondSpacer, value(project.getLocation()));
      secondRow.setAlignment(Pos.CENTER_LEFT);
      HBox.setHgrow(secondSpacer, Priority.ALWAYS);
      VBox content = new VBox(16, firstRow, secondRow, value(project.getDescription()));
      return buildViewOnlySectionCard(OnboardingScreenText.PROJ_CARD_PREFIX + sectionNumber, content, onEdit);
   }

   public static VBox buildEducationCardViewOnly(Education education, int sectionNumber, Runnable onEdit) {
      Region firstSpacer = new Region();
      HBox firstRow = new HBox(16,
            value(education.getDegree()), value(education.getInstitution()), firstSpacer,
            value(formatMonthYear(education.getEndDate())));
      firstRow.setAlignment(Pos.CENTER_LEFT);
      HBox.setHgrow(firstSpacer, Priority.ALWAYS);
      Region secondSpacer = new Region();
      HBox secondRow = new HBox(16, secondSpacer, value(education.getGpa()));
      secondRow.setAlignment(Pos.CENTER_LEFT);
      HBox.setHgrow(secondSpacer, Priority.ALWAYS);
      VBox content = new VBox(16, firstRow, secondRow);
      return buildViewOnlySectionCard(OnboardingScreenText.EDU_CARD_PREFIX + sectionNumber, content, onEdit);
   }

   public static VBox buildSkillsCardViewOnly(ObservableList<Skill> skills, Runnable onEdit) {
      FlowPane chips = new FlowPane();
      chips.setHgap(16);
      chips.setVgap(12);
      for (Skill skill : skills) {
         Label chip = value(skill.getSkillName());
         chip.getStyleClass().add("onboarding-chiplet-button");
         chips.getChildren().add(chip);
      }
      return buildViewOnlySectionCard(OnboardingScreenText.SKL_TITLE, chips, onEdit);
   }

   public static VBox buildAwardsCertsCardViewOnly(AwardsCerts award, int sectionNumber, Runnable onEdit) {
      Region spacer = new Region();
      HBox row = new HBox(16,
            value(award.getTitle()), value(award.getIssuer()), spacer,
            value(formatMonthYear(award.getDateReceived())));
      row.setAlignment(Pos.CENTER_LEFT);
      HBox.setHgrow(spacer, Priority.ALWAYS);
      return buildViewOnlySectionCard(OnboardingScreenText.AWDCT_CARD_PREFIX + sectionNumber, row, onEdit);
   }

   public static VBox buildWebsitesCardViewOnly(ObservableList<Websites> websites, Runnable onEdit) {
      FlowPane chips = new FlowPane();
      chips.setHgap(16);
      chips.setVgap(12);
      for (Websites website : websites) {
         String platform = Inputs.nullToEmpty(website.getUrlType());
         String url = Inputs.nullToEmpty(website.getUrl());
         Label chip = value(platform.isBlank() ? url : url.isBlank() ? platform : platform + " · " + url);
         chip.getStyleClass().add("onboarding-chiplet-button");
         chips.getChildren().add(chip);
      }
      return buildViewOnlySectionCard(OnboardingScreenText.WEB_TITLE, chips, onEdit);
   }

   private static Label value(String text) {
      Label value = new Label(Inputs.nullToEmpty(text));
      value.getStyleClass().add("onboarding-overview-value");
      return value;
   }

   private static String formatMonthYear(String date) {
      if (date == null || date.isBlank()) {
         return "";
      }
      String[] parts = date.trim().split("[/\\-.]", -1);
      if (parts.length == 3) {
         return parts[0].length() == 4 ? parts[1] + "/" + parts[0] : parts[0] + "/" + parts[2];
      }
      if (parts.length == 2) {
         return parts[0].length() == 4 ? parts[1] + "/" + parts[0] : parts[0] + "/" + parts[1];
      }
      return date;
   }
}
