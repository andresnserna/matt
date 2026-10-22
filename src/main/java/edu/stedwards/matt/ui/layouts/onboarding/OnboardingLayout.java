/**
 * Architecture layer: view
 * JavaFX layout for the onboarding screen.
 */
package edu.stedwards.matt.ui.layouts.onboarding;

import javafx.scene.text.TextFlow;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

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

public class OnboardingLayout extends BorderPane {
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
    public OnboardingLayout(){
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
        Button duplicate = Buttons.button(FontAwesomeSolid.COPY,
                        event -> {
                                if (onDuplicate != null)
                                        onDuplicate.run();
                        },
                        "experience-icon-button");

        duplicate.setAccessibleText("Duplicate section");

        Button delete = Buttons.button(FontAwesomeSolid.TRASH,
                        event -> {
                                if (onDelete != null)
                                        onDelete.run();
                        },
                        "experience-icon-button", "experience-delete-button");
        delete.setAccessibleText("Delete section");

        return VisualElements.buildParsedInfoCardEditable(heading, List.of(fields), List.of(duplicate, delete));
    }

    /**
     * Displays and observes a collection of editable onboarding cards, refreshing
     * it when an
     * item is duplicated or removed.
     */
    private <T> void showEditableSections(ObservableList<T> items, String title, Supplier<T> emptyItemFactory, Function<T, T> copyItem, EditableSectionFactory<T> cardFactory) {
        Objects.requireNonNull(items, "items");
        detachShownItemsListener();
        if (items.isEmpty()) {
                items.add(emptyItemFactory.get());
        }

        // TODO: Set the final spacing and placement for the repeated section cards
        // here.
        VBox cards = new VBox();
        cards.setFillWidth(true);
        Runnable refresh = () -> {
                cards.getChildren().clear();
                for (int i = 0; i < items.size(); i++) {
                        int index = i;
                        T item = items.get(i);
                        cards.getChildren().add(cardFactory.build(item, i + 1,
                                        () -> items.add(index + 1, copyItem.apply(item)),
                                        () -> {
                                                if (items.size() > 1) {
                                                        items.remove(item);
                                                }
                                        }));
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

    /**
     * Builds an editable education card for the education onboarding step.
     */
    public static VBox buildEducationSectionEditable(Education education, int sectionNumber, Runnable onDuplicate, Runnable onDelete) {
        final Education sectionData = education == null ? new Education() : education;

        TextField institution = Inputs.inputField(OnboardingScreenText.LABEL_INSTITUTION_PLACEHOLDER,260, Double.MAX_VALUE);
        TextField degree = Inputs.inputField(OnboardingScreenText.LABEL_DEGREE_PLACEHOLDER,220, Double.MAX_VALUE);
        TextField gpa = Inputs.inputField(OnboardingScreenText.LABEL_GPA_PLACEHOLDER, 90, 120);
       
        institution.setText(Inputs.nullToEmpty(sectionData.getInstitution()));
        degree.setText(Inputs.nullToEmpty(sectionData.getDegree()));
        gpa.setText(Inputs.nullToEmpty(sectionData.getGpa()));
        institution.textProperty().addListener((o, oldValue, newValue) -> sectionData.setInstitution(newValue));
        degree.textProperty().addListener((o, oldValue, newValue) -> sectionData.setDegree(newValue));
        gpa.textProperty().addListener((o, oldValue, newValue) -> sectionData.setGpa(newValue));

        VBox institutionGroup = Inputs.buildInputGroup(TextStyles.buildLabel(OnboardingScreenText.EDU_LABEL_INSTITUTION, "input-label"), institution);
        VBox degreeGroup = Inputs.buildInputGroup(TextStyles.buildLabel(OnboardingScreenText.EDU_LABEL_DEGREE, "input-label"), degree);
        HBox dates = new HBox(
                VisualElements.dateGroup(OnboardingScreenText.EDU_LABEL_START_DATE, sectionData.getStartDate(),
                        value -> sectionData.setStartDate(value)),
                VisualElements.dateGroup(OnboardingScreenText.EDU_LABEL_END_DATE, sectionData.getEndDate(),
                        value -> sectionData.setEndDate(value)));
        VBox gpaGroup = Inputs.buildInputGroup(
                TextStyles.buildLabel(OnboardingScreenText.EDU_LABEL_GPA, "input-label"), gpa);

        // TODO: Set final spacing and placement for education fields here.
        VBox fields = new VBox(new HBox(institutionGroup, degreeGroup, dates), gpaGroup);
        return buildEditableSectionCard(OnboardingScreenText.EDU_CARD_PREFIX + sectionNumber, fields, onDuplicate, onDelete);
    }

    /**
     * Displays one editable card for each education model in the scrollable wizard area.
     */
    public void showEducationStep(ObservableList<Education> educationItems) {
        showEditableSections(educationItems, OnboardingScreenText.EDU_TITLE, Education::new, Education::copy, OnboardingLayout::buildEducationSectionEditable);
    }

    /**
     * Builds one editable experience card, initializes it from the supplied model, and writes
     * field changes back to that model. The callbacks let the containing step handle duplication
     * and deletion.
     */
    public static VBox buildExperienceSectionEditable(Experience experience, int sectionNumber, Runnable onDuplicate, Runnable onDelete) {
        final Experience sectionData = experience == null ? new Experience() : experience;

        // Create and initialize the editable fields from the supplied parser/profile data.
        TextField jobTitle = Inputs.inputField(OnboardingScreenText.LABEL_EXPERIENCE_JOB_TITLE_EXAMPLE, 240, Double.MAX_VALUE);
        TextField companyName = Inputs.inputField(OnboardingScreenText.LABEL_EXPERIENCE_COMPANY_EXAMPLE, 240, Double.MAX_VALUE);
        TextArea description = Inputs.textArea(OnboardingScreenText.LABEL_EXPERIENCE_DESCRIPTION_EXAMPLE,400, Double.MAX_VALUE, 100, 180);

        jobTitle.setText(Inputs.nullToEmpty(sectionData.getJobTitle()));
        companyName.setText(Inputs.nullToEmpty(sectionData.getCompanyName()));
        description.setText(Inputs.nullToEmpty(sectionData.getDescription()));

        jobTitle.textProperty().addListener((observable, oldValue, newValue) -> sectionData.setJobTitle(newValue));
        companyName.textProperty().addListener((observable, oldValue, newValue) -> sectionData.setCompanyName(newValue));
        description.textProperty().addListener((observable, oldValue, newValue) -> sectionData.setDescription(newValue));

        // Arrange the labeled fields into the rows shown in the experience card design.
        // TODO: Finalize spacing and placement for the experience fields in this container.
        VBox jobTitleGroup = Inputs.buildInputGroup(
                TextStyles.buildLabel(OnboardingScreenText.EXP_LABEL_JOB_TITLE, "input-label"), jobTitle);
        VBox companyGroup = Inputs.buildInputGroup(
                TextStyles.buildLabel(OnboardingScreenText.EXP_LABEL_COMPANY_NAME, "input-label"), companyName);
        HBox dates = new HBox(
                VisualElements.dateGroup(OnboardingScreenText.EXP_LABEL_START_DATE, sectionData.getStartDate(),
                        value -> sectionData.setStartDate(value)),
                VisualElements.dateGroup(OnboardingScreenText.EXP_LABEL_END_DATE, sectionData.getEndDate(),
                        value -> sectionData.setEndDate(value)));

        HBox mainFields = new HBox(16, jobTitleGroup, companyGroup, dates);
        mainFields.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(jobTitleGroup, Priority.ALWAYS);
        HBox.setHgrow(companyGroup, Priority.ALWAYS);
        HBox.setHgrow(dates, Priority.ALWAYS);

        VBox descriptionGroup = Inputs.buildInputGroup(TextStyles.buildLabel(OnboardingScreenText.EXP_LABEL_DESCRIPTION, "input-label"), description);
        VBox inputGroups = new VBox(20, mainFields, descriptionGroup);

        // Add card actions; the parent step supplies callbacks that update its model list.
        Button duplicate = Buttons.button(FontAwesomeSolid.COPY,
                event -> { if (onDuplicate != null) onDuplicate.run(); },
                "experience-icon-button");
        duplicate.setAccessibleText("Duplicate experience");
        Button delete = Buttons.button(FontAwesomeSolid.TRASH,
                event -> { if (onDelete != null) onDelete.run(); },
                "experience-icon-button", "experience-delete-button");
        delete.setAccessibleText("Delete experience");

        return VisualElements.buildParsedInfoCardEditable(
                OnboardingScreenText.EXP_CARD_PREFIX + sectionNumber,
                List.of(inputGroups), List.of(duplicate, delete), "experience-editable-card");
    }

    /**
     * Displays one editable card for each experience model in the scrollable wizard area.
     * Map parser results into {@link Experience} objects before passing the observable list here;
     * changes to the list refresh the displayed cards.
     */
    public void showExperienceStep(ObservableList<Experience> experiences) {
        showEditableSections(experiences, OnboardingScreenText.EXP_TITLE, Experience::new, Experience::copy, OnboardingLayout::buildExperienceSectionEditable);
    }

    /**
     * Builds an editable project card for the projects onboarding step.
     */
    public static VBox buildProjectsSectionEditable(Projects project, int sectionNumber, Runnable onDuplicate, Runnable onDelete) {
        final Projects sectionData = project == null ? new Projects() : project;

        TextField name = Inputs.inputField(OnboardingScreenText.LABEL_PROJECT_TITLE_PLACEHOLDER,240, Double.MAX_VALUE);
        TextField url = Inputs.inputField(OnboardingScreenText.LABEL_PROJECT_URL_PLACEHOLDER,260, Double.MAX_VALUE);
        TextArea description = Inputs.textArea(OnboardingScreenText.LABEL_PROJECT_DESCRIPTION_PLACEHOLDER,400, Double.MAX_VALUE, 100, 180);
        
        name.setText(Inputs.nullToEmpty(sectionData.getProjectName()));
        url.setText(Inputs.nullToEmpty(sectionData.getUrl()));
        description.setText(Inputs.nullToEmpty(sectionData.getDescription()));
        name.textProperty().addListener((o, oldValue, newValue) -> sectionData.setProjectName(newValue));
        url.textProperty().addListener((o, oldValue, newValue) -> sectionData.setUrl(newValue));
        description.textProperty().addListener((o, oldValue, newValue) -> sectionData.setDescription(newValue));

        VBox nameGroup = Inputs.buildInputGroup(TextStyles.buildLabel(OnboardingScreenText.PROJ_LABEL_TITLE, "input-label"), name);
        VBox urlGroup = Inputs.buildInputGroup(TextStyles.buildLabel(OnboardingScreenText.PROJ_LABEL_URL, "input-label"), url);
        HBox dates = new HBox(
                VisualElements.dateGroup(OnboardingScreenText.PROJ_LABEL_START_DATE, sectionData.getStartDate(),
                        value -> sectionData.setStartDate(value)),
                VisualElements.dateGroup(OnboardingScreenText.PROJ_LABEL_END_DATE, sectionData.getEndDate(),
                        value -> sectionData.setEndDate(value)));
        VBox descriptionGroup = Inputs.buildInputGroup(TextStyles.buildLabel(OnboardingScreenText.PROJ_LABEL_DESCRIPTION, "input-label"), description);

        // TODO: Set final spacing and placement for project fields here.
        VBox fields = new VBox(new HBox(nameGroup, urlGroup), dates, descriptionGroup);
        return buildEditableSectionCard(OnboardingScreenText.PROJ_CARD_PREFIX + sectionNumber,
                fields, onDuplicate, onDelete);
    }

    /**
     * Displays one editable card for each project model in the scrollable wizard area.
     * Map parsed project data into {@link Projects} objects before passing the list here.
     */
    public void showProjectsStep(ObservableList<Projects> projects){
        showEditableSections(projects, OnboardingScreenText.PROJ_TITLE, Projects::new, Projects::copy, OnboardingLayout::buildProjectsSectionEditable);
    }


    /**
     * Builds an editable skills card for the skills onboarding step.
     */
    public static VBox buildSkillsSectionEditable() {
        return null;
    }

    /**
     * Displays one editable card for each experience model in the scrollable wizard area.
     * Map parser results into {@link Experience} objects before passing the observable list here;
     * changes to the list refresh the displayed cards.
     */
    public void showSkillsStep(ObservableList<Skill> skills){

    }

    /**
     * Builds an editable awards and certifications card for that onboarding step.
     */
    public static VBox buildAwardsCertsSectionEditable(AwardsCerts award, int sectionNumber, Runnable onDuplicate, Runnable onDelete) {
        final AwardsCerts sectionData = award == null ? new AwardsCerts() : award;

        TextField title = Inputs.inputField(OnboardingScreenText.LABEL_AWARD_TITLE_PLACEHOLDER,240, Double.MAX_VALUE);
        TextField issuer = Inputs.inputField(OnboardingScreenText.LABEL_ISSUER_PLACEHOLDER, 240, Double.MAX_VALUE);
        TextArea description = Inputs.textArea(OnboardingScreenText.LABEL_AWARD_DESCRIPTION_PLACEHOLDER, 400, Double.MAX_VALUE, 100, 180);
        
        title.setText(Inputs.nullToEmpty(sectionData.getTitle()));
        issuer.setText(Inputs.nullToEmpty(sectionData.getIssuer()));
        description.setText(Inputs.nullToEmpty(sectionData.getDescription()));
        title.textProperty().addListener((o, oldValue, newValue) -> sectionData.setTitle(newValue));
        issuer.textProperty().addListener((o, oldValue, newValue) -> sectionData.setIssuer(newValue));
        description.textProperty().addListener((o, oldValue, newValue) -> sectionData.setDescription(newValue));

        VBox titleGroup = Inputs.buildInputGroup(TextStyles.buildLabel(OnboardingScreenText.AWDCT_LABEL_TITLE, "input-label"), title);
        VBox issuerGroup = Inputs.buildInputGroup(TextStyles.buildLabel(OnboardingScreenText.AWDCT_LABEL_ISSUER, "input-label"), issuer);
        VBox date = VisualElements.dateGroup(OnboardingScreenText.AWDCT_LABEL_DATE_ISSUED,sectionData.getDateReceived(), sectionData::setDateReceived);
        VBox descriptionGroup = Inputs.buildInputGroup(TextStyles.buildLabel(OnboardingScreenText.AWDCT_LABEL_DESCRIPTION, "input-label"), description);

        // TODO: Set final spacing and placement for award fields here.
        VBox fields = new VBox(new HBox(titleGroup, issuerGroup, date), descriptionGroup);
        return buildEditableSectionCard(OnboardingScreenText.AWDCT_CARD_PREFIX + sectionNumber, fields, onDuplicate, onDelete);
    }

    /**
     * Displays one editable card for each award or certification model in the wizard area.
     */
    public void showAwardsStep(ObservableList<AwardsCerts> awards) {
        showEditableSections(awards, OnboardingScreenText.AWDCT_TITLE, AwardsCerts::new, AwardsCerts::copy, OnboardingLayout::buildAwardsCertsSectionEditable);
    }

    /**
     * Builds an editable website links card for the websites onboarding step.
     */
    public static VBox buildWebsitesSectionEditable() {
        return null;
    }

    /**
     * Displays one editable card for each experience model in the scrollable wizard
     * area.
     * Map parser results into {@link Experience} objects before passing the
     * observable list here;
     * changes to the list refresh the displayed cards.
     */
    public void showWebsitesStep(ObservableList<Websites> websites) {

    }
   
}
