package fr.tanchoulet.bakefile;

import java.util.ArrayList;

/**
 * Liste de blocs de Bakefile
 * 
 * @see fr.tanchoulet.bakefile.Block
 * 
 * @author Gaston Chenet
 * @version 1.0
 */
public class BlockList extends ArrayList<Block> {
    /**
     * Recherche un bloc par son nom
     * 
     * @param blockName Nom du bloc à rechercher
     * @return Le bloc recherché, ou null si non trouvé
     */
    public Block find(String blockName) {
        for (Block block : this) {
            if (block.name.equals(blockName))
                return block;
        }

        return null;
    }

    @Override
    /**
     * Permet d'afficher la liste de blocs
     * 
     * @return La liste de blocs sous forme de chaine de caractères
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (Block block : this) {
            builder.append(block.toString());
            builder.append("\n");
        }

        return builder.toString();
    }
}
