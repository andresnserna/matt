/**
 * Architecture layer: view
 * JavaFX layout for the onboarding screen.
 */
package edu.stedwards.matt.ui.layouts.onboarding;

import javafx.scene.text.TextFlow;
import edu.stedwards.matt.ui.TextStyles;
import edu.stedwards.matt.ui.interaction.Buttons;
import edu.stedwards.matt.ui.interaction.Inputs;
import edu.stedwards.matt.ui.interaction.VisualElements;
import edu.stedwards.matt.ui.labels.OnboardingScreenText;
import edu.stedwards.matt.models.entity.AwardsCerts;
import edu.stedwards.matt.models.entity.Education;
import edu.stedwards.matt.models.entity.Experience;
import edu.stedwards.matt.models.entity.Projects;
import edu.stedwards.matt.models.entity.Skill;
import edu.stedwards.matt.models.entity.Websites;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.function.Consumer;
import java.util.function.BiConsumer;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import edu.stedwards.matt.controllers.OnboardingStepController;

/**
 * Builds the onboarding layout for first-time or introductory user flow.
 * This layout is created by the startup flow when the app needs to guide the user through
 * setup, and the resulting node is attached to a BaseWindow for display.
 */

// onboarding - education=
// onboarding - experiance
// onboarding - projects
// onboarding - skills
// onboarding - awards/certs
// onboarding - websites

public class OnboardingStepLayout extends BorderPane {
    private final OnboardingStepController controller;
    // Holds the active wizard page and observes whichever section list is currently displayed.
    private Node currentStep;
    private ObservableList<?> shownItems;
    private ListChangeListener<?> shownItemsListener;

    /**
     * Wraps the selected wizard step in the shared scrollable content area.
     */
    public void showStep(Node step) {
        currentStep = VisualElements.wizardScrollable(step);
        VBox.setVgrow(currentStep, Priority.ALWAYS);
        setCenter(currentStep);
        BorderPane.setAlignment(currentStep, Pos.CENTER_LEFT);
        BorderPane.setMargin(currentStep, new Insets(0, 0, 24, 0));
    } 

    /**
     * Sets up the shared onboarding background, heading, and navigation footer.
     */
    public OnboardingStepLayout(OnboardingStepController controller){
        this.controller = controller;
        getStyleClass().addAll("wizard-background", "screen-padding");
        
        TextFlow title = TextStyles.buildTitle(OnboardingScreenText.PLACEHOLDER_TITLE, "onboarding-wizard-title");
        setTop(title);
        
        // setCenter(showStep());
        
        setBottom(new Buttons().buildNextButton());
    }

    /**
     * Builds a shared editable card with the existing copy and delete controls.
     */
    private static VBox buildEditableSectionCard(String heading, Node fields, Runnable onDuplicate, Runnable onDelete) {
        Button duplicate = Buttons.button(OnboardingScreenText.CONTENT_CARD_COPY_ICON,
                        event -> {
                                if (onDuplicate != null)
                                        onDuplicate.run();
                        },
                        "experience-icon-button");

        duplicate.setAccessibleText("Duplicate section");

        Button delete = Buttons.button(OnboardingScreenText.CONTENT_CARD_DELETE_ICON,
                        event -> {
                                if (onDelete != null)
                                        onDelete.run();
                        },
                        "experience-icon-button", "experience-delete-button");
        delete.setAccessibleText("Delete section");

        return VisualElements.buildParsedInfoCard(heading, List.of(fields), List.of(duplicate, delete));
    }

    /**
     * Displays and observes a collection of editable onboarding cards, refreshing
     * it when an
     * item is duplicated or removed.
     */
    private <T> void showEditableSections(ObservableList<T> items, String title, Supplier<T> emptyItemFactory,
            BiConsumer<ObservableList<T>, Integer> duplicateItem,
            BiConsumer<ObservableList<T>, T> deleteItem,
            EditableSectionFactory<T> cardFactory) {
        detachShownItemsListener();
        controller.ensureInitialItem(items, emptyItemFactory);

        VBox cards = new VBox(16);
        cards.setFillWidth(true);
        Runnable refresh = () -> {
                cards.getChildren().clear();
                for (int i = 0; i < items.size(); i++) {
                        int index = i;
                        T item = items.get(i);
                        cards.getChildren().add(cardFactory.build(item, i + 1,
                                        () -> duplicateItem.accept(items, index),
                                        () -> deleteItem.accept(items, item)));
                }
        };

        ListChangeListener<T> listener = change -> refresh.run();
        items.addListener(listener);
        shownItems = items;
        shownItemsListener = listener;
        refresh.run();
        setTop(TextStyles.buildTitle(title, "onboarding-wizard-title"));
        showStep(cards);
    }

