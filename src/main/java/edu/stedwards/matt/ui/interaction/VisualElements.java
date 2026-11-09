package edu.stedwards.matt.ui.interaction;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;

import org.kordamp.ikonli.javafx.FontIcon;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.parser.SVGLoader;
import com.github.weisj.jsvg.ui.jfx.FXSVGCanvas;
import com.github.weisj.jsvg.view.ViewBox;

import edu.stedwards.matt.dao.UserDAO;
import edu.stedwards.matt.models.entity.User;
import edu.stedwards.matt.ui.TextStyles;
import edu.stedwards.matt.ui.labels.ApplicationScreenText;
import edu.stedwards.matt.ui.labels.LoginScreenText;
import edu.stedwards.matt.ui.labels.MainScreenText;
import edu.stedwards.matt.ui.labels.OnboardingScreenText;
import edu.stedwards.matt.ui.labels.OtherScreenText;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;

public class VisualElements {
   private static final PseudoClass SCROLLABLE_PSEUDO_CLASS = PseudoClass.getPseudoClass("scrollable");
   private static final double COLLAPSED_SIDEBAR_WIDTH = 64;
   private static final double DRAWER_SIDE_INSET = 8;
   private double sidebarWidth = 240;

   public double getSidebarWidth() {
      return sidebarWidth;
   }

   public void setSidebarWidth(double sidebarWidth) {
      if (!Double.isFinite(sidebarWidth) || sidebarWidth <= 2 * DRAWER_SIDE_INSET) {
         throw new IllegalArgumentException("Sidebar width must be greater than 16 pixels");
      }
      this.sidebarWidth = sidebarWidth;
   }
   
   /** A reusable scroll pane with a styled scrollbar and resizable content. */
   public static ScrollPane scrollable(Node content) {
      ScrollPane scrollPane = new ScrollPane(content);
      scrollPane.setFitToWidth(true);
      scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
      scrollPane.getStyleClass().add("content-scroll");
      return scrollPane;
   }

   /** Scrollable wizard content with an inset shadow only while content overflows. */
   public static StackPane wizardScrollable(Node content) {
      ScrollPane scrollPane = new ScrollPane(content);
      scrollPane.setFitToWidth(true);
      scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
      scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
      scrollPane.getStyleClass().add("wizard-scroll-pane");

      StackPane container = new StackPane(scrollPane);
      container.getStyleClass().add("wizard-scroll-container");
      container.setMinHeight(0);

      Node[] observedViewport = {null};
      Runnable[] refreshScrollableState = new Runnable[1];
      refreshScrollableState[0] = () -> {
         Node viewport = scrollPane.lookup(".viewport");
         if (viewport == null) {
            return;
         }
         if (viewport != observedViewport[0]) {
            observedViewport[0] = viewport;
            viewport.layoutBoundsProperty().addListener(
                  (observable, oldBounds, newBounds) -> refreshScrollableState[0].run());
         }

         boolean canScroll = content.getBoundsInLocal().getHeight()
               > viewport.getBoundsInLocal().getHeight() + 1;
         scrollPane.pseudoClassStateChanged(SCROLLABLE_PSEUDO_CLASS, canScroll);
      };

      content.layoutBoundsProperty().addListener(
            (observable, oldBounds, newBounds) -> refreshScrollableState[0].run());
      scrollPane.layoutBoundsProperty().addListener(
            (observable, oldBounds, newBounds) -> refreshScrollableState[0].run());
      scrollPane.skinProperty().addListener(
            (observable, oldSkin, newSkin) -> refreshScrollableState[0].run());
      scrollPane.sceneProperty().addListener(
            (observable, oldScene, newScene) -> refreshScrollableState[0].run());
      return container;
   }

   /** Container box displaying uniform un-editable information. */
   public VBox infoContainer(double width, double height, String headingTitle, int headingTitleNum, Node content) {
      Label heading = new Label(headingTitleNum + ". " + headingTitle);
      HBox headingBar = new HBox(heading);
      headingBar.setAlignment(Pos.CENTER_LEFT);

      VBox container = new VBox(headingBar, content);
      container.getStyleClass().add("info-container");
      container.setPrefSize(width, height);
      return container;
   }

