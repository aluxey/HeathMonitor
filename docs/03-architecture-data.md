# 03 - Architecture Fonctionnelle et Modèle de Données

## 1. Format d'application recommandé

## Recommandation

Construire d'abord une application `Android native`.

## Justification

- `Health Connect` est un point d'entrée Android natif
- les données santé concernées sont sur le téléphone
- une application web ou PWA ne peut pas devenir un bon point d'accès à ces données
- une approche local-first réduit fortement la complexité

## Ce qu'il ne faut pas faire au départ

- démarrer par une web app responsive
- construire un backend alors qu'il n'existe pas encore de besoin validé
- viser iOS dès le premier cycle

## 2. Architecture fonctionnelle simple

L'architecture doit rester monolithique, lisible et évolutive.

### Blocs fonctionnels

1. `Connectors`
   lecture des sources disponibles
2. `Normalization`
   transformation vers un modèle métier commun
3. `Storage`
   persistance locale des données normalisées
4. `Aggregation`
   calcul des indicateurs journaliers et tendances
5. `Presentation`
   tableaux de bord, tendances, objectifs, statut des sources

## 3. Architecture applicative recommandée

### Couche `integration`

Responsabilités :

- lecture `Health Connect`
- mapping des types externes vers des types internes
- gestion des permissions

### Couche `domain`

Responsabilités :

- logique métier sur les métriques
- règles de priorité de source
- règles de calcul des agrégats

### Couche `data`

Responsabilités :

- stockage local
- repositories
- exécution des synchronisations

### Couche `ui`

Responsabilités :

- dashboard
- tendances
- objectifs
- paramètres et sources

## 4. Stack technique MVP

### Application

- `Kotlin`
- `Jetpack Compose`
- `Android Jetpack`

### Santé et données

- `Health Connect SDK`
- `Room`
- `WorkManager`

### Architecture

- `Coroutines`
- `Flow`
- DI simple avec `Koin` ou injection manuelle

### Visualisation

- bibliothèque de graphes légère

Décision recommandée :

- éviter les frameworks lourds
- éviter les patterns trop abstraits
- rester sur un socle standard Android maintenable

## 5. Sources de données et contraintes

## Source pivot recommandée

`Health Connect`

Pourquoi :

- point d'agrégation Android adapté au MVP
- modèle d'autorisations clair
- meilleure trajectoire qu'une intégration directe à plusieurs fournisseurs

## Sources envisagées

### `Samsung Health`

À privilégier via `Health Connect` dans le MVP.

### `Yazio`

À privilégier via `Health Connect` si les données nutritionnelles y remontent correctement.

### `Zepp / Amazfit`

À considérer comme source indirecte ou contrainte externe.

Position recommandée :

- ne pas baser le MVP sur une intégration directe Zepp
- vérifier pendant le spike si Zepp alimente correctement l'écosystème Android utilisé
- si ce n'est pas le cas, traiter Zepp comme sujet V2 ou import

## 6. Règles de synchronisation

### Principes

- lecture unidirectionnelle au départ
- pas d'écriture vers les plateformes externes
- synchronisation déclenchée à l'ouverture et via tâche planifiée légère
- journalisation simple des erreurs

### Gestion des conflits

Pour une même métrique, il faut une règle explicite :

- source prioritaire par défaut
- fallback si la source prioritaire ne remonte rien
- affichage de la provenance retenue

## 7. Modèle de données métier

## Entités minimales

### `user_profile`

- `id`
- `timezone`
- `weight_unit`
- `energy_unit`
- `created_at`
- `updated_at`

### `data_source`

- `id`
- `type`
- `display_name`
- `status`
- `priority`
- `last_sync_at`
- `last_error`

### `metric_record`

- `id`
- `metric_type`
- `value`
- `unit`
- `start_at`
- `end_at`
- `source_id`
- `external_id`
- `is_manual`
- `created_at`

### `daily_aggregate`

- `id`
- `date`
- `metric_type`
- `value`
- `unit`
- `source_id`
- `quality_flag`
- `computed_at`

### `goal`

- `id`
- `metric_type`
- `target_value`
- `period_type`
- `start_date`
- `end_date`
- `is_active`

### `sync_run`

- `id`
- `source_id`
- `started_at`
- `ended_at`
- `status`
- `records_read`
- `message`

## 8. Liste initiale des métriques

- `steps`
- `sleep_duration`
- `weight`
- `calories_in`
- `exercise_duration`

Option :

- remplacer `exercise_duration` par `exercise_sessions_count` si les données sont plus fiables

## 9. Schéma logique simplifié

```text
data_source -> metric_record -> daily_aggregate
                         \-> goal (par metric_type)
data_source -> sync_run
```

## 10. Décisions techniques structurantes

- stocker les enregistrements normalisés, pas seulement des résumés journaliers
- recalculer les agrégats localement
- distinguer les données manuelles et importées
- prévoir dès le départ la provenance et la qualité de la donnée
