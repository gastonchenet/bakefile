package fr.tanchoulet.bakefile;

import java.io.FileNotFoundException;
import java.util.HashSet;

/**
 * Classe d'exécution principale du programme
 *
 * @author Gaston Chenet
 * @version 1.3
 */
public class Main {
    /**
     * Méthode d'exécution du programme
     * 
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

        boolean debug = new HashSet<String>().contains("-d");
        BlockList blocks = parser.parse();
        String blockName = "all";

        if (args.length > 0) {
            blockName = args[0];
        }

        Block block = blocks.find(blockName);

        if (block == null) {
            block = blocks.get(0);
        }

        if (block == null) {
            System.err.println("No targets.");
        }

        Executor executor = new Executor(blocks, debug);
        executor.execute(block);
    }
}
