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
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.function.Consumer;
import java.util.function.BiConsumer;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import edu.stedwards.matt.controllers.OnboardingStepController;

/**
 * Builds the onboarding layout for first-time or introductory user flow.
 */
public class OnboardingStepLayout extends BorderPane {
    private final OnboardingStepController controller;
    private Node currentStep;
    private ObservableList<?> shownItems;
    private ListChangeListener<?> shownItemsListener;

    // Data lists that follow the user through the wizard
    private ObservableList<Education> education = FXCollections.observableArrayList();
    private ObservableList<Experience> experiences = FXCollections.observableArrayList();
    private ObservableList<Projects> projects = FXCollections.observableArrayList();
    private ObservableList<Skill> skills = FXCollections.observableArrayList();
    private ObservableList<AwardsCerts> awards = FXCollections.observableArrayList();
    private ObservableList<Websites> websites = FXCollections.observableArrayList();

    private Button nextButton;

    public void showStep(Node step) {
        currentStep = VisualElements.wizardScrollable(step);
        VBox.setVgrow(currentStep, Priority.ALWAYS);
        setCenter(currentStep);
        BorderPane.setAlignment(currentStep, Pos.CENTER_LEFT);
        BorderPane.setMargin(currentStep, new Insets(0, 0, 24, 0));
    } 

    public OnboardingStepLayout(OnboardingStepController controller){
        this.controller = controller;
        getStyleClass().addAll("wizard-background", "screen-padding");
        
        TextFlow title = TextStyles.buildTitle(OnboardingScreenText.PLACEHOLDER_TITLE, "onboarding-wizard-title");
        setTop(title);
        
        HBox bottomBar = new Buttons().buildNextButton();
        if (!bottomBar.getChildren().isEmpty() && bottomBar.getChildren().get(0) instanceof Button) {
            nextButton = (Button) bottomBar.getChildren().get(0);
        }
        setBottom(bottomBar);

        // Start wizard
        showEducationStep(education);
    }

    private static VBox buildEditableSectionCard(String heading, Node fields, Runnable onDuplicate, Runnable onDelete) {
        Button duplicate = Buttons.button(OnboardingScreenText.CONTENT_CARD_COPY_ICON,
                        event -> { if (onDuplicate != null) onDuplicate.run(); },
                        "experience-icon-button");
        duplicate.setAccessibleText("Duplicate section");

        Button delete = Buttons.button(OnboardingScreenText.CONTENT_CARD_DELETE_ICON,
                        event -> { if (onDelete != null) onDelete.run(); },
                        "experience-icon-button", "experience-delete-button");
        delete.setAccessibleText("Delete section");

        return VisualElements.buildParsedInfoCard(heading, List.of(fields), List.of(duplicate, delete));
    }

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

    private void detachShownItemsListener() {
        if (shownItems != null && shownItemsListener != null) {
                removeListListener(shownItems, shownItemsListener);
                shownItems = null;
                shownItemsListener = null;
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static void removeListListener(ObservableList<?> items, ListChangeListener<?> listener) {
            ((ObservableList) items).removeListener((ListChangeListener) listener);
    }

    @FunctionalInterface
    private interface EditableSectionFactory<T> {
            VBox build(T item, int sectionNumber, Runnable onDuplicate, Runnable onDelete);
    }

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
            return parts[0].length() == 4 ? new String[] {parts[1], parts[0]} : new String[] {parts[0], parts[2]};
        }
        if (parts.length == 2) {
            return parts[0].length() == 4 ? new String[] {parts[1], parts[0]} : new String[] {parts[0], parts[1]};
        }
        return new String[] {"", ""};
    }

    // --- EDU ---

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

    public void showEducationStep(ObservableList<Education> dataItems) {
        education = Objects.requireNonNull(dataItems, "education items");
        showEditableSections(education, OnboardingScreenText.EDU_TITLE, Education::new,
                (items, index) -> controller.duplicateEducation(items, index),
                (items, item) -> controller.removeEducation(items, item),
                this::buildEducationSectionEditable);
        if (nextButton != null) nextButton.setOnAction(e -> handleEducationNext(experiences));
    }

