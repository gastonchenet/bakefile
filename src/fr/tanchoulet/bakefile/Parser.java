package fr.tanchoulet.bakefile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Permet la décomposition d'une chaine de caractères en 'blocs' Bakefile
 * @see fr.tanchoulet.bakefile.Block
 *
 * @author Gaston Chenet
 * @version 1.1
 */
public class Parser {
    /**
     * Mot clé permettant de reconnaitre '.PHONY', sous forme d'une expression régulière
     */
    private static final String PHONY_KEYWORD = Pattern.quote(".PHONY");

    /**
     * Le modèle permettant de reconnaitre les commentaires d'un Bakefile
     */
    private static final Pattern COMMENT_PATTERN = Pattern.compile("[ \\t]*#.*$", Pattern.MULTILINE);

    /**
     * Le modèle permettant de reconnaitre les assignations de variables d'un Bakefile
     */
    private static final Pattern VARIABLE_ASSIGN_PATTERN = Pattern.compile("^\\s*(?<key>[a-zA-Z_][a-zA-Z0-9_.-]*)\\s*=\\s*(?<value>.+)", Pattern.MULTILINE);

    /**
     * Le modèle permettant de reconnaitre les appels de variables d'un Bakefile
     */
    private static final Pattern VARIABLE_CALL_PATTERN = Pattern.compile("\\$\\([a-zA-Z_][a-zA-Z0-9_.-]*\\)");

    /**
     * Le modèle permettant de reconnaitre le bloc de '.PHONY'
     */
    private static final Pattern PHONY_PATTERN = Pattern.compile(String.format("%s[ \\t]*:[ \\t]*(?<refs>(?:[a-zA-Z._-]+[ \\t]*)*)", Parser.PHONY_KEYWORD));

    /**
     * Chaine de caractères à manipuler
     */
    private final String content;

    /**
     * Retire tous les commentaires d'une chaine de caractères
     * @return La chaine de caractères sans aucun commentaires
     */
    private static String uncomment(String content) {
        return COMMENT_PATTERN.matcher(content).replaceAll("");
    }

    /**
     * Remplace toutes les occurrences des variables dans une chaine de caractères
     * @param content Le contenu de la chaine de caractères
     * @return Le contenu de la chaine de caractères avec les variables remplacées par leurs valeurs
     */
    private static String replaceVars(String content) {
        Matcher variableMatcher = VARIABLE_ASSIGN_PATTERN.matcher(content);
        Map<String, String> symbolTable = new HashMap<>();

        while (variableMatcher.find()) {
            symbolTable.put(variableMatcher.group("key"), variableMatcher.group("value").trim());
        }

        // Comme il est possible d'invoquer des variables dans d'autres variables, il est important de réxécuter le
        // programme de remplacement tant qu'il y a des variables encore non-touchées présentes dans le programme.
        while (Parser.VARIABLE_CALL_PATTERN.matcher(content).find()) {
            for (Map.Entry<String, String> entry : symbolTable.entrySet()) {
                // Transforme les chaines de caractères en expressions régulières
                String regularKey = Pattern.quote("$(" + entry.getKey() + ")");
                String regularValue = Matcher.quoteReplacement(entry.getValue());

                content = content.replaceAll(regularKey, regularValue);
            }
        }

        return content;
    }

    /**
     * Crée une instance de cette à partir d'un nom de fichier en récupérant son contenu
     * @param filename Le nom du fichier à partir duquel créer l'instance
     * @return Une instance de la classe contenant le contenu brut du fichier
     * @throws FileNotFoundException Si le fichier est introuvable
     */
    public static Parser fromFile(String filename) throws FileNotFoundException {
        File file = new File(filename);
        Scanner scanner = new Scanner(file);
        StringBuilder result = new StringBuilder();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            result.append(line).append("\n");
        }

        scanner.close();

        // Retire directement tous les commentaires d'un texte de façon à éviter d'être dérangés durant l'exécution
        // du programme
        String uncommented = Parser.uncomment(result.toString());

        return new Parser(uncommented);
    }

    /**
     * Les données de construction d'un 'Parser'
     * @param content Le contenu du fichier 'Bakefile'
     */
    private Parser(String content) {
        this.content = content;
    }

    /**
     * Récupère les valeurs des blocs '.PHONY'
     * @return La liste des références annotées comme appartenant au '.PHONY'
     */
    private List<String> getPhony() {
        Matcher matcher = PHONY_PATTERN.matcher(this.content);
        List<String> phony = new ArrayList<>();

        while (matcher.find()) {
            String refLine = matcher.group("refs");
            String[] refs = refLine.split(" +");
            Collections.addAll(phony, refs);
        }

        return phony;
    }

    /**
     * Structuration des données du fichier 'Bakefile' en une liste de Blocs
     * @see fr.tanchoulet.bakefile.Block
     *
     * Cette méthode crée un bloc en utilisant la méthode statique de parsing de la classe 'Block'
     * @see fr.tanchoulet.bakefile.Block#parse(String, List<String>)
     *
     * @return La liste de blocs contenus dans le 'Bakefile'
     */
    public Map<String, Block> parse() {
        Map<String, Block> blocks = new HashMap<>();
        String varsReplacedContent = Parser.replaceVars(this.content);
        Matcher blockMatcher = Block.PATTERN.matcher(varsReplacedContent);
        List<String> phony = this.getPhony();

        while (blockMatcher.find()) {
            Block block = Block.parse(blockMatcher.group(), phony);

            // Étant donné que la formation d'un bloc '.PHONY' à la même syntaxe qu'un bloc général, il est important
            // de retirer les occurrences de '.PHONY' des blocs
            if (block.name.matches(Parser.PHONY_KEYWORD)) continue;

            blocks.put(block.name, block);
        }

        return blocks;
    }
}