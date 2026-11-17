package edu.stedwards.matt.ui.layouts.template;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.io.StringReader;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import edu.stedwards.matt.controllers.TemplateEditorController;
import edu.stedwards.matt.dao.TemplateDAO;
import edu.stedwards.matt.models.OnboardingSession;
import edu.stedwards.matt.models.entity.Template;
import edu.stedwards.matt.ui.TextStyles;
import edu.stedwards.matt.ui.interaction.Buttons;
import edu.stedwards.matt.ui.interaction.VisualElements;
import edu.stedwards.matt.ui.layouts.other.MainLayout;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

public class TemplatesHome extends BorderPane {
   public record TemplateCardData(String name, String editedLabel, String fileLoc, int templateId) {
      public TemplateCardData(String name, String editedLabel) {
         this(name, editedLabel, null, 0);
      }

      public TemplateCardData(String name, String editedLabel, String fileLoc) {
         this(name, editedLabel, fileLoc, 0);
      }
   }

   private final FlowPane cards = new FlowPane(43, 34);

   public TemplatesHome() {
      this(loadTemplatesForCurrentUser());
   }

   private static List<TemplateCardData> loadTemplatesForCurrentUser() {
      var profile = OnboardingSession.getInstance().getUserProfile();
      int userId = profile != null ? profile.getUserID() : 0;
      if (userId <= 0) return Collections.emptyList();

      return new TemplateDAO().getTemplatesForUser(userId).stream()
            .map(TemplatesHome::toCardData)
            .toList();
   }

   private static TemplateCardData toCardData(Template template) {
      String updated = "Saved template";
      if (template.getFileLoc() != null) {
         try {
            var modified = java.nio.file.Files.getLastModifiedTime(java.nio.file.Path.of(template.getFileLoc()));
            updated = "Updated " + modified.toInstant()
                  .atZone(java.time.ZoneId.systemDefault()).toLocalDate().toString();
         } catch (java.io.IOException | RuntimeException ignored) {
            // Older or unavailable template files still appear in the library.
         }
      }
      return new TemplateCardData(template.getTemplateName(), updated, template.getFileLoc(), template.getTemplateId());
   }

   public TemplatesHome(List<TemplateCardData> templates) {
      this(templates, null);
   }

   public TemplatesHome(List<TemplateCardData> templates, Consumer<TemplateCardData> onTemplateSelected) {
      getStyleClass().addAll("screen-padding");

      TextFlow title = TextStyles.buildTitle("My Templates", "screen-title");
      setTop(title);

      cards.getStyleClass().add("template-card-grid");
      cards.setPadding(new Insets(42, 5, 0, 5));
      cards.setMaxWidth(Double.MAX_VALUE);
      
      Consumer<TemplateCardData> clickHandler = (onTemplateSelected != null) 
            ? onTemplateSelected 
            : this::handleTemplateCardSelected;

      for (TemplateCardData template : templates) {
         cards.getChildren().add(buildTemplateCard(template, clickHandler));
      }
      if (templates.isEmpty()) {
         Label emptyState = new Label("No templates yet. Create one to get started.");
         emptyState.getStyleClass().add("template-card-preview");
         cards.getChildren().add(emptyState);
      }
      setCenter(VisualElements.scrollable(cards));

      Button newTemplate = Buttons.button(FontAwesomeSolid.PLUS_CIRCLE,
            "New Template", event -> handleNewTemplate(),
            Region.USE_COMPUTED_SIZE, "new-template-button");
      newTemplate.setContentDisplay(ContentDisplay.RIGHT);
      
      HBox footer = new HBox(newTemplate);
      footer.setAlignment(Pos.BOTTOM_RIGHT);
      footer.setPadding(new Insets(21, 0, 0, 0));
      setBottom(footer);

   }

   /** Open the template editor with the selected template's content preloaded. */
   public void handleTemplateCardSelected(TemplateCardData template) {
      if (getScene() != null && getScene().getRoot() instanceof MainLayout mainLayout) {
         TemplateEditorController controller = new TemplateEditorController();
         TemplatesEditor editor = new TemplatesEditor(controller, template.name(), template.fileLoc(), template.templateId());
         mainLayout.showStep(editor);
      }
   }

   /** Open a blank template editor for creating a new template. */
   public void handleNewTemplate() {
      if (getScene() != null && getScene().getRoot() instanceof MainLayout mainLayout) {
         TemplateEditorController controller = new TemplateEditorController();
         mainLayout.showStep(new TemplatesEditor(controller));
      }
   }

   private Button buildTemplateCard(TemplateCardData template, Consumer<TemplateCardData> onTemplateSelected) {
      Button card = new Button();
      card.getStyleClass().add("template-card");
      card.setPrefSize(340, 216);
      card.setMinSize(267, 187);
      card.setMaxWidth(340);
      card.setAccessibleText(template.name());
      if (onTemplateSelected != null) {
         card.setOnAction(event -> onTemplateSelected.accept(template));
      }

      Label preview = new Label(readPreview(template.fileLoc()));
      preview.setWrapText(true);
      preview.setMaxWidth(Double.MAX_VALUE);
      preview.setMaxHeight(118);
      preview.getStyleClass().add("template-card-preview");
      Region fade = new Region();
      fade.setPrefHeight(36);
      fade.setMaxWidth(Double.MAX_VALUE);
      fade.getStyleClass().add("template-card-preview-fade");
      StackPane previewArea = new StackPane(preview, fade);
      StackPane.setAlignment(preview, Pos.TOP_LEFT);
      StackPane.setAlignment(fade, Pos.BOTTOM_CENTER);
      previewArea.getStyleClass().add("template-card-preview-area");
      VBox.setVgrow(previewArea, Priority.ALWAYS);

      Label edited = new Label(template.editedLabel());
      edited.getStyleClass().add("template-card-edited-label");
      Label name = new Label(template.name());
      name.getStyleClass().add("template-card-name");
      HBox nameFooter = new HBox(name);
      nameFooter.getStyleClass().add("template-card-footer");
      nameFooter.setAlignment(Pos.CENTER_LEFT);

      VBox content = new VBox(previewArea, edited, nameFooter);
      content.setMouseTransparent(true);
      card.setGraphic(content);
      card.setContentDisplay(javafx.scene.control.ContentDisplay.GRAPHIC_ONLY);
      return card;
   }

   private static String readPreview(String fileLoc) {
      if (fileLoc == null || fileLoc.isBlank()) return "";
      try {
         String html = java.nio.file.Files.readString(java.nio.file.Path.of(fileLoc));
         StringBuilder text = new StringBuilder();
         new ParserDelegator().parse(new StringReader(html), new HTMLEditorKit.ParserCallback() {
            @Override
            public void handleText(char[] data, int position) {
               if (text.length() > 0) text.append(' ');
               text.append(data);
            }
         }, true);
         String[] words = text.toString().trim().split("\\s+");
         if (words.length == 0 || words[0].isBlank()) return "";
         return String.join(" ", java.util.Arrays.copyOf(words, Math.min(8, words.length)));
      } catch (java.io.IOException | RuntimeException e) {
         return "";
      }
   }
}
