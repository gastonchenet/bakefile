SRC_DIR := src
BUILD_DIR := build
DOC_DIR := doc
JAR := bakefile.jar
MANIFEST := Manifest.mf
PACKAGE := fr.tanchoulet.bakefile
PACKAGE_DIR := fr/tanchoulet/bakefile
SRC_ROOT := $(SRC_DIR)/$(PACKAGE_DIR)
BUILD_ROOT := $(BUILD_DIR)/$(PACKAGE_DIR)
ARGS := -d $(BUILD_DIR) -cp $(BUILD_DIR) -implicit:none

.PHONY : all run jar rebuild build javadoc clean


# Commandes

all : run                                                                                       # Exécution de la commande 'run' par défaut


run :                                                                                           # Exécution du fichier JAR
	java -jar $(JAR) -d


jar : build                                                                                     # Création de l'archive JAR
	jar cfm $(JAR) $(MANIFEST) -C $(BUILD_DIR) $(PACKAGE_DIR)


rebuild : clean build                                                                           # Compilation du projet de zéro



build : $(BUILD_ROOT)/Main.class                                                                # Compilation du projet entier



clean :                                                                                         # Nettoyage des fichiers build et de la javadoc
	rm -rf $(BUILD_DIR)/* $(DOC_DIR)/*


javadoc :                                                                                       # Création de la JavaDoc
	javadoc -d $(DOC_DIR) -sourcepath $(SRC_DIR) -subpackages $(PACKAGE)



# Compilation par fichier

$(BUILD_ROOT)/Main.class : $(SRC_ROOT)/Main.java $(BUILD_ROOT)/Parser.class $(BUILD_ROOT)/Block.class $(BUILD_ROOT)/BlockList.class $(BUILD_ROOT)/Executor.class
	javac $(ARGS) $(SRC_ROOT)/Main.java

$(BUILD_ROOT)/Parser.class : $(SRC_ROOT)/Parser.java $(BUILD_ROOT)/Block.class $(BUILD_ROOT)/BlockList.class
	javac $(ARGS) $(SRC_ROOT)/Parser.java

$(BUILD_ROOT)/Block.class : $(SRC_ROOT)/Block.java
	javac $(ARGS) $(SRC_ROOT)/Block.java

$(BUILD_ROOT)/Executor.class : $(SRC_ROOT)/Executor.java $(BUILD_ROOT)/Block.class $(BUILD_ROOT)/BlockList.class
	javac $(ARGS) $(SRC_ROOT)/Executor.java

$(BUILD_ROOT)/BlockList.class : $(SRC_ROOT)/BlockList.java $(BUILD_ROOT)/Block.class
	javac $(ARGS) $(BUILD_DIR) $(SRC_ROOT)/BlockList.java