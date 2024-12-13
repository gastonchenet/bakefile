SRC_DIR := src
BUILD_DIR := build
DOC_DIR := doc
JAR := bakefile.jar
MANIFEST := Manifest.mf
PACKAGE := fr.tanchoulet.bakefile
PACKAGE_DIR := fr/tanchoulet/bakefile
SRC_ROOT := $(SRC_DIR)/$(PACKAGE_DIR)
BUILD_ROOT := $(BUILD_DIR)/$(PACKAGE_DIR)


# Commandes

all : run                                                                                       # Exécution de la commande 'run' par défaut


run :                                                                                           # Exécution du fichier JAR
	java -jar $(JAR) Main.class


jar : build                                                                                     # Création de l'archive JAR
	jar cfm $(JAR) $(MANIFEST) -C $(BUILD_DIR) $(PACKAGE_DIR)


rebuild : clean build                                                                           # Compilation du projet de zéro



build : $(BUILD_ROOT)/Main.class                                                                # Compilation du projet entier



clean :                                                                                         # Nettoyage des fichiers build et de la javadoc
	rm -rf $(BUILD_DIR)/* $(DOC_DIR)/*


javadoc :                                                                                       # Création de la JavaDoc
	javadoc -d $(DOC_DIR) -sourcepath $(SRC_DIR) -subpackages $(PACKAGE)



# Compilation par fichier

$(BUILD_ROOT)/Main.class : $(SRC_ROOT)/Parser.java $(SRC_ROOT)/Block.java $(SRC_ROOT)/BlockList.java $(SRC_ROOT)/Executor.java
	javac -d $(BUILD_DIR) -cp $(BUILD_DIR) $(SRC_ROOT)/Main.java -implicit:none

$(BUILD_ROOT)/Parser.class : $(SRC_ROOT)/Block.java
	javac -d $(BUILD_DIR) -cp $(BUILD_DIR) $(SRC_ROOT)/Parser.java -implicit:none

$(BUILD_ROOT)/Block.class :
	javac -d $(BUILD_DIR) -cp $(BUILD_DIR) $(SRC_ROOT)/Block.java -implicit:none

$(BUILD_ROOT)/Executor.class : $(SRC_ROOT)/Block.java $(SRC_ROOT)/BlockList.java
	javac -d $(BUILD_DIR) -cp $(BUILD_DIR) $(SRC_ROOT)/Executor.java -implicit:none

$(BUILD_ROOT)/BlockList.class : $(SRC_ROOT)/Block.java
	javac -d $(BUILD_DIR) -cp $(BUILD_DIR) $(SRC_ROOT)/BlockList.java -implicit:none