package edu.stedwards.matt.ui.layouts.other;

import edu.stedwards.matt.ui.interaction.Buttons;
import edu.stedwards.matt.ui.interaction.Inputs;
import edu.stedwards.matt.ui.interaction.VisualElements;
import edu.stedwards.matt.ui.labels.LoginScreenText;
import edu.stedwards.matt.ui.layouts.application.ApplicationHome;
import edu.stedwards.matt.ui.layouts.template.TemplatesEditor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import java.util.List;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

/**
 * A scrollable gallery of the reusable UI components in the interaction package.
 */
public class EverythingLayout extends BorderPane {

   public EverythingLayout() {
      getStyleClass().add("screen-padding");

      VBox gallery = new VBox(20);
      gallery.setPadding(new Insets(20));
      gallery.getChildren().addAll(
            section("Buttons", buttonExamples()),
            section("Inputs", inputExamples()),
            section("Visual Elements", visualElementExamples()),
            section("Application and Template Components", appComponentExamples()));

      javafx.scene.control.ScrollPane scrollPane = VisualElements.scrollable(gallery);
      scrollPane.setFitToHeight(false);
      setCenter(scrollPane);
   }

   private VBox buttonExamples() {
      Buttons buttons = new Buttons();
      VBox examples = new VBox(12);
      examples.getChildren().addAll(
            example("Buttons.button(String, EventHandler, double, String...)",
                  Buttons.button("Text Button", null, 180, "sidebar-button")),
            example("Buttons.button(FontAwesomeSolid, String, EventHandler, double, String...)",
                  Buttons.button(FontAwesomeSolid.HOME, "Icon Button", null, 180, "sidebar-button")),
            example("Buttons.button(FontAwesomeSolid, EventHandler, String...)",
                  Buttons.button(FontAwesomeSolid.STAR, null, "sidebar-button")),
            example("Buttons.buildNextButton()", buttons.buildNextButton()),
            example("Buttons.buildUploadButtonGroup(int, int, EventHandler)",
                  buttons.buildUploadButton(240, 8, null)));
      return examples;
   }

   private VBox inputExamples() {
      VBox examples = new VBox(12);
      TextField field = Inputs.inputField("Type something…", 260, 360);
      TextArea textArea = Inputs.textArea("Write a few lines…", 320, 420, 100, 140, ".text-area");
      Label fieldLabel = new Label("Example field");
      fieldLabel.getStyleClass().add("input-label");
      examples.getChildren().addAll(
            example("Inputs.buildInputFieldWIcon(String, FontAwesomeSolid, double, double, String...)",
                  Inputs.buildInputFieldWIcon(LoginScreenText.USERNAME_PLACEHOLDER,
                        LoginScreenText.USERNAME_ICON, 360, 420)),
            example("Inputs.buildPasswordFieldWIcon(String, FontAwesomeSolid, double, double, String...)",
                  Inputs.buildPasswordFieldWIcon(LoginScreenText.PASSWORD_PLACEHOLDER,
                        LoginScreenText.PASSWORD_ICON, 360, 420)),
            example("Inputs.inputField(String, double, double, String...)", field),
            example("Inputs.textArea(String, double, double, double, double, String...)", textArea),
            example("Inputs.buildInputGroup(Label, TextField)", Inputs.buildInputGroup(fieldLabel,
                  Inputs.inputField("Grouped input…", 260, 360))));
      return examples;
   }

