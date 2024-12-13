package fr.tanchoulet.bakefile;

import java.util.ArrayList;

public class BlockList extends ArrayList<Block> {
    public Block find(String blockName) {
        for (Block block : this) {
            if (block.name.equals(blockName)) return block;
        }

        return null;
    }
}