   /** Main navigation sidebar anchored to the left of the screen. */
   public VBox mainSideBar() {
      VBox sideBar = new VBox();
      sideBar.getStyleClass().add("sidebar");
      sideBar.setMinWidth(COLLAPSED_SIDEBAR_WIDTH);
      sideBar.setPrefWidth(getSidebarWidth());
      double buttonWidth = getSidebarWidth() - (2 * DRAWER_SIDE_INSET) - 4;

      VBox menuButtons = new VBox();
      menuButtons.setSpacing(8);

      Button homeButton = Buttons.button(MainScreenText.BUTTON_HOME_ICON, MainScreenText.BUTTON_HOME, null, buttonWidth, "sidebar-button");
      Button templatesButton = Buttons.button(MainScreenText.BUTTON_TEMPLATES_ICON, MainScreenText.BUTTON_TEMPLATES, null, buttonWidth, "sidebar-button");
      Button applicationsButton = Buttons.button(MainScreenText.BUTTON_APPLICATIONS_ICON, MainScreenText.BUTTON_APPLICATIONS, null, buttonWidth, "sidebar-button");
      Button myInfoButton = Buttons.button(MainScreenText.BUTTON_MY_INFO_ICON, MainScreenText.BUTTON_MY_INFO, null, buttonWidth, "sidebar-button");
      Button settingsButton = Buttons.button(MainScreenText.BUTTON_SETTINGS_ICON, MainScreenText.BUTTON_SETTINGS, null, buttonWidth, "sidebar-button");

      menuButtons.getChildren().addAll(homeButton, templatesButton, applicationsButton, myInfoButton, settingsButton);

      VBox recentApplicationsContent = new VBox();
      VBox recentApplicationsDrawer = collapsableDrawer(
            OtherScreenText.PLACEHOLDER_TITLE,
            recentApplicationsContent,
            "sidebar-recent-items",
            getSidebarWidth());

      VBox recentMaterialsContent = new VBox();
      VBox recentMaterialsDrawer = collapsableDrawer(
            OtherScreenText.PLACEHOLDER_TITLE,
            recentMaterialsContent,
            "sidebar-menu-items",
            getSidebarWidth());

      Region spacer = new Region();
      VBox.setVgrow(spacer, Priority.ALWAYS);

      HBox footer = new HBox();
      footer.setAlignment(Pos.CENTER_LEFT);

      Label versionLabel = new Label(OtherScreenText.APP_VERSION);
      versionLabel.getStyleClass().add("sidebar-version-label");

      Region footerSpacer = new Region();
      HBox.setHgrow(footerSpacer, Priority.ALWAYS);

      Button toggle = new Button("‹");
      toggle.getStyleClass().add("sidebar-toggle-button");
      toggle.setAccessibleText("Collapse sidebar");
      footer.getChildren().addAll(versionLabel, footerSpacer, toggle);
      VBox.setVgrow(footer, Priority.NEVER);

      sideBar.getChildren().addAll(
         menuButtons, 
         recentMaterialsDrawer, recentApplicationsDrawer, 
         spacer, 
         footer
      );

      List<Node> sideBarItems = List.of(menuButtons, recentMaterialsDrawer, recentApplicationsDrawer, versionLabel);

      toggle.setOnAction(event -> {
         boolean collapsing = sideBar.getPrefWidth() > COLLAPSED_SIDEBAR_WIDTH;
         sideBar.setPrefWidth(collapsing ? COLLAPSED_SIDEBAR_WIDTH : getSidebarWidth());
         sideBar.getStyleClass().remove("sidebar-collapsed");

         if (collapsing) {
            sideBar.getStyleClass().add("sidebar-collapsed");
            toggle.setText("›");
            toggle.setAccessibleText("Expand sidebar");
            setMenuBarItems(false, sideBarItems);
         } else {
            toggle.setText("‹");
            toggle.setAccessibleText("Collapse sidebar");
            setMenuBarItems(true, sideBarItems);
         }
      });

      return sideBar;
   }

   private void setMenuBarItems(boolean visible, List<Node> items) {
      for (Node item : items) {
         item.setVisible(visible);
         item.setManaged(visible);
      }
   }

