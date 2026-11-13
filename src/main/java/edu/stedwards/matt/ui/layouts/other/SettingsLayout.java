package edu.stedwards.matt.ui.layouts.other;

import edu.stedwards.matt.ui.TextStyles;
import edu.stedwards.matt.ui.interaction.Buttons;
import edu.stedwards.matt.ui.labels.OtherScreenText;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextFlow;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

/** Settings screen shell with the logout action. */
public class SettingsLayout extends BorderPane {

   public SettingsLayout() {
      getStyleClass().addAll("screen-padding", "settings-screen");

      TextFlow title = TextStyles.buildTitle(OtherScreenText.SETTINGS_TITLE, "settings-title");
      setTop(title);

      Button logoutButton = Buttons.button(
            FontAwesomeSolid.SIGN_OUT_ALT,
            OtherScreenText.SETTINGS_LOGOUT_BUTTON,
            event -> handleLogout(),
            javafx.scene.layout.Region.USE_COMPUTED_SIZE,
            "settings-logout-button");
      logoutButton.setAccessibleText(OtherScreenText.SETTINGS_LOGOUT_BUTTON);
      logoutButton.setContentDisplay(ContentDisplay.RIGHT);


      HBox footer = new HBox(logoutButton);
      footer.setAlignment(Pos.CENTER_RIGHT);
      setBottom(footer);
      BorderPane.setMargin(footer, new Insets(0, 0, 0, 0));
   }

   /**
    * TODO: javi implement. Called when the user presses the Log Out button.
    * Takes no arguments and returns nothing; coordinate closing the signed-in session,
    * then navigate the current scene back to the login screen.
    */
   protected void handleLogout() {
      closeSession();
   }

   /**
    * TODO: javi implement. Called by handleLogout() as part of ending the current account session.
    * Takes no arguments and returns nothing; clear the signed-in user's session state and perform
    * any required session cleanup before the UI returns to login.
    */
   protected void closeSession() {
   }
}
