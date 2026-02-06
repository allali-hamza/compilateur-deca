# Compilateur Deca

Un compilateur pour le langage Deca développé en Java dans le cadre du projet de Génie Logiciel à l'Ensimag.

## Équipe

- Hamza ALLALI
- Abdellah Bensalem 
- Mouad Ikne 
- Ayoub Tyamani
- Chaimae Lahoui

## Description

Ce projet implémente un compilateur complet pour le langage Deca, comprenant :
- Analyse lexicale
- Analyse syntaxique
- Analyse contextuelle
- Génération de code assembleur

## Compilation

Pour compiler le projet, exécuter :
```bash
mvn compile
```

## Lancer les tests


### Tests par catégorie

Dans le dossier `src/test/script/created/` :
```bash
./test_all.sh      # Tous les tests
./test_lex.sh      # Tests lexicaux
./test_synt.sh     # Tests syntaxiques
./test_ctx.sh      # Tests contextuels
./test_codegen.sh  # Tests de génération de code
```

## Utilisation du compilateur
```bash
src/main/bin/decac [options] <fichier.deca>
```

### Options disponibles

- `-b` (banner) : Affiche une bannière indiquant le nom de l'équipe
- `-p` (parse) : Arrête decac après l'étape de construction de l'arbre et affiche la décompilation
- `-v` (verification) : Arrête decac après l'étape de vérifications contextuelles
- `-n` (no check) : Supprime les tests à l'exécution
- `-r X` (registers) : Limite les registres banalisés disponibles à R0 ... R{X-1}, avec 4 ≤ X ≤ 16
- `-d` (debug) : Active les traces de debug (répéter l'option plusieurs fois pour plus de traces)