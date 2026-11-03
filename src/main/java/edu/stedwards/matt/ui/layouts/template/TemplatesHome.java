package edu.stedwards.matt.ui.layouts.template;

import edu.stedwards.matt.ui.TextStyles;
import edu.stedwards.matt.ui.interaction.VisualElements;
import edu.stedwards.matt.ui.labels.TemplatesScreenText;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import java.util.List;
import java.util.Collections;
import java.util.function.Consumer;

public class TemplatesHome extends BorderPane {
   public record TemplateCardData(String name, String editedLabel) { }

   public TemplatesHome() {
      this(Collections.emptyList());
   }

   public TemplatesHome(List<TemplateCardData> templates) {
      this(templates, null);
   }

   public TemplatesHome(List<TemplateCardData> templates, Consumer<TemplateCardData> onTemplateSelected) {
      getStyleClass().addAll("screen-padding");
      // setPadding(new Insets(46, 48, 43, 48));

      TextFlow title = TextStyles.buildTitle("My Templates", "screen-title");
      setTop(title);

      FlowPane cards = new FlowPane(43, 34);
      cards.getStyleClass().add("template-card-grid");
      cards.setPadding(new Insets(42, 5, 0, 5));
      cards.setMaxWidth(Double.MAX_VALUE);
      for (TemplateCardData template : templates) {
         cards.getChildren().add(buildTemplateCard(template, onTemplateSelected));
      }
      setCenter(VisualElements.scrollable(cards));

      Button newTemplate = new Button("New Template  ⊕");
      newTemplate.getStyleClass().add("new-template-button");
      HBox footer = new HBox(newTemplate);
      footer.setAlignment(Pos.BOTTOM_RIGHT);
      footer.setPadding(new Insets(21, 0, 0, 0));
      setBottom(footer);
   }

   /** Load template records from the database and populate the template cards. */
   public void loadTemplatesFromDatabase() {
      // TODO: javi implement this button
   }

   /** Open the template editor with the selected template's content preloaded. */
   public void handleTemplateCardSelected() {
      // TODO: javi implement this button
   }

   /** Open a blank template editor for creating a new template. */
   public void handleNewTemplate() {
      // TODO: javi implement this button
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

      Label preview = new Label(TemplatesScreenText.TEMPLATE_CARD_PREVIEW_PLACEHOLDER);
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
}