    // --- EXP ---

    public VBox buildExperienceSectionEditable(Experience experience, int sectionNumber, Runnable onDuplicate, Runnable onDelete) {
        final Experience sectionData = experience == null ? new Experience() : experience;
        TextField jobTitle = Inputs.inputField(OnboardingScreenText.LABEL_EXPERIENCE_JOB_TITLE_EXAMPLE, 240, Double.MAX_VALUE);
        TextField companyName = Inputs.inputField(OnboardingScreenText.LABEL_EXPERIENCE_COMPANY_EXAMPLE, 240, Double.MAX_VALUE);
        TextArea description = Inputs.textArea(OnboardingScreenText.LABEL_EXPERIENCE_DESCRIPTION_EXAMPLE, 400, Double.MAX_VALUE, 100, 120);

        jobTitle.setText(Inputs.nullToEmpty(sectionData.getJobTitle()));
        companyName.setText(Inputs.nullToEmpty(sectionData.getCompanyName()));
        description.setText(Inputs.nullToEmpty(sectionData.getDescription()));

        jobTitle.textProperty().addListener((observable, oldValue, newValue) -> controller.updateExperienceJobTitle(sectionData, newValue));
        companyName.textProperty().addListener((observable, oldValue, newValue) -> controller.updateExperienceCompany(sectionData, newValue));
        description.textProperty().addListener((observable, oldValue, newValue) -> controller.updateExperienceDescription(sectionData, newValue));

        VBox jobTitleGroup = Inputs.buildInputGroup(TextStyles.buildLabel(OnboardingScreenText.EXP_LABEL_JOB_TITLE, "input-label"), jobTitle);
        VBox companyGroup = Inputs.buildInputGroup(TextStyles.buildLabel(OnboardingScreenText.EXP_LABEL_COMPANY_NAME, "input-label"), companyName);
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

        Button duplicate = Buttons.button(FontAwesomeSolid.COPY,
                event -> { if (onDuplicate != null) onDuplicate.run(); }, "experience-icon-button");
        duplicate.setAccessibleText("Duplicate experience");
        Button delete = Buttons.button(FontAwesomeSolid.TRASH,
                event -> { if (onDelete != null) onDelete.run(); }, "experience-icon-button", "experience-delete-button");
        delete.setAccessibleText("Delete experience");

        return VisualElements.buildParsedInfoCard(OnboardingScreenText.EXP_CARD_PREFIX + sectionNumber,
                List.of(inputGroups), List.of(duplicate, delete), "experience-editable-card");
    }

    public void showExperienceStep(ObservableList<Experience> dataItems) {
        experiences = Objects.requireNonNull(dataItems, "experience items");
        showEditableSections(experiences, OnboardingScreenText.EXP_TITLE, Experience::new,
                (items, index) -> controller.duplicateExperience(items, index),
                (items, item) -> controller.removeExperience(items, item),
                this::buildExperienceSectionEditable);
        if (nextButton != null) nextButton.setOnAction(e -> handleExperienceNext(projects));
    }

    // --- PROJ ---

