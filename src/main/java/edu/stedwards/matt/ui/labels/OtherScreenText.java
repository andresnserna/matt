package edu.stedwards.matt.ui.labels;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

public class OtherScreenText {
   public static final FontAwesomeSolid DRAWER_DOWN_ICON = FontAwesomeSolid.CHEVRON_DOWN;
   public static final FontAwesomeSolid DRAWER_UP_ICON = FontAwesomeSolid.CHEVRON_UP;
   public static final FontAwesomeSolid SIDEBAR_OPEN_ICON = FontAwesomeSolid.ANGLE_DOUBLE_RIGHT;
   public static final FontAwesomeSolid SIDEBAR_CLOSE_ICON = FontAwesomeSolid.ANGLE_DOUBLE_LEFT;
   public static final FontAwesomeSolid RECENT_ITEM_EXTERNAL_LINK_ICON = FontAwesomeSolid.EXTERNAL_LINK_ALT;

   public static final String PLACEHOLDER_TITLE = "PLACEHOLDER";
   public static final String PLACEHOLDER_MESSAGE = "This is placeholder text for the screen this text is in. the actual content will differ.";
   public static final String APP_VERSION = "Version: " + edu.stedwards.matt.SystemInfo.projectVersion();

   public static final String SETTINGS_TITLE = "Settings";
   public static final String SETTINGS_LOGOUT_BUTTON = "Log Out...";
   public static final String SETTINGS_SAVE_BUTTON = "Save";
   public static final String SETTINGS_OLLAMA_STATUS_LABEL = "Ollama Status:";
   public static final String SETTINGS_MODEL_PICKER_INSTRUCTION = "Select the language model you would like to use when generating application materials. (models listed are installed on your machine)";
   public static final String SETTINGS_MODEL_LABEL = "Model";
   public static final String SETTINGS_CAPABILITIES_LABEL = "Capabilities";
   public static final String SETTINGS_DOWNLOADS_LABEL = "Download files to…";
   public static final String SETTINGS_REFRESH_BUTTON = "Refresh";
   public static final String SETTINGS_SELECT_BUTTON = "Select…";
   public static final String SETTINGS_MODEL_PROMPT = "No models installed. Please install models using ollama pull.";
   public static final String SETTINGS_DOWNLOADS_PROMPT = "Enter directory name";
   public static final String SETTINGS_DIRECTORY_CHOOSER_TITLE = "Select Downloads Location";
   public static final String SETTINGS_DISCONNECTED = "Disconnected";
   public static final String SETTINGS_UNAVAILABLE = "unavailable";
}
