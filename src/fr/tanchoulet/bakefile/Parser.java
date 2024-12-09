package fr.tanchoulet.bakefile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Permet la décomposition d'une chaine de caractères en 'blocs' Bakefile
 * @see fr.tanchoulet.bakefile.Block
 *
 * @author Gaston Chenet
 * @version 1.0
 */
public class Parser {
    /**
     * L'expression régulière permettant de reconnaitre les commentaires d'un Bakefile
     */
    private static final Pattern COMMENT_PATTERN = Pattern.compile("[\\t ]*#.*$", Pattern.MULTILINE);

    /**
     * Chaine de caractères à manipuler
     */
    private final String content;

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
        return new Parser(result.toString());
    }

    /**
     * Les données de construction d'un 'Parser'
     * @param content Le contenu du fichier 'Bakefile'
     */
    private Parser(String content) {
        this.content = content;
    }

    /**
     * Retire tous les commentaires du contenu du fichier
     * @return Le contenu du fichier dans aucun commentaires
     */
    private String getUncommentedContent() {
        return COMMENT_PATTERN.matcher(this.content).replaceAll("");
    }

    /**
     * Structuration des données du fichier 'Bakefile' en une liste de Blocs
     * @see fr.tanchoulet.bakefile.Block
     *
     * Cette méthode crée un bloc en utilisant la méthode statique de parsing de la classe 'Block'
     * @see fr.tanchoulet.bakefile.Block#parse(String)
     *
     * @return La liste de blocs contenus dans le 'Bakefile'
     */
    public List<Block> parse() {
        List<Block> blocks = new ArrayList<Block>();

        String uncommented = this.getUncommentedContent();
        Matcher blockMatcher = Block.PATTERN.matcher(uncommented);

        while (blockMatcher.find()) {
            Block block = Block.parse(blockMatcher.group());
            blocks.add(block);
        }

        return blocks;
    }
}