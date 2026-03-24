# TODO — HeathMonitor

## Contexte
Remplacement des données mock par de la persistance, suivi historique des mesures et sauvegarde d'un profil de base utilisateur (taille, poids, etc). Tâches listées et priorisées.

---

## Priorité : Immédiate (à faire cette itération)
- [ ] Supprimer / isoler les données mock
  - [ ] Identifier tous les fichiers/points où les mocks sont utilisés
  - [ ] Remplacer par des appels vers la couche stockage ou adapter pour permettre injection de données
- [ ] Créer la couche de stockage persistant
  - [ ] Choix DB (SQLite pour local/embedded, Postgres pour serveur) — définir dans une PR séparée
  - [ ] Modèle de données initial :
    - UserProfile: id, user_id, height_cm, weight_kg, date_of_birth, sex, unit_preferences, created_at, updated_at
    - Measurement: id, user_id, type (weight, hr, bp, spo2, steps, sleep), value (float/json), unit, recorded_at (timestamp), created_at
  - [ ] API/Service pour CRUD des Measurements
- [ ] Sauvegarde et affichage du profil de base
  - [ ] Page/API `GET/POST/PUT /profile`
  - [ ] Validation (taille>0, poids>0, date valide)
- [ ] Historique & visualisation
  - [ ] Implémenter liste paginée `GET /measurements?type=weight&from=&to=&limit=&page=`
  - [ ] Graphiques côté front (ex: Chart.js / Recharts) : courbe pour chaque type
  - [ ] Affichage d'un intervalle de référence (dernier N jours, comparaisons)
- [ ] Migration & seed
  - [ ] Script pour transformer/insérer les données mock existantes dans la DB
- [ ] Tests
  - [ ] Tests unitaires pour la couche stockage et endpoints
  - [ ] Tests d'intégration basiques pour historique / sauvegarde profil

---

## Priorité : Prochaine étape (2–6 semaines)
- [ ] Export / import CSV (mesures et profil)
- [ ] Fonctionnalité "valeurs de base" / "valeurs de référence"
  - [ ] Permettre à l'utilisateur d'enregistrer des valeurs de base (taille, poids, IMC cible)
  - [ ] Affichage et comparaison par défaut dans les graphiques
- [ ] Alertes / seuils
  - [ ] Définir seuils par type et alerter (UI + notifications)
- [ ] Aggregations
  - [ ] Calculs journaliers/hebdomadaires (moyenne, min/max) en tâche planifiée
- [ ] Améliorations UI/UX
  - [ ] Page dédiée "Historique complet"
  - [ ] Possibilité de filtrer par date, type de mesure
- [ ] Auth & permissions (si multi‑utilisateurs)
- [ ] Documentation développeur : README mise à jour pour setup DB & migrations

---

## Priorité : Long terme (3+ mois)
- [ ] Synchronisation multi‑appareils et offline
- [ ] Connecteurs externes (wearables, Apple Health/Google Fit import)
- [ ] Chiffrement au repos, contrôle d'accès, RGPD (export & suppression des données)
- [ ] Visualisations avancées & recommandations
- [ ] Backups automatisés et politique de rétention
- [ ] Tests end‑to‑end et monitors CI/CD

---

## Maintenance & gestion technique
- [ ] Ajouter tests CI (GitHub Actions)
- [ ] Ajouter linting + formatters
- [ ] Documenter la structure DB et endpoints (OpenAPI / swagger)
- [ ] Plan de migration pour breaking-changes du modèle Measurement