   /** Creates an expanded-by-default drawer that collapses vertically. */
   public VBox collapsableDrawer(String title, Node content, String styleClass, double parentContainerWidth){
      Objects.requireNonNull(title, "title");
      Objects.requireNonNull(content, "content");
      Objects.requireNonNull(styleClass, "styleClass");
      double width = parentContainerWidth - 2 * DRAWER_SIDE_INSET;

      Label heading = new Label(title);
      heading.getStyleClass().add("sidebar-drawer-title");

      Region headerSpacer = new Region();
      HBox.setHgrow(headerSpacer, Priority.ALWAYS);

      Button toggle = Buttons.button(OtherScreenText.DRAWER_UP_ICON, event -> {
               boolean collapsing = content.isManaged();
               content.setVisible(!collapsing);
               content.setManaged(!collapsing);

               Button source = (Button) event.getSource();
               FontIcon icon = (FontIcon) source.getGraphic();
               icon.setIconCode(collapsing
                     ? OtherScreenText.DRAWER_DOWN_ICON
                     : OtherScreenText.DRAWER_UP_ICON);
               source.setAccessibleText((collapsing ? "Expand " : "Collapse ") + title);
            },
            "drawer-toggle");

      toggle.setAccessibleText("Collapse " + title);

      Line drawerBar = new Line(0, 0, width - 8, 0);
      drawerBar.getStyleClass().add("drawer-bar");

      HBox header = new HBox(heading, headerSpacer, toggle);
      header.getStyleClass().add("drawer-header");
      header.setAlignment(Pos.CENTER_LEFT);

      VBox drawer = new VBox(header, drawerBar, content);
      drawer.getStyleClass().addAll("drawer", styleClass);
      drawer.setMinWidth(width);
      drawer.setPrefWidth(width);
      drawer.setMaxWidth(width);
      content.getStyleClass().add("drawer-content");

      return drawer;
   }

   public static FXSVGCanvas startScreenMattLogo() {
      URL logoResource = VisualElements.class.getResource("/images/matt-logo-1.svg");
      if (logoResource == null) {
         System.err.println("[StartLayout] ERROR: /images/matt-logo-1.svg was not found on the classpath.");
         throw new IllegalStateException("Matt logo resource not found");
      }

      SVGDocument document;
      try {
         document = new SVGLoader().load(logoResource);
      } catch (RuntimeException exception) {
         System.err.println("[StartLayout] ERROR: JSVG failed to parse the Matt logo.");
         exception.printStackTrace();
         throw exception;
      }
      if (document == null) {
         System.err.println("[StartLayout] ERROR: JSVG returned no document for the Matt logo.");
         throw new IllegalStateException("JSVG returned no document for the Matt logo");
      }

      FXSVGCanvas logo = new FXSVGCanvas();
      logo.setRenderBackend(FXSVGCanvas.RenderBackend.AWT);
      logo.setDocument(document);
      logo.setShowTransparentPattern(false);
      logo.getStyleClass().add("start-screen-logo");
      logo.setMinSize(220, 157);
      logo.setPrefSize(220, 157);
      logo.setMaxSize(220, 157);

      return logo;
   }
   
   public static HBox buildLoadingBar(ProgressBar loadingProgress, Label loadingPercentage) {
      HBox row = new HBox(10);
      row.setAlignment(Pos.CENTER);

      loadingProgress.setPrefWidth(260);
      loadingProgress.getStyleClass().add("start-screen-progress");
      loadingPercentage.getStyleClass().add("start-screen-loading-percentage");
      loadingProgress.progressProperty()
         .addListener((observable, oldProgress, newProgress) ->
               loadingPercentage.setText(Math.round(newProgress.doubleValue() * 100) + "%"));

      row.getChildren().addAll(loadingProgress, loadingPercentage);

      return row;
   }

   public static HBox buildCheckBoxWLabel() {
      CheckBox rememberMe = new CheckBox(LoginScreenText.REMEMBER_ME_CHECKBOX);
      rememberMe.getStyleClass().add("remember-me-checkbox");

      HBox row = new HBox(8, rememberMe);
      row.setAlignment(Pos.CENTER_LEFT);
      return row;
   }

   /** Builds job-posting input choices: file upload, posting URL, or pasted description. */
   public static VBox buildUploadMethodGroup3(double width){
      int uploadWidth = (int) Math.round(width);
      HBox uploadControl = new Buttons().buildUploadButton(uploadWidth, 8, null);
      TextField jobUrl = Inputs.inputField(ApplicationScreenText.JOB_URL_PLACEHOLDER, width, width);
      TextField jobDescription = Inputs.inputField(
            ApplicationScreenText.JOB_DESCRIPTION_PLACEHOLDER, width, width);

      uploadControl.setPrefWidth(width);
      uploadControl.setMinWidth(0);
      uploadControl.setMaxWidth(width);
      jobUrl.setMinWidth(0);
      jobUrl.setMaxWidth(width);
      jobDescription.setMinWidth(0);
      jobDescription.setMaxWidth(width);

      VBox methods = new VBox(16, uploadControl, jobUrl, jobDescription);
      methods.setAlignment(Pos.CENTER_LEFT);
      methods.setPrefWidth(width);
      methods.setMinWidth(0);
      methods.setMaxWidth(width);
      methods.getStyleClass().add("upload-method-group");
      return methods;
   }

