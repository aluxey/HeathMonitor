# ROADMAP - CyberDoc

## Direction produit

CyberDoc doit rester un MVP Android natif, local-first, centre sur `Health Connect`, avec une valeur simple:

- lire des donnees sante deja disponibles sur l'appareil
- les normaliser localement
- les rendre lisibles dans un dashboard plus clair que les apps sources

Le MVP ne doit pas deriver vers un backend serveur, un hub multi-plateforme ou une promesse d'analyse medicale.

## Etat d'avancement au 2026-03-25

- Phase 1 reste bloquee par la configuration du SDK Android local et la verification sur appareil
- Phase 2 est bien engagee: le dashboard principal lit maintenant les agregats journaliers locaux
- Phase 3 est bien engagee: la saisie manuelle et les objectifs sont maintenant relies au backend local
- Phase 4 et Phase 5 restent a lancer apres validation de l'application sur appareil

## Phase 1 - Validation reelle sur appareil

Objectif:

Verifier que l'application tourne dans un environnement Android reel et que les donnees MVP remontent effectivement.

Livrables:

- SDK Android configure localement
- build debug executable
- installation sur appareil
- verification des permissions `Health Connect`
- confirmation des metriques MVP reellement exploitables

## Phase 2 - Dashboard branche sur les vraies donnees

Objectif:

Fermer completement le flux `Health Connect -> Room -> agregats -> dashboard`.

Livrables:

- [ ] sync reelle fiable sur appareil
- [x] dernier sync et statut visibles dans l'application
- [x] tendances `7 jours` et `30 jours`
- [ ] gestion complete des donnees vides ou partielles
- [x] suppression du maximum de donnees demo dans les ecrans principaux

## Phase 3 - Flux utilisateur MVP complets

Objectif:

Rendre les fonctions visibles dans l'UI vraiment utilisables.

Livrables:

- [x] saisie manuelle branchee au backend local
- [x] objectifs modifiables dans l'application
- [x] ecran profil aligne avec le scope reel principal
- [ ] navigation onboarding / permissions / app stable sur tous les cas reels

## Phase 4 - Stabilisation technique

Objectif:

Transformer la base actuelle en application maintenable.

Livrables:

- logique d'etat sortie des composables
- architecture UI plus propre
- suppression des incoherences entre domaine et UI
- reduction des comportements temporaires au demarrage

## Phase 5 - Qualite et reprise facile

Objectif:

Rendre le projet testable, transmissible et facile a reprendre.

Livrables:

- tests unitaires et DAO
- au moins un test d'integration du pipeline principal
- migrations Room versionnees
- documentation de setup local
- documentation des flux MVP et des limites connues

## Definition de succes

Le projet est dans un bon etat MVP si:

- il compile et se lance sur appareil
- les metriques coeur sont lues de facon fiable
- le dashboard repose sur des donnees reelles
- la saisie manuelle et les objectifs sont utilisables
- les erreurs et etats partiels sont comprehensibles
- la reprise du projet est simple via les docs et les tests
