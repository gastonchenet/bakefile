package fr.tanchoulet.bakefile;

import java.io.FileNotFoundException;
import java.util.Map;

/**
 * Classe d'exécution principale du programme
 *
 * @author Gaston Chenet
 * @version 1.3
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

        //Dictionaire qui contient les blocks parsée
        Map<String,Block> blocks = parser.parse();

        if (args.length < 1) {
            System.err.println("Input the block name.");
            return;
        }

        Block block = blocks.get(args[0]);

        if (block == null) {
            System.err.println("Wrong block name.");
            return;
        }

        Executor executor = new Executor(blocks);

        //Execute les commandes extraites du Bakefile
        executor.execute(block);
    }
}
