package edu.stedwards.matt.ui.layouts.application;

import java.util.List;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import edu.stedwards.matt.dao.JobDAO;
import edu.stedwards.matt.dao.TemplateDAO;
import edu.stedwards.matt.models.OnboardingSession;
import edu.stedwards.matt.models.entity.JobPosting;
import edu.stedwards.matt.models.entity.Template;
import edu.stedwards.matt.services.DocumentExportService;
import edu.stedwards.matt.services.TemplateService;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/** Application details and template selection screen. */
public class ApplicationGenerateMaterials extends BorderPane {

   // Promoted to instance variable so your database method can update its text
   private Text company;

   // The saved job-posting record (parsed + confirmed by the upload flow).
   private JobPosting job;
   private Label started;
   private final Label generationStatus = new Label();

   /** Build the generate screen from a saved JobPosting record. */
   public ApplicationGenerateMaterials(JobPosting job) {
      this.job = job;
      System.out.println("[ApplicationGenerateMaterials] Screen opened with " + summarizeJob(job));
      getStyleClass().add("screen-padding");

      Text application = new Text(ApplicationScreenText.APPLICATION_DETAIL_TITLE_PREFIX);
      application.getStyleClass().add("screen-title");

      // Initialize the instance variable
      company = new Text("Company Name");
      company.getStyleClass().addAll("screen-title", "screen-title-split");
      loadJobFromDatabase();
      TextFlow title = TextStyles.buildTitle(application, company);

      started = new Label("Started: " + formatStartedDate(job != null ? job.getDateAdded() : null));
      started.getStyleClass().add("relative-time-label");
      System.out.println("[ApplicationGenerateMaterials] Started label rendered as '" + started.getText() + "'.");
      VBox header = new VBox(4, title, started);
      setTop(header);

      HBox columns = buildColumns();
      VBox content = new VBox(columns);
      content.setFillWidth(true);
      content.setMaxWidth(Double.MAX_VALUE);
      content.setPadding(new Insets(76, 0, 24, 0));
      content.setMinHeight(0);
      VBox.setVgrow(columns, Priority.ALWAYS);
      setCenter(content);
      BorderPane.setMargin(content, new Insets(0, 0, 12, 0));
   }

   /** Build the generate screen using a job posting ID and pull the saved record from the database. */
   public ApplicationGenerateMaterials(int jobId) {
      this.job = null;
      getStyleClass().add("screen-padding");

      Text application = new Text(ApplicationScreenText.APPLICATION_DETAIL_TITLE_PREFIX);
      application.getStyleClass().add("screen-title");

      // Initialize the instance variable
      company = new Text("Company Name");
      company.getStyleClass().addAll("screen-title", "screen-title-split");

      if (jobId > 0) {
         JobDAO jobDAO = new JobDAO();
         JobPosting found = jobDAO.getJobById(jobId);
         this.job = found;
         if (found != null) {
            System.out.println("[ApplicationGenerateMaterials] Loaded job (id=" + jobId + ") from database.");
         } else {
            System.out.println("[ApplicationGenerateMaterials] No job found in database for id=" + jobId);
            // Fallback so the About panel / started label render with empty values instead of crashing
            this.job = new JobPosting();
         }
      } else {
         System.out.println("[ApplicationGenerateMaterials] No job ID provided; screen will show fallback data.");
      }

      loadJobFromDatabase();
      TextFlow title = TextStyles.buildTitle(application, company);

      started = new Label("Started: " + formatStartedDate(job != null ? job.getDateAdded() : null));
      started.getStyleClass().add("relative-time-label");
      System.out.println("[ApplicationGenerateMaterials] Started label rendered as '" + started.getText() + "'.");
      VBox header = new VBox(4, title, started);
      setTop(header);

      HBox columns = buildColumns();
      VBox content = new VBox(columns);
      content.setFillWidth(true);
      content.setMaxWidth(Double.MAX_VALUE);
      content.setPadding(new Insets(76, 0, 24, 0));
      content.setMinHeight(0);
      VBox.setVgrow(columns, Priority.ALWAYS);
      setCenter(content);
      BorderPane.setMargin(content, new Insets(0, 0, 12, 0));
   }