    public VBox buildProjectsSectionEditable(Projects project, int sectionNumber, Runnable onDuplicate, Runnable onDelete) {
        final Projects sectionData = project == null ? new Projects() : project;

        // Uses your new simplified model schema: Title, URL, and Description
        TextField name = Inputs.inputField(OnboardingScreenText.LABEL_PROJECT_TITLE_PLACEHOLDER, 240, Double.MAX_VALUE);
        TextField url = Inputs.inputField(OnboardingScreenText.PROJ_LABEL_URL, 220, Double.MAX_VALUE); 
        TextArea description = Inputs.textArea(OnboardingScreenText.LABEL_PROJECT_DESCRIPTION_PLACEHOLDER, 400, Double.MAX_VALUE, 100, 180);
        
        name.setText(Inputs.nullToEmpty(sectionData.getTitle()));
        url.setText(Inputs.nullToEmpty(sectionData.getUrl()));
        description.setText(Inputs.nullToEmpty(sectionData.getDescription()));
        
        // Match the controller's exact method names
        name.textProperty().addListener((o, oldValue, newValue) -> controller.updateProjectTitle(sectionData, newValue));
        url.textProperty().addListener((o, oldValue, newValue) -> controller.updateProjectUrl(sectionData, newValue));
        description.textProperty().addListener((o, oldValue, newValue) -> controller.updateProjectDescription(sectionData, newValue));

        VBox nameGroup = Inputs.buildInputGroup(TextStyles.buildLabel(OnboardingScreenText.PROJ_LABEL_TITLE, "input-label"), name);
        VBox urlGroup = Inputs.buildInputGroup(TextStyles.buildLabel(OnboardingScreenText.PROJ_LABEL_URL, "input-label"), url);
        VBox descriptionGroup = Inputs.buildInputGroup(TextStyles.buildLabel(OnboardingScreenText.PROJ_LABEL_DESCRIPTION, "input-label"), description);
        
        description.setMaxWidth(Double.MAX_VALUE);
        descriptionGroup.setMaxWidth(Double.MAX_VALUE);

        Region firstRowSpacer = new Region();
        HBox firstRow = new HBox(16, nameGroup, firstRowSpacer, urlGroup);
        firstRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(firstRowSpacer, Priority.ALWAYS);

        VBox fields = new VBox(16, firstRow, descriptionGroup);
        return buildEditableSectionCard(OnboardingScreenText.PROJ_CARD_PREFIX + sectionNumber,
                fields, onDuplicate, onDelete);
    }

    public void showProjectsStep(ObservableList<Projects> dataItems){
        projects = Objects.requireNonNull(dataItems, "project items");
        showEditableSections(projects, OnboardingScreenText.PROJ_TITLE, Projects::new,
                (items, index) -> controller.duplicateProject(items, index),
                (items, item) -> controller.removeProject(items, item),
                this::buildProjectsSectionEditable);
        if (nextButton != null) nextButton.setOnAction(e -> handleProjectsNext(skills));
    }

    // --- SKILLS ---

    public static Button buildSkillsSectionEditable(Skill skill, Runnable onDelete) {
        final Skill sectionData = skill == null ? new Skill() : skill;
        Button chip = Buttons.button(
                Inputs.nullToEmpty(sectionData.getSkillName()),
                event -> { if (onDelete != null) onDelete.run(); },
                Region.USE_COMPUTED_SIZE, "onboarding-chiplet-button");
        chip.setAccessibleText("Delete skill " + Inputs.nullToEmpty(sectionData.getSkillName()));
        return chip;
    }

    private HBox buildSkillsSectionEditable(ObservableList<Skill> skillsList) {
        Label prompt = TextStyles.buildLabel(OnboardingScreenText.SKL_ADD_PROMPT, "onboarding-chiplet-prompt");
        TextField skillName = Inputs.inputField(OnboardingScreenText.SKL_ADD_PLACEHOLDER, 340, 500);
        Button add = Buttons.button("+", event -> {
            if (controller.addSkill(skillsList, skillName.getText())) {
                skillName.clear();
            }
        }, 36, "onboarding-chiplet-add-button");
        add.setAccessibleText("Add skill");
        HBox inputRow = new HBox(12, prompt, skillName, add);
        inputRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(skillName, Priority.NEVER);
        return inputRow;
    }

    public void showSkillsStep(ObservableList<Skill> dataItems){
        skills = Objects.requireNonNull(dataItems, "skill items");
        showEditableChipletSection(skills, OnboardingScreenText.SKL_TITLE, OnboardingScreenText.SKL_CONFIRM_MESSAGE,
                buildSkillsSectionEditable(skills), (items, item) -> controller.removeSkill(items, item),
                OnboardingStepLayout::buildSkillsSectionEditable);
        if (nextButton != null) nextButton.setOnAction(e -> handleSkillsNext(awards));
    }

