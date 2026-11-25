package edu.stedwards.matt.ui.layouts.other;

import edu.stedwards.matt.dao.SettingsDAO;
import edu.stedwards.matt.models.OnboardingSession;
import edu.stedwards.matt.models.entity.User;
import edu.stedwards.matt.services.OllamaService;
import edu.stedwards.matt.services.OllamaUtils;
import edu.stedwards.matt.ui.TextStyles;
import edu.stedwards.matt.ui.interaction.Buttons;
import edu.stedwards.matt.ui.labels.OtherScreenText;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
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
   private final SettingsDAO settingsDAO = new SettingsDAO();
   private final OllamaService ollamaService = new OllamaService();

   private final ComboBox<String> modelCombo = new ComboBox<>();
   private final TextField downloadsField = new TextField();
   private final Text modelDetailsValue = new Text(OtherScreenText.SETTINGS_UNAVAILABLE);
   private final Text capabilitiesValue = new Text(OtherScreenText.SETTINGS_UNAVAILABLE);
   private final Label statusValue = new Label(OtherScreenText.SETTINGS_DISCONNECTED);
   private final Region statusDot = new Region();
   private final Label feedbackLabel = new Label();

   private boolean updatingModelCombo = false;

   public SettingsLayout() {
      getStyleClass().addAll("screen-padding", "settings-screen");
      TextFlow title = TextStyles.buildTitle(OtherScreenText.SETTINGS_TITLE, "settings-title");
      setTop(title);

      VBox content = new VBox(18, buildOllamaSettingsCard(), buildOtherSettingsCard(), buildPopupTestingCard());
      content.getStyleClass().add("settings-content");
      setCenter(content);

      feedbackLabel.getStyleClass().add("settings-instruction");
      Region footerSpacer = new Region();
      HBox.setHgrow(footerSpacer, Priority.ALWAYS);

      Button saveButton = Buttons.button(OtherScreenText.SETTINGS_SAVE_BUTTON, event -> handleSave(),
            Region.USE_COMPUTED_SIZE, "settings-save-button");
      Button logoutButton = Buttons.button(FontAwesomeSolid.SIGN_OUT_ALT,
            OtherScreenText.SETTINGS_LOGOUT_BUTTON, event -> handleLogout(),
            Region.USE_COMPUTED_SIZE, "settings-logout-button");
      logoutButton.setAccessibleText(OtherScreenText.SETTINGS_LOGOUT_BUTTON);
      logoutButton.setContentDisplay(ContentDisplay.RIGHT);

      HBox footer = new HBox(14, feedbackLabel, footerSpacer, saveButton, logoutButton);
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
      modelCombo.setOnAction(event -> {
         if (!updatingModelCombo) {
            updateModelDetails(modelCombo.getValue());
         }
      });

      // Load saved preferences from SQLite and refresh Ollama status/models on entry
      loadSavedSettings();
      refreshSettings();
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

   /** Displays buttons for trying common JOptionPane button combinations. */
   private VBox buildPopupTestingCard() {
      Label heading = new Label("Popup tests");
      heading.getStyleClass().add("settings-status-heading");

      FlowPane buttons = new FlowPane(10, 10);
      buttons.getChildren().addAll(
            popupTestButton("JOptionPane: OK", this::showMessageDialog),
            popupTestButton("JOptionPane: OK / Cancel", () -> showConfirmDialog(JOptionPane.OK_CANCEL_OPTION)),
            popupTestButton("JOptionPane: Yes / No", () -> showConfirmDialog(JOptionPane.YES_NO_OPTION)),
            popupTestButton("JOptionPane: Yes / No / Cancel", () -> showConfirmDialog(JOptionPane.YES_NO_CANCEL_OPTION)));

      VBox card = new VBox(12, heading, buttons);
      card.getStyleClass().add("settings-card");
      return card;
   }

   private Button popupTestButton(String label, Runnable showPopup) {
      return Buttons.button(label, event -> showPopup.run(), Region.USE_COMPUTED_SIZE, "settings-action-button");
   }

   private void showMessageDialog() {
      showSwingPopup(() -> JOptionPane.showMessageDialog(null, OtherScreenText.PLACEHOLDER_MESSAGE,
            "Popup Test", JOptionPane.INFORMATION_MESSAGE));
   }

   private void showConfirmDialog(int optionType) {
      showSwingPopup(() -> JOptionPane.showConfirmDialog(null, OtherScreenText.PLACEHOLDER_MESSAGE,
            "Popup Test", optionType, JOptionPane.QUESTION_MESSAGE));
   }

   /** Runs a modal Swing popup on the Swing thread and restores the JavaFX scene afterward. */
   private void showSwingPopup(Runnable showPopup) {
      Node sceneRoot = getScene() == null ? null : getScene().getRoot();
      if (sceneRoot != null) {
         sceneRoot.setDisable(true);
      }

      SwingUtilities.invokeLater(() -> {
         try {
            showPopup.run();
         } catch (RuntimeException exception) {
            exception.printStackTrace();
         } finally {
            if (sceneRoot != null) {
               Platform.runLater(() -> sceneRoot.setDisable(false));
            }
         }
      });
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
      if (selected != null) {
         downloadsField.setText(selected.getAbsolutePath());
      }
   }

   /** Loads saved download directory and model preference from SQLite for the active user. */
   private void loadSavedSettings() {
      int userId = getSignedInUserId();
      if (userId <= 0) {
         return;
      }
      try {
         String savedPath = settingsDAO.getOllamaDownloadsLocation(userId);
         if (savedPath != null && !savedPath.isBlank()) {
            downloadsField.setText(savedPath);
         }
      } catch (Exception e) {
         setFeedback("Could not load saved settings.", true);
      }
   }

   /** Load installed models and current status from the Ollama service, then refresh the UI. */
   protected void refreshSettings() {
      setFeedback("Checking local Ollama status...", false);
      int userId = getSignedInUserId();
      String currentlySelected = modelCombo.getValue();

      Thread worker = new Thread(() -> {
         String status = "Disconnected";
         List<String> models = List.of();
         String activeRunner = null;
         String savedModel = null;

         try {
            status = ollamaService.getOllamaStatus();
            models = ollamaService.listInstalledModels();
            activeRunner = ollamaService.getCurrentRunner();
            if (userId > 0) {
               savedModel = settingsDAO.getOllamaModelName(userId);
               settingsDAO.updateOllamaStatus(userId, status);
            }
         } catch (Exception e) {
            System.err.println("Error refreshing Ollama settings: " + e.getMessage());
         }

         final String finalStatus = (status == null || status.isBlank()) ? "Disconnected" : status;
         final List<String> finalModels = models != null ? models : List.of();
         final String preferredModel = pickPreferredModel(finalModels, currentlySelected, savedModel, activeRunner);

         Platform.runLater(() -> {
            applyStatusIndicator(finalStatus);

            updatingModelCombo = true;
            try {
               modelCombo.getItems().setAll(finalModels);
               if (finalModels.isEmpty()) {
                  modelCombo.getSelectionModel().clearSelection();
                  modelCombo.setPromptText("No installed Ollama models found");
                  modelDetailsValue.setText(OtherScreenText.SETTINGS_UNAVAILABLE);
                  capabilitiesValue.setText(OtherScreenText.SETTINGS_UNAVAILABLE);
                  setFeedback("Disconnected".equalsIgnoreCase(finalStatus)
                        ? "Ollama is offline or unavailable."
                        : "No installed Ollama models found.", true);
               } else {
                  modelCombo.setPromptText(OtherScreenText.SETTINGS_MODEL_PROMPT);
                  modelCombo.setValue(preferredModel);
                  setFeedback("", false);
               }
            } finally {
               updatingModelCombo = false;
            }

            if (preferredModel != null && !preferredModel.isBlank()) {
               updateModelDetails(preferredModel);
            }
         });
      }, "settings-ollama-refresh");
      worker.setDaemon(true);
      worker.start();
   }

   /** Fetch and display parsed model details for the selected installed model. */
   protected void updateModelDetails(String modelName) {
      if (modelName == null || modelName.isBlank()) {
         modelDetailsValue.setText(OtherScreenText.SETTINGS_UNAVAILABLE);
         capabilitiesValue.setText(OtherScreenText.SETTINGS_UNAVAILABLE);
         return;
      }

      modelDetailsValue.setText("Loading...");
      capabilitiesValue.setText("Loading...");

      Thread worker = new Thread(() -> {
         String rawOutput = "";
         try {
            rawOutput = ollamaService.getModelDetails(modelName);
         } catch (Exception e) {
            System.err.println("Failed to inspect model '" + modelName + "': " + e.getMessage());
         }

         Map<String, String> parsed = OllamaUtils.parseModelDetails(rawOutput);
         String formattedSummary = formatModelSummary(parsed);
         String formattedCapabilities = parsed.getOrDefault("capabilities", OtherScreenText.SETTINGS_UNAVAILABLE);

         Platform.runLater(() -> {
            modelDetailsValue.setText(formattedSummary.isBlank()
                  ? OtherScreenText.SETTINGS_UNAVAILABLE
                  : formattedSummary);
            capabilitiesValue.setText(formattedCapabilities.isBlank()
                  ? OtherScreenText.SETTINGS_UNAVAILABLE
                  : formattedCapabilities);
         });
      }, "settings-model-details");
      worker.setDaemon(true);
      worker.start();
   }

   /** End the signed-in session, perform session cleanup, and navigate to login. */
   protected void handleLogout() {
      closeSession();
      if (getScene() != null) {
         getScene().setRoot(new LoginLayout());
      }
   }

   /** Clear the signed-in user's session state before returning to the login screen. */
   protected void closeSession() {
      OnboardingSession.getInstance().clearSession();
   }

   /** Persist selected model and download directory, then refresh the displayed status. */
   protected void handleSave() {
      int userId = getSignedInUserId();
      if (userId <= 0) {
         setFeedback("No signed-in user found. Unable to save settings.", true);
         return;
      }

      String selectedModel = modelCombo.getValue();
      String downloadsPath = downloadsField.getText() == null ? "" : downloadsField.getText().trim();

      if (!downloadsPath.isEmpty()) {
         Path dir = Path.of(downloadsPath);
         if (Files.exists(dir) && !Files.isDirectory(dir)) {
            setFeedback("Download location must be a folder, not a file.", true);
            return;
         }
      }

      setFeedback("Saving settings...", false);

      Thread worker = new Thread(() -> {
         try {
            if (selectedModel != null && !selectedModel.isBlank()) {
               settingsDAO.setOllamaModelName(userId, selectedModel);
               ollamaService.refreshModelSelection(selectedModel);
            }
            settingsDAO.setOllamaDownloadsLocation(userId, downloadsPath);

            String updatedStatus = ollamaService.getOllamaStatus();
            settingsDAO.updateOllamaStatus(userId, updatedStatus);

            Platform.runLater(() -> {
               applyStatusIndicator(updatedStatus);
               if (selectedModel != null && !selectedModel.isBlank()) {
                  updateModelDetails(selectedModel);
               }
               setFeedback("Settings saved.", false);
            });
         } catch (Exception e) {
            System.err.println("Error saving settings: " + e.getMessage());
            Platform.runLater(() -> setFeedback("Failed to save settings: " + e.getMessage(), true));
         }
      }, "settings-save-thread");
      worker.setDaemon(true);
      worker.start();
   }

   // ==================== Private Helpers ====================

   private int getSignedInUserId() {
      User user = OnboardingSession.getInstance().getUserProfile();
      return user != null ? user.getUserID() : -1;
   }

   private void applyStatusIndicator(String status) {
      statusDot.getStyleClass().removeAll("status-running", "status-idle", "status-disconnected");
      statusValue.getStyleClass().removeAll("status-running", "status-idle", "status-disconnected");

      if ("Running".equalsIgnoreCase(status)) {
         statusValue.setText("Running");
         statusDot.getStyleClass().add("status-running");
         statusValue.getStyleClass().add("status-running");
      } else if ("Idle".equalsIgnoreCase(status)) {
         statusValue.setText("Idle");
         statusDot.getStyleClass().add("status-idle");
         statusValue.getStyleClass().add("status-idle");
      } else {
         statusValue.setText(OtherScreenText.SETTINGS_DISCONNECTED);
         statusDot.getStyleClass().add("status-disconnected");
         statusValue.getStyleClass().add("status-disconnected");
      }
   }

   private static String pickPreferredModel(
         List<String> installed,
         String currentSelection,
         String savedModel,
         String activeRunner) {
      if (installed.isEmpty()) {
         return null;
      }
      if (currentSelection != null && installed.contains(currentSelection)) {
         return currentSelection;
      }
      if (savedModel != null && installed.contains(savedModel)) {
         return savedModel;
      }
      if (activeRunner != null && installed.contains(activeRunner)) {
         return activeRunner;
      }
      return installed.get(0);
   }

   private static String formatModelSummary(Map<String, String> parsed) {
      if (parsed == null || parsed.isEmpty()) {
         return OtherScreenText.SETTINGS_UNAVAILABLE;
      }
      List<String> parts = new ArrayList<>();
      for (Map.Entry<String, String> entry : parsed.entrySet()) {
         if ("capabilities".equalsIgnoreCase(entry.getKey())) {
            continue;
         }
         parts.add(entry.getKey() + ": " + entry.getValue());
      }
      return parts.isEmpty() ? OtherScreenText.SETTINGS_UNAVAILABLE : String.join("\n", parts);
   }

   private void setFeedback(String message, boolean isError) {
      feedbackLabel.setText(message == null ? "" : message);
      feedbackLabel.setStyle(isError ? "-fx-text-fill: #c0392b;" : "");
   }
}