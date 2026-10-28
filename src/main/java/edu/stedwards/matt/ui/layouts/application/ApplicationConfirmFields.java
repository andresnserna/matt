package edu.stedwards.matt.ui.layouts.application;

import edu.stedwards.matt.ui.TextStyles;
import edu.stedwards.matt.ui.interaction.Buttons;
import edu.stedwards.matt.ui.interaction.Inputs;
import edu.stedwards.matt.ui.interaction.VisualElements;
import edu.stedwards.matt.ui.labels.ApplicationScreenText;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.beans.binding.Bindings;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import javafx.scene.text.TextFlow;

public class ApplicationConfirmFields extends BorderPane {

   public ApplicationConfirmFields(){
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
      
      var finish = Buttons.button(ApplicationScreenText.BUTTON_FINISH_ICON,
            ApplicationScreenText.FINISH_BUTTON, null, Region.USE_COMPUTED_SIZE, "application-finish-button");
      HBox footer = new HBox(finish);
      footer.setAlignment(Pos.CENTER_RIGHT);
      setBottom(footer);
   }

   private HBox buildOverviewColumns() {
      VBox posting = new VBox(12,
            VisualElements.buildJobDetailsRawTextContainer(
                  ("This is a preview of the text parsed by the parser. ").repeat(20)),
            VisualElements.buildAttachedFileLabel("JiuhhiJKhgkjhG_download_9_00_12.pdf"));
      HBox retryRow = new HBox(Buttons.button(FontAwesomeSolid.SYNC,
            ApplicationScreenText.TRY_AGAIN_BUTTON, null, Region.USE_COMPUTED_SIZE,
            "application-retry-button"));
      retryRow.setAlignment(Pos.CENTER);
      posting.getChildren().add(retryRow);
      posting.setMinWidth(0);
      posting.setMaxWidth(Double.MAX_VALUE);

      VBox parsedFields = new VBox(14);
      Label heading = new Label(ApplicationScreenText.PARSED_INFO_TITLE);
      // heading.getStyleClass().add("about-job-heading");
      heading.getStyleClass().add("onboarding-wizard-subtitle");
      Label subtitle = new Label(ApplicationScreenText.PARSED_INFO_MESSAGE);
      // subtitle.getStyleClass().add("onboarding-wizard-subtitle");
      subtitle.getStyleClass().add("about-job-heading");
      subtitle.setWrapText(true);
      parsedFields.getChildren().addAll(heading, subtitle,
            parsedField("Job Title", "Senior Software Developer – Full Stack Engineering"),
            parsedField("Company", "Dream Company, LP"),
            buildPayRange(),
            parsedArea(ApplicationScreenText.PARSED_RESPONSIBILITIES_LABEL,
                  "– asdd\n– asdasd\n– asdasd\n– asdaSDasd"),
            parsedArea(ApplicationScreenText.PARSED_REQUIREMENTS_LABEL,
                  "– asdd\n– asdasd\n– asdasd\n– asdaSDasd"));
      parsedFields.setMinWidth(0);
      parsedFields.setMaxWidth(Double.MAX_VALUE);
      parsedFields.setAlignment(Pos.TOP_LEFT);

      HBox columns = new HBox(28, posting, parsedFields);
      columns.setAlignment(Pos.TOP_LEFT);
      columns.setFillHeight(true);
      columns.setMaxWidth(Double.MAX_VALUE);
      // Keep the parsed form at half of the app's content area. Center the pair
      // inside this screen region, which is already laid out to the sidebar's right.
      posting.prefWidthProperty().bind(Bindings.max(0, columns.widthProperty().subtract(28).multiply(0.40)));
      parsedFields.prefWidthProperty().bind(Bindings.max(0, columns.widthProperty().subtract(28).multiply(0.50)));
      columns.setAlignment(Pos.TOP_CENTER);
      return columns;
   }

   private VBox parsedField(String labelText, String value) {
      TextField field = Inputs.inputField("", 100, Double.MAX_VALUE);
      field.setText(value);
      field.setMaxWidth(Double.MAX_VALUE);
      VBox group = Inputs.buildInputGroup(TextStyles.buildLabel(labelText, "input-label"), field);
      HBox.setHgrow(field, Priority.ALWAYS);
      group.setMaxWidth(Double.MAX_VALUE);
      return group;
   }

   private VBox parsedArea(String labelText, String value) {
      TextArea area = Inputs.textArea("", 100, Double.MAX_VALUE, 100, 130);
      area.setText(value);
      area.setWrapText(true);
      area.setMaxWidth(Double.MAX_VALUE);
      VBox group = Inputs.buildInputGroup(TextStyles.buildLabel(labelText, "input-label"), area);
      HBox.setHgrow(area, Priority.ALWAYS);
      group.setMaxWidth(Double.MAX_VALUE);
      return group;
   }

   private HBox buildPayRange() {
      VBox low = parsedPayField(ApplicationScreenText.PARSED_PAY_RANGE_LOW_LABEL, "$124,000.00");
      VBox high = parsedPayField(ApplicationScreenText.PARSED_PAY_RANGE_HIGH_LABEL, "$154,000.00");
      HBox.setHgrow(low, Priority.ALWAYS);
      HBox.setHgrow(high, Priority.ALWAYS);
      HBox row = new HBox(18, low, high);
      row.setAlignment(Pos.BOTTOM_LEFT);
      row.setFillHeight(true);
      row.setMaxWidth(Double.MAX_VALUE);
      return row;
   }

   private VBox parsedPayField(String labelText, String value) {
      TextField field = Inputs.inputField("", 100, Double.MAX_VALUE);
      field.setText(value);
      field.setMaxWidth(Double.MAX_VALUE);
      VBox fieldWithSuffix = Inputs.wrapInputGroupWithSuffix(field, "yr");
      fieldWithSuffix.getStyleClass().add("input-field-spacing");
      VBox group = new VBox(8, TextStyles.buildLabel(labelText, "input-label"), fieldWithSuffix);
      group.setMaxWidth(Double.MAX_VALUE);
      HBox.setHgrow(field, Priority.ALWAYS);
      return group;
   }

}
