package edu.stedwards.matt.ui.layouts.application;

import java.io.File;

import edu.stedwards.matt.ui.TextStyles;
import edu.stedwards.matt.ui.interaction.Buttons;
import edu.stedwards.matt.ui.interaction.VisualElements;
import edu.stedwards.matt.ui.labels.ApplicationScreenText;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
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
   
   // The clean architectural callback for navigation
   private final Runnable onNext;

   // Default constructor for Matt.java preview compatibility
   public ApplicationUploadJob() {
      this(() -> {
          // Fallback navigation if no callback is provided
          edu.stedwards.matt.ui.layouts.other.MainLayout shell = 
              (edu.stedwards.matt.ui.layouts.other.MainLayout) javafx.stage.Window.getWindows().get(0).getScene().getRoot();
          shell.showStep(new ApplicationConfirmFields());
      });
   }

   // The MVC constructor that takes the navigation logic from the outside
   public ApplicationUploadJob(Runnable onNext) {
      this.onNext = onNext;
      getStyleClass().addAll("screen-padding");
      
      TextFlow title = TextStyles.buildTitle(ApplicationScreenText.NEW_APPLICATION_TITLE, "screen-title");
      setTop(title);
      
      Label instruction = new Label(ApplicationScreenText.NEW_APPLICATION_UPLOAD_MESSAGE);
      instruction.getStyleClass().add("onboarding-wizard-subtitle");
      instruction.setWrapText(true);
      instruction.setMaxWidth(Double.MAX_VALUE);

      VBox uploadOptions = VisualElements.buildUploadMethodGroup3(800);
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
      
      // Wire up the Next button to our handleNext method
    HBox nextBtn = new Buttons().buildNextButton();
    nextBtn.setOnMouseClicked(e -> handleNext());
      setBottom(nextBtn);
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

   /** Send the selected job posting to the parser and navigate to Main - Applications - Confirm Fields with parsed data. */
   public void handleNext() {
      // 1. Determine which input method the user used and pass it to your parser session
      if (selectedJobFile != null) {
          System.out.println("Sending file to parser...");
      } else if (enteredJobUrl != null && !enteredJobUrl.isEmpty()) {
          System.out.println("Sending URL to parser...");
      } else if (enteredJobText != null && !enteredJobText.isEmpty()) {
          System.out.println("Sending text to parser...");
      } else {
          System.out.println("No job data provided. Please upload or paste a job description.");
          return; // Prevent navigation if they haven't provided anything
      }
      
      // 2. Fire the clean navigation callback
      if (onNext != null) {
          onNext.run();
      }
   }
}