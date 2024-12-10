package fr.tanchoulet.bakefile;

import java.io.FileNotFoundException;
import java.util.Map;

/**
 * Classe d'exécution principale du programme
 *
 * @author Gaston Chenet
 * @version 1.0
 */
public class Main {
    /**
     * Méthode d'exécution du programme
     * @param args Les arguments d'exécution
     */
    public static void main(String[] args) {
        Parser parser;

        try {
            parser = Parser.fromFile("bakefile");
        } catch (FileNotFoundException error) {
            // Retourne une erreur si le fichier 'bakefile' est inexistant
            System.out.println("File not found, 'bakefile' might not exist");
            return;
        }

        Map<String,Block> blocks = parser.parse();

        CircularDependencyValidator validator = new CircularDependencyValidator(blocks);
        if (args.length < 1) {
            System.err.println("arg missing");
            return;
        }

        boolean hasCircularDependency = validator.hasCircularDependency(args[0]);

        // Parcours la map des blocks
        for (Map.Entry<String, Block> entry : blocks.entrySet()) {
            System.out.println(entry);
        }

        System.out.println(hasCircularDependency);
    }
}
