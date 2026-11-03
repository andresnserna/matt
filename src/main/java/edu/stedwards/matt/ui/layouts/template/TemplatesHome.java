package edu.stedwards.matt.ui.layouts.template;

import edu.stedwards.matt.ui.TextStyles;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import java.util.List;
import java.util.Collections;

public class TemplatesHome extends BorderPane {
   public record TemplateCardData(String name, String editedLabel, String previewText) { }

   public TemplatesHome() {
      this(Collections.emptyList());
   }

   public TemplatesHome(List<TemplateCardData> templates) {
      getStyleClass().add("templates-home");
      setPadding(new Insets(52, 54, 48, 54));

      TextFlow title = TextStyles.buildTitle("My Templates", "screen-title");
      title.getStyleClass().add("templates-home-title");
      setTop(title);

      FlowPane cards = new FlowPane(48, 38);
      cards.getStyleClass().add("template-card-grid");
      cards.setPadding(new Insets(48, 6, 0, 6));
      for (TemplateCardData template : templates) {
         cards.getChildren().add(buildTemplateCard(template));
      }
      setCenter(cards);

      Button newTemplate = new Button("New Template  ⊕");
      newTemplate.getStyleClass().add("new-template-button");
      HBox footer = new HBox(newTemplate);
      footer.setAlignment(Pos.BOTTOM_RIGHT);
      footer.setPadding(new Insets(24, 0, 0, 0));
      setBottom(footer);
   }

   private VBox buildTemplateCard(TemplateCardData template) {
      VBox card = new VBox();
      card.getStyleClass().add("template-card");
      card.setPrefSize(382, 242);
      card.setMinSize(300, 210);
      card.setMaxWidth(382);

      Text preview = new Text(template.previewText());
      preview.getStyleClass().add("template-card-preview");
      VBox previewArea = new VBox(preview);
      previewArea.getStyleClass().add("template-card-preview-area");
      VBox.setVgrow(previewArea, Priority.ALWAYS);

      Label edited = new Label(template.editedLabel());
      edited.getStyleClass().add("template-card-edited-label");
      Label name = new Label(template.name());
      name.getStyleClass().add("template-card-name");
      HBox nameFooter = new HBox(name);
      nameFooter.getStyleClass().add("template-card-footer");
      nameFooter.setAlignment(Pos.CENTER_LEFT);

      card.getChildren().addAll(previewArea, edited, nameFooter);
      return card;
   }
}
