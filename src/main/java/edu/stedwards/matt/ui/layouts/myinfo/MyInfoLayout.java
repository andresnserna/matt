package edu.stedwards.matt.ui.layouts.myinfo;

import edu.stedwards.matt.controllers.ProfileController;
import edu.stedwards.matt.models.entity.User;
import edu.stedwards.matt.ui.TextStyles;
import edu.stedwards.matt.ui.interaction.Inputs;
import edu.stedwards.matt.ui.interaction.VisualElements;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Day 5 View Task: Displays and edits the user's personal info populated from onboarding/database.
 */
public class MyInfoLayout extends BorderPane {

    private final ProfileController profileController = new ProfileController();
    private User currentUser;

    private final TextField firstNameField = Inputs.inputField("First Name", 240, Double.MAX_VALUE);
    private final TextField lastNameField = Inputs.inputField("Last Name", 240, Double.MAX_VALUE);
    private final TextField emailField = Inputs.inputField("Email", 260, Double.MAX_VALUE);
    private final TextField phoneField = Inputs.inputField("Phone Number", 220, Double.MAX_VALUE);
    private final TextField address1Field = Inputs.inputField("Address Line 1", 280, Double.MAX_VALUE);
    private final TextField address2Field = Inputs.inputField("Address Line 2", 280, Double.MAX_VALUE);
    private final TextField cityField = Inputs.inputField("City", 200, Double.MAX_VALUE);
    private final TextField stateField = Inputs.inputField("State", 100, 140);
    private final TextField zipField = Inputs.inputField("ZIP Code", 120, 160);
    private final TextField apiKeyField = Inputs.inputField("Ollama API Key", 280, Double.MAX_VALUE);
    private final TextArea summaryArea = Inputs.textArea("Professional Summary", 500, Double.MAX_VALUE, 100, 140);
    private final Label statusFeedbackLabel = new Label();

    public MyInfoLayout() {
        getStyleClass().add("screen-padding");
        setTop(TextStyles.buildTitle("My Info", "onboarding-wizard-title"));

        VBox form = buildForm();
        setCenter(VisualElements.wizardScrollable(form));

        loadUserDataIntoFields();
    }

    private VBox buildForm() {
        VBox firstGroup = Inputs.buildInputGroup(TextStyles.buildLabel("First Name", "input-label"), firstNameField);
        VBox lastGroup = Inputs.buildInputGroup(TextStyles.buildLabel("Last Name", "input-label"), lastNameField);
        HBox nameRow = new HBox(16, firstGroup, lastGroup);
        HBox.setHgrow(firstGroup, Priority.ALWAYS);
        HBox.setHgrow(lastGroup, Priority.ALWAYS);

        VBox emailGroup = Inputs.buildInputGroup(TextStyles.buildLabel("Email", "input-label"), emailField);
        VBox phoneGroup = Inputs.buildInputGroup(TextStyles.buildLabel("Phone", "input-label"), phoneField);
        HBox contactRow = new HBox(16, emailGroup, phoneGroup);
        HBox.setHgrow(emailGroup, Priority.ALWAYS);
        HBox.setHgrow(phoneGroup, Priority.ALWAYS);

        VBox addr1Group = Inputs.buildInputGroup(TextStyles.buildLabel("Address Line 1", "input-label"), address1Field);
        VBox addr2Group = Inputs.buildInputGroup(TextStyles.buildLabel("Address Line 2", "input-label"), address2Field);
        HBox addressRow = new HBox(16, addr1Group, addr2Group);
        HBox.setHgrow(addr1Group, Priority.ALWAYS);
        HBox.setHgrow(addr2Group, Priority.ALWAYS);

        VBox cityGroup = Inputs.buildInputGroup(TextStyles.buildLabel("City", "input-label"), cityField);
        VBox stateGroup = Inputs.buildInputGroup(TextStyles.buildLabel("State", "input-label"), stateField);
        VBox zipGroup = Inputs.buildInputGroup(TextStyles.buildLabel("ZIP", "input-label"), zipField);
        HBox locationRow = new HBox(16, cityGroup, stateGroup, zipGroup);
        HBox.setHgrow(cityGroup, Priority.ALWAYS);

        VBox apiKeyGroup = Inputs.buildInputGroup(TextStyles.buildLabel("Ollama API Key", "input-label"), apiKeyField);
        VBox summaryGroup = Inputs.buildInputGroup(TextStyles.buildLabel("Professional Summary", "input-label"), summaryArea);

        Button saveButton = new Button("Save Changes");
        saveButton.getStyleClass().add("primary-button");
        saveButton.setOnAction(e -> handleSaveProfile());

        HBox actionRow = new HBox(16, saveButton, statusFeedbackLabel);
        actionRow.setAlignment(Pos.CENTER_LEFT);

        VBox container = new VBox(18, nameRow, contactRow, addressRow, locationRow, apiKeyGroup, summaryGroup, actionRow);
        container.setPadding(new Insets(20, 0, 20, 0));
        container.setFillWidth(true);
        return container;
    }

    /** Loads the user's personal info from OnboardingSession / SQLite into the form fields. */
    public void loadUserDataIntoFields() {
        currentUser = profileController.getCurrentUserProfile();
        if (currentUser == null) {
            currentUser = new User();
        }
        firstNameField.setText(Inputs.nullToEmpty(currentUser.getFirstName()));
        lastNameField.setText(Inputs.nullToEmpty(currentUser.getLastName()));
        emailField.setText(Inputs.nullToEmpty(currentUser.getEmail()));
        phoneField.setText(Inputs.nullToEmpty(currentUser.getPhoneNumber()));
        address1Field.setText(Inputs.nullToEmpty(currentUser.getAddressLine1()));
        address2Field.setText(Inputs.nullToEmpty(currentUser.getAddressLine2()));
        cityField.setText(Inputs.nullToEmpty(currentUser.getCity()));
        stateField.setText(Inputs.nullToEmpty(currentUser.getState()));
        zipField.setText(Inputs.nullToEmpty(currentUser.getZip()));
        apiKeyField.setText(Inputs.nullToEmpty(currentUser.getOllamaApiKey()));
        summaryArea.setText(Inputs.nullToEmpty(currentUser.getSummary()));
    }

    /** Saves all edited fields back to SQLite and updates OnboardingSession. */
    public void handleSaveProfile() {
        if (currentUser == null) {
            currentUser = new User();
        }
        currentUser.setFirstName(firstNameField.getText().trim());
        currentUser.setLastName(lastNameField.getText().trim());
        currentUser.setEmail(emailField.getText().trim());
        currentUser.setPhoneNumber(phoneField.getText().trim());
        currentUser.setAddressLine1(address1Field.getText().trim());
        currentUser.setAddressLine2(address2Field.getText().trim());
        currentUser.setCity(cityField.getText().trim());
        currentUser.setState(stateField.getText().trim());
        currentUser.setZip(zipField.getText().trim());
        currentUser.setOllamaApiKey(apiKeyField.getText().trim());
        currentUser.setSummary(summaryArea.getText().trim());
        currentUser.setOnboarded(true);

        boolean saved = profileController.saveUserProfile(currentUser);
        if (saved) {
            statusFeedbackLabel.setTextFill(Color.web("#2ecc71"));
            statusFeedbackLabel.setText("Profile saved successfully!");
        } else {
            statusFeedbackLabel.setTextFill(Color.web("#e74c3c"));
            statusFeedbackLabel.setText("Failed to save profile.");
        }
    }
}
