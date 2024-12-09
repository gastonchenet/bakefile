package fr.tanchouler.bakefile;

import java.io.FileNotFoundException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String content;

        try {
            content = FileReader.read("bakefile");
        } catch (FileNotFoundException error) {
            System.out.println("File not found");
            return;
        }

        Parser parser = new Parser(content);
        List<Block> blocks = parser.parse();

        for (Block block : blocks) {
            System.out.println(block);
        }
    }
}
