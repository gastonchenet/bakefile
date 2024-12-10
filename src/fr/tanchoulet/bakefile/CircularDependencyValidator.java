package fr.tanchoulet.bakefile;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Vérifie s'il n'y a aucune dépendances circulaires à partir d'un bloc donné
 * @see fr.tanchoulet.bakefile.Block
 *
 * @author Louis Tanchou
 * @version 1.1
 */
public class CircularDependencyValidator {

    /**
     * La totalité des blocs présents dans le Bakefile
     */
    private final Map<String, Block> blocks;

    /**
     * Les données de construction d'un 'CircularDependencyValidator'
     * @param blocks La totalité des blocs présents dans le Bakefile
     */
    public CircularDependencyValidator(Map<String, Block> blocks) {
        this.blocks = blocks;
    }

    /**
     * Vérifie récursivement s'il n'y a aucune dépendances circulaires à partir d'un bloc donnée.
     * @param block Le bloc à partir duquel il faut vérifier les dépendances circulaires
     * @return S'il y a une dépendance circulaire
     */
    public boolean hasCircularDependency(Block block) {
        return this.hasCircularDependency(block, new LinkedList<>());
    }

    /**
     * Vérifie récursivement s'il n'y a aucune dépendances circulaires à partir d'un bloc donnée.
     * @param block Le bloc à partir duquel il faut vérifier les dépendances circulaires
     * @return S'il y a une dépendance circulaire
     */
    private boolean hasCircularDependency(Block block, List<String> visited) {
        if (visited.contains(block.name)) return true;

        visited.add(block.name);

        for (String ref : block.references) {
            Block refBlock = blocks.get(ref);
            if (refBlock == null) continue;

            // Vérifie récursivement si une des références de ce même bloc n'a pas déja été parcourue
            if (this.hasCircularDependency(refBlock)) return true;
        }

        return false;
    }
}