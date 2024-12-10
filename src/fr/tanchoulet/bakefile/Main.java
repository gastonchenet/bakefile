package fr.tanchoulet.bakefile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

/**
 * Classe d'exécution principale du programme
 *
 * @author Gaston Chenet
 * @version 1.2
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
            System.err.println("Input the block name.");
            return;
        }

        Block block = blocks.get(args[0]);

        if (block == null) {
            System.err.println("Wrong block name.");
            return;
        }

        if (validator.hasCircularDependency(block)) {
            System.err.println("Circular dependency found.");
            return;
        }

        block.execute(blocks);

        // Parcours la map des blocks
        /*for (Map.Entry<String, Block> entry : blocks.entrySet()) {
            System.out.println(entry);
        }*/
    }
}
