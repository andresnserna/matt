package edu.stedwards.matt.ui.layouts.other;

import edu.stedwards.matt.ui.TextStyles;
import edu.stedwards.matt.ui.interaction.Buttons;
import edu.stedwards.matt.ui.labels.OtherScreenText;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

/** Settings screen for local model selection and download preferences. */
public class SettingsLayout extends BorderPane {
   private final ComboBox<String> modelCombo = new ComboBox<>();
   private final TextField downloadsField = new TextField();
   private final Text modelDetailsValue = new Text(OtherScreenText.SETTINGS_UNAVAILABLE);
   private final Text capabilitiesValue = new Text(OtherScreenText.SETTINGS_UNAVAILABLE);
   private final Label statusValue = new Label(OtherScreenText.SETTINGS_DISCONNECTED);
   private final Region statusDot = new Region();

   public SettingsLayout() {
      getStyleClass().addAll("screen-padding", "settings-screen");
      TextFlow title = TextStyles.buildTitle(OtherScreenText.SETTINGS_TITLE, "settings-title");
      setTop(title);
      // BorderPane.setMargin(title, new Insets(22, 0, 22, 0));

      VBox content = new VBox(18, buildOllamaSettingsCard(), buildOtherSettingsCard(), buildPopupTestingCard());
      content.getStyleClass().add("settings-content");
      setCenter(content);

      Button saveButton = Buttons.button(OtherScreenText.SETTINGS_SAVE_BUTTON, event -> handleSave(),
            Region.USE_COMPUTED_SIZE, "settings-save-button");
      Button logoutButton = Buttons.button(FontAwesomeSolid.SIGN_OUT_ALT,
            OtherScreenText.SETTINGS_LOGOUT_BUTTON, event -> handleLogout(),
            Region.USE_COMPUTED_SIZE, "settings-logout-button");
      logoutButton.setAccessibleText(OtherScreenText.SETTINGS_LOGOUT_BUTTON);
      logoutButton.setContentDisplay(ContentDisplay.RIGHT);

      HBox footer = new HBox(14, saveButton, logoutButton);
      footer.setAlignment(Pos.CENTER_RIGHT);
      footer.getStyleClass().add("settings-footer");
      setBottom(footer);
      BorderPane.setMargin(footer, new Insets(22, 0, 0, 0));

      statusDot.getStyleClass().addAll("settings-status-dot", "status-disconnected");
      statusValue.getStyleClass().addAll("settings-status-text", "status-disconnected");
      modelDetailsValue.getStyleClass().add("settings-detail-value");
      capabilitiesValue.getStyleClass().add("settings-detail-value");
      modelCombo.setPromptText(OtherScreenText.SETTINGS_MODEL_PROMPT);
      modelCombo.setMaxWidth(Double.MAX_VALUE);
      modelCombo.setOnAction(event -> updateModelDetails(modelCombo.getValue()));
   }

   /** Builds the Ollama status, installed model picker, and selected model details card. */
   public VBox buildOllamaSettingsCard() {
      Label statusLabel = new Label(OtherScreenText.SETTINGS_OLLAMA_STATUS_LABEL);
      statusLabel.getStyleClass().add("settings-status-heading");
      HBox status = new HBox(9, statusLabel, statusValue, statusDot);
      status.setAlignment(Pos.CENTER_LEFT);
      Region spacer = new Region();
      HBox.setHgrow(spacer, Priority.ALWAYS);
      Button refreshButton = Buttons.button(OtherScreenText.SETTINGS_REFRESH_BUTTON,
            event -> refreshSettings(), Region.USE_COMPUTED_SIZE, "settings-action-button");
      HBox statusRow = new HBox(status, spacer, refreshButton);
      statusRow.setAlignment(Pos.CENTER_LEFT);

      Label instruction = new Label(OtherScreenText.SETTINGS_MODEL_PICKER_INSTRUCTION);
      instruction.setWrapText(true);
      instruction.getStyleClass().add("settings-instruction");
      modelCombo.getStyleClass().add("settings-model-picker");

      HBox modelRow = new HBox(10,
            buildDetailLabel(OtherScreenText.SETTINGS_MODEL_LABEL),
            buildDetailValue(modelDetailsValue),
            buildDetailLabel(OtherScreenText.SETTINGS_CAPABILITIES_LABEL),
            buildDetailValue(capabilitiesValue));
      modelRow.setAlignment(Pos.TOP_LEFT);
      modelRow.getStyleClass().add("settings-details-row");

      VBox card = new VBox(12, statusRow, instruction, modelCombo, modelRow);
      card.getStyleClass().add("settings-card");
      return card;
   }

