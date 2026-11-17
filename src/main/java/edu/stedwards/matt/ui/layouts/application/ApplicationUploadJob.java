package edu.stedwards.matt.ui.layouts.application;

import java.io.File;
import java.util.function.Consumer;

import edu.stedwards.matt.models.OnboardingSession;
import edu.stedwards.matt.models.entity.JobPosting;
import edu.stedwards.matt.services.JobIngestService;
import edu.stedwards.matt.services.JobIngestServiceImpl;
import edu.stedwards.matt.ui.TextStyles;
import edu.stedwards.matt.ui.interaction.Buttons;
import edu.stedwards.matt.ui.interaction.VisualElements;
import edu.stedwards.matt.ui.labels.ApplicationScreenText;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;

public class ApplicationUploadJob extends BorderPane {

   // State variables to hold the user's input before they hit "Next"
   private File selectedJobFile;
   private String enteredJobUrl;
   private String enteredJobText;
   
   // The receiving service: takes file / URL / pasted text, routes each to the
   // Ollama-backed parser, persists the parsed JobPosting, and returns the saved record.
   private final JobIngestService ingestService = new JobIngestServiceImpl();

   // Status label shown while the (potentially slow) Ollama parse runs off the FX thread.
   private Label statusLabel;

   // The clean architectural callback for navigation
   private final Consumer<JobPosting> onNext;

   // Default constructor for Matt.java preview compatibility
   public ApplicationUploadJob() {

      this(job -> {
          // Fallback navigation if no callback is provided
          edu.stedwards.matt.ui.layouts.other.MainLayout shell = 
              (edu.stedwards.matt.ui.layouts.other.MainLayout) javafx.stage.Window.getWindows().get(0).getScene().getRoot();
          shell.showStep(new ApplicationConfirmFields());
      });
   }

   // The MVC constructor that takes the navigation logic from the outside
   public ApplicationUploadJob(Consumer<JobPosting> onNext) {
      
      this.onNext = onNext;
      getStyleClass().addAll("screen-padding");
      
      TextFlow title = TextStyles.buildTitle(ApplicationScreenText.NEW_APPLICATION_TITLE, "screen-title");
      setTop(title);
      
      Label instruction = new Label(ApplicationScreenText.NEW_APPLICATION_UPLOAD_MESSAGE);
      instruction.getStyleClass().add("onboarding-wizard-subtitle");
      instruction.setWrapText(true);
      instruction.setMaxWidth(Double.MAX_VALUE);

      VBox uploadOptions = VisualElements.buildUploadMethodGroup3(800,
            e -> handleUpload(),
            url -> handleJobUrlEntry(url),
            text -> handleJobPostTextEntry(text));
      uploadOptions.prefWidthProperty().bind(
            Bindings.min(800, Bindings.max(0, widthProperty().subtract(100))));
      uploadOptions.maxWidthProperty().bind(uploadOptions.prefWidthProperty());
      VBox content = new VBox(44, instruction, uploadOptions);
      content.setFillWidth(true);
      content.setMaxWidth(Double.MAX_VALUE);
      content.setPadding(new Insets(105, 0, 24, 0));

      var scrollContent = VisualElements.wizardScrollable(content);
      setCenter(scrollContent);
      BorderPane.setMargin(scrollContent, new Insets(0, 0, 24, 0));
      
      // Wire up the Next button plus a status label for the async parse.
      statusLabel = new Label();

      HBox nextBtn = new Buttons().buildNextButton();
      Button nextButton = extractButton(nextBtn);
      if (nextButton != null) {
         nextButton.setOnAction(e -> handleNext());
      }

      HBox bottom = new HBox(12, nextBtn, statusLabel);
      bottom.setAlignment(Pos.CENTER_RIGHT);
      setBottom(bottom);
   }

