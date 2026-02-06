#! /bin/sh

# Auteur : gl56
# Version du : 11/01/2026
# ============================================================
# Automatisation des testes...
# ============================================================


export GL=$(pwd) # vaut mieux un pwd pour être "sûr" que le test fonctionnera pour tout le monde !
export PATH=$GL/src/main/bin:$PATH
export PATH=$GL/src/test/script:$PATH
export PATH=$GL/global/bin:$PATH

echo "=================================================="
echo " ÉTAPE 1 : mvn clean + mvn test "
echo "=================================================="

# On compile le compilateur Java et on lance les tests unitaires
# On lance jacoco parallélement ! 
mvn clean test -Djacoco.skip=false
# On vérifie si la compilation a réussi
if [ $? -ne 0 ]; then
    echo "ERREUR : La compilation mvn a échoué."
    exit 1
fi

echo "=================================================="
echo " ÉTAPE 2 : Tests des fichiers .deca"
echo "=================================================="

# On cherche tous les fichiers .deca dans src/test/deca
# On utilise 'find' pour aller dans tous les sous-dossiers
FILES=$(find src/test/deca -name "*.deca")

for fichier in $FILES
do
    echo "--------------------------------------------------"
    echo "Test de : $fichier"
    
    # A. On compile le fichier 
    decac "$fichier"
    
    # On vérifie si decac a réussi 
    if [ $? -eq 0 ]; then
        # On récupère le nom du fichier .ass généré 
        fichier_ass="${fichier%.deca}.ass" #(on remplace extension .deca par .ass)
        
        if [ -f "$fichier_ass" ]; then
            echo "-> Compilation OK. Exécution avec IMA :"
            # B. On exécute  IMA
            ima "$fichier_ass"
        else
            echo "-> ERREUR : Le fichier .ass n'a pas été généré."
        fi
    else
        echo "-> ECHEC Compilation (si c'est un test invalide c'est ok!)"
    fi
done

echo "=================================================="
echo " ÉTAPE 3 : Génération du rapport de couverture"
echo "=================================================="

# Génération du rapport Jacoco
./src/test/script/jacoco-report.sh

echo "Terminé ! le rapport de couverture est dans target/site/jacoco/index.html"
