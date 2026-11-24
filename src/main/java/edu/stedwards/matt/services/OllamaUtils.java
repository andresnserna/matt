package edu.stedwards.matt.services;

import java.util.Map;

/** Parsing helpers for the textual output returned by local Ollama commands. */
public final class OllamaUtils {
   private OllamaUtils() { }

   /** TODO: Parse model architecture, parameters, and attributes from `ollama show` output. */
   public static Map<String, String> parseModelDetails(String showOutput) { return Map.of(); }

   /** TODO: Parse the active runner/model name from `ollama ps` output. */
   public static String parseCurrentRunner(String psOutput) { return null; }

   /** TODO: Interpret `ollama ps` output as Running, Idle, or Disconnected. */
   public static String parseStatus(String psOutput) { return null; }
}