   /** Builds a labeled month/day/year input group and sends edits to the provided value handler. */
   public static VBox dateGroup(String labelText, String value, Consumer<String> onDateChanged) {
      String[] parts = dateParts(value);
      TextField month = Inputs.inputField(OnboardingScreenText.DATE_MONTH_PLACEHOLDER, 54, 70);
      TextField day = Inputs.inputField(OnboardingScreenText.DATE_DAY_PLACEHOLDER, 54, 70);
      TextField year = Inputs.inputField(OnboardingScreenText.DATE_YEAR_PLACEHOLDER, 82, 100);
      month.setText(parts[0]);
      day.setText(parts[1]);
      year.setText(parts[2]);

      Runnable update = () -> {
         String combined = String.join("/", month.getText().trim(), day.getText().trim(), year.getText().trim());
         onDateChanged.accept(combined.equals("//") ? "" : combined);
      };
      month.textProperty().addListener((observable, oldValue, newValue) -> update.run());
      day.textProperty().addListener((observable, oldValue, newValue) -> update.run());
      year.textProperty().addListener((observable, oldValue, newValue) -> update.run());

      HBox dateFields = new HBox(8, month, new Label("/"), day, new Label("/"), year);
      dateFields.setAlignment(Pos.CENTER_LEFT);
      Label fieldLabel = TextStyles.buildLabel(labelText, "input-label");
      return Inputs.buildInputGroup(fieldLabel, dateFields);
   }

   private static String[] dateParts(String value) {
      String[] empty = {"", "", ""};
      if (value == null || value.isBlank()) {
         return empty;
      }

      String[] parts = value.trim().split("[/\\-.]", -1);
      if (parts.length != 3) {
         return empty;
      }
      if (parts[0].length() == 4) {
         return new String[] {parts[1], parts[2], parts[0]};
      }
      return parts;
   }

   /** Builds an editable parsed-information card with caller-provided fields and actions. */
   public static VBox buildParsedInfoCard(String headingText, List<? extends Node> inputGroups, List<Button> buttons, String... styleClasses) {
      Objects.requireNonNull(headingText, "headingText");
      Objects.requireNonNull(inputGroups, "inputGroups");
      Objects.requireNonNull(buttons, "buttons");

      Label heading = new Label(headingText);
      heading.getStyleClass().add("parsed-info-card-heading");

      Region headingSpacer = new Region();
      HBox.setHgrow(headingSpacer, Priority.ALWAYS);

      HBox header = new HBox(heading, headingSpacer);
      header.setAlignment(Pos.CENTER_LEFT);
      header.getChildren().addAll(buttons);
      header.getStyleClass().add("parsed-info-card-header");

      VBox content = new VBox(20);
      content.getChildren().addAll(inputGroups);
      content.getStyleClass().add("parsed-info-card-content");
      content.setMaxWidth(Double.MAX_VALUE);
      VBox.setVgrow(content, Priority.ALWAYS);

      VBox card = new VBox(header, content);
      card.getStyleClass().add("parsed-info-card");
      card.getStyleClass().addAll(styleClasses);
      card.setMaxWidth(Double.MAX_VALUE);
      VBox.setVgrow(card, Priority.ALWAYS);
      return card;
   }

   /** Overloaded method for single-button cards. */
   public static VBox buildParsedInfoCard(String headingText, List<? extends Node> inputGroups, Button button, String... styleClasses) {
      Objects.requireNonNull(headingText, "headingText");
      Objects.requireNonNull(inputGroups, "inputGroups");
      Objects.requireNonNull(button, "buttons");

      Label heading = new Label(headingText);
      heading.getStyleClass().add("parsed-info-card-heading");

      Region headingSpacer = new Region();
      HBox.setHgrow(headingSpacer, Priority.ALWAYS);

      HBox header = new HBox(heading, headingSpacer);
      header.setAlignment(Pos.CENTER_LEFT);
      header.getChildren().add(button);
      header.getStyleClass().add("parsed-info-card-header");

      VBox content = new VBox(20);
      content.getChildren().addAll(inputGroups);
      content.getStyleClass().add("parsed-info-card-content");
      content.setMaxWidth(Double.MAX_VALUE);
      VBox.setVgrow(content, Priority.ALWAYS);

      VBox card = new VBox(header, content);
      card.getStyleClass().add("parsed-info-card");
      card.getStyleClass().addAll(styleClasses);
      card.setMaxWidth(Double.MAX_VALUE);
      VBox.setVgrow(card, Priority.ALWAYS);
      return card;
   }