    /**
     * Shows a single editable chiplet section. List changes rebuild the flow pane;
     * each chip's delete action removes its corresponding item.
     */
    private <T> void showEditableChipletSection(
            ObservableList<T> items, String title, String instruction, Node inputRow,
            BiConsumer<ObservableList<T>, T> deleteItem, ChipletFactory<T> chipFactory) {
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

    // --- AWARDS ---

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
        VBox date = monthYearGroup(OnboardingScreenText.AWDCT_LABEL_DATE_ISSUED, sectionData.getDateIssued(),
                value -> controller.updateAwardDate(sectionData, value));

        Region rowSpacer = new Region();
        HBox awardRow = new HBox(16, titleGroup, issuerGroup, rowSpacer, date);
        awardRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(rowSpacer, Priority.ALWAYS);
        return buildEditableSectionCard(OnboardingScreenText.AWDCT_CARD_PREFIX + sectionNumber, awardRow, onDuplicate, onDelete);
    }

    public void showAwardsStep(ObservableList<AwardsCerts> dataItems) {
        awards = Objects.requireNonNull(dataItems, "award items");
        showEditableSections(awards, OnboardingScreenText.AWDCT_TITLE, AwardsCerts::new,
                (items, index) -> controller.duplicateAward(items, index),
                (items, item) -> controller.removeAward(items, item),
                this::buildAwardsCertsSectionEditable);
        if (nextButton != null) nextButton.setOnAction(e -> handleAwardsNext(websites));
    }

    // --- WEBSITES ---

    private HBox buildWebsitesSectionEditable(ObservableList<Websites> websiteList) {
        Label prompt = TextStyles.buildLabel(OnboardingScreenText.WEB_ADD_PROMPT, "onboarding-chiplet-prompt");
        TextField platform = Inputs.inputField(OnboardingScreenText.LABEL_PLATFORM_PLACEHOLDER, 150, 220);
        TextField url = Inputs.inputField(OnboardingScreenText.LABEL_WEBSITE_URL_PLACEHOLDER, 300, 480);
        Button add = Buttons.button("+", event -> {
            if (controller.addWebsite(websiteList, platform.getText(), url.getText())) {
                platform.clear();
                url.clear();
            }
        }, 36, "onboarding-chiplet-add-button");
        add.setAccessibleText("Add website");
        HBox inputRow = new HBox(12, prompt, platform, url, add);
        inputRow.setAlignment(Pos.CENTER_LEFT);
        return inputRow;
    }

    public void showWebsitesStep(ObservableList<Websites> dataItems) {
        websites = Objects.requireNonNull(dataItems, "website items");
        showEditableChipletSection(websites, OnboardingScreenText.WEB_TITLE, OnboardingScreenText.WEB_CONFIRM_MESSAGE,
                buildWebsitesSectionEditable(websites), (items, item) -> controller.removeWebsite(items, item),
                OnboardingStepLayout::buildWebsiteChipEditable);
        if (nextButton != null) nextButton.setOnAction(e ->
                handleWebsitesNext(education, experiences, projects, skills, awards, websites));
    }

    private static Button buildWebsiteChipEditable(Websites website, Runnable onDelete) {
        String platform = Inputs.nullToEmpty(website.getPlatform());
        String url = Inputs.nullToEmpty(website.getUrl());
        String chipText = platform.isBlank() ? url : url.isBlank() ? platform : platform + " · " + url;
        Button chip = Buttons.button(chipText, event -> onDelete.run(), Region.USE_COMPUTED_SIZE, "onboarding-chiplet-button");
        chip.setAccessibleText("Delete website " + chipText);
        return chip;
    }

    // ==========================================
    // --- STATE ROUTING (NAVIGATION METHODS) ---
    // ==========================================
    
    public void handleEducationNext(ObservableList<Experience> dataItems) {
        showExperienceStep(dataItems);
    }

    public void handleExperienceNext(ObservableList<Projects> dataItems) {
        showProjectsStep(dataItems);
    }

