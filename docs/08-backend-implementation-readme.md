# 08 - Backend Implementation README

## Objectif

Fournir un plan d'implémentation backend Android (local) clair et exécutable, aligné avec la stratégie :

- source unique : `Health Connect`,
- normalisation locale,
- agrégation locale,
- exposition de données propres pour l'UI dashboard.

Ce document sert de checklist de développement et d'ordre des tickets.

## Périmètre

Inclus :

- lecture `Health Connect`,
- mapping vers modèle domaine,
- persistance locale,
- calcul d'agrégats journaliers,
- synchronisation manuelle + automatique,
- exposition via use cases / viewmodels.

Exclus (pour l'instant) :

- backend serveur distant,
- intégrations API directes Zepp/Yazio/Samsung,
- analytics avancée et export.

## Architecture cible (rappel)

```text
integration/healthconnect  ->  data/local  ->  domain/usecases  ->  ui
```

- `integration`: accès SDK, permissions, lecture records.
- `data`: cache local, repositories, transactions.
- `domain`: règles métier, normalisation, agrégation.
- `ui`: consomme uniquement des modèles prêts à afficher.

## Pré-requis techniques

1. `androidx.health.connect:connect-client` configuré.
2. Permissions Health Connect déclarées et demandées runtime.
3. Base locale (Room) prête pour :
   `data_source`, `metric_record`, `daily_aggregate`, `goal`, `sync_run`.
4. Coroutines + dispatcher IO centralisé.
5. Horloge injectée (`TimeProvider`) pour code testable.

## Contrats backend à figer

1. Types de métriques MVP :
   `steps`, `sleep_duration`, `weight`, `hydration`, `calories_in`.
2. Règles d'agrégation :
   - `steps` somme journalière,
   - `sleep_duration` consolidation session/nuit,
   - `weight` dernière valeur connue,
   - `hydration` somme journalière,
   - `calories_in` somme journalière.
3. Règles de conflit multi-sources :
   - conserver provenance,
   - dédoublonner par `(metricType, startAt, endAt, sourceId, externalId)`,
   - stratégie fallback si données partielles.

## Checklist d'implémentation

## Phase A - Integration Health Connect

- [ ] Créer `HealthConnectRepository`.
- [ ] Implémenter vérification disponibilité SDK.
- [ ] Implémenter gestion permissions runtime.
- [ ] Lire records : steps, sleep, weight, hydration, nutrition/calories.
- [ ] Récupérer métadonnées de provenance (package/source).
- [ ] Ajouter gestion erreurs techniques (permission/refus/SDK absent).

## Phase B - Normalisation

- [ ] Créer `MetricsNormalizer`.
- [ ] Mapper chaque record Health Connect vers modèle interne.
- [ ] Uniformiser unités (ex: kg, kcal, ml, minutes).
- [ ] Ajouter flag `isManual` et `sourceApp`.
- [ ] Ajouter tests unitaires du mapping.

## Phase C - Persistance locale

- [ ] Créer schéma Room complet (entités + dao + indices).
- [ ] Implémenter repositories data (`Source`, `Metric`, `Aggregate`, `Sync`).
- [ ] Ajouter upsert et déduplication robuste.
- [ ] Ajouter migrations DB versionnées.
- [ ] Ajouter tests DAO (insert/read/update/dedup).

## Phase D - Agrégation métier

- [ ] Créer `DailySummaryUseCase`.
- [ ] Calculer agrégats journaliers par type.
- [ ] Calculer tendance 7 jours.
- [ ] Calculer source principale + dernier update.
- [ ] Ajouter tests métier sur cas limites (données manquantes, doublons).

## Phase E - Synchronisation

- [ ] Créer `SyncHealthDataUseCase`.
- [ ] Pipeline : `read -> normalize -> persist -> aggregate -> sync_run`.
- [ ] Gérer statut sync (`SUCCESS/PARTIAL/FAILED`) et message.
- [ ] Déclenchement :
  - ouverture app,
  - action manuelle utilisateur,
  - tâche planifiée légère (`WorkManager`) si conservé.
- [ ] Ajouter tests d'intégration du pipeline.

## Phase F - Exposition UI

- [ ] Créer `DashboardRepository` orienté lecture UI.
- [ ] Exposer `DashboardSnapshot` prêt à rendre.
- [ ] Connecter ViewModel dashboard au use case.
- [ ] Créer écran `Permissions et sources` (état réel backend).
- [ ] Gérer états UI : loading, empty, partial data, error.

## Tickets ordonnés (ordre recommandé)

1. `BE-01` Setup Health Connect + permissions runtime.
2. `BE-02` Lecteurs records MVP (steps/sleep/weight/hydration/calories).
3. `BE-03` MetricsNormalizer + tests unitaires.
4. `BE-04` Schéma Room final + DAO + migrations.
5. `BE-05` Repositories data + dedup.
6. `BE-06` DailySummaryUseCase + tendance 7 jours.
7. `BE-07` SyncHealthDataUseCase pipeline complet.
8. `BE-08` Historique sync (`sync_run`) + erreurs.
9. `BE-09` Dashboard read model + ViewModel branché.
10. `BE-10` Écran `Permissions et sources` branché backend.

## Definition of Done (DoD) par ticket

Chaque ticket est terminé si :

1. code compilable,
2. tests unitaires/DAO pertinents passants,
3. logging d'erreur minimal ajouté,
4. contrat de sortie documenté (modèle ou interface),
5. aucun accès direct Health Connect depuis l'UI.

## Risques et mitigations

1. Données incohérentes entre sources.
   Mitigation : règles d'agrégation explicites + provenance persistée.
2. Permissions incomplètes utilisateur.
   Mitigation : écran source/permission clair + états partiels.
3. Variations d'unités selon source.
   Mitigation : normalisation stricte avant persist.
4. Dédoublonnage insuffisant.
   Mitigation : clé technique stable + tests avec données redondantes.

## Livrable attendu de la phase backend

À la fin de cette phase, l'app doit :

- lire les données `Health Connect` réelles,
- afficher un dashboard cohérent basé sur données normalisées locales,
- montrer la provenance et le dernier sync,
- supporter les cas partiels sans casser l'interface.
