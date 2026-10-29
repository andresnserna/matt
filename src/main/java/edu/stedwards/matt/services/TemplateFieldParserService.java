package edu.stedwards.matt.services;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/** Extracts unique bracketed dynamic fields from serialized template HTML. */
public class TemplateFieldParserService {
    private static final Pattern FIELD_PATTERN = Pattern.compile("\\[([^\\[\\]]+)]");

    public Set<String> extractFields(String html) {
        Set<String> fields = new LinkedHashSet<>();
        if (html == null || html.isBlank()) {
            return fields;
        }

        Document document = Jsoup.parse(html);
        Matcher matcher = FIELD_PATTERN.matcher(document.body().wholeText());
        while (matcher.find()) {
            String field = matcher.group(1).replaceAll("\\s+", " ").trim();
            if (!field.isEmpty()) {
                fields.add(field);
            }
        }
        return fields;
    }
}
