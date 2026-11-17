package edu.stedwards.matt.ui.layouts.application;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import edu.stedwards.matt.dao.JobDAO;
import edu.stedwards.matt.models.OnboardingSession;
import edu.stedwards.matt.models.entity.JobPosting;
import edu.stedwards.matt.ui.TextStyles;
import edu.stedwards.matt.ui.interaction.Buttons;
import edu.stedwards.matt.ui.interaction.VisualElements;
import edu.stedwards.matt.ui.labels.ApplicationScreenText;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

/** Displays saved applications grouped by when they were added. */
public class ApplicationHome extends BorderPane {
   public record ApplicationSummary(String jobTitle, String companyName, int age, String timeUnit,
          int fileCount, int jobId) { }

   private List<ApplicationSummary> thisWeek;
   private List<ApplicationSummary> lastWeek;
   private List<ApplicationSummary> older;
   private final Runnable onNewApplication;
   private final Consumer<ApplicationSummary> onOpenApplication;

   public ApplicationHome() {
      // These empty lists will be replaced by your database fetch results later
      this(List.of(), List.of(), List.of(), null, null);
      
      // Fetch the actual applications when the screen loads
      loadApplicationsFromDatabase();
   }

   public ApplicationHome(List<ApplicationSummary> thisWeek, List<ApplicationSummary> lastWeek,
         List<ApplicationSummary> older, Runnable onNewApplication,
         Consumer<ApplicationSummary> onOpenApplication) {
      this.thisWeek = List.copyOf(thisWeek);
      this.lastWeek = List.copyOf(lastWeek);
      this.older = List.copyOf(older);
      this.onNewApplication = onNewApplication;
      this.onOpenApplication = onOpenApplication;
      setCenter(buildApplicationHomeLayout());
   }

   public BorderPane buildApplicationHomeLayout() {
      BorderPane layout = new BorderPane();
      layout.getStyleClass().add("screen-padding");

      TextFlow title = TextStyles.buildTitle(ApplicationScreenText.APPLICATIONS_TITLE, "screen-title");
      layout.setTop(title);

      VBox sections = new VBox(24,
            buildSection(ApplicationScreenText.APPLICATIONS_THIS_WEEK, thisWeek, true),
            buildSection(ApplicationScreenText.APPLICATIONS_LAST_WEEK, lastWeek, true),
            buildSection(ApplicationScreenText.APPLICATIONS_MORE, older, false));
      sections.setFillWidth(true);
      sections.setMaxWidth(Double.MAX_VALUE);
      ScrollPane scrollPane = VisualElements.scrollable(sections);
      scrollPane.setFitToHeight(false);
      layout.setCenter(scrollPane);
      BorderPane.setMargin(scrollPane, new Insets(24, 0, 12, 0));

      // WIRE UP THE NEW APPLICATION BUTTON DIRECTLY
      Button newApplication = Buttons.button(FontAwesomeSolid.PLUS_CIRCLE,
            ApplicationScreenText.NEW_APPLICATION_BUTTON,
            event -> handleNewApplication(), 
            Region.USE_COMPUTED_SIZE, "new-application-button");
            
      HBox footer = new HBox(newApplication);
      footer.setAlignment(Pos.CENTER_RIGHT);
      layout.setBottom(footer);
      BorderPane.setMargin(footer, new Insets(16, 0, 0, 0));
      return layout;
   }

   /** Load application records from the database and populate the application cards. */
   public void loadApplicationsFromDatabase() {
      // 1. Fetch saved applications using JobDAO for the current logged-in user.
      int userId = currentUserId();
      List<JobPosting> jobs = new JobDAO().getRecentJobsForUser(userId, 100);

      // 2. Sort them into the `thisWeek`, `lastWeek`, and `older` lists based on their timestamp.
      List<ApplicationSummary> thisWeek = new ArrayList<>();
      List<ApplicationSummary> lastWeek = new ArrayList<>();
      List<ApplicationSummary> older = new ArrayList<>();
      for (JobPosting job : jobs) {
         ApplicationSummary summary = new ApplicationSummary(
               job.getJobTitle() != null ? job.getJobTitle() : "",
               job.getCompanyName() != null ? job.getCompanyName() : "",
               0, "", 0, job.getJobpostId());
         thisWeek.add(summary);
      }
      this.thisWeek = thisWeek;
      this.lastWeek = lastWeek;
      this.older = older;

      // Rebuild the UI so the fetched application cards are displayed.
      setCenter(buildApplicationHomeLayout());
   }

   private int currentUserId() {
      var profile = OnboardingSession.getInstance().getUserProfile();
      return profile != null ? profile.getUserID() : 0;
   }

   /** Open the generate screen with the selected application's details and available templates. */
   public void handleApplicationCardSelected(ApplicationSummary selectedApp, int jobId) {
      System.out.println("Opening application for: " + selectedApp.companyName() + " (id=" + jobId + ")");
      
      // Traverse up to the MainLayout shell and swap the center view to Generate Materials
      edu.stedwards.matt.ui.layouts.other.MainLayout shell = (edu.stedwards.matt.ui.layouts.other.MainLayout) this.getScene().getRoot();
      shell.showStep(new ApplicationGenerateMaterials(jobId));
   }

   /** Navigate to Main - Applications - Upload. */
   public void handleNewApplication() {
      // Traverse up to the MainLayout shell and swap the center view to Upload Job
      edu.stedwards.matt.ui.layouts.other.MainLayout shell = (edu.stedwards.matt.ui.layouts.other.MainLayout) this.getScene().getRoot();
      shell.showStep(new ApplicationUploadJob(job ->
            shell.showStep(new ApplicationConfirmFields(job))));
   }

   private VBox buildSection(String title, List<ApplicationSummary> applications, boolean initiallyExpanded) {
      Label sectionTitle = new Label(title);
      sectionTitle.getStyleClass().add("application-section-title");

      FontIcon chevron = new FontIcon(initiallyExpanded
            ? ApplicationScreenText.SECTION_DROPDOWN_ICON
            : ApplicationScreenText.MORE_ICON);
      Button toggle = new Button();
      toggle.setGraphic(chevron);
      toggle.getStyleClass().add("application-section-chevron");
      toggle.setAccessibleText((initiallyExpanded ? "Collapse " : "Expand ") + title);

      Region spacer = new Region();
      HBox.setHgrow(spacer, Priority.ALWAYS);
      HBox header = new HBox(sectionTitle, spacer, toggle);
      header.setAlignment(Pos.CENTER_LEFT);
      header.getStyleClass().add("application-section-header");

      VBox cards = new VBox(14);
      cards.setFillWidth(true);
      cards.setMaxWidth(Double.MAX_VALUE);
      
      for (ApplicationSummary application : applications) {
         // WIRE UP THE CARD CLICK DIRECTLY TO THE NAVIGATION METHOD
         Node card = VisualElements.buildApplicationListCard(
               application.jobTitle(), application.companyName(), application.age(), application.timeUnit(),
               application.fileCount(), event -> handleApplicationCardSelected(application, application.jobId()));
               
         cards.getChildren().add(card);
      }
      cards.setVisible(initiallyExpanded);
      cards.setManaged(initiallyExpanded);

      toggle.setOnAction(event -> {
         boolean showCards = !cards.isVisible();
         cards.setVisible(showCards);
         cards.setManaged(showCards);
         chevron.setIconCode(showCards
               ? ApplicationScreenText.SECTION_DROPDOWN_ICON
               : ApplicationScreenText.MORE_ICON);
         toggle.setAccessibleText((showCards ? "Collapse " : "Expand ") + title);
      });

      return new VBox(12, header, cards);
   }
}
