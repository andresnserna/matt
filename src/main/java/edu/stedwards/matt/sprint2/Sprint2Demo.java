package edu.stedwards.matt.sprint2;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Sprint2Demo {

    private enum Screen {
        WELCOME,
        LOGIN,
        ONBOARDING_UPLOAD,
        PROFILE_FOUND,
        PROFILE_MISSING,
        PROFILE_VERIFY,
        HOME,
        APPLICATIONS,
        JOB_SOURCE,
        JOB_FOUND,
        JOB_MISSING,
        JOB_VERIFY,
        GENERATE_MATERIALS,
        GENERATED,
        NOT_IMPLEMENTED
    }

    private final Scanner input = new Scanner(System.in);
    private final DemoBackend backend = new DemoBackend();
    private final Map<String, String> profileFields = new LinkedHashMap<>();
    private final Map<String, String> jobFields = new LinkedHashMap<>();
    private Screen screen = Screen.WELCOME;
    private Screen returnFromNotImplemented = Screen.HOME;
    private String generatedTemplate = "";
    private boolean running = true;

    public static void main(String[] args) {
        new Sprint2Demo().run();
    }

    private void run() {
        System.out.println("Welcome to matt");
        System.out.println("Type \"matt quit\" at any prompt to exit the demo.");
        System.out.println("Type \"back\" at a text prompt to cancel and return to the previous screen.");

        while (running) {
            switch (screen) {
                case WELCOME -> showWelcome();
                case LOGIN -> showLogin();
                case ONBOARDING_UPLOAD -> uploadResume();
                case PROFILE_FOUND -> showProfileFound();
                case PROFILE_MISSING -> fillMissingProfileFields();
                case PROFILE_VERIFY -> verifyProfile();
                case HOME -> showHome();
                case APPLICATIONS -> showApplications();
                case JOB_SOURCE -> uploadJobPosting();
                case JOB_FOUND -> showJobFound();
                case JOB_MISSING -> fillMissingJobFields();
                case JOB_VERIFY -> verifyJob();
                case GENERATE_MATERIALS -> showGenerateMaterials();
                case GENERATED -> showGenerated();
                case NOT_IMPLEMENTED -> showNotImplemented();
            }
        }

        System.out.println("Demo stopped.");
    }

    private void showWelcome() {
        printHeading("Welcome to matt");
        int choice = choose(List.of("Log in"), "Choose an option");
        if (choice == 1) {
            screen = Screen.LOGIN;
        } else if (choice == 0) {
            running = false;
        }
    }

    private void showLogin() {
        printHeading("Log in");
        System.out.println("Demo sign-in: no credentials are required.");
        int choice = choose(List.of("Continue to onboarding"), "Choose an option");
        if (choice == 1) {
            if (backend.logIn("demo-user")) {
                screen = Screen.ONBOARDING_UPLOAD;
            }
        } else if (choice == 0) {
            screen = Screen.WELCOME;
        }
    }

    private void uploadResume() {
        printHeading("Start onboarding - upload your resume");
        String resume = readInput("Paste a resume excerpt or enter a file path: ");
        if (resume == null) {
            screen = Screen.LOGIN;
            return;
        }

        System.out.println("Parsing resume...");
        profileFields.clear();
        profileFields.putAll(backend.parseResume(resume));
        screen = Screen.PROFILE_FOUND;
    }

    private void showProfileFound() {
        printHeading("Here's the fields we found");
        printFields(profileFields);
        int choice = choose(List.of("Continue to missing fields"), "Choose an option");
        if (choice == 1) {
            screen = Screen.PROFILE_MISSING;
        } else if (choice == 0) {
            screen = Screen.ONBOARDING_UPLOAD;
        }
    }

    private void fillMissingProfileFields() {
        printHeading("Here's what we couldn't fill in");
        List<String> missingFields = printMissingFields(profileFields);
        List<String> options = new ArrayList<>(missingFields);
        options.add("Continue with what we have");
        int choice = choose(options, "Choose a field to edit, or continue");

        if (choice == 0) {
            screen = Screen.PROFILE_FOUND;
        } else if (choice == options.size()) {
            screen = Screen.PROFILE_VERIFY;
        } else {
            String field = missingFields.get(choice - 1);
            editMissingField(field, profileFields);
        }
    }

    private void verifyProfile() {
        printHeading("Verify your fields");
        printFields(profileFields);
        int choice = choose(List.of("Save", "Edit missing fields"), "Choose an option");
        if (choice == 1) {
            if (backend.saveProfile(profileFields)) {
                System.out.println("Profile saved.");
                screen = Screen.HOME;
            }
        } else if (choice == 2) {
            screen = Screen.PROFILE_MISSING;
        } else if (choice == 0) {
            screen = Screen.PROFILE_MISSING;
        }
    }

    private void showHome() {
        printHeading("Home");
        int choice = choose(List.of("Applications", "Templates", "Personal info", "Settings"),
                "Choose an option");
        switch (choice) {
            case 0 -> screen = Screen.PROFILE_VERIFY;
            case 1 -> screen = Screen.APPLICATIONS;
            case 2 -> showNotImplementedFrom(Screen.HOME, "Templates");
            case 3 -> showNotImplementedFrom(Screen.HOME, "Personal info");
            case 4 -> showNotImplementedFrom(Screen.HOME, "Settings");
            default -> {
            }
        }
    }

    private void showApplications() {
        printHeading("Home - My applications");
        int choice = choose(List.of("New app", "My applications"), "Choose an option");
        if (choice == 0) {
            screen = Screen.HOME;
        } else if (choice == 1) {
            screen = Screen.JOB_SOURCE;
        } else if (choice == 2) {
            showNotImplementedFrom(Screen.APPLICATIONS, "My applications");
        }
    }

    private void uploadJobPosting() {
        printHeading("Home - My applications - New app");
        int choice = choose(List.of("URL", "Copy/paste", "File upload"), "Choose a posting source");
        if (choice == 0) {
            screen = Screen.APPLICATIONS;
            return;
        }

        String source = switch (choice) {
            case 1 -> "URL";
            case 2 -> "Copy/paste";
            case 3 -> "File upload";
            default -> null;
        };
        if (source == null) {
            return;
        }

        String data = switch (choice) {
            case 1 -> readInput("Enter the job posting URL: ");
            case 2 -> readMultilineInput(
                    "Paste the job posting. Enter END on its own line when finished:\n");
            case 3 -> readInput("Enter a file path or paste the file contents: ");
            default -> null;
        };
        if (data == null) {
            return;
        }

        System.out.println("Parsing job posting...");
        jobFields.clear();
        jobFields.putAll(backend.parseJobPosting(source, data));
        screen = Screen.JOB_FOUND;
    }

    private void showJobFound() {
        printHeading("Here's the fields we found");
        printFields(jobFields);
        int choice = choose(List.of("Continue to missing fields"), "Choose an option");
        if (choice == 1) {
            screen = Screen.JOB_MISSING;
        } else if (choice == 0) {
            screen = Screen.JOB_SOURCE;
        }
    }

    private void fillMissingJobFields() {
        printHeading("Here's what we couldn't fill in");
        List<String> missingFields = printMissingFields(jobFields);
        List<String> options = new ArrayList<>(missingFields);
        options.add("Continue with what we have");
        int choice = choose(options, "Choose a field to edit, or continue");

        if (choice == 0) {
            screen = Screen.JOB_FOUND;
        } else if (choice == options.size()) {
            screen = Screen.JOB_VERIFY;
        } else {
            String field = missingFields.get(choice - 1);
            editMissingField(field, jobFields);
        }
    }

    private void verifyJob() {
        printHeading("Verify your fields");
        printFields(jobFields);
        int choice = choose(List.of("Save", "Edit missing fields"), "Choose an option");
        if (choice == 1) {
            if (backend.saveApplication(jobFields)) {
                System.out.println("Application saved.");
                screen = Screen.GENERATE_MATERIALS;
            }
        } else if (choice == 2) {
            screen = Screen.JOB_MISSING;
        } else if (choice == 0) {
            screen = Screen.JOB_MISSING;
        }
    }

    private void showGenerateMaterials() {
        printHeading("Home - My applications - New app - Generate materials");
        int choice = choose(List.of("Generate cover letter", "Generate tailored resume"),
                "Choose an option");
        if (choice == 0) {
            screen = Screen.JOB_VERIFY;
        } else if (choice == 1 || choice == 2) {
            generatedTemplate = choice == 1 ? "Cover Letter" : "Tailored Resume";
            backend.generateMaterials(generatedTemplate, jobFields);
            screen = Screen.GENERATED;
        }
    }

    private void showGenerated() {
        printHeading("Generate " + generatedTemplate);
        System.out.println("Demo output: " + generatedTemplate + " generation is ready to be wired up.");
        int choice = choose(List.of("Finish demo"), "Choose an option");
        if (choice == 0) {
            screen = Screen.GENERATE_MATERIALS;
        } else if (choice == 1) {
            running = false;
        }
    }

    private void showNotImplementedFrom(Screen returnScreen, String feature) {
        returnFromNotImplemented = returnScreen;
        System.out.println(feature + " (not implemented)");
        screen = Screen.NOT_IMPLEMENTED;
    }

    private void showNotImplemented() {
        printHeading("Not implemented");
        int choice = choose(List.of(), "Choose an option");
        if (choice == 0) {
            screen = returnFromNotImplemented;
        }
    }

    private int choose(List<String> options, String prompt) {
        System.out.println();
        for (int i = 0; i < options.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, options.get(i));
        }
        System.out.println("0. Back");

        while (running) {
            String response = readInput(prompt + ": ");
            if (response == null) {
                return 0;
            }
            try {
                int choice = Integer.parseInt(response);
                if (choice >= 0 && choice <= options.size()) {
                    return choice;
                }
            } catch (NumberFormatException ignored) {
                // Re-prompt with the valid range below.
            }
            System.out.println("Choose a number from 0 to " + options.size() + ".");
        }
        return 0;
    }

    private void editMissingField(String field, Map<String, String> fields) {
        printHeading("Edit " + field);
        String value = readInput("Enter " + field + ": ");
        if (value == null) {
            return;
        }

        int choice = choose(List.of("Accept edit", "Discard edit"), "Choose an option");
        if (choice == 1) {
            fields.put(field, value);
        }
    }

    private String readInput(String prompt) {
        System.out.print(prompt);
        System.out.flush();
        if (!input.hasNextLine()) {
            running = false;
            return null;
        }
        String response = input.nextLine().trim();
        if (response.equalsIgnoreCase("matt quit")) {
            running = false;
            return null;
        }
        if (response.equalsIgnoreCase("back")) {
            return null;
        }
        return response;
    }

    private String readMultilineInput(String prompt) {
        System.out.print(prompt);
        StringBuilder text = new StringBuilder();
        while (running && input.hasNextLine()) {
            String line = input.nextLine();
            if (line.trim().equalsIgnoreCase("matt quit")) {
                running = false;
                return null;
            }
            if (line.trim().equalsIgnoreCase("back")) {
                return null;
            }
            if (line.trim().equalsIgnoreCase("END")) {
                return text.toString().trim();
            }
            if (!text.isEmpty()) {
                text.append(System.lineSeparator());
            }
            text.append(line);
        }
        running = false;
        return null;
    }

    private List<String> printMissingFields(Map<String, String> fields) {
        List<String> missingFields = new ArrayList<>();
        for (Map.Entry<String, String> field : fields.entrySet()) {
            if (field.getValue() == null || field.getValue().isBlank()) {
                missingFields.add(field.getKey());
            }
        }
        if (missingFields.isEmpty()) {
            System.out.println("No missing fields.");
        } else {
            for (int i = 0; i < missingFields.size(); i++) {
                System.out.printf("%d. %s: [blank]%n", i + 1, missingFields.get(i));
            }
        }
        return missingFields;
    }

    private void printFields(Map<String, String> fields) {
        for (Map.Entry<String, String> field : fields.entrySet()) {
            String value = field.getValue();
            System.out.printf("- %s: %s%n", field.getKey(),
                    value == null || value.isBlank() ? "[blank]" : value);
        }
    }

    private void printHeading(String heading) {
        System.out.println();
        System.out.println("=== " + heading + " ===");
    }
}
