# Changelog de ce fork

Base : [RivrsMc/CoreProtect](https://github.com/RivrsMc/CoreProtect) v23.2 (lui-même fork de [PlayPro/CoreProtect](https://github.com/PlayPro/CoreProtect)).

## Traçage des renommages d'objets

Ajout d'une fonctionnalité absente de CoreProtect : savoir qui a renommé quel objet, avec
quel ancien nom, quel nouveau nom, où et quand.

### Détection

- **Enclume vanilla** : tout renommage via enclume est détecté, y compris un renommage
  simple (un seul objet dans la case 0 — le cas le plus fréquent, qui était bloqué par un
  bug pré-existant du fork de base exigeant un objet dans les deux cases).
- **Commandes tierces** : toute commande contenant "rename" (insensible à la casse,
  n'importe où dans le message, pour couvrir les sous-commandes type `/cmi rename`)
  déclenche un sondage de l'objet tenu en main (principale et secondaire) toutes les
  secondes pendant jusqu'à 60 secondes, pour détecter un changement de nom même si le
  plugin tiers ne l'applique pas instantanément.
- Compatible confirmé : EssentialsX (`/itemrename`, `/irename`), EpicRename (`/rename`),
  CMI (`/anvil` via l'enclume vanilla, `/rename` via la détection par commande).

### Nouveau

- Table `item_rename` (MySQL + SQLite), avec index `(wid, x, z, time)` et `(user, time)`.
- Commande `/co itemrenames [joueur] [limite]` (alias `/co renames`, `/co ir`), permission
  `coreprotect.lookup.itemrenames` (hérite de `coreprotect.lookup`).
- Option de config `item-rename-logging` (défaut `true`).
- `Action.ITEM_RENAME` ajouté à `CoreProtectPreLogEvent` pour la parité API.

### Fichiers principaux

`database/Database.java`, `database/statement/ItemRenameStatement.java`,
`database/logger/ItemRenameLogger.java`, `consumer/process/ItemRenameProcess.java`,
`consumer/Queue.java` (`queueItemRename`), `utility/ItemRenameUtil.java`,
`listener/player/InventoryChangeListener.java` (`checkAnvilOperation`),
`listener/player/PlayerCommandListener.java` (`checkRenameCommand` + sondage),
`command/ItemRenameCommand.java`, `event/CoreProtectPreLogEvent.java`, `config/Config.java`.

### Limites connues

- Ne couvre que les renommages qui finissent par modifier l'objet en main du joueur.
- Un plugin qui renomme via sa propre interface graphique (pas l'enclume vanilla, pas un
  argument de commande direct) n'est pas couvert.
- Fenêtre de sondage limitée à 60 secondes après la commande.

## Build

Nécessite un JDK 25 pour compiler (paper-api est distribué avec des class files
nécessitant un compilateur JDK 25 minimum), même si le bytecode produit cible Java 21 :

```bash
export JAVA_HOME=/chemin/vers/jdk-25
mvn -DskipTests package
```
