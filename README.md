# Projet Génie Logiciel, Ensimag.
gl56, 01/01/2026.

**Partie 1 : { println("Hello world") ;}**
    Un script utilitaire `deca_test` a été ajouté à la racine pour simplifier le lancement des différentes étapes de compilation. Il permet d'utiliser des noms de fichiers abrégés.

1. Installation (à faire une seule fois) :
Rendez le script exécutable avec la commande suivante :
    $ chmod +x deca_test

Pour executer les tests test_lex, test_synt ou test_context:
$ ./deca_test synt nom_test.deca    (execute le test test_synt)
$ ./deca_test ctx nom_test.deca    (execute le test test_context)
$ ./deca_test lex nom_test.deca    (execute le test test_lex)
$ ./deca_test decac nom_test.deca    (execute le test decac)
 nom_test : Le nom du fichier test doit etre dans le repertoire /src/test/deca