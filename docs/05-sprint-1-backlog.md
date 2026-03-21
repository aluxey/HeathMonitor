# 05 - Sprint 1 Backlog Exécutable

## Objectif

Transformer le cadrage et les fondations en première boucle réellement testable sur appareil Android.

## Déjà lancé

- structure projet Android native `Kotlin + Compose`
- base locale `Room`
- couches `integration / domain / data / ui`
- contrats `Health Connect`
- dashboard initial branché sur données locales
- écran `Sources` branché sur disponibilité et permissions

## Sprint 1 - à finir

### Bloc A - Spike appareil

- installer et lancer l'application sur le téléphone Android cible
- vérifier la disponibilité réelle de `Health Connect`
- vérifier la présence réelle des permissions coeur
- confirmer quelles métriques remontent proprement : `steps`, `sleep_duration`, `weight`, `calories_in`

### Bloc B - Synchro réelle

- remplacer le stub de synchro par lecture réelle `Health Connect`
- mapper les enregistrements externes vers `metric_record`
- recalculer `daily_aggregate` après import
- journaliser un `sync_run` fiable

### Bloc C - MVP écran d'accueil

- enrichir les cartes avec écart à l'objectif
- ajouter la vue `7 jours` et `30 jours`
- expliciter les trous de données

### Bloc D - Manuel

- ajouter la saisie manuelle du poids
- distinguer visuellement la donnée manuelle
- recalculer les agrégats après saisie

## Définition de fini Sprint 1

- l'application se lance sur appareil
- l'utilisateur voit un dashboard cohérent
- l'écran `Sources` explique clairement permissions et synchro
- au moins une synchro réelle peut être exécutée