    /**
     * Detaches the current list listener before a different onboarding step is
     * displayed.
     */
    private void detachShownItemsListener() {
        if (shownItems != null && shownItemsListener != null) {
                removeListListener(shownItems, shownItemsListener);
                shownItems = null;
                shownItemsListener = null;
        }
    }

    /**
     * Removes a listener from an observable list whose generic item type is no
     * longer statically known.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static void removeListListener(ObservableList<?> items, ListChangeListener<?> listener) {
            ((ObservableList) items).removeListener((ListChangeListener) listener);
    }

    /**
     * Builds a section card and receives callbacks for copying or deleting its
     * model.
     */
    @FunctionalInterface
    private interface EditableSectionFactory<T> {
            VBox build(T item, int sectionNumber, Runnable onDuplicate, Runnable onDelete);
    }

    /** Builds a labeled month/year input and stores its value as MM/YYYY. */
    private static VBox monthYearGroup(String labelText, String value, Consumer<String> onDateChanged) {
        String[] parts = monthYearParts(value);
        TextField month = Inputs.inputField(OnboardingScreenText.DATE_MONTH_PLACEHOLDER, 54, 70);
        TextField year = Inputs.inputField(OnboardingScreenText.DATE_YEAR_PLACEHOLDER, 82, 100);
        month.setText(parts[0]);
        year.setText(parts[1]);
        Runnable update = () -> {
            String combined = month.getText().trim() + "/" + year.getText().trim();
            onDateChanged.accept(combined.equals("/") ? "" : combined);
        };
        month.textProperty().addListener((observable, oldValue, newValue) -> update.run());
        year.textProperty().addListener((observable, oldValue, newValue) -> update.run());

        HBox dateFields = new HBox(8, month, new Label("/"), year);
        dateFields.setAlignment(Pos.CENTER_LEFT);
        return Inputs.buildInputGroup(TextStyles.buildLabel(labelText, "input-label"), dateFields);
    }

    private static String[] monthYearParts(String value) {
        if (value == null || value.isBlank()) {
            return new String[] {"", ""};
        }
        String[] parts = value.trim().split("[/\\-.]", -1);
        if (parts.length == 3) {
            return parts[0].length() == 4
                    ? new String[] {parts[1], parts[0]}
                    : new String[] {parts[0], parts[2]};
        }
        if (parts.length == 2) {
            return parts[0].length() == 4
                    ? new String[] {parts[1], parts[0]}
                    : new String[] {parts[0], parts[1]};
        }
        return new String[] {"", ""};
    }

    /**
     * Builds an editable education card for the education onboarding step.
     */
    public VBox buildEducationSectionEditable(Education education, int sectionNumber, Runnable onDuplicate, Runnable onDelete) {
        final Education sectionData = education == null ? new Education() : education;

        TextField institution = Inputs.inputField(OnboardingScreenText.LABEL_INSTITUTION_PLACEHOLDER,260, Double.MAX_VALUE);
        TextField degree = Inputs.inputField(OnboardingScreenText.LABEL_DEGREE_PLACEHOLDER,220, Double.MAX_VALUE);
        TextField gpa = Inputs.inputField(OnboardingScreenText.LABEL_GPA_PLACEHOLDER, 90, 120);
       
        institution.setText(Inputs.nullToEmpty(sectionData.getInstitution()));
        degree.setText(Inputs.nullToEmpty(sectionData.getDegree()));
        gpa.setText(Inputs.nullToEmpty(sectionData.getGpa()));
        institution.textProperty().addListener((o, oldValue, newValue) -> controller.updateEducationInstitution(sectionData, newValue));
        degree.textProperty().addListener((o, oldValue, newValue) -> controller.updateEducationDegree(sectionData, newValue));
        gpa.textProperty().addListener((o, oldValue, newValue) -> controller.updateEducationGpa(sectionData, newValue));

        VBox institutionGroup = Inputs.buildInputGroup(TextStyles.buildLabel(OnboardingScreenText.EDU_LABEL_INSTITUTION, "input-label"), institution);
        VBox degreeGroup = Inputs.buildInputGroup(TextStyles.buildLabel(OnboardingScreenText.EDU_LABEL_DEGREE, "input-label"), degree);
        VBox graduation = monthYearGroup(OnboardingScreenText.EDU_LABEL_GRADUATION, sectionData.getEndDate(),
                value -> controller.updateEducationEndDate(sectionData, value));
        VBox gpaGroup = Inputs.buildInputGroup(TextStyles.buildLabel(OnboardingScreenText.EDU_LABEL_GPA, "input-label"), gpa);

        HBox firstRow = new HBox(16, degreeGroup, institutionGroup);
        Region firstRowSpacer = new Region();
        firstRow.getChildren().addAll(firstRowSpacer, graduation, gpaGroup);
        firstRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(firstRowSpacer, Priority.ALWAYS);
        VBox fields = new VBox(16, firstRow);
        return buildEditableSectionCard(OnboardingScreenText.EDU_CARD_PREFIX + sectionNumber, fields, onDuplicate, onDelete);
    }

