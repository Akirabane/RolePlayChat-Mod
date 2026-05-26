# RolePlayChat Mod

Mod Forge pour Minecraft 1.20.1 ajoutant deux canaux de chat isolés, conçus pour les serveurs roleplay.

---

## Fonctionnalités

### Deux canaux distincts

| Canal | Portée | Couleur |
|-------|--------|---------|
| **RP** | Proximité — 16 blocs autour de l'émetteur | Ambre `#FFAA00` |
| **HRP** | Global — tous les joueurs connectés | Bleu `#55AAFF` |

- Le canal **RP** simule la voix en jeu : seuls les joueurs physiquement proches reçoivent le message.
- Le canal **HRP** (Hors RolePlay) est global et sert aux échanges OOC entre joueurs.

### Historiques séparés

Chaque canal possède son propre historique scrollable, indépendant du chat vanilla.  
Si vous êtes sur HRP pendant qu'un joueur parle en RP à côté de vous, les messages RP sont tout de même enregistrés dans l'historique RP. En switchant sur RP, vous retrouvez ces messages.

### Interface

- Deux boutons **RP** / **HRP** s'affichent au-dessus du champ de saisie lorsque le chat est ouvert.
- Le bouton du canal actif est mis en surbrillance dans sa couleur. Un simple clic gauche change de canal.
- Format des messages : `[CANAL] <Pseudo> message`

### Comportement réseau

- Le chat vanilla est entièrement intercepté et remplacé (côté client et côté serveur).
- Les commandes (`/...`) ne sont pas affectées et fonctionnent normalement.
- Le canal de chaque joueur est mémorisé côté serveur. À la connexion, le joueur est automatiquement placé en RP.

---

## Installation

1. Installer [Forge 1.20.1](https://files.minecraftforge.net/) (version 47.4.x recommandée).
2. Télécharger le JAR depuis les [Releases](../../releases).
3. Placer le fichier `.jar` dans le dossier `mods/` de votre instance.
4. Le mod doit être installé **côté serveur ET côté client**.

---

## Compatibilité

| Dépendance | Version |
|-----------|---------|
| Minecraft | 1.20.1 |
| Forge | 47.4.x |
| Java | 17+ |

---

## Structure du projet

```
src/main/java/com/dualchat/
├── Channel.java                     # Enum RP / HRP (couleur, nom)
├── DualChatMod.java                 # Point d'entrée, constante RP_RANGE
├── client/
│   ├── ChannelToggleButtons.java    # Rendu et clic des boutons dans ChatScreen
│   ├── ClientChannelHistory.java    # Historiques par canal (100 messages max)
│   ├── ClientChannelState.java      # Canal actif local + swap d'historique
│   ├── ClientChatReceiver.java      # Affichage des messages reçus
│   └── ClientEventHandler.java      # Intercept ClientChatEvent + login
├── network/
│   ├── NetworkHandler.java          # Enregistrement des packets
│   ├── C2SChatMessagePacket.java    # Client → Serveur : envoi d'un message
│   ├── C2SSetChannelPacket.java     # Client → Serveur : changement de canal
│   └── S2CChatMessagePacket.java    # Serveur → Client : diffusion d'un message
└── server/
    ├── ChatRouter.java              # Logique de routage RP / HRP
    ├── PlayerChannelState.java      # Canal actif par joueur (UUID)
    └── ServerEventHandler.java      # Intercept ServerChatEvent + login/logout
```

---

## Build

```bash
./gradlew build
```

Le JAR compilé se trouve dans `build/libs/`.

---

## Licence

MIT
