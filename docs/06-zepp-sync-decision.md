# 06 - Décision d'intégration Zepp

## Décision retenue

Le MVP ne dépend pas d'une intégration directe `Zepp`.

Le chemin de lancement retenu est :

- lecture via `Health Connect`
- détection de la provenance réelle des données par package source
- priorité `Zepp / Amazfit` si les données sont bien visibles dans `Health Connect`
- fallback sur `Samsung Health` ou autre source réellement présente

## Pourquoi

### 1. Zepp direct augmente fortement le risque

Une synchro directe `Zepp` introduit immédiatement :

- une dépendance fournisseur supplémentaire
- une surface d'intégration moins standard pour Android
- un risque de maintenance supérieur au bénéfice MVP

### 2. Health Connect reste le point pivot cohérent

`Health Connect` est déjà le socle recommandé pour :

- les permissions
- la lecture locale
- la provenance des enregistrements
- un MVP Android local-first

### 3. Le vrai test est sur appareil

La question utile n'est pas "Zepp a-t-il une API quelque part".

La question utile MVP est :

- est-ce que `Zepp` alimente bien `Health Connect` sur ton téléphone
- et si oui, sur quelles métriques exactement

## Règle produit pour Sprint 1

- si `steps`, `sleep_duration` ou `weight` remontent avec provenance `Zepp/Amazfit` dans `Health Connect`, on les exploite
- sinon on ne bloque pas le MVP et on prend la meilleure source disponible dans `Health Connect`
- si `Zepp` ne remonte rien de fiable, on garde `Zepp direct` comme sujet d'investigation V2

## Ce qu'il faut vérifier sur appareil

- disponibilité de `Health Connect`
- permissions accordées
- présence des paquets source `Zepp` ou `Amazfit`
- présence réelle des métriques coeur sur 7 à 30 jours
