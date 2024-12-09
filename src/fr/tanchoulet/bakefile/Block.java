package fr.tanchouler.bakefile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Block {
    public static final Pattern PATTERN = Pattern.compile("(?<name>[a-zA-Z._-]+) *: *(?:[a-zA-Z._-]+ *)*(?:\\n[ \\t]+[^ \\t].*)*");
    private static final Pattern REF_PATTERN = Pattern.compile("(?<=:)(?<ref>.*)");
    private static final Pattern LINE_PATTERN = Pattern.compile("\\n[ \\t]+(?<line>[^ \\t].*)");

    public final String name;
    public final String[] references;
    public final String[] commands;

    public static Block parse(String rawData) {
        String name = rawData.split(": ")[0].trim();
        List<String> refs = new ArrayList<>();
        List<String> lines = new ArrayList<>();

        Matcher refMatcher = Block.REF_PATTERN.matcher(rawData);
        Matcher lineMatcher = Block.LINE_PATTERN.matcher(rawData);

        while (refMatcher.find()) {
            String line = refMatcher.group("ref");
            Collections.addAll(refs, line.trim().split(" "));
        }

        while (lineMatcher.find()) {
            String line = lineMatcher.group("line");
            lines.add(line);
        }

        String[] refsArray = refs.toArray(new String[0]);
        String[] linesArray = lines.toArray(new String[0]);

        return new Block(name, refsArray, linesArray);
    }

    private Block(String name, String[] references, String[] commands) {
        this.name = name;
        this.references = references;
        this.commands = commands;
    }

    public void execute() {
        // TODO: Implement
    }

    public String toString() {
        StringBuilder result = new StringBuilder("Block<" + name + ", [");

        for (String reference : references) {
            result.append(reference).append(", ");
        }

        result.delete(result.length() - 2, result.length());
        result.append("], [");

        for (String command : commands) {
            result.append(command).append(", ");
        }

        result.delete(result.length() - 2, result.length());
        result.append("]>");

        return result.toString();
    }
}
