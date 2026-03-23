# 07 - Backend Plan via Health Connect

## Synthèse

Le projet peut s'appuyer sur une architecture simple et propre :

- `Health Connect` devient la source unique d'entrée.
- `Zepp`, `Yazio`, `Samsung Health` alimentent `Health Connect`.
- L'application lit uniquement `Health Connect`, puis normalise et agrège localement.

Chaîne cible :

```text
Zepp -> Health Connect <- Yazio
                     <- Samsung Health
                     -> ton application
```

## 1) Impact direct sur le projet

L'application ne doit pas intégrer Zepp ou Yazio directement.

Elle doit :

1. demander les permissions `Health Connect`,
2. lire les types de données autorisés,
3. normaliser les données en local,
4. afficher la source si nécessaire.

Ce modèle est aligné MVP :

- Android natif,
- local-first,
- simple,
- maintenable.

## 2) Source unique d'entrée

`Health Connect` est la source unique.

L'app lit les records standardisés `Health Connect` et ne raisonne pas en API propriétaires.

Exemples de données possibles selon permissions :

- Zepp : pas, sommeil, fréquence cardiaque, activité, parfois poids.
- Yazio : calories, nutrition, eau/hydratation, parfois poids.
- Samsung Health : pas, activité, sommeil, fréquence cardiaque, poids, autres métriques selon appareil.

## 3) Architecture recommandée

### Couche 1 - Health Connect repository

Repository dédié à la lecture des données brutes depuis `Health Connect`.

### Couche 2 - Normalisation

Transformation des records vers un modèle interne.

```kotlin
data class DailyMetric(
    val type: MetricType,
    val value: Double,
    val unit: String,
    val date: LocalDate,
    val sourceApp: String?,
    val isManual: Boolean = false
)
```

### Couche 3 - Agrégation métier

Calculs principaux :

- total de pas du jour,
- sommeil total,
- calories consommées,
- hydratation,
- tendance 7 jours.

### Couche 4 - UI

Le dashboard lit uniquement les modèles internes, jamais `Health Connect` directement.

## 4) Gestion des conflits multi-sources

Comme plusieurs apps écrivent dans `Health Connect`, il faut des règles explicites.

Risques :

- doublons,
- valeurs différentes,
- données partielles selon la source.

Stratégie MVP :

- lire toutes les entrées,
- conserver la dernière valeur ou agréger selon le type,
- exposer la source principale si nécessaire.

Exemple de règles :

- `steps` : somme journalière,
- `sleep` : dernière session consolidée,
- `weight` : dernière mesure connue,
- `nutrition` : somme journalière,
- `hydration` : somme journalière.

Modèle complémentaire conseillé :

```kotlin
data class MetricSourceInfo(
    val provider: String?,
    val lastUpdatedAt: Instant?
)
```

## 5) Ordre d'implémentation backend

### Priorité 1

Lire :

- Steps
- Sleep
- Weight
- Hydration
- Nutrition / calories (si disponibles)
- Heart rate (si utile)

### Priorité 2

Créer un écran `Permissions et sources` avec :

- apps détectées,
- types de données autorisés,
- dernier sync visible,
- état de lecture.

### Priorité 3

Construire le dashboard :

- pas du jour,
- sommeil,
- calories,
- eau,
- poids,
- tendance simple.

## 6) Positionnement produit

Positionnement recommandé :

> L'application centralise les données bien-être déjà synchronisées dans Health Connect, notamment depuis Zepp, Yazio ou Samsung Health.

Ce positionnement est plus robuste qu'une intégration API fournisseur par fournisseur.

## 7) Vigilances techniques

### 1. Ne pas stocker seulement les totaux

Conserver aussi :

- source,
- date,
- type,
- unité.

### 2. Vérifier les permissions type par type

Une app visible dans `Health Connect` n'écrit pas forcément toutes les métriques.

Exemples :

- Yazio peut écrire nutrition mais pas sommeil.
- Zepp peut écrire sommeil/pas mais pas nutrition.

### 3. Prévoir une fallback manuelle

Conserver une saisie manuelle MVP pour :

- poids,
- hydratation,
- humeur,
- notes.

## 8) Logique cible

```text
HealthConnectRepository
  -> readSteps()
  -> readSleep()
  -> readWeight()
  -> readHydration()
  -> readNutrition()

MetricsNormalizer
  -> map raw records to internal models

DailySummaryUseCase
  -> build dashboard data

DashboardViewModel
  -> expose ready-to-render UI state
```

UI cible :

```text
Dashboard
- Pas aujourd'hui
- Sommeil nuit dernière
- Eau aujourd'hui
- Calories aujourd'hui
- Poids dernière mesure
- Tendance 7 jours
- Source principale / dernière synchro
```

## Conclusion

Le backend du MVP doit se brancher directement sur `Health Connect`, avec normalisation et agrégation locales, en laissant Zepp/Yazio/Samsung Health alimenter cette couche.