    /**
     * Displays one editable card for each education model in the scrollable wizard area.
     */
    public void showEducationStep(ObservableList<Education> educationItems) {
        showEditableSections(educationItems, OnboardingScreenText.EDU_TITLE, Education::new,
                (items, index) -> controller.duplicateEducation(items, index),
                (items, item) -> controller.removeEducation(items, item),
                this::buildEducationSectionEditable);
    }

    /**
     * Builds one editable experience card, initializes it from the supplied model, and writes
     * field changes back to that model. The callbacks let the containing step handle duplication
     * and deletion.
     */
    public VBox buildExperienceSectionEditable(Experience experience, int sectionNumber, Runnable onDuplicate, Runnable onDelete) {
        final Experience sectionData = experience == null ? new Experience() : experience;

        // Create and initialize the editable fields from the supplied parser/profile data.
        TextField jobTitle = Inputs.inputField(OnboardingScreenText.LABEL_EXPERIENCE_JOB_TITLE_EXAMPLE, 240, Double.MAX_VALUE);
        TextField companyName = Inputs.inputField(OnboardingScreenText.LABEL_EXPERIENCE_COMPANY_EXAMPLE, 240, Double.MAX_VALUE);
        TextArea description = Inputs.textArea(OnboardingScreenText.LABEL_EXPERIENCE_DESCRIPTION_EXAMPLE, 400, Double.MAX_VALUE, 100, 120);

        jobTitle.setText(Inputs.nullToEmpty(sectionData.getJobTitle()));
        companyName.setText(Inputs.nullToEmpty(sectionData.getCompanyName()));
        description.setText(Inputs.nullToEmpty(sectionData.getDescription()));

        jobTitle.textProperty().addListener((observable, oldValue, newValue) -> controller.updateExperienceJobTitle(sectionData, newValue));
        companyName.textProperty().addListener((observable, oldValue, newValue) -> controller.updateExperienceCompany(sectionData, newValue));
        description.textProperty().addListener((observable, oldValue, newValue) -> controller.updateExperienceDescription(sectionData, newValue));

        // Arrange the labeled fields into the rows shown in the experience card design.
        VBox jobTitleGroup = Inputs.buildInputGroup(
                TextStyles.buildLabel(OnboardingScreenText.EXP_LABEL_JOB_TITLE, "input-label"), jobTitle);
        VBox companyGroup = Inputs.buildInputGroup(
                TextStyles.buildLabel(OnboardingScreenText.EXP_LABEL_COMPANY_NAME, "input-label"), companyName);
        HBox dates = new HBox(24,
                VisualElements.dateGroup(OnboardingScreenText.EXP_LABEL_START_DATE, sectionData.getStartDate(),
                        value -> controller.updateExperienceStartDate(sectionData, value)),
                VisualElements.dateGroup(OnboardingScreenText.EXP_LABEL_END_DATE, sectionData.getEndDate(),
                        value -> controller.updateExperienceEndDate(sectionData, value)));

        Region mainFieldsSpacer = new Region();
        HBox mainFields = new HBox(16, jobTitleGroup, companyGroup, mainFieldsSpacer, dates);
        mainFields.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(mainFieldsSpacer, Priority.ALWAYS);

        Label descriptionLabel = TextStyles.buildLabel(OnboardingScreenText.EXP_LABEL_DESCRIPTION, "input-label");
        descriptionLabel.setLabelFor(description);
        HBox descriptionInput = new HBox(description);
        HBox.setHgrow(description, Priority.ALWAYS);
        VBox descriptionGroup = new VBox(new HBox(descriptionLabel), descriptionInput);
        descriptionGroup.setAlignment(Pos.TOP_LEFT);
        descriptionGroup.getStyleClass().add("input-field-spacing");
        VBox inputGroups = new VBox(16, mainFields, descriptionGroup);

        // Add card actions; the parent step supplies callbacks that update its model list.
        Button duplicate = Buttons.button(FontAwesomeSolid.COPY,
                event -> { if (onDuplicate != null) onDuplicate.run(); },
                "experience-icon-button");
        duplicate.setAccessibleText("Duplicate experience");
        Button delete = Buttons.button(FontAwesomeSolid.TRASH,
                event -> { if (onDelete != null) onDelete.run(); },
                "experience-icon-button", "experience-delete-button");
        delete.setAccessibleText("Delete experience");

        return VisualElements.buildParsedInfoCard(
                OnboardingScreenText.EXP_CARD_PREFIX + sectionNumber,
                List.of(inputGroups), List.of(duplicate, delete), "experience-editable-card");
    }

