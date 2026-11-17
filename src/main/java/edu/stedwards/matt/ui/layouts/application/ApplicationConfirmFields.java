package edu.stedwards.matt.ui.layouts.application;

import edu.stedwards.matt.models.entity.JobPosting;
import edu.stedwards.matt.services.JobIngestService;
import edu.stedwards.matt.services.JobIngestServiceImpl;
import edu.stedwards.matt.ui.TextStyles;
import edu.stedwards.matt.ui.interaction.Buttons;
import edu.stedwards.matt.ui.interaction.Inputs;
import edu.stedwards.matt.ui.interaction.VisualElements;
import edu.stedwards.matt.ui.labels.ApplicationScreenText;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import javafx.scene.text.TextFlow;

public class ApplicationConfirmFields extends BorderPane {

   // The saved job-posting record being confirmed/edited.
   private final JobPosting job;

   // The receiving service used to persist edits back to the database.
   private final JobIngestService ingestService = new JobIngestServiceImpl();

   // Editable field controls, populated from the saved record.
   private TextField jobTitleField;
   private TextField companyField;
   private TextField locationField;
   private TextField payLowField;
   private TextField payHighField;
   private TextField payPeriodField;
   private TextArea responsibilitiesArea;
   private TextArea requirementsArea;
   private TextArea niceToHaveArea;
   private TextArea benefitsArea;

   private Label statusLabel;

   public ApplicationConfirmFields() {
      
      this(new JobPosting());
   }

   /** Build the confirm-fields screen from a saved JobPosting record (parsed + persisted by the upload flow). */
   public ApplicationConfirmFields(JobPosting job) {

      this.job = job;
      getStyleClass().addAll("screen-padding");
      
      TextFlow title = TextStyles.buildTitle(ApplicationScreenText.NEW_APPLICATION_TITLE, "screen-title");
      setTop(title);
      
      VBox content = new VBox(18, buildOverviewColumns());
      content.setFillWidth(true);
      content.setMaxWidth(Double.MAX_VALUE);
      content.setPadding(new Insets(30, 0, 24, 0));
      var scrollContent = VisualElements.scrollable(content);
      setCenter(scrollContent);
      BorderPane.setMargin(scrollContent, new Insets(0, 0, 24, 0));
      
      statusLabel = new Label();

      var finish = Buttons.button(ApplicationScreenText.BUTTON_FINISH_ICON,
            ApplicationScreenText.FINISH_BUTTON, null, Region.USE_COMPUTED_SIZE, "application-finish-button");
            
      // WIRE UP THE FINISH BUTTON
      finish.setOnAction(e -> saveAndFinish());

      HBox bottom = new HBox(12, finish, statusLabel);
      bottom.setAlignment(Pos.CENTER_RIGHT);
      setBottom(bottom);
   }

   /** Clear stored job-posting and parsed data, then return to Main - Applications - Upload. */
   public void handleTryAgain() {
      // Traverse up to the MainLayout shell and swap the center view to Upload
      edu.stedwards.matt.ui.layouts.other.MainLayout shell = (edu.stedwards.matt.ui.layouts.other.MainLayout) this.getScene().getRoot();
      shell.showStep(new ApplicationUploadJob());
   }

   /** Persist any edits to the job-posting record, then open its generate screen. */
   private void saveAndFinish() {
      // 1. Read edited values back into the job record.
      job.setJobTitle(jobTitleField.getText());
      job.setCompanyName(companyField.getText());
      job.setLocation(locationField.getText());
      try { job.setPayMin(parsePay(payLowField.getText())); } catch (Exception ignored) {}
      try { job.setPayMax(parsePay(payHighField.getText())); } catch (Exception ignored) {}
      job.setPayPeriod(payPeriodField.getText());
      job.setResponsibilities(responsibilitiesArea.getText());
      job.setRequirements(requirementsArea.getText());
      job.setNiceToHave(niceToHaveArea.getText());
      job.setBenefits(benefitsArea.getText());

      // 2. Validate required fields before writing to the database.
      if (job.getJobTitle() == null || job.getJobTitle().isBlank()) {
         statusLabel.setText("Job Title is required before saving.");
         statusLabel.getStyleClass().add("error-text");
         return;
      }

      // 3. Persist off the JavaFX thread so a slow DB call doesn't freeze the window.
      statusLabel.setText("Saving job posting... please wait.");
      statusLabel.getStyleClass().remove("error-text");

      Task<Boolean> task = new Task<Boolean>() {
         @Override protected Boolean call() throws Exception {
            return ingestService.saveJobPosting(job);
         }
      };

      task.setOnSucceeded(e -> {
         statusLabel.setText("");
         if (Boolean.TRUE.equals(task.getValue())) {
            navigateToGenerate(job);
         } else {
            statusLabel.setText("Failed to save changes to the database.");
            statusLabel.getStyleClass().add("error-text");
         }
      });

      task.setOnFailed(e -> {
         statusLabel.setText("Save failed: " + (task.getException() != null ? task.getException().getMessage() : "unknown"));
         statusLabel.getStyleClass().add("error-text");
         System.err.println("Job update task failed: " + (task.getException() != null ? task.getException().getMessage() : "unknown"));
      });

      new Thread(task).start();
   }

   private static double parsePay(String text) {
      if (text == null || text.isBlank()) return 0.0;
      return Double.parseDouble(text.replaceAll("[^\\d.]", ""));
   }

   private void navigateToGenerate(JobPosting job) {
      edu.stedwards.matt.ui.layouts.other.MainLayout shell =
            (edu.stedwards.matt.ui.layouts.other.MainLayout) this.getScene().getRoot();
      shell.showStep(new ApplicationGenerateMaterials(job));
   }

