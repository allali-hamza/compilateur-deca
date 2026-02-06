#!/bin/bash

DECAC="./src/main/bin/decac"
TEST_DIR="./src/test/deca/context"

RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

echo "=================================================="
echo "     LANCEMENT DES TESTS CONTEXTUELS (Verif)"
echo "=================================================="

pass=0
fail=0

run_context_test() {
    local file=$1
    local type=$2 

    # Exécution de decac -v
    $DECAC -v "$file" > /dev/null 2>&1
    local ret=$?

    if [ "$type" == "valid" ]; then
        if [ $ret -eq 0 ]; then
            echo -e "[${GREEN}OK${NC}] $file"
            ((pass++))
        else
            echo -e "[${RED}KO${NC}] $file (Erreur contextuelle inattendue)"
            ((fail++))
        fi
    else # invalid
        if [ $ret -ne 0 ]; then
            echo -e "[${GREEN}OK${NC}] $file (Erreur détectée)"
            ((pass++))
        else
            echo -e "[${RED}KO${NC}] $file (Pas d'erreur détectée !)"
            ((fail++))
        fi
    fi
}

echo -e "\n${BLUE}--- Tests Valides (decac -v) ---${NC}"
for f in $(find "$TEST_DIR/valid" -name "*.deca"); do
    run_context_test "$f" "valid"
done

echo -e "\n${BLUE}--- Tests Invalides (decac -v) ---${NC}"
for f in $(find "$TEST_DIR/invalid" -name "*.deca"); do
    run_context_test "$f" "invalid"
done

echo "=================================================="
echo -e "Résultat Contexte : ${GREEN}$pass Succès${NC} / ${RED}$fail Echecs${NC}"
echo "=================================================="