package edu.stedwards.matt.ui.layouts.onboarding;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import edu.stedwards.matt.ui.interaction.Buttons;
import edu.stedwards.matt.ui.interaction.Inputs;
import edu.stedwards.matt.ui.labels.OnboardingScreenText;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class ResumeUploadStep extends BorderPane {
   private Path selectedResume;
   private final TextField apiKeyField = Inputs.textField(
         "XXXXX - XXXXX - XXXXX - XXXXX", 200, "resume-upload-api-key");

   private TextFlow buildScreenTitle() {
      Text titleText = new Text(OnboardingScreenText.UPLOAD_TITLE);
      titleText.getStyleClass().add("welcome-title-prefix");

      TextFlow title = new TextFlow(titleText);
      title.getStyleClass().add("welcome-title");
      title.setTextAlignment(TextAlignment.CENTER);
      return title;
   }

   private Label buildInstruction(String text) {
      Label instruction = new Label(text);
      instruction.getStyleClass().add("resume-upload-instruction");
      instruction.setWrapText(true);
      instruction.setMaxWidth(Double.MAX_VALUE);
      instruction.setAlignment(Pos.CENTER_LEFT);
      return instruction;
   }

   private void chooseResume(ActionEvent event) {
      FileChooser chooser = new FileChooser();
      chooser.setTitle("Choose your résumé");
      chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
            "Supported résumé files", "*.docx", "*.pdf", "*.txt", "*.rtf"));

      Window owner = getScene() == null ? null : getScene().getWindow();
      File file = chooser.showOpenDialog(owner);
      if (file == null) {
         return;
      }

      selectedResume = file.toPath();
      Button uploadButton = (Button) event.getSource();
      uploadButton.setText(file.getName());
   }

   private VBox buildContent() {
      HBox uploadSection = new Buttons()
            .buildUploadButtonGroup(576, 8, this::chooseResume);
      VBox content = new VBox(
            30,
            buildInstruction(OnboardingScreenText.UPLOAD_MESSAGE_1),
            uploadSection,
            buildInstruction(OnboardingScreenText.UPLOAD_MESSAGE_2),
            apiKeyField);
      content.setAlignment(Pos.CENTER);
      content.setMaxWidth(Double.MAX_VALUE);
      return content;
   }

   public Optional<Path> getSelectedResume() {
      return Optional.ofNullable(selectedResume);
   }

   public String getOllamaApiKey() {
      return apiKeyField.getText();
   }

   public ResumeUploadStep() {
      getStyleClass().addAll("app-onboarding", "welcome-screen", "resume-upload-screen");

      TextFlow title = buildScreenTitle();
      setTop(title);
      BorderPane.setMargin(title, new Insets(0, 0, 48, 0));

      VBox content = buildContent();
      setCenter(content);
      BorderPane.setMargin(content, new Insets(0, 0, 12, 0));

      setBottom(new Buttons().buildNextButton());
   }
}