   public static void buildUploadMethodGroup2() {
   }

   /** Builds the login form with icon fields, a remember option, a primary login action, and a sign-up link. */
   public static VBox buildLoginBox(EventHandler<ActionEvent> onLogin, EventHandler<ActionEvent> onSignup,
         double boxWidth, double fieldWidth, double buttonWidth) {
      VBox box = new VBox(18);
      box.setAlignment(Pos.CENTER);
      box.setPrefWidth(boxWidth);
      box.setMaxWidth(boxWidth);
      box.getStyleClass().add("login-box");
      box.getChildren().addAll(
            Inputs.buildInputFieldWIcon(LoginScreenText.USERNAME_PLACEHOLDER, LoginScreenText.USERNAME_ICON, fieldWidth, fieldWidth, "login-field"),
            Inputs.buildPasswordFieldWIcon(LoginScreenText.PASSWORD_PLACEHOLDER, LoginScreenText.PASSWORD_ICON, fieldWidth, fieldWidth, "login-field"),
            buildCheckBoxWLabel());
      Button login = Buttons.button(LoginScreenText.LOGIN_BUTTON, onLogin, buttonWidth, "login-submit");
      Button signup = Buttons.button(LoginScreenText.SIGNUP_BUTTON, onSignup, 180, "signup-link-button");
      box.getChildren().addAll(login, signup);
      return box;
   }

