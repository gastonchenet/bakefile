package fr.tanchoulet.bakefile;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CircularDependencyValidator {
    private final List<String> visitedBlocks = new LinkedList<>();
    private final Map<String, Block> allBlocks;

    public CircularDependencyValidator(Map<String, Block> allBlocks) {
        this.allBlocks = allBlocks;
    }

    public boolean hasCircularDependency(Block block) {
        if (visitedBlocks.contains(block.name)) {
            return true;
        }

        visitedBlocks.add(block.name);

        for (String ref : block.references) {
            Block refBlock = allBlocks.get(ref);
            if (refBlock == null) {
                continue;
            }

            if (hasCircularDependency(refBlock)) {
                return true;
            }
        }

        return false;
    }
}