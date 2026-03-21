# 04 - Roadmap, Décisions et Risques

## 1. Roadmap par phases

## Phase 0 - Cadrage

Durée cible : `3 à 5 jours`

Livrables :

- métriques MVP figées
- wireframes basiques
- règles de priorité des sources
- définition du succès MVP

## Phase 1 - Spike technique

Durée cible : `1 à 2 semaines`

Objectif :

Valider la faisabilité réelle sur ton téléphone et tes apps.

À vérifier :

- lecture `Health Connect`
- disponibilité des pas
- disponibilité du sommeil
- disponibilité du poids
- disponibilité des calories
- comportement de Zepp dans ton écosystème actuel

Critère de sortie :

- on sait exactement quelles données seront fiables dans le MVP

## Phase 2 - Fondations techniques

Durée cible : `1 à 2 semaines`

Travaux :

- structure projet
- base locale
- modèle de données
- couche intégration
- couche agrégation

## Phase 3 - MVP fonctionnel

Durée cible : `2 à 4 semaines`

Travaux :

- écran d'accueil
- objectifs
- tendances
- saisie manuelle
- écran sources et sync

## Phase 4 - Stabilisation

Durée cible : `1 à 2 semaines`

Travaux :

- correction bugs
- cohérence de données
- nettoyage UX
- test usage réel quotidien

## Phase 5 - Validation produit

Durée cible : `2 semaines`

Travaux :

- usage personnel réel
- collecte des irritants
- arbitrage V2 basé sur le vécu

## 2. Décisions à prendre avant de coder

### Décisions produit

- Android only ou non
- nombre exact de métriques MVP
- profondeur d'historique au lancement
- présence ou non d'une saisie manuelle calories

### Décisions techniques

- local only ou sauvegarde distante
- stratégie de synchronisation
- stratégie de déduplication
- niveau de journalisation technique

## 3. Recommandations fermes

### Recommandation 1

Ne pas construire de backend dans le MVP.

### Recommandation 2

Ne pas viser une intégration directe Zepp comme dépendance de lancement.

### Recommandation 3

Ne pas dépasser 4 ou 5 métriques coeur.

### Recommandation 4

Ne pas faire de promesse d'intelligence ou de coaching tant que la fiabilité de base n'est pas démontrée.

## 4. Principaux risques

## Risques techniques

### Intégrations instables

Toutes les apps ne remontent pas forcément les mêmes données au même endroit ni avec la même fraîcheur.

### Qualité de données

Le sommeil, les calories et l'activité peuvent diverger fortement selon la source.

### Déduplication

Une même donnée peut être visible plusieurs fois si plusieurs applications écrivent dans la même source pivot.

## Risques produit

### MVP trop large

Le risque principal est de vouloir tout faire dès le départ.

### Valeur insuffisante

Si le dashboard n'est qu'une copie des apps existantes, le produit n'apporte pas de valeur.

### Absence de confiance

Si la provenance des données n'est pas claire, l'utilisateur n'adoptera pas le produit.

## Risques UX

### Trop de données

Une interface dense dégrade la lisibilité et l'usage quotidien.

### Données manquantes

Il faut expliquer clairement pourquoi une métrique est vide.

## 5. Arbitrages recommandés

### Arbitrage A - simplicité contre couverture

Prendre la simplicité.

### Arbitrage B - local-first contre sync cloud

Prendre local-first.

### Arbitrage C - Android natif contre multi-plateforme

Prendre Android natif.

### Arbitrage D - qualité de données contre volume de métriques

Prendre la qualité de données.

## 6. Définition du go / no-go après spike

## Go

Le projet continue si :

- au moins 4 métriques sont récupérables proprement
- le dashboard apporte une meilleure synthèse que les apps sources
- la maintenance prévue reste compatible avec un projet solo

## No-go ou re-cadrage

Le projet doit être re-cadré si :

- les données coeur ne remontent pas de façon fiable
- Zepp devient un verrou incontournable
- l'application doit finalement gérer trop de cas d'intégration spécifiques

## 7. Prochaines sorties documentaires utiles

Après validation de ce cadrage, les documents suivants seront utiles :

- wireframes des écrans MVP
- user flows
- backlog sprint 1
- conventions de données et règles d'agrégation
- critères de test du spike technique
