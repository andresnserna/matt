package edu.stedwards.matt.ui.layouts.application;

import java.util.List;

import edu.stedwards.matt.ui.TextStyles;
import edu.stedwards.matt.ui.interaction.Buttons;
import edu.stedwards.matt.ui.interaction.VisualElements;
import edu.stedwards.matt.ui.labels.ApplicationScreenText;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.control.Button;

/** Application details and template selection screen. */
public class ApplicationGenerateMaterials extends BorderPane {

   public ApplicationGenerateMaterials() {
      getStyleClass().add("screen-padding");

      Text application = new Text(ApplicationScreenText.APPLICATION_DETAIL_TITLE_PREFIX);
      application.getStyleClass().add("screen-title");
      Text company = new Text("Company Name");
      company.getStyleClass().addAll("screen-title", "screen-title-split");
      TextFlow title = TextStyles.buildTitle(application, company);

      Label started = new Label("Started: Mar 13th, 2026");
      started.getStyleClass().add("relative-time-label");
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

   private HBox buildColumns() {
      Label aboutHeading = new Label(ApplicationScreenText.ABOUT_THE_JOB_TITLE);
      aboutHeading.getStyleClass().add("about-job-heading");
      VBox jobPanel = VisualElements.buildJobDetailsContainer(
            "Job Title", "City, ST", "Company Name", "$100,000/yr – $101,000/yr",
            "– some requirement sentence.\n– another requirement sentence that is two lines instead of one.\n– another requirement sentences",
            "– some Nice to Have sentence.\n– another Nice to Have sentence that is two lines instead of one.\n– another Nice to Have sentence",
            "– some requirement sentence.");
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
      VBox generate = new VBox(16, generateHeading, templatePanel);
      generate.setMinWidth(0);
      generate.setMaxWidth(Double.MAX_VALUE);
      VBox.setVgrow(templatePanel, Priority.ALWAYS);

      Region columnSpacer = new Region();
      HBox.setHgrow(columnSpacer, Priority.ALWAYS);
      HBox columns = new HBox(about, columnSpacer, generate);
      columns.setAlignment(Pos.TOP_LEFT);
      columns.setFillHeight(true);
      columns.setMaxWidth(Double.MAX_VALUE);

      // Anchor the columns to the content area's edges and let the center gap flex.
      about.prefWidthProperty().bind(Bindings.max(0, columns.widthProperty().multiply(0.46)));
      generate.prefWidthProperty().bind(Bindings.max(0, columns.widthProperty().multiply(0.46)));
      jobPanel.prefWidthProperty().bind(about.widthProperty());
      templatePanel.prefWidthProperty().bind(generate.widthProperty());
      return columns;
   }

   private VBox buildTemplatePanel() {
      FlowPane cards = new FlowPane(14, 14);
      cards.getStyleClass().add("template-card-grid");
      cards.setPadding(Insets.EMPTY);
      List<Button> buttons = List.of(
            Buttons.buildTemplatePickerCard("Resume\nTemplate", null, 170, 140, "application-template-card"),
            Buttons.buildTemplatePickerCard("Cover Letter\nTemplate", null, 170, 140, "application-template-card"),
            Buttons.buildTemplatePickerCard("Cover Letter\nTemplate 2", null, 170, 140, "application-template-card"));
      cards.getChildren().addAll(buttons);

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
}