    /** Displays one editable experience card for each model in the wizard. */
    public void showExperienceStep(ObservableList<Experience> experiences) {
        showEditableSections(experiences, OnboardingScreenText.EXP_TITLE, Experience::new,
                (items, index) -> controller.duplicateExperience(items, index),
                (items, item) -> controller.removeExperience(items, item),
                this::buildExperienceSectionEditable);
    }

    /**
     * Builds an editable project card for the projects onboarding step.
     */
    public VBox buildProjectsSectionEditable(Projects project, int sectionNumber, Runnable onDuplicate, Runnable onDelete) {
        final Projects sectionData = project == null ? new Projects() : project;

        TextField name = Inputs.inputField(OnboardingScreenText.LABEL_PROJECT_TITLE_PLACEHOLDER,240, Double.MAX_VALUE);
        TextField role = Inputs.inputField(OnboardingScreenText.PROJ_LABEL_ROLE, 220, Double.MAX_VALUE);
        TextField location = Inputs.inputField(OnboardingScreenText.PROJ_LABEL_LOCATION, 220, Double.MAX_VALUE);
        TextArea description = Inputs.textArea(OnboardingScreenText.LABEL_PROJECT_DESCRIPTION_PLACEHOLDER,400, Double.MAX_VALUE, 100, 180);
        
        name.setText(Inputs.nullToEmpty(sectionData.getProjectName()));
        role.setText(Inputs.nullToEmpty(sectionData.getRole()));
        location.setText(Inputs.nullToEmpty(sectionData.getLocation()));
        description.setText(Inputs.nullToEmpty(sectionData.getDescription()));
        name.textProperty().addListener((o, oldValue, newValue) -> controller.updateProjectName(sectionData, newValue));
        role.textProperty().addListener((o, oldValue, newValue) -> controller.updateProjectRole(sectionData, newValue));
        location.textProperty().addListener((o, oldValue, newValue) -> controller.updateProjectLocation(sectionData, newValue));
        description.textProperty().addListener((o, oldValue, newValue) -> controller.updateProjectDescription(sectionData, newValue));

        VBox nameGroup = Inputs.buildInputGroup(TextStyles.buildLabel(OnboardingScreenText.PROJ_LABEL_TITLE, "input-label"), name);
        VBox roleGroup = Inputs.buildInputGroup(TextStyles.buildLabel(OnboardingScreenText.PROJ_LABEL_ROLE, "input-label"), role);
        HBox dates = new HBox(24,
                VisualElements.dateGroup(OnboardingScreenText.PROJ_LABEL_START_DATE, sectionData.getStartDate(),
                        value -> controller.updateProjectStartDate(sectionData, value)),
                VisualElements.dateGroup(OnboardingScreenText.PROJ_LABEL_END_DATE, sectionData.getEndDate(),
                        value -> controller.updateProjectEndDate(sectionData, value)));
        VBox locationGroup = Inputs.buildInputGroup(TextStyles.buildLabel(OnboardingScreenText.PROJ_LABEL_LOCATION, "input-label"), location);
        VBox descriptionGroup = Inputs.buildInputGroup(TextStyles.buildLabel(OnboardingScreenText.PROJ_LABEL_DESCRIPTION, "input-label"), description);
        description.setMaxWidth(Double.MAX_VALUE);
        descriptionGroup.setMaxWidth(Double.MAX_VALUE);

        Region firstRowSpacer = new Region();
        HBox firstRow = new HBox(16, nameGroup, roleGroup, firstRowSpacer, dates);
        firstRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(firstRowSpacer, Priority.ALWAYS);

        HBox secondRow = new HBox(16, descriptionGroup, locationGroup);
        secondRow.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(descriptionGroup, Priority.ALWAYS);
        VBox fields = new VBox(16, firstRow, secondRow);
        return buildEditableSectionCard(OnboardingScreenText.PROJ_CARD_PREFIX + sectionNumber,
                fields, onDuplicate, onDelete);
    }