    public void handleProjectsNext(ObservableList<Skill> dataItems) {
        showSkillsStep(dataItems);
    }

    public void handleSkillsNext(ObservableList<AwardsCerts> dataItems) {
        showAwardsStep(dataItems);
    }

    public void handleAwardsNext(ObservableList<Websites> dataItems) {
        showWebsitesStep(dataItems);
    }

    public void handleWebsitesNext(
            ObservableList<Education> educationItems,
            ObservableList<Experience> experienceItems,
            ObservableList<Projects> projectItems,
            ObservableList<Skill> skillItems,
            ObservableList<AwardsCerts> awardItems,
            ObservableList<Websites> websiteItems) {
        // Build the final overview step and pass all collected lists and back-navigation runnables
        OnboardingOverviewStep overview = new OnboardingOverviewStep(
            educationItems, experienceItems, projectItems, skillItems, awardItems, websiteItems,
            () -> showEducationStep(educationItems),
            () -> showExperienceStep(experienceItems),
            () -> showProjectsStep(projectItems),
            () -> showSkillsStep(skillItems),
            () -> showAwardsStep(awardItems),
            () -> showWebsitesStep(websiteItems)
        );
        getScene().setRoot(overview); // Move to final review screen
    }

    // ==========================================
    // --- AI PARSING LOGIC HOOKS ---
    // ==========================================

    /** Call this method from your main upload screen and pass in the Map from ProfileController.parseResumeText() */
    public void populateAllFromParsedData(Map<String, String> parsedData) {
        if (parsedData == null || parsedData.isEmpty()) return;
        
        populateEducationFromParsedData(parsedData);
        populateExperienceFromParsedData(parsedData);
        populateProjectsFromParsedData(parsedData);
        populateSkillsFromParsedData(parsedData);
        populateAwardsFromParsedData(parsedData);
        populateWebsitesFromParsedData(parsedData);
    }

    public void populateEducationFromParsedData(Map<String, String> parsedData) {
        String institution = parsedData.getOrDefault("Education_Institution", "");
        String degree = parsedData.getOrDefault("Education_Degree", "");
        if (!institution.isBlank() || !degree.isBlank()) {
            Education edu = new Education();
            edu.setInstitution(institution);
            edu.setDegree(degree);
            education.add(edu);
        }
    }

    public void populateExperienceFromParsedData(Map<String, String> parsedData) {
        String company = parsedData.getOrDefault("Experience_Company", "");
        String title = parsedData.getOrDefault("Experience_Title", "");
        if (!company.isBlank() || !title.isBlank()) {
            Experience exp = new Experience();
            exp.setCompanyName(company);
            exp.setJobTitle(title);
            experiences.add(exp);
        }
    }

    public void populateProjectsFromParsedData(Map<String, String> parsedData) {
        String title = parsedData.getOrDefault("Project_Title", "");
        String url = parsedData.getOrDefault("Project_Url", "");
        if (!title.isBlank()) {
            Projects proj = new Projects();
            proj.setTitle(title);
            proj.setUrl(url);
            projects.add(proj);
        }
    }

    public void populateSkillsFromParsedData(Map<String, String> parsedData) {
        String rawSkills = parsedData.getOrDefault("Skills", "");
        if (!rawSkills.isBlank()) {
            String[] skillArray = rawSkills.split(",");
            for (String skillName : skillArray) {
                controller.addSkill(skills, skillName.trim()); 
            }
        }
    }

    public void populateAwardsFromParsedData(Map<String, String> parsedData) {
        String title = parsedData.getOrDefault("Award_Title", "");
        if (!title.isBlank()) {
            AwardsCerts award = new AwardsCerts();
            award.setTitle(title);
            awards.add(award);
        }
    }

    public void populateWebsitesFromParsedData(Map<String, String> parsedData) {
        String platform = parsedData.getOrDefault("Website_Platform", "");
        String url = parsedData.getOrDefault("Website_Url", "");
        if (!url.isBlank()) {
            controller.addWebsite(websites, platform, url);
        }
    }
}
