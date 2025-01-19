package fr.tanchoulet.bakefile;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe d'exécution principale du programme
 *
 * @author Gaston Chenet
 * @version 1.4
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

        BlockList blocks = parser.parse();
        String blockName = "";

        // Si un argument est passé, il est considéré comme le nom du bloc à exécuter
        if (args.length > 0) {
            blockName = args[0];
        }

        Block block = blocks.find(blockName);

        // Si le bloc n'existe pas, on prend le premier bloc de la liste
        if (block == null) {
            block = blocks.get(0);
        }

        // Si l'argument '-d' est présent mais que le bloc n'existe pas, on affiche un
        // message d'erreur
        if (blockName != "-d" && block == null) {
            System.err.println("Target not found: " + blockName);
            return;
        }

        // Si le bloc est vide, on affiche un message d'erreur
        if (block == null) {
            System.err.println("No targets.");
            return;
        }

        // Si le bloc est vide, on affiche un message d'erreur
        if (block.isEmpty()) {
            System.err.println("Nothing to be done for '" + block.name + "'");
            return;
        }

        Set<String> argsSet = new HashSet<String>();
        Collections.addAll(argsSet, args);

        // Active le mode debug si l'argument '-d' ou '--debug' est présent
        boolean debug = argsSet.contains("-d") || argsSet.contains("--debug");

        // Exécute les commandes du bloc ainsi que les commandes des dépendances
        Executor executor = new Executor(blocks, debug);
        executor.execute(block);
    }
}
