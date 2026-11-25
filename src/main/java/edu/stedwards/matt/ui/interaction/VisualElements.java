package edu.stedwards.matt.ui.interaction;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.parser.SVGLoader;
import com.github.weisj.jsvg.ui.jfx.FXSVGCanvas;
import edu.stedwards.matt.dao.UserDAO;
import edu.stedwards.matt.models.OnboardingSession;
import edu.stedwards.matt.services.UserService;
import edu.stedwards.matt.models.entity.User;
import edu.stedwards.matt.ui.TextStyles;
import edu.stedwards.matt.ui.layouts.onboarding.OnboardingWelcome;
import edu.stedwards.matt.ui.layouts.other.MainLayout;
import fr.brouillard.oss.cssfx.CSSFX;
import edu.stedwards.matt.ui.labels.ApplicationScreenText;
import edu.stedwards.matt.ui.labels.LoginScreenText;
import edu.stedwards.matt.ui.labels.MainScreenText;
import edu.stedwards.matt.ui.labels.OnboardingScreenText;
import edu.stedwards.matt.ui.labels.OtherScreenText;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
      return mainSideBar(null);
   }

   /** Main navigation sidebar with actions delegated to its owning MainLayout. */
   public VBox mainSideBar(MainLayout mainLayout) {
      VBox sideBar = new VBox();
      sideBar.getStyleClass().add("sidebar");
      sideBar.setMinWidth(COLLAPSED_SIDEBAR_WIDTH);
      sideBar.setPrefWidth(getSidebarWidth());
      double buttonWidth = getSidebarWidth() - (2 * DRAWER_SIDE_INSET) - 4;

      VBox menuButtons = new VBox();
      menuButtons.setSpacing(8);

      // Button homeButton = Buttons.button(MainScreenText.BUTTON_HOME_ICON, MainScreenText.BUTTON_HOME, null, buttonWidth, "sidebar-button");
      Button templatesButton = Buttons.button(MainScreenText.BUTTON_TEMPLATES_ICON, MainScreenText.BUTTON_TEMPLATES, null, buttonWidth, "sidebar-button");
      Button applicationsButton = Buttons.button(MainScreenText.BUTTON_APPLICATIONS_ICON, MainScreenText.BUTTON_APPLICATIONS, null, buttonWidth, "sidebar-button");
      Button myInfoButton = Buttons.button(MainScreenText.BUTTON_MY_INFO_ICON, MainScreenText.BUTTON_MY_INFO, null, buttonWidth, "sidebar-button");
      Button settingsButton = Buttons.button(MainScreenText.BUTTON_SETTINGS_ICON, MainScreenText.BUTTON_SETTINGS, null, buttonWidth, "sidebar-button");

      if (mainLayout != null) {
         // homeButton.setOnAction(event -> mainLayout.handleHomeNavigation());
         templatesButton.setOnAction(event -> mainLayout.handleTemplatesNavigation());
         applicationsButton.setOnAction(event -> mainLayout.handleApplicationsNavigation());
         myInfoButton.setOnAction(event -> mainLayout.handleMyInfoNavigation());
         settingsButton.setOnAction(event -> mainLayout.handleSettingsNavigation());
      }
      // menuButtons.getChildren().addAll(homeButton, templatesButton, applicationsButton, myInfoButton, settingsButton);
      menuButtons.getChildren().addAll(templatesButton, applicationsButton, myInfoButton, settingsButton);

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
      return buildUploadMethodGroup3(width, null, null, null);
   }

   /** Builds job-posting inputs and sends user actions back to the owning screen. */
   public static VBox buildUploadMethodGroup3(double width, EventHandler<ActionEvent> onUpload,
         Consumer<String> onUrlChanged, Consumer<String> onTextChanged) {
      int uploadWidth = (int) Math.round(width);
      HBox uploadControl = new Buttons().buildUploadButton(uploadWidth, 8, onUpload);
      TextField jobUrl = Inputs.inputField(ApplicationScreenText.JOB_URL_PLACEHOLDER, width, width);
      javafx.scene.control.TextArea jobDescription = Inputs.textArea(
            ApplicationScreenText.JOB_DESCRIPTION_PLACEHOLDER, width, width, 180, 260);
      jobDescription.setWrapText(true);

      uploadControl.setPrefWidth(width);
      uploadControl.setMinWidth(0);
      uploadControl.setMaxWidth(width);
      jobUrl.setMinWidth(0);
      jobUrl.setMaxWidth(width);
      jobDescription.setMinWidth(0);
      jobDescription.setMaxWidth(width);
      if (onUrlChanged != null) {
         jobUrl.textProperty().addListener((observable, oldValue, newValue) -> onUrlChanged.accept(newValue));
      }
      if (onTextChanged != null) {
         jobDescription.textProperty().addListener((observable, oldValue, newValue) -> onTextChanged.accept(newValue));
      }

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
   public static VBox buildLoginBox(BiConsumer<String, String> onLogin, EventHandler<ActionEvent> onSignup,
         double boxWidth, double fieldWidth, double buttonWidth) {
      VBox box = new VBox(18);
      box.setAlignment(Pos.CENTER);
      box.setPrefWidth(boxWidth);
      box.setMaxWidth(boxWidth);
      box.getStyleClass().add("login-box");
      TextField usernameField = Inputs.inputField(LoginScreenText.USERNAME_PLACEHOLDER, fieldWidth, fieldWidth, "login-field-text");
      PasswordField passwordField = Inputs.passwordField(LoginScreenText.PASSWORD_PLACEHOLDER, fieldWidth, fieldWidth, "login-field-text");
      box.getChildren().addAll(
            Inputs.wrapInputFieldWithIcon(usernameField, LoginScreenText.USERNAME_ICON, "login-field"),
            Inputs.wrapInputFieldWithIcon(passwordField, LoginScreenText.PASSWORD_ICON, "login-field"),
            buildCheckBoxWLabel());
      Button login = Buttons.button(LoginScreenText.LOGIN_BUTTON, null, buttonWidth, "login-submit");
      login.setOnAction(event -> onLogin.accept(usernameField.getText(), passwordField.getText()));
      Button signup = Buttons.button(LoginScreenText.SIGNUP_BUTTON, onSignup, 180, "signup-link-button");
      box.getChildren().addAll(login, signup);
      return box;
   }

   public static void showSignupDialog(ActionEvent event) {
      if (!(event.getSource() instanceof Node source) || source.getScene() == null) {
         throw new IllegalArgumentException("The signup dialog requires a login screen source node");
      }

      TextField usernameField = new TextField();
      usernameField.setPromptText(LoginScreenText.USERNAME_PLACEHOLDER);
      usernameField.getStyleClass().add("signup-field");

      TextField emailField = new TextField();
      emailField.setPromptText("Email (e.g., name@domain.com)");
      emailField.getStyleClass().add("signup-field");

      PasswordField passwordField = new PasswordField();
      passwordField.setPromptText(LoginScreenText.PASSWORD_PLACEHOLDER);
      passwordField.getStyleClass().add("signup-field");

      Label usernameLabel = new Label(LoginScreenText.USERNAME_PLACEHOLDER);
      usernameLabel.getStyleClass().add("signup-field-label");
      usernameLabel.setLabelFor(usernameField);

      Label emailLabel = new Label("Email");
      emailLabel.getStyleClass().add("signup-field-label");
      emailLabel.setLabelFor(emailField);

      Label passwordLabel = new Label(LoginScreenText.PASSWORD_PLACEHOLDER);
      passwordLabel.getStyleClass().add("signup-field-label");
      passwordLabel.setLabelFor(passwordField);

      GridPane fields = new GridPane();
      fields.getStyleClass().add("signup-fields");
      fields.add(usernameLabel, 0, 0);
      fields.add(usernameField, 1, 0);
      fields.add(emailLabel, 0, 1);
      fields.add(emailField, 1, 1);
      fields.add(passwordLabel, 0, 2);
      fields.add(passwordField, 1, 2);

      Label message = new Label(LoginScreenText.SIGNUP_MESSAGE);
      message.getStyleClass().add("signup-message");
      message.setWrapText(true);
      message.setMaxWidth(Double.MAX_VALUE);

      Label errorLabel = new Label("");
      errorLabel.getStyleClass().add("signup-error");
      errorLabel.setWrapText(true);
      errorLabel.setMaxWidth(Double.MAX_VALUE);

      FXSVGCanvas logo = loadSignupLogo();
      HBox actions = new HBox();
      actions.getStyleClass().add("signup-actions");
      Region spacer = new Region();
      HBox.setHgrow(spacer, Priority.ALWAYS);
      Button cancelButton = new Button(LoginScreenText.CANCEL_BUTTON);
      cancelButton.getStyleClass().add("signup-cancel-button");
      Button signupButton = new Button(LoginScreenText.SIGNUP_BUTTON);
      signupButton.getStyleClass().add("signup-submit-button");
      actions.getChildren().addAll(spacer, cancelButton, signupButton);

      VBox content = new VBox(logo, message, fields, errorLabel, actions);
      content.getStyleClass().add("signup-dialog");
      Scene scene = new Scene(content);
      scene.getStylesheets().addAll(source.getScene().getStylesheets());

      Stage dialog = new Stage();
      dialog.setTitle(LoginScreenText.SIGNUP_BUTTON);
      dialog.initOwner(source.getScene().getWindow());
      dialog.initModality(Modality.WINDOW_MODAL);
      dialog.setScene(scene);
      dialog.setResizable(false);

      Runnable submitAction = () -> handleSignupSubmit(
            usernameField.getText(),
            emailField.getText(),
            passwordField.getText(),
            errorLabel,
            dialog,
            source);

      cancelButton.setOnAction(e -> handleSignupCancel(dialog));
      signupButton.setOnAction(e -> submitAction.run());
      passwordField.setOnAction(e -> submitAction.run());
      try {
         CSSFX.start();
      } catch (Exception e) {
         System.err.println("CSSFX could not start: " + e);
      }
      dialog.showAndWait();
   }

   private static void handleSignupSubmit(
         String username,
         String email,
         String password,
         Label errorLabel,
         Stage dialog,
         Node source) {
      UserService userService = new UserService();
      List<String> validationErrors = userService.validateRegistration(username, email, password);

      if (!validationErrors.isEmpty()) {
         errorLabel.setText(String.join("\n", validationErrors));
         dialog.sizeToScene();
         return;
      }

      try {
         User createdUser = userService.registerUser(username, email, password, "", "");
         if (createdUser == null) {
            errorLabel.setText("That username or email is already in use.");
            dialog.sizeToScene();
            return;
         }

         OnboardingSession.getInstance().setUserProfile(createdUser);
         dialog.close();
         if (source != null && source.getScene() != null) {
            source.getScene().setRoot(new OnboardingWelcome());
         }
      } catch (IllegalArgumentException e) {
         errorLabel.setText(e.getMessage());
         dialog.sizeToScene();
      }
   }

   /** Close the sign-up window when Cancel is pressed. */
   public static void handleSignupCancel(Stage dialog) {
      if (dialog != null) {
         dialog.close();
      }
   }

   /** Creates a local account; profile information is collected during onboarding. */
   public static void handleSignupSubmit(String username, String password, Stage dialog) {
      if (username == null || username.isBlank() || password == null || password.isBlank()) return;
      if (new UserDAO().createLocalAccount(username.trim(), password) > 0) dialog.close();
   }

   private static FXSVGCanvas loadSignupLogo() {
      URL logoResource = VisualElements.class.getResource("/images/matt-logo-1.svg");
      if (logoResource == null) {
         throw new IllegalStateException("Matt logo resource not found");
      }

      SVGDocument document = new SVGLoader().load(logoResource);
      if (document == null) {
         throw new IllegalStateException("JSVG returned no document for the Matt logo");
      }
      FXSVGCanvas logo = new FXSVGCanvas();
      logo.setRenderBackend(FXSVGCanvas.RenderBackend.AWT);
      logo.setDocument(document);
      logo.setShowTransparentPattern(false);
      logo.getStyleClass().add("signup-logo");
      return logo;
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
      return buildJobDetailsContainer(title, location, company, payRange, "", requirements, niceToHave, benefits);
   }

   /** Builds a scrollable job summary with all parsed posting details. */
   public static VBox buildJobDetailsContainer(String title, String location, String company, String payRange,
         String responsibilities, String requirements, String niceToHave, String benefits) {
      System.out.println("[VisualElements] Building job details: title_present=" + hasContent(title)
            + ", location_present=" + hasContent(location) + ", company_present=" + hasContent(company)
            + ", pay='" + safe(payRange) + "', responsibilities_present=" + hasContent(responsibilities)
            + ", requirements_present=" + hasContent(requirements)
            + ", nice_to_have_present=" + hasContent(niceToHave)
            + ", benefits_present=" + hasContent(benefits) + ".");
      VBox content = new VBox(14);
      Label heading = new Label(safe(title) + "  –  " + safe(location)); heading.getStyleClass().add("job-detail-title");
      Label companyLabel = new Label(safe(company)); companyLabel.getStyleClass().add("job-detail-company");
      Label pay = new Label(safe(payRange)); pay.getStyleClass().add("job-detail-pay");
      content.getChildren().addAll(heading, companyLabel, pay);
      addDetailSection(content, "Responsibilities", responsibilities);
      addDetailSection(content, "Requirements", requirements);
      addDetailSection(content, "Nice to Have", niceToHave);
      addDetailSection(content, "Benefits", benefits);
      ScrollPane scroll = VisualElements.scrollable(content);
      VBox container = new VBox(scroll); container.setPrefSize(540, 560);
      VBox.setVgrow(scroll, Priority.ALWAYS);
      container.getStyleClass().add("job-details-container");
      return container;
   }

   private static void addDetailSection(VBox content, String title, String value) {
      if (value != null && !value.isBlank()) {
         System.out.println("[VisualElements] Adding '" + title + "' detail section.");
         content.getChildren().add(detailSection(title, value));
      } else {
         System.out.println("[VisualElements] Skipping empty '" + title + "' detail section.");
      }
   }

   private static boolean hasContent(String value) {
      return value != null && !value.isBlank();
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

   /** Builds a score ring and message. A null or out-of-range score displays the unavailable state. */
   public static HBox buildScoreWheel(Double score, FontAwesomeSolid icon, String unavailableMessage,
         String oneOfFive, String twoOfFive, String threeOfFive, String fourOfFive, String fiveOfFive) {
      boolean available = score != null && Double.isFinite(score) && score >= 0.0 && score <= 1.0;
      String message = unavailableMessage;
      if (available) {
         int band = Math.min(5, (int) Math.floor(score * 5.0) + 1);
         message = switch (band) {
            case 1 -> oneOfFive;
            case 2 -> twoOfFive;
            case 3 -> threeOfFive;
            case 4 -> fourOfFive;
            default -> fiveOfFive;
         };
      }

      ProgressIndicator ring = new ProgressIndicator(available ? score : 0.0);
      ring.setMinSize(58, 58);
      ring.setPrefSize(58, 58);
      ring.setMaxSize(58, 58);
      ring.getStyleClass().add("application-score-ring");
      if (!available) ring.getStyleClass().add("application-score-unavailable");
      FontIcon centerIcon = new FontIcon(icon);
      centerIcon.getStyleClass().add("application-score-icon");
      if (!available) centerIcon.getStyleClass().add("application-score-icon-unavailable");
      centerIcon.setTranslateY(-9);
      Circle centerMask = new Circle(15, Color.WHITE);
      centerMask.setStroke(null);
      centerMask.setMouseTransparent(true);
      centerMask.setTranslateY(-9);
      StackPane indicator = new StackPane(ring, centerMask, centerIcon);
      indicator.getStyleClass().add("application-score-indicator");

      Label scoreMessage = new Label(message);
      scoreMessage.setWrapText(true);
      scoreMessage.setMinWidth(0);
      scoreMessage.setMaxWidth(Double.MAX_VALUE);
      scoreMessage.getStyleClass().add("application-score-message");
      HBox.setHgrow(scoreMessage, Priority.ALWAYS);
      HBox result = new HBox(14, indicator, scoreMessage);
      result.setAlignment(Pos.CENTER_LEFT);
      result.setMinWidth(0);
      result.setMaxWidth(Double.MAX_VALUE);
      result.getStyleClass().add("application-score");
      return result;
   }
}
