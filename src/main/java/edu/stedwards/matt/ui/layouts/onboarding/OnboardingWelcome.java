/**
 * Architecture layer: view
 * JavaFX layout for the onboarding welcome screen.
 */
package edu.stedwards.matt.ui.layouts.onboarding;

import edu.stedwards.matt.ui.interaction.Buttons;
import edu.stedwards.matt.ui.interaction.VisualElements;
import edu.stedwards.matt.ui.TextStyles;
import edu.stedwards.matt.ui.labels.OnboardingScreenText;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class OnboardingWelcome extends BorderPane {

   private TextFlow buildScreenTitle() {
      Text welcome = new Text(OnboardingScreenText.WELC_TITLE);
      welcome.getStyleClass().add("onboarding-wizard-title");

      Text brand = new Text("Matt");
      brand.getStyleClass().addAll("onboarding-wizard-title", "onboarding-wizard-title-split");

      return TextStyles.buildTitle(welcome, brand);
   }

   private VBox buildWelcomeMessage() {
      Label introduction = new Label(OnboardingScreenText.WELC_MESSAGE);
      introduction.getStyleClass().add("onboarding-wizard-subtitle");
      introduction.setWrapText(true);
      introduction.setMaxWidth(Double.MAX_VALUE);

      Label resume = new Label(OnboardingScreenText.WELC_MESSAGE_ITEM1);
      resume.getStyleClass().add("onboarding-wizard-list-item");

      Text prefix = new Text(OnboardingScreenText.WELC_MESSAGE_ITEM2_PREFIX);
      prefix.getStyleClass().add("onboarding-wizard-list-item");

      Text link = new Text(OnboardingScreenText.WELC_MESSAGE_ITEM2_LINK);
      link.getStyleClass().add("welcome-help-link");
      link.setCursor(Cursor.HAND);

      Text suffix = new Text(OnboardingScreenText.WELC_MESSAGE_ITEM2_SUFFIX);
      suffix.getStyleClass().add("onboarding-wizard-list-item");

      TextFlow ollamaKey = new TextFlow(prefix, link, suffix);
      ollamaKey.getStyleClass().add("onboarding-wizard-list-item");

      VBox message = new VBox(8, introduction, resume, ollamaKey);
      message.setMaxWidth(Double.MAX_VALUE);
      return message;
   }

   public OnboardingWelcome() {
      getStyleClass().addAll("wizard-background", "screen-padding");
      setTop(buildScreenTitle());
      var scrollContent = VisualElements.wizardScrollable(buildWelcomeMessage());
      setCenter(scrollContent);
      BorderPane.setMargin(scrollContent, new Insets(0, 0, 24, 0));
      setBottom(new Buttons().buildNextButton());
      BorderPane.setAlignment(getCenter(), Pos.CENTER_LEFT);
   }
}
