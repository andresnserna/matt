/**
 * Architecture layer: model
 * Data-access component that persists and retrieves app settings.
 */
package edu.stedwards.matt.dao;


/**
 * Handles raw database access for app settings and preferences.
 * This class is called by the service layer whenever settings need to be loaded, saved,
 * or updated, and it should keep SQL operations focused on the settings table only.
 * It is not responsible for JavaFX UI construction, window creation, or business rules.
 */
public class SettingsDAO {

   /** TODO: Query and return the saved Ollama model name for this user. */
   public String getOllamaModelName(int userId) { return null; }

   /** TODO: Persist the selected Ollama model name for this user. */
   public void setOllamaModelName(int userId, String modelName) { }

   /** TODO: Query and return the saved downloads directory for this user. */
   public String getOllamaDownloadsLocation(int userId) { return null; }

   /** TODO: Persist the downloads directory for this user. */
   public void setOllamaDownloadsLocation(int userId, String path) { }

   /** TODO: Persist the latest locally detected Ollama status for this user. */
   public void updateOllamaStatus(int userId, String status) { }
}
