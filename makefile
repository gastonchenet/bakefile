SRC_DIR := src
BUILD_DIR := build
DOC_DIR := doc
JAR := bakefile.jar
MANIFEST := Manifest.mf
PACKAGE := fr.tanchoulet.bakefile
PACKAGE_DIR := fr/tanchoulet/bakefile
ROOT := $(SRC_DIR)/$(PACKAGE_DIR)



# Commandes

all : run                                                                                       # Exécution de la commande 'run' par défaut


run : jar                                                                                       # Exécution du fichier JAR
	java -jar $(JAR)


jar : build                                                                                     # Création de l'archive JAR
	jar cfm $(JAR) $(MANIFEST) -C $(BUILD_DIR) $(PACKAGE_DIR)


rebuild : clean build                                                                           # Compilation du projet de zéro



build : Main.class                                                                              # Compilation du projet entier



clean :                                                                                         # Nettoyage des fichiers build et de la javadoc
	rm -rf $(BUILD_DIR)/* $(DOC_DIR)/*


javadoc :                                                                                       # Création de la JavaDoc
	javadoc -d $(DOC_DIR) -sourcepath $(SRC_DIR) -subpackages $(PACKAGE)



# Compilation par fichier

Main.class : Parser.class Block.class                                                           # Compilation de Main (Refs: Parser, Block)
	javac -d $(BUILD_DIR) -cp $(BUILD_DIR) $(ROOT)/Main.java -implicit:none


Parser.class : Block.class                                                                      # Compilation de Parser (Refs: Block)
	javac -d $(BUILD_DIR) -cp $(BUILD_DIR) $(ROOT)/Parser.java -implicit:none


Block.class :                                                                                   # Compilation de Block (Refs: x)
	javac -d $(BUILD_DIR) -cp $(BUILD_DIR) $(ROOT)/Block.java -implicit:none