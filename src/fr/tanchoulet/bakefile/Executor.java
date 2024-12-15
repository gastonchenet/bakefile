package fr.tanchoulet.bakefile;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Execute et vérifie s'il n'y a aucune dépendances circulaires à partir d'un
 * bloc donné
 * 
 * @see fr.tanchoulet.bakefile.Block
 *
 * @author Louis Tanchou, Gaston Chenet
 * @version 1.4
 */
public class Executor {
    /**
     * Motif de découpe des commandes
     */
    private static final Pattern COMMAND_SPLIT_PATTERN = Pattern.compile("\"(.*?)\"|\\S+");

    /**
     * La totalité des blocs présents dans le Bakefile
     */
    private final BlockList blocks;

    /**
     * Si le mode debug est activé
     */
    private final boolean debug;

    /**
     * Découpe une commande en plusieurs parties
     * 
     * @param command La commande à découper
     * @return Les différentes parties de la commande
     */
    private static List<String> splitCommand(String command) {
        Matcher matcher = Executor.COMMAND_SPLIT_PATTERN.matcher(command);
        List<String> parts = new ArrayList<>();

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                parts.add(matcher.group(1));
            } else {
                parts.add(matcher.group());
            }
        }

        return parts;
    }

    /**
     * Données de configuration pour l'exécution des commandes
     * 
     * @param blocks Tous les blocks qui ont été parsée
     * @param debug  Si le mode debug est activé
     */
    public Executor(BlockList blocks, boolean debug) {
        this.blocks = blocks;
        this.debug = debug;
    }

    /**
     * Exécute les commandes d'un bloc.
     *
     * @param block Le bloc source des commandes à exécuter.
     */
    private void executeCommands(Block block) {
        for (String command : block.commands) {
            List<String> commandParts = Executor.splitCommand(command);
            ProcessBuilder pb = new ProcessBuilder(commandParts);

            // Gestion des erreurs pour l'exécution des commandes liées au blocks
            try {
                Process process = pb.start();

                if (this.debug) {
                    System.out.println(command);
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;

                while ((line = reader.readLine()) != null) {
                    if (this.debug) {
                        System.out.println(line);
                    }
                }

                reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                while ((line = reader.readLine()) != null) {
                    if (this.debug) {
                        System.out.println(line);
                    }
                }

                int exitCode = process.waitFor();
                if (exitCode != 0)
                    throw new Exception("Ya un pb chef");
            } catch (Exception error) {
                break;
            }
        }
    }

    /**
     * Vérifie si un bloc doit être recompilé en fonction de ses modifications
     * 
     * @param block   Le bloc source à vérifier
     * @param visited Les blocs déjà visités
     * @return true si le bloc doit être recompilé
     */
    private boolean shouldRecompile(Block block, List<String> visited) {
        visited = new ArrayList<>(visited);
        visited.add(block.name);

        File blockFile = new File(block.name);

        if (block.phony || !blockFile.exists())
            return true;

        long lastModified = blockFile.lastModified();

        for (String referenceName : block.references) {
            Block reference = this.blocks.find(referenceName);
            File refFile = new File(referenceName);

            if (visited.contains(referenceName))
                continue;

            if (!refFile.exists() || refFile.lastModified() > lastModified)
                return true;

            if (reference != null && this.shouldRecompile(reference, visited))
                return true;
        }

        return false;
    }

    /**
     * Vérifie si un bloc doit être recompilé en fonction de ses modifications
     * 
     * @param block Le bloc source à vérifier
     * @return true si le bloc doit être recompilé
     */
    private boolean shouldRecompile(Block block) {
        return this.shouldRecompile(block, new ArrayList<>());
    }

    /**
     * Exécute les commandes d'un bloc récursivement
     *
     * @param block Le bloc source des commandes à exécuter
     */
    public void execute(Block block) {
        if (!this.shouldRecompile(block))
            return;

        for (String referenceName : block.references) {
            Block reference = this.blocks.find(referenceName);

            if (reference == null)
                continue;

            this.execute(reference);
        }

        this.executeCommands(block);
    }
}