   /** Builds the download directory preference card. */
   public HBox buildOtherSettingsCard() {
      Label label = new Label(OtherScreenText.SETTINGS_DOWNLOADS_LABEL);
      label.getStyleClass().add("settings-downloads-label");
      downloadsField.setPromptText(OtherScreenText.SETTINGS_DOWNLOADS_PROMPT);
      downloadsField.getStyleClass().add("settings-downloads-field");
      HBox.setHgrow(downloadsField, Priority.ALWAYS);
      Button selectButton = Buttons.button(OtherScreenText.SETTINGS_SELECT_BUTTON,
            event -> chooseDownloadsDirectory(), Region.USE_COMPUTED_SIZE, "settings-action-button");
      HBox card = new HBox(12, label, downloadsField, selectButton);
      card.setAlignment(Pos.CENTER_LEFT);
      card.getStyleClass().add("settings-card");
      return card;
   }

   /** Displays buttons for trying common JavaFX alert button combinations. */
   private VBox buildPopupTestingCard() {
      Label heading = new Label("Popup tests");
      heading.getStyleClass().add("settings-status-heading");

      FlowPane buttons = new FlowPane(10, 10);
      buttons.getChildren().addAll(
            popupTestButton("Alert: OK", Alert.AlertType.INFORMATION, ButtonType.OK),
            popupTestButton("Alert: OK / Cancel", Alert.AlertType.CONFIRMATION,
                  ButtonType.OK, ButtonType.CANCEL),
            popupTestButton("Alert: Yes / No", Alert.AlertType.CONFIRMATION,
                  ButtonType.YES, ButtonType.NO),
            popupTestButton("Alert: Yes / No / Cancel", Alert.AlertType.CONFIRMATION,
                  ButtonType.YES, ButtonType.NO, ButtonType.CANCEL));

      VBox card = new VBox(12, heading, buttons);
      card.getStyleClass().add("settings-card");
      return card;
   }

   private Button popupTestButton(String label, Alert.AlertType alertType, ButtonType... buttonTypes) {
      Button button = Buttons.button(label,
            event -> showPopupTest(alertType, buttonTypes), Region.USE_COMPUTED_SIZE, "settings-action-button");
      return button;
   }

   private void showPopupTest(Alert.AlertType alertType, ButtonType... buttonTypes) {
      Alert alert = new Alert(alertType);
      alert.setTitle("Popup Test");
      alert.setHeaderText("Placeholder alert");
      alert.setContentText(OtherScreenText.PLACEHOLDER_MESSAGE);
      alert.getButtonTypes().setAll(buttonTypes);
      if (getScene() != null && getScene().getWindow() != null) {
         alert.initOwner(getScene().getWindow());
      }
      alert.showAndWait();
   }

   private Label buildDetailLabel(String labelText) {
      Label label = new Label(labelText);
      label.getStyleClass().add("settings-detail-label");
      label.setMinWidth(Region.USE_PREF_SIZE);
      return label;
   }

   private VBox buildDetailValue(Text value) {
      TextFlow valueFlow = new TextFlow(value);
      valueFlow.getStyleClass().add("settings-detail-flow");
      VBox valueColumn = new VBox(valueFlow);
      valueColumn.getStyleClass().add("settings-detail");
      HBox.setHgrow(valueColumn, Priority.ALWAYS);
      return valueColumn;
   }

   /** Opens a directory chooser and displays the selected path for later saving. */
   private void chooseDownloadsDirectory() {
      DirectoryChooser chooser = new DirectoryChooser();
      chooser.setTitle(OtherScreenText.SETTINGS_DIRECTORY_CHOOSER_TITLE);
      java.io.File selected = chooser.showDialog(getScene() == null ? null : getScene().getWindow());
      if (selected != null) downloadsField.setText(selected.getAbsolutePath());
   }

   /** TODO: Load installed models and current status from the Ollama service, then refresh the UI. */
   protected void refreshSettings() { }

   /** TODO: Fetch and display parsed model details for the selected installed model. */
   protected void updateModelDetails(String modelName) { }

   /** TODO: End the signed-in session, perform session cleanup, and navigate to login. */
   protected void handleLogout() { closeSession(); }

   /** TODO: Clear the signed-in user's session state before returning to the login screen. */
   protected void closeSession() { }

   /** TODO: Persist selected model and download directory, then refresh the displayed status. */
   protected void handleSave() { }
}