   /** Open the file chooser, validate the selected file type, and prepare the file for parsing. */
   public void handleUpload() {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Upload Job Description");
      fileChooser.getExtensionFilters().addAll(
          new FileChooser.ExtensionFilter("PDF Documents", "*.pdf"),
          new FileChooser.ExtensionFilter("Word Documents", "*.doc", "*.docx"),
          new FileChooser.ExtensionFilter("Text Files", "*.txt")
      );
      
      File file = fileChooser.showOpenDialog(this.getScene().getWindow());
      if (file != null) {
          this.selectedJobFile = file;
          System.out.println("File selected for parsing: " + file.getName());
          // Note: You will need to wire this method to the actual button inside VisualElements.buildUploadMethodGroup3
      }
   }

   /** Retain the entered job URL and send it for parsing only when Next is pressed. */
   public void handleJobUrlEntry(String url) {
      this.enteredJobUrl = url;
      System.out.println("URL temporarily stored.");
   }

   /** Retain the entered job-post text and send it for parsing only when Next is pressed. */
   public void handleJobPostTextEntry(String text) {
      this.enteredJobText = text;
      System.out.println("Text temporarily stored.");
   }

   /** Send the selected job posting to the parser (off the JavaFX thread) and, on success,
    *  navigate to Main - Applications - Confirm Fields with the saved record. */
   public void handleNext() {
      // 1. Determine which input method the user used.
      String source;
      String payload;
      if (selectedJobFile != null) {
          source = "file";
          payload = selectedJobFile.getAbsolutePath();
      } else if (enteredJobUrl != null && !enteredJobUrl.isEmpty()) {
          source = "url";
          payload = enteredJobUrl;
      } else if (enteredJobText != null && !enteredJobText.isEmpty()) {
          source = "text";
          payload = enteredJobText;
      } else {
          statusLabel.setText("No job data provided. Please upload or paste a job description.");
          statusLabel.getStyleClass().add("error-text");
          return; // Prevent navigation if they haven't provided anything
      }

      int userId = currentUserId();

      // 2. Run the blocking Ollama parse + DB save off the JavaFX thread so the window
      //    does not freeze while waiting on the AI.
      statusLabel.setText("Parsing job description with Ollama... please wait.");
      statusLabel.getStyleClass().remove("error-text");
      setNextButtonDisabled(true);

      Task<JobPosting> task = new Task<JobPosting>() {
         @Override protected JobPosting call() throws Exception {
            switch (source) {
               case "file": return ingestService.parseJobFromFile(userId, payload);
               case "url":  return ingestService.parseJobFromUrl(userId, payload);
               default:     return ingestService.parseJobFromText(userId, payload);
            }
         }
      };

      task.setOnSucceeded(e -> {
         JobPosting job = task.getValue();
         setNextButtonDisabled(false);
         statusLabel.setText("");
         if (job != null) {
            System.out.println("Parsed job (unsaved). Navigating to Confirm Fields for review.");
            onNext.accept(job);
         } else {
            statusLabel.setText("Failed to parse and save the job posting.");
            statusLabel.getStyleClass().add("error-text");
         }
      });

      task.setOnFailed(e -> {
         setNextButtonDisabled(false);
         String msg = task.getException() != null ? task.getException().getMessage() : "unknown error";
         statusLabel.setText("Parse failed: " + msg);
         statusLabel.getStyleClass().add("error-text");
         System.err.println("Job ingest task failed: " + msg);
      });

      new Thread(task).start();
   }

   private int currentUserId() {
      var profile = OnboardingSession.getInstance().getUserProfile();
      return profile != null ? profile.getUserID() : 0;
   }

   private Button extractButton(HBox container) {
      if (container == null || container.getChildren().isEmpty()) return null;
      return container.getChildren().stream()
            .filter(Button.class::isInstance)
            .map(Button.class::cast)
            .findFirst()
            .orElse(null);
   }

   private void setNextButtonDisabled(boolean disabled) {
      HBox bottom = (HBox) getBottom();
      if (bottom == null) return;
      for (var node : bottom.getChildren()) {
         if (node instanceof Button btn) {
            btn.setDisable(disabled);
         }
      }
   }
}