    /**
     * Displays one editable card for each project model in the scrollable wizard area.
     * Map parsed project data into {@link Projects} objects before passing the list here.
     */
    public void showProjectsStep(ObservableList<Projects> projects){
        showEditableSections(projects, OnboardingScreenText.PROJ_TITLE, Projects::new,
                (items, index) -> controller.duplicateProject(items, index),
                (items, item) -> controller.removeProject(items, item),
                this::buildProjectsSectionEditable);
    }


    /** Builds a clickable skill chip; clicking it runs the supplied remove action. */
    public static Button buildSkillsSectionEditable(Skill skill, Runnable onDelete) {
        final Skill sectionData = skill == null ? new Skill() : skill;
        Button chip = Buttons.button(
                Inputs.nullToEmpty(sectionData.getSkillName()),
                event -> {
                    if (onDelete != null) {
                        onDelete.run();
                    }
                },
                Region.USE_COMPUTED_SIZE,
                "onboarding-chiplet-button");
        chip.setAccessibleText("Delete skill " + Inputs.nullToEmpty(sectionData.getSkillName()));

        return chip;
    }

    /** Builds the prompt, text field, and add button used above the skill chips. */
    private HBox buildSkillsSectionEditable(ObservableList<Skill> skills) {
        Label prompt = TextStyles.buildLabel(OnboardingScreenText.SKL_ADD_PROMPT, "onboarding-chiplet-prompt");
        TextField skillName = Inputs.inputField(OnboardingScreenText.SKL_ADD_PLACEHOLDER, 340, 500);
        Button add = Buttons.button("+", event -> {
            if (controller.addSkill(skills, skillName.getText())) {
                skillName.clear();
            }
        }, 36, "onboarding-chiplet-add-button");
        add.setAccessibleText("Add skill");

        HBox inputRow = new HBox(12, prompt, skillName, add);
        inputRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(skillName, Priority.NEVER);
        return inputRow;
    }

    /** Displays the add-skill row and wrapping, removable skill chips. */
    public void showSkillsStep(ObservableList<Skill> skills){
        showEditableChipletSection(
                skills,
                OnboardingScreenText.SKL_TITLE,
                OnboardingScreenText.SKL_CONFIRM_MESSAGE,
                buildSkillsSectionEditable(skills),
                (items, item) -> controller.removeSkill(items, item),
                OnboardingStepLayout::buildSkillsSectionEditable);
    }

    /**
     * Shows a single editable chiplet section. List changes rebuild the flow pane;
     * each chip's delete action removes its corresponding item.
     */
    private <T> void showEditableChipletSection(
            ObservableList<T> items,
            String title,
            String instruction,
            Node inputRow,
            BiConsumer<ObservableList<T>, T> deleteItem,
            ChipletFactory<T> chipFactory) {
        Objects.requireNonNull(items, "items");
        Objects.requireNonNull(inputRow, "inputRow");
        Objects.requireNonNull(chipFactory, "chipFactory");
        detachShownItemsListener();

        Label instructionLabel = new Label(instruction);
        instructionLabel.getStyleClass().add("onboarding-wizard-subtitle");

        FlowPane chips = new FlowPane();
        chips.setHgap(16);
        chips.setVgap(12);

        Runnable refresh = () -> {
            chips.getChildren().clear();
            for (T item : items) {
                chips.getChildren().add(chipFactory.build(item, () -> deleteItem.accept(items, item)));
            }
        };
        ListChangeListener<T> listener = change -> refresh.run();
        items.addListener(listener);
        shownItems = items;
        shownItemsListener = listener;
        refresh.run();

        VBox content = new VBox(28, instructionLabel, inputRow, new Separator(), chips);
        content.setFillWidth(true);
        content.getStyleClass().add("onboarding-chiplet-section");
        setTop(TextStyles.buildTitle(title, "onboarding-wizard-title"));
        showStep(content);
    }

    @FunctionalInterface
    private interface ChipletFactory<T> {
        Node build(T item, Runnable onDelete);
    }

