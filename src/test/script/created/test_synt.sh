#!/bin/bash

# Configuration
DECAC="./src/main/bin/decac"
TEST_DIR="./src/test/deca/syntax"

# Couleurs
RED='\033[0;31m'
GREEN='\033[0;32m'
GREY='\033[1;30m'
NC='\033[0m'

echo "=================================================="
echo "      LANCEMENT DES TESTS DE SYNTAXE (Parse)"
echo "=================================================="

pass=0
fail=0

# Fonction de test
run_synt_test() {
    local file=$1
    local type=$2 # "valid" ou "invalid"

    # Exécution de decac -p
    $DECAC -p "$file" > /dev/null 2>&1
    local ret=$?

    if [ "$type" == "valid" ]; then
        if [ $ret -eq 0 ]; then
            echo -e "[${GREEN}OK${NC}] $file (Lex/Parse réussi)"
            ((pass++))
        else
            echo -e "[${RED}KO${NC}] $file (Echec inattendu du parser)"
            ((fail++))
        fi
    else # invalid
        if [ $ret -ne 0 ]; then
            echo -e "[${GREEN}OK${NC}] $file (Erreur détectée comme prévu)"
            ((pass++))
        else
            echo -e "[${RED}KO${NC}] $file (Le parser aurait dû échouer !)"
            ((fail++))
        fi
    fi
}

# 1. Tests Valides
echo -e "\n${GREY}--- Tests Valides (decac -p doit passer) ---${NC}"
for f in $(find "$TEST_DIR/valid" -name "*.deca"); do
    run_synt_test "$f" "valid"
done

# 2. Tests Invalides
echo -e "\n${GREY}--- Tests Invalides (decac -p doit échouer) ---${NC}"
for f in $(find "$TEST_DIR/invalid" -name "*.deca"); do
    run_synt_test "$f" "invalid"
done

echo "=================================================="
echo -e "Résultat Syntaxe : ${GREEN}$pass Succès${NC} / ${RED}$fail Echecs${NC}"
echo "=================================================="