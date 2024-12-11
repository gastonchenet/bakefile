package fr.tanchoulet.bakefile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Execute et vérifie s'il n'y a aucune dépendances circulaires à partir d'un bloc donné
 * @see fr.tanchoulet.bakefile.Block
 *
 * @author Louis Tanchou
 * @version 1.1
 */
public class Executor {
    /**
     * La totalité des blocs présents dans le Bakefile
     */
    private final Map<String, Block> blocks;

    Executor(Map<String, Block> blocks) {
        this.blocks = blocks;
    }

    /**
     * Exécute un bloc
     * @param block Le bloc à exécuter
     */
    public void execute(Block block) {
        this.execute(block, new HashSet<>());
    }

    /**
     * Exécute un bloc
     * @param block Le bloc à exécuter
     */
    public void execute(Block block, Set<Block> visited) {
        if (visited.contains(block)) return;
        visited.add(block);

        for (String reference : block.references) {
            Block blockReferencies = blocks.get(reference);
            if (blockReferencies == null) continue;
            this.execute(blockReferencies);
        }

        for (String command : block.commands) {
            ProcessBuilder pb = new ProcessBuilder(command.split(" +"));


            try {
                Process process = pb.start();
                System.out.println(command);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;

                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }

                reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }

                int exitCode = process.waitFor();
                if (exitCode != 0) throw new Exception("Ya un pb chef");
            } catch (Exception error) {
                break;
            }
        }
    }
}
