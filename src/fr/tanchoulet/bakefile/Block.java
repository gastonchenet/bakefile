package fr.tanchoulet.bakefile;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contient toutes les données d'un 'bloc' de Bakefile
 * <p>
 * Un bloc de Bakefile suit ce modèle :
 *  <name> : [references...]
 *  <line>
 * ...
 * <p>
 * Exemple :
 *  Foo.class : Bar.class Baz.class
 *  javac -d build foo.java
 *
 * @author Gaston Chenet
 * @version 1.4
 */
public class Block {
    /**
     * Modèle permettant de repérer un bloc dans une chaine de caractères
     */
    public static final Pattern PATTERN = Pattern
            .compile("(?:^|\n)(?<name>[a-zA-Z._-]+)[ \\t]*:[ \\t]*(?:[a-zA-Z._-]+[ \\t]*)*(?:\\n[ \\t]+[^ \\t].*)*");

    /**
     * Modèle permettant de récupérer références d'un bloc
     */
    private static final Pattern REF_PATTERN = Pattern.compile("(?:^|\n)[a-zA-Z._-]+[ \\t]*:[ \\t]*(?<ref>.*)");

    /**
     * Modèle permettant de récupérer les lignes de commandes d'un bloc
     */
    private static final Pattern LINE_PATTERN = Pattern.compile("\\n[ \\t]+(?<line>[^ \\t].*)");

    /**
     * Variables stockées dans un 'bloc' Bakefile.
     */
    public final String name;
    public final String[] references;
    public final String[] commands;
    public final boolean phony;

    /**
     * Permet de transformer une chaine de caractères reconnue en tant 'bloc' en une
     * instance de la classe 'Block'
     * 
     * @param rawData Les données brutes sour forme d'une chaine de caractères
     * @return Les données d'un bloc sous une forme structurée
     */
    public static Block parse(String rawData, List<String> phonyElements) {
        // Le nom se trouve obligatoirement avant les ':'
        String name = rawData.split(":")[0].trim();
        List<String> refs = new ArrayList<>();
        List<String> lines = new ArrayList<>();
        boolean phony = phonyElements.contains(name);

        Matcher refMatcher = Block.REF_PATTERN.matcher(rawData);
        Matcher lineMatcher = Block.LINE_PATTERN.matcher(rawData);

        while (refMatcher.find()) {
            String line = refMatcher.group("ref");

            // Ajout de chacune des références dans la liste des références, celles-ci
            // séparées par un ou plusieurs espaces
            Collections.addAll(refs, line.trim().split(" +"));
        }

        while (lineMatcher.find()) {
            String line = lineMatcher.group("line");
            lines.add(line);
        }

        String[] refsArray = refs.toArray(new String[0]);
        String[] linesArray = lines.toArray(new String[0]);

        return new Block(name, refsArray, linesArray, phony);
    }

    /**
     * Les données de construction d'un bloc
     * 
     * @param name       Le nom du bloc (sert de références pour les autres blocs)
     * @param references Les blocs noms des blocks référencés en celui-ci
     * @param commands   La liste des commandes à exécuter après l'exécution des
     *                   références
     * @param phony      Est-ce dans la ligne. PHONY
     */
    private Block(String name, String[] references, String[] commands, boolean phony) {
        this.name = name;
        this.references = references;
        this.commands = commands;
        this.phony = phony;
    }

    /**
     * Affichage des instances de la classe
     * Exemple : Block<Foo.class, [Bar.class, Baz.class], [@javac -d build foo.java]>
     *
     * @return Une chaine de caractères lisible représentant les valeurs d'une
     *         instance de la classe
     */
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

        if (commands.length > 0) {
            result.delete(result.length() - 2, result.length());
        }

        result.append("]");

        if (phony) result.append(", PHONY");

        result.append(">");

        return result.toString();
    }
}