    /**
     * Builds an editable awards and certifications card for that onboarding step.
     */
    public VBox buildAwardsCertsSectionEditable(AwardsCerts award, int sectionNumber, Runnable onDuplicate, Runnable onDelete) {
        final AwardsCerts sectionData = award == null ? new AwardsCerts() : award;

        TextField awardName = Inputs.inputField(OnboardingScreenText.LABEL_AWARD_TITLE_PLACEHOLDER,240, Double.MAX_VALUE);
        TextField issuer = Inputs.inputField(OnboardingScreenText.LABEL_ISSUER_PLACEHOLDER, 240, Double.MAX_VALUE);
        
        awardName.setText(Inputs.nullToEmpty(sectionData.getTitle()));
        issuer.setText(Inputs.nullToEmpty(sectionData.getIssuer()));
        awardName.textProperty().addListener((o, oldValue, newValue) -> controller.updateAwardTitle(sectionData, newValue));
        issuer.textProperty().addListener((o, oldValue, newValue) -> controller.updateAwardIssuer(sectionData, newValue));

        VBox titleGroup = Inputs.buildInputGroup(TextStyles.buildLabel(OnboardingScreenText.AWDCT_LABEL_TITLE, "input-label"), awardName);
        VBox issuerGroup = Inputs.buildInputGroup(TextStyles.buildLabel(OnboardingScreenText.AWDCT_LABEL_ISSUER, "input-label"), issuer);
        VBox date = monthYearGroup(OnboardingScreenText.AWDCT_LABEL_DATE_ISSUED, sectionData.getDateReceived(),
                value -> controller.updateAwardDate(sectionData, value));

        Region rowSpacer = new Region();
        HBox awardRow = new HBox(16, titleGroup, issuerGroup, rowSpacer, date);
        awardRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(rowSpacer, Priority.ALWAYS);
        return buildEditableSectionCard(OnboardingScreenText.AWDCT_CARD_PREFIX + sectionNumber, awardRow, onDuplicate, onDelete);
    }

    /**
     * Displays one editable card for each award or certification model in the wizard area.
     */
    public void showAwardsStep(ObservableList<AwardsCerts> awards) {
        showEditableSections(awards, OnboardingScreenText.AWDCT_TITLE, AwardsCerts::new,
                (items, index) -> controller.duplicateAward(items, index),
                (items, item) -> controller.removeAward(items, item),
                this::buildAwardsCertsSectionEditable);
    }

    /** Builds the platform and URL entry row for adding website chips. */
    private HBox buildWebsitesSectionEditable(ObservableList<Websites> websites) {
        Label prompt = TextStyles.buildLabel(OnboardingScreenText.WEB_ADD_PROMPT, "onboarding-chiplet-prompt");
        TextField platform = Inputs.inputField(OnboardingScreenText.LABEL_PLATFORM_PLACEHOLDER, 150, 220);
        TextField url = Inputs.inputField(OnboardingScreenText.LABEL_WEBSITE_URL_PLACEHOLDER, 300, 480);
        Button add = Buttons.button("+", event -> {
            if (controller.addWebsite(websites, platform.getText(), url.getText())) {
                platform.clear();
                url.clear();
            }
        }, 36, "onboarding-chiplet-add-button");
        add.setAccessibleText("Add website");

        HBox inputRow = new HBox(12, prompt, platform, url, add);
        inputRow.setAlignment(Pos.CENTER_LEFT);
        return inputRow;
    }

    /** Displays a platform and URL entry row with wrapping, removable website chips. */
    public void showWebsitesStep(ObservableList<Websites> websites) {
        showEditableChipletSection(
                websites,
                OnboardingScreenText.WEB_TITLE,
                OnboardingScreenText.WEB_CONFIRM_MESSAGE,
                buildWebsitesSectionEditable(websites),
                (items, item) -> controller.removeWebsite(items, item),
                OnboardingStepLayout::buildWebsiteChipEditable);
    }

    /** Builds a clickable website chip; clicking it runs the supplied remove action. */
    private static Button buildWebsiteChipEditable(Websites website, Runnable onDelete) {
        String platform = Inputs.nullToEmpty(website.getUrlType());
        String url = Inputs.nullToEmpty(website.getUrl());
        String chipText = platform.isBlank() ? url : url.isBlank() ? platform : platform + " · " + url;
        Button chip = Buttons.button(
                chipText,
                event -> onDelete.run(),
                Region.USE_COMPUTED_SIZE,
                "onboarding-chiplet-button");
        chip.setAccessibleText("Delete website " + chipText);
        return chip;
    }
   
}