   public static void showSignupDialog(ActionEvent event) {
      if (!(event.getSource() instanceof Node source) || source.getScene() == null) {
         throw new IllegalArgumentException("The signup dialog requires a login screen source node");
      }

      Node sceneRoot = source.getScene().getRoot();
      sceneRoot.setDisable(true);
      SwingUtilities.invokeLater(() -> {
         try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            Color white = Color.WHITE;
            Color orange = new Color(0xE2, 0x95, 0x78);
            JTextField usernameField = new JTextField(20);
            JLabel usernameLabel = new JLabel(LoginScreenText.USERNAME_PLACEHOLDER);
            usernameLabel.setLabelFor(usernameField);
            JPasswordField passwordField = new JPasswordField(20);
            JLabel passwordLabel = new JLabel(LoginScreenText.PASSWORD_PLACEHOLDER);
            passwordLabel.setLabelFor(passwordField);

            for (JTextField field : new JTextField[] { usernameField, passwordField }) {
               field.setBackground(white);
               field.setSelectionColor(orange);
               field.setSelectedTextColor(white);
               field.setPreferredSize(new Dimension(320, 34));
               field.setMaximumSize(new Dimension(320, 34));
            }

            JPanel form = new JPanel();
            form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
            form.setBackground(white);
            form.setBorder(new EmptyBorder(12, 20, 12, 20));

            JLabel logo = new JLabel(loadSignupLogo());
            logo.setAlignmentX(Component.CENTER_ALIGNMENT);
            form.add(logo);
            form.add(Box.createVerticalStrut(12));
            addSignupField(form, usernameLabel, usernameField, white);
            form.add(Box.createVerticalStrut(12));
            addSignupField(form, passwordLabel, passwordField, white);
            form.setPreferredSize(new Dimension(360, 250));

            JButton signupButton = new JButton(LoginScreenText.SIGNUP_BUTTON);
            signupButton.setUI(new BasicButtonUI());
            signupButton.setBackground(orange);
            signupButton.setForeground(white);
            signupButton.setOpaque(true);
            signupButton.setContentAreaFilled(true);
            signupButton.setFocusPainted(false);

            JButton cancelButton = new JButton("Cancel");

            JOptionPane optionPane = new JOptionPane(
                  form,
                  JOptionPane.PLAIN_MESSAGE,
                  JOptionPane.DEFAULT_OPTION,
                  null,
                  new Object[] { signupButton, cancelButton },
                  signupButton);

            JDialog dialog = optionPane.createDialog(LoginScreenText.SIGNUP_BUTTON);

            // Connect button actions to the handler methods
            signupButton.addActionListener(e -> handleSignupSubmit(
               usernameField.getText(), 
               new String(passwordField.getPassword()), 
               dialog
            ));
            cancelButton.addActionListener(e -> handleSignupCancel(dialog));

            dialog.setBackground(white);
            setDialogBackground(dialog.getContentPane(), white);
            dialog.setModal(true);
            dialog.setVisible(true);
            dialog.dispose();
         } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
               | UnsupportedLookAndFeelException exception) {
            throw new IllegalStateException("Could not initialize the system look-and-feel signup dialog",
                  exception);
         } finally {
            Platform.runLater(() -> sceneRoot.setDisable(false));
         }
      });
   }

   /** Close the sign-up window when Cancel is pressed. */
   public static void handleSignupCancel(JDialog dialog) {
      if (dialog != null) {
         dialog.dispose();
      }
   }

   /** Save the sign-up dialog's username and password as a new user account in the database. */
   public static void handleSignupSubmit(String username, String password, JDialog dialog) {
      if (username == null || username.isBlank() || password == null || password.isBlank()) {
         JOptionPane.showMessageDialog(dialog, "Username and password cannot be empty.", "Sign-Up Error", JOptionPane.ERROR_MESSAGE);
         return;
      }

      User user = new User();
      user.setUsername(username.trim());
      user.setPasswordHash(password);

      int generatedUserId = new UserDAO().saveAndGetId(user);
      boolean created = generatedUserId > 0;
      if (created) {
         JOptionPane.showMessageDialog(dialog, "Account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
         dialog.dispose();
      } else {
         JOptionPane.showMessageDialog(dialog, "Failed to create account. Username may already exist.", "Sign-Up Error", JOptionPane.ERROR_MESSAGE);
      }
   }

   private static void addSignupField(JPanel form, JLabel label, JTextField field, Color background) {
      JPanel group = new JPanel();
      group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
      group.setBackground(background);
      group.setAlignmentX(Component.CENTER_ALIGNMENT);
      label.setAlignmentX(Component.LEFT_ALIGNMENT);
      field.setAlignmentX(Component.LEFT_ALIGNMENT);
      group.add(label);
      group.add(Box.createVerticalStrut(5));
      group.add(field);
      form.add(group);
   }

   private static ImageIcon loadSignupLogo() {
      URL logoResource = VisualElements.class.getResource("/images/matt-logo-1.svg");
      if (logoResource == null) {
         throw new IllegalStateException("Matt logo resource not found");
      }

      SVGDocument document = new SVGLoader().load(logoResource);
      if (document == null) {
         throw new IllegalStateException("JSVG returned no document for the Matt logo");
      }

      BufferedImage image = new BufferedImage(160, 114, BufferedImage.TYPE_INT_ARGB);
      Graphics2D graphics = image.createGraphics();
      try {
         graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         document.render(null, graphics, new ViewBox(0, 0, image.getWidth(), image.getHeight()));
      } finally {
         graphics.dispose();
      }
      return new ImageIcon(image);
   }

   private static void setDialogBackground(Component component, Color background) {
      if (!(component instanceof JButton)) {
         component.setBackground(background);
      }
      if (component instanceof Container container) {
         for (Component child : container.getComponents()) {
            setDialogBackground(child, background);
         }
      }
   }

   /** Builds a clickable application card with job and company details. */
   public static Button buildApplicationListCard(String jobTitle, String companyName, int age, String timeUnit,
         int fileCount, EventHandler<ActionEvent> onOpen) {
      Label title = new Label(safe(jobTitle)); title.getStyleClass().add("application-card-title");
      Label company = new Label(safe(companyName)); company.getStyleClass().add("application-card-company");
      VBox details = new VBox(8, title, new HBox(16, company, buildFileCountBadge(fileCount)));
      Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
      FontIcon arrow = new FontIcon(ApplicationScreenText.APPLICATION_CARD_OPEN_ICON);
      arrow.getStyleClass().add("application-card-arrow-icon");
      HBox cardContent = new HBox(18, details, spacer, buildRelativeTimeLabel(age, timeUnit, true), arrow);
      cardContent.setAlignment(Pos.CENTER_LEFT);
      cardContent.setMaxWidth(Double.MAX_VALUE);
      Button card = new Button();
      card.setGraphic(cardContent);
      card.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
      card.setAlignment(Pos.CENTER_LEFT);
      card.setMaxWidth(Double.MAX_VALUE);
      card.setOnAction(onOpen);
      card.setAccessibleText(safe(jobTitle) + " at " + safe(companyName));
      card.getStyleClass().add("application-card-button");
      return card;
   }

   /** Builds a scrollable job summary with details. */
   public static VBox buildJobDetailsContainer(String title, String location, String company, String payRange,
         String requirements, String niceToHave, String benefits) {
      VBox content = new VBox(14);
      Label heading = new Label(safe(title) + "  –  " + safe(location)); heading.getStyleClass().add("job-detail-title");
      Label companyLabel = new Label(safe(company)); companyLabel.getStyleClass().add("job-detail-company");
      Label pay = new Label(safe(payRange)); pay.getStyleClass().add("job-detail-pay");
      content.getChildren().addAll(heading, companyLabel, pay,
            detailSection("Requirements", requirements), detailSection("Nice to Have", niceToHave),
            detailSection("Benefits", benefits));
      ScrollPane scroll = VisualElements.scrollable(content);
      VBox container = new VBox(scroll); container.setPrefSize(540, 560);
      VBox.setVgrow(scroll, Priority.ALWAYS);
      container.getStyleClass().add("job-details-container");
      return container;
   }

   /** Builds a bordered template panel containing cards for template options. */
   public static VBox buildTemplatePickerContainer(List<TemplateOption> templates, double width, double height) {
      Objects.requireNonNull(templates, "templates");
      FlowPane cards = new FlowPane(16, 16); cards.getStyleClass().add("template-card-grid");
      for (TemplateOption template : templates) {
         cards.getChildren().add(Buttons.buildTemplatePickerCard(template.name(), template.onSelect(),
               225, 185, template.styleClass()));
      }
      VBox picker = new VBox(cards);
      picker.setPrefSize(width, height);
      picker.getStyleClass().add("template-picker-container");
      return picker;
   }

   public record TemplateOption(String name, EventHandler<ActionEvent> onSelect, String styleClass) {
      public TemplateOption(String name, EventHandler<ActionEvent> onSelect) { this(name, onSelect, "template-card-teal"); }
   }

   /** Shows job posting text in a bordered preview with a bottom fade. */
   public static VBox buildJobDetailsRawTextContainer(String rawText) {
      Label text = new Label(safe(rawText)); text.setWrapText(true); text.setMaxWidth(Double.MAX_VALUE); text.getStyleClass().add("raw-job-text");
      ScrollPane scroll = VisualElements.scrollable(text); scroll.setPrefViewportHeight(420); scroll.setMinWidth(0); scroll.setMaxWidth(Double.MAX_VALUE);
      Region fadeOverlay = new Region(); fadeOverlay.setMouseTransparent(true); fadeOverlay.setMinHeight(90);
      fadeOverlay.setMaxHeight(90); StackPane.setAlignment(fadeOverlay, Pos.BOTTOM_CENTER);
      StackPane preview = new StackPane(scroll, fadeOverlay); preview.setPrefHeight(420); preview.setMinWidth(0); preview.setMaxWidth(Double.MAX_VALUE);
      preview.getStyleClass().add("raw-job-fade");
      StackPane.setAlignment(scroll, Pos.TOP_RIGHT);
      VBox container = new VBox(preview); container.setMinWidth(0); container.setMaxWidth(Double.MAX_VALUE); container.getStyleClass().add("raw-job-container");
      return container;
   }

   private static String safe(String input) {
      return input == null ? "" : input;
   }

   private static Label buildFileCountBadge(int count) {
      Label badge = new Label(count + " files");
      badge.getStyleClass().add("file-count-badge");
      return badge;
   }

   private static Label buildRelativeTimeLabel(int age, String unit, boolean compact) {
      Label timeLabel = new Label(age + " " + safe(unit) + (age == 1 ? "" : "s") + " ago");
      timeLabel.getStyleClass().add("relative-time-label");
      return timeLabel;
   }

   private static VBox detailSection(String title, String content) {
      Label heading = new Label(title);
      heading.getStyleClass().add("job-detail-section-heading");
      Label body = new Label(safe(content));
      body.setWrapText(true);
      body.getStyleClass().add("job-detail-section-body");
      return new VBox(4, heading, body);
   }
}