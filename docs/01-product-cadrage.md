# 01 - Product Cadrage

## 1. Reformulation du besoin

L'objectif n'est pas de créer une nouvelle application de santé générique.

L'objectif est de construire un poste de pilotage personnel qui centralise des données aujourd'hui dispersées entre plusieurs applications pour donner une vision quotidienne simple, cohérente et exploitable.

Le produit doit permettre de répondre rapidement à des questions concrètes :

- est-ce que ma journée est alignée avec mes objectifs
- comment évoluent mes tendances sur 7 jours et 30 jours
- quelles données sont manquantes, incohérentes ou dupliquées
- quelles habitudes ont le plus d'impact sur mon équilibre quotidien

## 2. Vision produit

Une application mobile personnelle qui :

- agrège les données utiles à partir d'une source Android fiable
- normalise des métriques hétérogènes
- affiche un tableau de bord très lisible
- permet un suivi régulier sans friction

Le produit ne vise pas :

- le diagnostic médical
- le coaching intelligent dès la V1
- la synchronisation complète avec tous les écosystèmes
- une couverture exhaustive de toutes les données santé possibles

## 3. Persona principal

### Persona coeur de cible

Utilisateur Android équipé d'une montre ou d'un tracker, qui utilise déjà plusieurs applications santé et nutrition, mais qui n'a pas de vue unifiée fiable et simple de son quotidien.

### Attentes

- ouvrir l'application en moins de 30 secondes pour comprendre sa journée
- suivre quelques métriques essentielles sans surcharge
- voir des tendances utiles et non des écrans saturés
- pouvoir faire confiance à la donnée affichée

## 4. Cas d'usage principaux

### Usage quotidien

- consulter les données du jour
- comparer avec l'objectif
- voir les évolutions sur 7 jours

### Usage hebdomadaire

- faire un bilan synthétique
- repérer les écarts récurrents
- ajuster ses objectifs

### Usage correctif

- comprendre l'origine d'une valeur
- corriger une donnée absente via saisie manuelle

## 5. Problèmes à résoudre

### Problème 1 : fragmentation

Les données sont réparties entre plusieurs applications ayant chacune leur logique, leur vocabulaire et leur expérience utilisateur.

### Problème 2 : hétérogénéité

Les mêmes métriques peuvent être décrites différemment selon la source :

- pas journaliers
- sommeil découpé ou résumé
- calories consommées
- poids et unités

### Problème 3 : perte de confiance

Quand deux sources remontent des valeurs différentes, l'utilisateur ne sait pas laquelle croire. Sans transparence sur la provenance, le produit perd sa valeur.

### Problème 4 : surcharge

Les apps existantes collectent bien, mais synthétisent mal un suivi multi-source orienté objectifs personnels.

## 6. Opportunité produit

Il existe une place pour un produit plus utile si le cadrage reste strict :

- peu de métriques
- lecture simple
- explication de la provenance
- logique local-first

## 7. Principes de produit

- un MVP intelligent vaut mieux qu'un faux hub universel
- l'application doit d'abord bien lire et bien afficher
- toute intégration incertaine doit être dégradée en sujet V2 ou en import manuel
- chaque écran doit répondre à une question simple

## 8. Positionnement retenu

Le bon point de départ est un produit de type :

- `journal de bord santé personnel`
- `agrégateur léger de données`
- `tableau de bord de tendances`

Le mauvais point de départ serait :

- un concurrent de Samsung Health ou Google Fit
- un produit médical
- un orchestrateur d'intégrations complexes multi-cloud

## 9. Décisions de cadrage recommandées

- périmètre Android uniquement au démarrage
- application centrée sur le suivi personnel et non sur le partage
- priorité aux métriques simples et robustes
- stockage local en premier
- pas d'authentification ni backend dans le MVP
