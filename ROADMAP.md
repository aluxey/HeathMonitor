# ROADMAP — HeathMonitor

## Objectif global
Permettre le suivi continu des données de santé, historiser les mesures, offrir un profil utilisateur persistant et des visualisations claires.

## Jalon 1 — Stabilisation & Persistance (0–2 semaines)
- Remplacer les données mock par une base de données locale/serveur
- Implémenter Measurement + UserProfile
- Endpoints CRUD pour profil et mesures
- Affichage de l'historique basique et graphique

Livrables :
- TODO.md (this file)
- Backend: stockage + API
- Frontend: page Profil + Historique
- Tests unitaires de base

## Jalon 2 — Fonctionnalités utilisateur & import/export (2–6 semaines)
- Import/Export CSV
- Valeurs de base et comparaisons
- Alertes simples (seuils)
- Agrégation journalière / hebdomadaire

Livrables :
- UI améliorée (filtres)
- Jobs d'agrégation
- Documentation utilisateur

## Jalon 3 — Robustesse & intégrations (6–12 semaines)
- Auth et multi‑utilisateur
- Sync/offline
- Connecteurs wearables / export vers Apple Health / Google Fit

Livrables :
- Pipeline de synchronisation
- Connecteurs minimum 1 appareil / service
- Tests d'intégration

## Jalon 4 — Conformité & production (3+ mois)
- Chiffrement, RGPD, sauvegardes, monitoring
- Visualisations avancées et recommandations
- Scalabilité & optimisation des historiques volumineux

Livrables :
- Politique de rétention
- Architecture de backup
- Tableaux de bord analytics
