/**
 * Architecture layer: view
 * JavaFX layout for the onboarding welcome screen.
 */
package edu.stedwards.matt.ui.layouts.onboarding;

import edu.stedwards.matt.ui.interaction.Buttons;
import edu.stedwards.matt.ui.labels.OnboardingScreenText;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

public class WelcomeStep extends BorderPane {
   private TextFlow buildScreenTitle() {
      Text welcome = new Text(OnboardingScreenText.WELCOME_SCREEN_MESSAGE);
      welcome.getStyleClass().add("welcome-title-prefix");

      Text brand = new Text("Matt");
      brand.getStyleClass().add("welcome-title-brand");

      TextFlow title = new TextFlow(welcome, brand);
      title.getStyleClass().add("welcome-title");
      title.setTextAlignment(TextAlignment.CENTER);
      return title;
   }

   private VBox buildWelcomeMessage() {
      Label introduction = new Label(OnboardingScreenText.WELCOME_MESSAGE_SUBTITLE);
      introduction.getStyleClass().add("welcome-copy");
      introduction.setWrapText(true);
      introduction.setMaxWidth(Double.MAX_VALUE);

      Label resume = new Label(OnboardingScreenText.WELCOME_MESSAGE_SUBTITLE_ITEM1);
      resume.getStyleClass().add("welcome-list-item");

      Text prefix = new Text(OnboardingScreenText.WELCOME_MESSAGE_SUBTITLE_ITEM2_PREFIX);
      prefix.getStyleClass().add("welcome-list-item");

      Text link = new Text(OnboardingScreenText.WELCOME_MESSAGE_SUBTITLE_ITEM2_LINK);
      link.getStyleClass().add("welcome-help-link");
      link.setCursor(Cursor.HAND);

      Text suffix = new Text(OnboardingScreenText.WELCOME_MESSAGE_SUBTITLE_ITEM2_SUFFIX);
      suffix.getStyleClass().add("welcome-list-item");

      TextFlow ollamaKey = new TextFlow(prefix, link, suffix);
      ollamaKey.getStyleClass().add("welcome-list-item");

      VBox message = new VBox(8, introduction, resume, ollamaKey);
      message.setMaxWidth(Double.MAX_VALUE);
      return message;
   }

   private HBox buildNextButton() {
      Button next = Buttons.button(OnboardingScreenText.BUTTON_NEXT, null, "onboarding-next-button");
      next.setGraphic(new Label("›"));
      next.setContentDisplay(ContentDisplay.RIGHT);
      next.setGraphicTextGap(8);

      HBox footer = new HBox(next);
      footer.setAlignment(Pos.CENTER_RIGHT);
      return footer;
   }

   public WelcomeStep() {
      getStyleClass().addAll("app-onboarding", "welcome-screen");
      setTop(buildScreenTitle());
      setCenter(buildWelcomeMessage());
      setBottom(buildNextButton());
      BorderPane.setAlignment(getCenter(), Pos.CENTER_LEFT);
   }
}
