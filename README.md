# Dual Chat Mod

Mod de chat à deux canaux (RP / HRP) pour serveurs Minecraft roleplay.

![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1-62B47A)
![Forge](https://img.shields.io/badge/Forge-47.4.x-1E2D4F)
![Version](https://img.shields.io/badge/version-1.0.5-blue)

## Mod ID
`dualchat`

## Compatibilité
- Minecraft 1.20.1
- Forge 47.x
- PlasmoVoice, Pehkui (optionnels)

## Fonctionnalités

- Deux canaux de chat : **RP** (proximité 16 blocs, ou distance PlasmoVoice) et **HRP** (global)
- Bascule entre canaux via des boutons au-dessus du chat
- Historique séparé par canal, badge de message non-lu
- Police personnalisable pour le canal RP
- Emotes `/me` affichées en 3D au-dessus de la tête
- **Le canal HRP se comporte comme le chat vanilla** : messages système, autres mods et lettres Crow y arrivent et restent cliquables
- **Routage automatique** : les messages RP (italique — Corbeau, Hygiène…) vont dans le canal RP, le reste dans HRP

## Changelog

### 1.0.5
- **Correction régression** : les messages vanilla / système / autres mods n'étaient plus visibles. Ils s'affichent à nouveau dans le canal HRP, **avec leur texte cliquable préservé** (ex. accepter une lettre Corbeau).
- **Routage RP/HRP** : les messages de style RP (italiques, envoyés par Corbeau, Hygiène, etc.) sont automatiquement dirigés vers le canal RP ; les messages classiques restent en HRP.

## Licence
CC BY-NC-ND 4.0 — Akirabane.
