# CyberDoc - Documentation projet

Cette documentation cadre le projet avant démarrage du développement.

## Sommaire

- [01-product-cadrage.md](01-product-cadrage.md) : vision produit, besoin, problèmes, personas, principes de cadrage
- [02-mvp-backlog.md](02-mvp-backlog.md) : périmètre MVP, hors MVP, user stories, critères d'acceptation, backlog initial
- [03-architecture-data.md](03-architecture-data.md) : format d'application, architecture fonctionnelle, stack, modèle de données, intégrations
- [04-roadmap-decisions-risks.md](04-roadmap-decisions-risks.md) : roadmap, arbitrages, risques, décisions à figer avant de coder
- [05-sprint-1-backlog.md](05-sprint-1-backlog.md) : backlog d'exécution pour transformer les fondations en premier incrément testable
- [06-zepp-sync-decision.md](06-zepp-sync-decision.md) : décision MVP sur la place de Zepp face à Health Connect
- [07-backend-health-connect-plan.md](07-backend-health-connect-plan.md) : plan backend de référence basé sur Health Connect comme source unique
- [08-backend-implementation-readme.md](08-backend-implementation-readme.md) : README d'implémentation backend avec checklist et ordre des tickets

## Positionnement retenu

Le projet est cadré comme une application de suivi santé personnel orientée bien-être et pilotage du quotidien.

Le MVP n'est pas un hub universel multi-plateforme. C'est une application Android native, local-first, centrée sur :

- la lecture de données via `Health Connect`
- quelques saisies manuelles
- une normalisation simple des données
- un tableau de bord lisible avec tendances et objectifs

## Principes de cadrage

- Priorité à la faisabilité en solo
- Complexité technique limitée au démarrage
- Pas de backend dans le MVP
- Pas de promesse médicale
- Pas d'intégration directe non maîtrisée si une source pivot existe déjà
