package fr.tanchouler.bakefile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class Parser {
    private final String content;

    public Parser(String content) {
        this.content = content;
    }

    public List<Block> parse() {
        List<Block> blocks = new ArrayList<Block>();

        Matcher blockMatcher = Block.PATTERN.matcher(content);

        while (blockMatcher.find()) {
            Block block = Block.parse(blockMatcher.group());
            blocks.add(block);
        }

        return blocks;
    }
}