   private VBox visualElementExamples() {
      VisualElements visuals = new VisualElements();
      VBox examples = new VBox(12);

      VBox drawerContent = new VBox(new Label("Drawer content preview"));
      VBox infoContent = new VBox(new Label("Information shown inside this container."));
      HBox loadingBar = VisualElements.buildLoadingBar(new ProgressBar(0.65), new Label("65%"));
      HBox experienceFields = new HBox(12,
            Inputs.buildInputGroup(new Label("Job Title"), Inputs.inputField("Job title", 220, 320)),
            Inputs.buildInputGroup(new Label("Company Name"), Inputs.inputField("Company", 220, 320)));
      Label roleDescriptionLabel = new Label("Role Description");
      roleDescriptionLabel.getStyleClass().add("input-label");
      VBox roleDescription = new VBox(8, roleDescriptionLabel,
            Inputs.textArea("What you achieved in this role", 320, Double.MAX_VALUE, 90, 140));
      Button copyButton = Buttons.button(FontAwesomeSolid.COPY, null, "experience-icon-button");
      Button deleteButton = Buttons.button(FontAwesomeSolid.TRASH, null, "experience-icon-button", "experience-delete-button");

      examples.getChildren().addAll(
            example("VisualElements.scrollable(Node)", previewBox(new Label("Scrollable content preview"))),
            example("VisualElements.wizardScrollable(Node)", VisualElements.wizardScrollable(
                  previewBox(new Label("Wizard scroll content preview")))),
            example("VisualElements.infoContainer(double, double, String, int, Node)",
                  visuals.infoContainer(320, 120, "Information", 1, infoContent)),
            example("VisualElements.buildUploadMethodGroup3(double)",
                  VisualElements.buildUploadMethodGroup3(420)),
            example("VisualElements.buildParsedInfoCardEditable(String, List<Node>, List<Button>, String...)",
                  VisualElements.buildParsedInfoCard(
                        "Experience 1", List.of(experienceFields, roleDescription),
                        List.of(copyButton, deleteButton))),
            example("VisualElements.mainSideBar()", visuals.mainSideBar()),
            example("VisualElements.buildCheckBoxWLabel()", VisualElements.buildCheckBoxWLabel()),
            example("VisualElements.collapsableDrawer(String, Node, String, double)",
                  visuals.collapsableDrawer("Drawer preview", drawerContent, "sidebar-menu-items", 320)),
            example("VisualElements.startScreenMattLogo()", VisualElements.startScreenMattLogo()),
            example("VisualElements.buildLoadingBar(ProgressBar, Label)", loadingBar));

      visuals.setSidebarWidth(280);
      Label widthLabel = new Label("Sidebar width: " + visuals.getSidebarWidth() + " px");
      Region widthPreview = new Region();
      widthPreview.setPrefSize(visuals.getSidebarWidth(), 8);
      widthPreview.setMaxWidth(visuals.getSidebarWidth());
      widthPreview.setStyle("-fx-background-color: #668A8C;");
      VBox widthExample = new VBox(6, widthLabel, widthPreview);
      widthExample.setAlignment(Pos.CENTER_LEFT);
      examples.getChildren().add(example("VisualElements.getSidebarWidth() / setSidebarWidth(double)", widthExample));
      return examples;
   }

