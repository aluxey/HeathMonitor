# 02 - MVP et Backlog Initial

## 1. Périmètre MVP

Le MVP doit prouver une chose : l'application t'apporte une vision plus utile que les apps sources, avec un coût de développement raisonnable.

### Objectif MVP

Permettre à un utilisateur Android de suivre simplement son quotidien via un tableau de bord unifié couvrant :

- activité quotidienne
- pas
- sommeil
- poids
- nutrition calories
- progression vers des objectifs simples

### Fonctionnalités MVP

- lecture des données via `Health Connect`
- import et mise à jour locale des données utiles
- tableau de bord `Aujourd'hui`
- vue tendances `7 jours` et `30 jours`
- objectifs simples par métrique
- saisie manuelle du poids
- écran de statut des sources et des permissions
- règles de priorité de source pour éviter les conflits

## 2. Métriques retenues pour le MVP

### Priorité 1

- `steps`
- `sleep_duration`
- `weight`
- `calories_in`

### Priorité 2

- `exercise_duration` ou `exercise_sessions_count`

Décision recommandée :

- commencer avec 4 métriques coeur
- ajouter l'activité d'exercice uniquement si la lecture est propre et stable dans le spike technique

## 3. Fonctionnalités hors MVP

### V2 plausible

- historique importé plus profond
- macros nutritionnelles
- journal de notes quotidiennes
- export CSV ou PDF
- objectifs avancés
- bilan hebdomadaire automatisé

### Hors MVP clair

- iOS
- application web comme produit principal
- synchronisation cloud multi-appareils
- écriture vers les apps tierces
- coaching IA
- recommandations médicales
- fréquence cardiaque, HRV, stress, oxygénation et autres données plus sensibles

## 4. Arbitrages produits

### Arbitrage 1 : centraliser versus intégrer partout

Le MVP doit centraliser via une source pivot. Il ne doit pas ouvrir plusieurs chantiers d'intégration incertains.

### Arbitrage 2 : profondeur versus fiabilité

Mieux vaut 4 métriques fiables qu'un dashboard large mais incohérent.

### Arbitrage 3 : insights versus lisibilité

Le MVP doit privilégier des tendances simples et des écarts à l'objectif. Les analyses avancées peuvent attendre.

## 5. User stories MVP

### Epic 1 - Connexion des données

- En tant qu'utilisateur, je veux autoriser l'application à lire mes données santé Android pour éviter les doubles saisies.
- En tant qu'utilisateur, je veux voir quelles permissions sont accordées ou manquantes pour comprendre pourquoi une donnée n'apparaît pas.

### Epic 2 - Tableau de bord

- En tant qu'utilisateur, je veux voir mes métriques du jour sur un seul écran pour comprendre ma journée en quelques secondes.
- En tant qu'utilisateur, je veux voir ma progression par rapport à mes objectifs pour savoir où j'en suis.

### Epic 3 - Tendances

- En tant qu'utilisateur, je veux voir mes tendances sur 7 jours et 30 jours pour repérer les évolutions.
- En tant qu'utilisateur, je veux distinguer une valeur du jour d'une tendance moyenne pour éviter les interprétations hâtives.

### Epic 4 - Fiabilité

- En tant qu'utilisateur, je veux connaître la source d'une donnée pour pouvoir lui faire confiance.
- En tant qu'utilisateur, je veux éviter les doublons quand plusieurs applications alimentent la même métrique.

### Epic 5 - Manuel

- En tant qu'utilisateur, je veux saisir mon poids manuellement si aucune source ne remonte l'information.

## 6. Critères d'acceptation MVP

### Tableau de bord

- l'écran d'accueil affiche les 4 métriques coeur
- chaque métrique montre la valeur du jour
- chaque métrique montre l'objectif et l'écart
- chaque carte indique l'état de fraîcheur de la donnée

### Tendances

- l'utilisateur peut basculer entre `7 jours` et `30 jours`
- chaque métrique affiche une courbe ou une évolution simple
- les périodes sans donnée sont explicites

### Source et permissions

- l'utilisateur voit les permissions nécessaires
- l'utilisateur voit la dernière synchronisation
- l'utilisateur voit la source prioritaire retenue

### Saisie manuelle

- l'utilisateur peut créer une mesure de poids
- la donnée manuelle est distinguée de la donnée importée
- la saisie met à jour les agrégats et les tendances

## 7. Backlog initial priorisé

## P0 - indispensable

- définir les métriques finales du MVP
- connecter `Health Connect`
- lire les données coeur
- stocker les enregistrements normalisés
- calculer les agrégats journaliers
- construire le tableau de bord
- gérer permissions et erreurs

## P1 - très important

- tendances 7 jours et 30 jours
- objectifs simples
- saisie manuelle du poids
- écran sources et synchronisation

## P2 - utile mais non bloquant

- export local
- filtre par période plus avancé
- notes quotidiennes

## 8. Définition de succès du MVP

Le MVP est validé si :

- les données sont lisibles et cohérentes au quotidien
- l'application apporte une synthèse plus rapide que les apps sources
- la maintenance des intégrations reste faible
- le produit est utilisable en usage réel pendant plusieurs semaines sans friction majeure