   /** Refresh this screen's job details from the saved database record. */
   public void loadJobFromDatabase() {
      System.out.println("[ApplicationGenerateMaterials] Starting job lookup. " + summarizeJob(job));
      if (job != null) {
         JobDAO jobDAO = new JobDAO();
         JobPosting activeJob = jobDAO.getJobById(job.getJobpostId());
         if (activeJob != null) {
            this.job = activeJob;
            System.out.println("[ApplicationGenerateMaterials] Using database record. " + summarizeJob(activeJob));
         } else {
            System.out.println("[ApplicationGenerateMaterials] Database lookup returned no row; retaining passed record.");
         }
      } else {
         System.out.println("[ApplicationGenerateMaterials] No JobPosting was passed to this screen; database lookup skipped.");
         started = new Label("Started: " + formatStartedDate(null));
         started.getStyleClass().add("relative-time-label");
      }
      company.setText(display(job != null ? job.getCompanyName() : null, "Company Name"));
      if (started != null) started.setText("Started: " + formatStartedDate(job != null ? job.getDateAdded() : null));
      System.out.println("[ApplicationGenerateMaterials] Header values: company='" + company.getText()
            + "', date_added='" + (job != null ? job.getDateAdded() : null)
            + "', started_label='" + (started != null ? started.getText() : "not created yet") + "'.");
   }

   private static String summarizeJob(JobPosting job) {
      if (job == null) return "job=null";
      return "jobpost_id=" + job.getJobpostId() + ", title='" + job.getJobTitle()
            + "', company='" + job.getCompanyName() + "', date_added='" + job.getDateAdded() + "'";
   }

   private static String display(String text, String fallback) {
      return text == null || text.isBlank() ? fallback : text;
   }

   private static String formatStartedDate(String dateAdded) {
      if (dateAdded == null || dateAdded.isBlank()) {
         System.out.println("[ApplicationGenerateMaterials] Cannot format started date: date_added is null or blank.");
         return "Date unavailable";
      }
      try {
         String formatted = LocalDateTime.parse(dateAdded.replace(' ', 'T')).toLocalDate()
               .format(DateTimeFormatter.ofPattern("MMM d, yyyy"));
         System.out.println("[ApplicationGenerateMaterials] Formatted date_added '" + dateAdded + "' as '" + formatted + "'.");
         return formatted;
      } catch (DateTimeParseException ignored) {
         try {
            String formatted = LocalDate.parse(dateAdded).format(DateTimeFormatter.ofPattern("MMM d, yyyy"));
            System.out.println("[ApplicationGenerateMaterials] Formatted date_added '" + dateAdded + "' as '" + formatted + "'.");
            return formatted;
         } catch (DateTimeParseException alsoIgnored) {
            System.out.println("[ApplicationGenerateMaterials] Could not parse date_added='" + dateAdded + "'; displaying it unchanged.");
            return dateAdded;
         }
      }
   }

   /** Send the job, user, and selected template data with a prompt to fill its dynamic fields, then generate the application material. */
   public void handleTemplateSelected(Template template, Button selectedButton) {
      if (template == null || job == null) {
         generationStatus.setText("A saved template and job posting are required.");
         return;
      }
      var signedInUser = OnboardingSession.getInstance().getUserProfile();
      if (signedInUser == null || signedInUser.getUserID() <= 0) {
         generationStatus.setText("Sign in before generating application materials.");
         return;
      }

      JobPosting selectedJob = job;
      generationStatus.setText("Generating your document…");
      selectedButton.setDisable(true);
      Task<Path> task = new Task<>() {
         @Override protected Path call() throws Exception {
            String html = new TemplateService().generateApplicationDocument(template, selectedJob, signedInUser);
            String fileName = display(selectedJob.getCompanyName(), "Application") + " - "
                  + display(selectedJob.getJobTitle(), "Materials");
            return new DocumentExportService().exportAsPDF(html, fileName);
         }
      };
      task.setOnSucceeded(event -> {
         selectedButton.setDisable(false);
         generationStatus.setText("Saved PDF to " + task.getValue());
      });
      task.setOnFailed(event -> {
         selectedButton.setDisable(false);
         Throwable error = task.getException();
         generationStatus.setText("Could not generate document: "
               + (error != null ? error.getMessage() : "unknown error"));
         if (error != null) error.printStackTrace();
      });
      Thread worker = new Thread(task, "application-document-generation");
      worker.setDaemon(true);
      worker.start();
   }

