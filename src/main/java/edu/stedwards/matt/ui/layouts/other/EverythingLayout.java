package edu.stedwards.matt.ui.layouts.other;

import edu.stedwards.matt.ui.interaction.Buttons;
import edu.stedwards.matt.ui.interaction.Inputs;
import edu.stedwards.matt.ui.interaction.VisualElements;
import edu.stedwards.matt.ui.labels.LoginScreenText;
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
            section("Visual Elements", visualElementExamples()));

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
            example("VisualElements.buildUploadMethodGroup3()", VisualElements.buildUploadMethodGroup3()),
            example("VisualElements.buildParsedInfoCardEditable(String, List<Node>, List<Button>, String...)",
                  VisualElements.buildParsedInfoCardEditable(
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
      TextFlow label = buildComponentName(name[0], name[1]);
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