   /** Builds previews of the application, job detail, template, and file components. */
   private VBox appComponentExamples() {
      VBox examples = new VBox(12);
      TemplatesEditor templateCreator = new TemplatesEditor();
      templateCreator.setPrefSize(640, 420);
      templateCreator.setMaxWidth(Double.MAX_VALUE);
      ApplicationHome applicationHome = new ApplicationHome(
            List.of(
                  new ApplicationHome.ApplicationSummary("Software Engineer", "Acrisure", 2, "h", 2),
                  new ApplicationHome.ApplicationSummary("Summer Analyst", "Google", 5, "d", 2)),
            List.of(
                  new ApplicationHome.ApplicationSummary("Entry Level Engineer", "Oracle", 1, "w", 3),
                  new ApplicationHome.ApplicationSummary("Product Designer", "Example Co.", 1, "w", 0)),
            List.of(
                  new ApplicationHome.ApplicationSummary("Data Analyst", "Acme", 2, "w", 1)),
            null, null);
      applicationHome.setPrefSize(1000, 680);
      applicationHome.setMaxWidth(Double.MAX_VALUE);
      String sampleCopy = "A preview of parsed job text. This section shows how the component wraps longer descriptions while keeping the content readable.";
      examples.getChildren().addAll(
            example("ApplicationHome(List<ApplicationSummary>, ...)", applicationHome),
            example("TemplatesCreator()", templateCreator),
            example("VisualElements.buildLoginBox(EventHandler, EventHandler, double, double, double)",
                  VisualElements.buildLoginBox(null, null, 516, 420, 420)),
            example("VisualElements.buildApplicationListCard(String, String, int, String, int, EventHandler)",
                  VisualElements.buildApplicationListCard("Software Engineer", "Acrisure", 5, "d", 2, null)),
            example("VisualElements.buildJobDetailsContainer(String, String, String, String, String, String, String)",
                  VisualElements.buildJobDetailsContainer("Job Title", "City, ST", "Company Name", "$100,000/yr – $101,000/yr",
                        "– some requirement sentence.\n– another requirement sentence that wraps onto two lines.",
                        "– some nice-to-have experience.\n– another preferred qualification.", "– health and dental coverage.")),
            example("VisualElements.buildTemplatePickerContainer(List<TemplateOption>, double, double)",
                  VisualElements.buildTemplatePickerContainer(List.of(
                        new VisualElements.TemplateOption("Resume Template", null, "template-card-teal"),
                        new VisualElements.TemplateOption("Cover Letter Template", null, "template-card-orange"),
                        new VisualElements.TemplateOption("Cover Letter Template 2", null, "template-card-orange")), 520, 440)),
            example("VisualElements.buildJobDetailsRawTextContainer(String)",
                  VisualElements.buildJobDetailsRawTextContainer((sampleCopy + " ").repeat(12))),
            example("VisualElements.buildSidebarListItem_App(int, String, String, String, EventHandler)",
                  VisualElements.buildSidebarListItem_App(4, "h", "Acrisure, Software Inc.", "Software Engineer", null)),
            example("VisualElements.buildSidebarListItem_Mat(int, String, String, EventHandler)",
                  VisualElements.buildSidebarListItem_Mat(4, "h", "resume - john whitmore.pdf", null)),
            example("VisualElements.buildFileCountBadge(int)", VisualElements.buildFileCountBadge(2)),
            example("VisualElements.buildRelativeTimeLabel(int, String, boolean)",
                  VisualElements.buildRelativeTimeLabel(5, "d", true)),
            example("VisualElements.buildAttachedFileLabel(String)",
                  VisualElements.buildAttachedFileLabel("JiuhhiJKHgkjhG_download_9_00_12.pdf")),
            example("Buttons.buildTemplatePickerCard(String, EventHandler, double, double, String...)",
                  Buttons.buildTemplatePickerCard("Resume Template", null, 225, 185, "template-card-teal")));
      return examples;
   }

   /** Builds the heading in the requested Class.componentName() format. */
   public static TextFlow buildComponentName(String className, String componentName) {
      TextFlow name = new TextFlow(new Text(className + "." + componentName));
      name.getStyleClass().add("component-name");
      return name;
   }

   private VBox section(String title, Node content) {
      Label heading = new Label(title);
      heading.getStyleClass().add("screen-title");
      VBox section = new VBox(12, heading, content);
      section.getStyleClass().add("everything-section");
      return section;
   }

   private VBox example(String componentName, Node component) {
      String[] name = componentName.split("\\.", 2);
      TextFlow label;
      if (name.length == 2) {
         label = buildComponentName(name[0], name[1]);
      } else {
         label = new TextFlow(new Text(componentName));
         label.getStyleClass().add("component-name");
      }
      VBox example = new VBox(8, label, component);
      example.setAlignment(Pos.CENTER_LEFT);
      example.getStyleClass().add("component-example");
      return example;
   }

   private VBox previewBox(Node content) {
      VBox preview = new VBox(content);
      preview.setPadding(new Insets(12));
      preview.setMinHeight(56);
      preview.setStyle("-fx-border-color: #b7c5c6; -fx-border-radius: 4; -fx-background-radius: 4;");
      VBox.setVgrow(preview, Priority.NEVER);
      return preview;
   }
}
