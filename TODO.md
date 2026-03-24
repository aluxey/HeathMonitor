# TODO - CyberDoc

## Objectif de cette liste

Cette todo reprend l'etat reel du projet et ordonne les prochaines etapes pour terminer un MVP coherent, testable et comprehensible.

Le projet vise un MVP Android natif, local-first, base sur `Health Connect`, `Room` et une UI `Compose`.

## Etat actuel du projet

- [x] Structure Android `Kotlin + Compose` en place
- [x] Base locale `Room` en place
- [x] Repositories `Health Connect -> normalisation -> persistance -> agregats` deja presents
- [x] Ecrans principaux deja maquettes et navigables
- [x] Synchronisation Health Connect deja implemente en backend local
- [ ] Build/test local non verifiable ici tant que le SDK Android n'est pas configure
- [ ] Plusieurs ecrans UI sont encore relies a des donnees statiques ou a des comportements factices

## Priorite 0 - Debloquer l'environnement et figer le perimetre MVP

- [ ] Configurer le SDK Android localement via `ANDROID_HOME` ou `local.properties`
- [ ] Verifier que `./gradlew test` et `./gradlew assembleDebug` s'executent
- [ ] Installer l'application sur un appareil Android reel
- [ ] Confirmer les metriques retenues pour le MVP:
  - `steps`
  - `sleep_duration`
  - `weight`
  - `hydration`
  - `calories_in`
- [ ] Decider explicitement si `heart_rate` et `exercise_duration` restent dans le MVP ou sortent du scope

## Priorite 1 - Rendre le flux de donnees reel de bout en bout

- [ ] Verifier sur appareil quelles permissions `Health Connect` remontent vraiment
- [ ] Valider la lecture reelle des donnees sur 7 jours
- [ ] Verifier la qualite des donnees importees:
  - doublons
  - trous de donnees
  - unites
  - provenance
- [ ] Afficher dans l'application le dernier sync, le statut du sync et les erreurs partielles
- [ ] Utiliser les agregats journaliers comme base du dashboard au lieu de seulement lire la derniere mesure
- [ ] Calculer et exposer les tendances `7 jours` et `30 jours`
- [ ] Expliquer clairement dans l'UI pourquoi une metrique est vide ou partielle

## Priorite 2 - Supprimer les ecarts entre UI, domaine et donnees

- [ ] Retirer les donnees de fallback purement demo quand les donnees reelles sont disponibles
- [ ] Supprimer le bootstrap temporaire au demarrage une fois les vrais etats vides geres proprement
- [ ] Aligner la liste des metriques UI avec le domaine:
  - l'ecran de saisie manuelle propose aujourd'hui `height` et `temperature`, qui ne sont pas supportes par le domaine
  - les ecrans objectifs/profil affichent encore des valeurs statiques
- [ ] Brancher l'ecran Home sur un read model reel complet
- [ ] Brancher l'ecran Goals sur les objectifs stockes en base
- [ ] Brancher l'ecran Profile sur de vraies donnees locales ou le reduire au scope MVP reel
- [ ] Eviter la double synchro implicite au lancement et centraliser la logique de refresh

## Priorite 3 - Finaliser les flux utilisateur MVP

- [ ] Rendre la saisie manuelle fonctionnelle pour les metriques retenues
- [ ] Apres saisie manuelle:
  - enregistrer la mesure via le use case
  - recalculer les agregats
  - rafraichir le dashboard
  - afficher un vrai message de succes ou d'erreur
- [ ] Permettre la creation et la modification d'objectifs depuis l'UI
- [ ] Ajouter les etats `loading`, `empty`, `partial`, `error` sur tous les ecrans critiques
- [ ] Rendre la navigation onboarding -> permissions -> app robuste sur tous les cas:
  - Health Connect absent
  - permissions refusees
  - permissions partielles
  - sync en echec

## Priorite 4 - Stabiliser l'architecture applicative

- [ ] Introduire des `ViewModel` ou un equivalent de state holder pour sortir la logique des composables
- [ ] Centraliser les appels use case et la gestion des erreurs
- [ ] Clarifier la responsabilite de chaque couche:
  - `integration`
  - `data`
  - `domain`
  - `ui`
- [ ] Garder l'UI consommatrice de modeles prets a afficher, sans acces direct a la logique d'integration
- [ ] Revoir les noms et documents encore incoherents avec le projet `CyberDoc`

## Priorite 5 - Qualite et tests

- [ ] Ajouter un dossier de tests unitaires
- [ ] Tester `DefaultMetricsNormalizer`
- [ ] Tester `DailyAggregateCalculator`
- [ ] Tester `RegisterManualMetricUseCase`
- [ ] Tester `SyncHealthConnectDataUseCase`
- [ ] Ajouter des tests DAO pour:
  - insertions
  - lecture par jour
  - recuperation des tendances
  - contrainte d'unicite sur les enregistrements
- [ ] Ajouter au minimum un test d'integration du pipeline `Health Connect -> normalisation -> Room -> dashboard`

## Priorite 6 - Base de livraison propre

- [ ] Remplacer `fallbackToDestructiveMigration` par de vraies migrations versionnees
- [ ] Documenter le setup local Android dans un README racine
- [ ] Documenter le flux MVP:
  - onboarding
  - permissions
  - sync
  - dashboard
  - saisie manuelle
- [ ] Ajouter une checklist de verification avant release
- [ ] Relire les textes affiches a l'utilisateur pour la clarte produit et la transparence vie privee

## Hors MVP a garder pour plus tard

- [ ] Export local CSV ou PDF
- [ ] Synchronisation cloud multi-appareils
- [ ] Integrations directes Zepp, Yazio ou Samsung hors `Health Connect`
- [ ] Analyses avancees et recommandations
- [ ] Coaching ou promesses "medicales"

## Definition de fini MVP

Le MVP peut etre considere comme termine si:

- [ ] l'application se lance et tourne sur appareil Android reel
- [ ] les permissions `Health Connect` sont gerables proprement
- [ ] au moins 4 metriques coeur sont lues de facon fiable
- [ ] le dashboard affiche des donnees reelles, lisibles et a jour
- [ ] la saisie manuelle fonctionne pour les metriques retenues
- [ ] les objectifs sont modifiables depuis l'application
- [ ] l'application supporte les etats vides, partiels et en erreur sans casser l'UX
- [ ] les tests critiques existent et passent
- [ ] la documentation de setup et d'usage est suffisante pour reprendre le projet sans ambiguite