   private HBox buildColumns() {
      System.out.println("[ApplicationGenerateMaterials] Building About the Job panel from " + summarizeJob(job));
      Label aboutHeading = new Label(ApplicationScreenText.ABOUT_THE_JOB_TITLE);
      aboutHeading.getStyleClass().add("about-job-heading");

      // Build the About the Job details panel using the saved job record.
      VBox jobPanel = VisualElements.buildJobDetailsContainer(
            display(job != null ? job.getJobTitle() : null, "Job Title"),
            display(job != null ? job.getLocation() : null, "Location unavailable"),
            display(job != null ? job.getCompanyName() : null, "Company Name"),
            payRange(),
            job != null ? job.getResponsibilities() : "",
            job != null ? job.getRequirements() : "",
            job != null ? job.getNiceToHave() : "",
            job != null ? job.getBenefits() : "");
      jobPanel.setMinWidth(0);
      jobPanel.setMaxWidth(Double.MAX_VALUE);

      VBox about = new VBox(16, aboutHeading, jobPanel);
      about.setMinWidth(0);
      about.setMaxWidth(Double.MAX_VALUE);
      VBox.setVgrow(jobPanel, Priority.ALWAYS);

      Label generateHeading = new Label(ApplicationScreenText.GENERATE_MATERIALS_BUTTON);
      generateHeading.getStyleClass().add("about-job-heading");
      VBox templatePanel = buildTemplatePanel();
      templatePanel.setMinWidth(0);
      templatePanel.setMaxWidth(Double.MAX_VALUE);
      generationStatus.setWrapText(true);
      generationStatus.getStyleClass().add("relative-time-label");
      VBox generate = new VBox(16, generateHeading, templatePanel, generationStatus);
      generate.setMinWidth(0);
      generate.setMaxWidth(Double.MAX_VALUE);
      VBox.setVgrow(templatePanel, Priority.ALWAYS);

      Region columnSpacer = new Region();
      HBox.setHgrow(columnSpacer, Priority.ALWAYS);
      HBox columns = new HBox(about, columnSpacer, generate);
      columns.setAlignment(Pos.TOP_LEFT);
      columns.setFillHeight(true);
      columns.setMaxWidth(Double.MAX_VALUE);

      about.prefWidthProperty().bind(Bindings.max(0, columns.widthProperty().multiply(0.46)));
      generate.prefWidthProperty().bind(Bindings.max(0, columns.widthProperty().multiply(0.46)));
      jobPanel.prefWidthProperty().bind(about.widthProperty());
      templatePanel.prefWidthProperty().bind(generate.widthProperty());
      return columns;
   }

   private static boolean hasText(String value) {
      return value != null && !value.isBlank();
   }

   private String payRange() {
      if (job == null || (job.getPayMin() <= 0 && job.getPayMax() <= 0)) return "Pay not provided";
      String period = job.getPayPeriod() == null || job.getPayPeriod().isBlank() ? "" : "/" + job.getPayPeriod();
      if (job.getPayMin() > 0 && job.getPayMax() > 0) {
         return String.format("$%,.0f%s – $%,.0f%s", job.getPayMin(), period, job.getPayMax(), period);
      }
      double pay = job.getPayMin() > 0 ? job.getPayMin() : job.getPayMax();
      return String.format("$%,.0f%s", pay, period);
   }

   private VBox buildTemplatePanel() {
      FlowPane cards = new FlowPane(14, 14);
      cards.getStyleClass().add("template-card-grid");
      cards.setPadding(Insets.EMPTY);

      int userId = currentUserId();
      List<Template> templates = new TemplateDAO().getTemplatesForUser(userId);

      if (templates.isEmpty()) {
         Button placeholder = Buttons.buildTemplatePickerCard("No templates available", null, 170, 140, "application-template-card");
         cards.getChildren().add(placeholder);
      } else {
         for (Template template : templates) {
            Button btn = Buttons.buildTemplatePickerCard(
                  template.getTemplateName(), null, 170, 140, "application-template-card");
            btn.setOnAction(e -> handleTemplateSelected(template, btn));
            cards.getChildren().add(btn);
         }
      }

      ScrollPane templateScroll = VisualElements.scrollable(cards);
      templateScroll.setFitToWidth(true);
      templateScroll.setFitToHeight(false);
      templateScroll.setMinHeight(0);
      VBox panel = new VBox(templateScroll);
      panel.setPrefHeight(580);
      panel.setMinHeight(0);
      panel.getStyleClass().add("template-picker-container");
      VBox.setVgrow(templateScroll, Priority.ALWAYS);
      return panel;
   }

   private int currentUserId() {
      var profile = OnboardingSession.getInstance().getUserProfile();
      return profile != null ? profile.getUserID() : 0;
   }
}
