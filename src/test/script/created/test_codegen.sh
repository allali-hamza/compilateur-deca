#!/bin/bash

DECAC="./src/main/bin/decac"
TEST_DIR="./src/test/deca/codegen"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
NC='\033[0m'

echo "=================================================="
echo "      LANCEMENT DES TESTS GENCODE (Exec)"
echo "=================================================="

pass=0
fail=0

run_gencode() {
    local file=$1
    local is_invalid=$2
    
    $DECAC "$file" > /dev/null 2>&1
    if [ $? -ne 0 ]; then
        echo -e "[${RED}COMPILE ERROR${NC}] $file"
        ((fail++))
        return
    fi
    
    local ass_file="${file%.deca}.ass"
    
    ima "$ass_file" > /dev/null 2>&1
    local ret=$?
    
    if [ "$is_invalid" = "true" ]; then
        if [ $ret -ne 0 ]; then
            echo -e "[${GREEN}OK (EXPECTED RUNTIME ERROR)${NC}] $file"
            ((pass++))
        else
            echo -e "[${RED}FAIL (NO RUNTIME ERROR)${NC}] $file"
            ((fail++))
        fi
    else
        if [ $ret -eq 0 ]; then
            echo -e "[${GREEN}OK${NC}] $file (Executé avec succès)"
            ((pass++))
        else
            echo -e "[${RED}RUNTIME ERROR${NC}] $file (IMA a renvoyé une erreur)"
            ((fail++))
        fi
    fi
    
    rm -f "$ass_file"
}

echo -e "\n${YELLOW}--- Tests CodeGen Valides ---${NC}"
for f in $(find "$TEST_DIR/valid" -name "*.deca"); do
    run_gencode "$f" "false"
done

echo -e "\n${YELLOW}--- Tests CodeGen Invalides ---${NC}"
for f in $(find "$TEST_DIR/invalid" -name "*.deca"); do
    run_gencode "$f" "true"
done

if [ -d "$TEST_DIR/perf" ]; then
    echo -e "\n${YELLOW}--- Tests Performance ---${NC}"
    for f in $(find "$TEST_DIR/perf" -name "*.deca"); do
        run_gencode "$f" "false"
    done
fi

echo "=================================================="
echo -e "Résultat Gencode : ${GREEN}$pass Succès${NC} / ${RED}$fail Echecs${NC}"
echo "=================================================="