   private HBox buildOverviewColumns() {
      VBox posting = new VBox(12,
            VisualElements.buildJobDetailsRawTextContainer(job.getRawText() != null ? job.getRawText() : ""),
         new Label(job.getSourceFileName() != null ? job.getSourceFileName() : "job-posting"));
            
      var retryBtn = Buttons.button(FontAwesomeSolid.SYNC,
            ApplicationScreenText.TRY_AGAIN_BUTTON, null, Region.USE_COMPUTED_SIZE,
            "application-retry-button");
            
      // WIRE UP THE TRY AGAIN BUTTON
      retryBtn.setOnAction(e -> handleTryAgain());

      HBox retryRow = new HBox(retryBtn);
      retryRow.setAlignment(Pos.CENTER);
      posting.getChildren().add(retryRow);
      posting.setMinWidth(0);
      posting.setMaxWidth(Double.MAX_VALUE);

      VBox parsedFields = new VBox(14);
      Label heading = new Label(ApplicationScreenText.PARSED_INFO_TITLE);
      heading.getStyleClass().add("onboarding-wizard-subtitle");
      Label subtitle = new Label(ApplicationScreenText.PARSED_INFO_MESSAGE);
      subtitle.getStyleClass().add("about-job-heading");
      subtitle.setWrapText(true);
      jobTitleField = new TextField(job.getJobTitle() != null ? job.getJobTitle() : "");
      companyField = new TextField(job.getCompanyName() != null ? job.getCompanyName() : "");
      locationField = new TextField(job.getLocation() != null ? job.getLocation() : "");
      responsibilitiesArea = new TextArea(job.getResponsibilities() != null ? job.getResponsibilities() : "");
      requirementsArea = new TextArea(job.getRequirements() != null ? job.getRequirements() : "");
      niceToHaveArea = new TextArea(job.getNiceToHave() != null ? job.getNiceToHave() : "");
      benefitsArea = new TextArea(job.getBenefits() != null ? job.getBenefits() : "");

      payLowField = new TextField(job.getPayMin() > 0 ? String.valueOf((int) job.getPayMin()) : "");
      payHighField = new TextField(job.getPayMax() > 0 ? String.valueOf((int) job.getPayMax()) : "");
      payPeriodField = new TextField(job.getPayPeriod() != null ? job.getPayPeriod() : "");

      parsedFields.getChildren().addAll(heading, subtitle,
            parsedField("Job Title", jobTitleField),
            parsedField("Company", companyField),
            parsedField("Location", locationField),
            buildPayRange(),
            parsedArea(ApplicationScreenText.PARSED_RESPONSIBILITIES_LABEL, responsibilitiesArea),
            parsedArea(ApplicationScreenText.PARSED_REQUIREMENTS_LABEL, requirementsArea),
            parsedArea("Nice to Have", niceToHaveArea),
            parsedArea("Benefits", benefitsArea));
      parsedFields.setMinWidth(0);
      parsedFields.setMaxWidth(Double.MAX_VALUE);
      parsedFields.setAlignment(Pos.TOP_LEFT);

      HBox columns = new HBox(28, posting, parsedFields);
      columns.setAlignment(Pos.TOP_LEFT);
      columns.setFillHeight(true);
      columns.setMaxWidth(Double.MAX_VALUE);
      posting.prefWidthProperty().bind(Bindings.max(0, columns.widthProperty().subtract(28).multiply(0.40)));
      parsedFields.prefWidthProperty().bind(Bindings.max(0, columns.widthProperty().subtract(28).multiply(0.50)));
      columns.setAlignment(Pos.TOP_CENTER);
      return columns;
   }

   private VBox parsedField(String labelText, TextField field) {
      field.setMaxWidth(Double.MAX_VALUE);
      VBox group = Inputs.buildInputGroup(TextStyles.buildLabel(labelText, "input-label"), field);
      HBox.setHgrow(field, Priority.ALWAYS);
      group.setMaxWidth(Double.MAX_VALUE);
      return group;
   }

   private VBox parsedArea(String labelText, TextArea area) {
      area.setWrapText(true);
      area.setMaxWidth(Double.MAX_VALUE);
      VBox group = Inputs.buildInputGroup(TextStyles.buildLabel(labelText, "input-label"), area);
      HBox.setHgrow(area, Priority.ALWAYS);
      group.setMaxWidth(Double.MAX_VALUE);
      return group;
   }

   private HBox buildPayRange() {
      VBox low = parsedPayField(ApplicationScreenText.PARSED_PAY_RANGE_LOW_LABEL, payLowField);
      VBox high = parsedPayField(ApplicationScreenText.PARSED_PAY_RANGE_HIGH_LABEL, payHighField);
      HBox.setHgrow(low, Priority.ALWAYS);
      HBox.setHgrow(high, Priority.ALWAYS);
      HBox row = new HBox(18, low, high);
      row.setAlignment(Pos.BOTTOM_LEFT);
      row.setFillHeight(true);
      row.setMaxWidth(Double.MAX_VALUE);
      return row;
   }

   private VBox parsedPayField(String labelText, TextField field) {
      field.setMaxWidth(Double.MAX_VALUE);
      VBox fieldWithSuffix = Inputs.wrapInputGroupWithSuffix(field, "yr");
      fieldWithSuffix.getStyleClass().add("input-field-spacing");
      VBox group = new VBox(8, TextStyles.buildLabel(labelText, "input-label"), fieldWithSuffix);
      group.setMaxWidth(Double.MAX_VALUE);
      HBox.setHgrow(field, Priority.ALWAYS);
      return group;
   }
}
