> **À propos de ce fork**
> Ce dépôt est un fork personnel de [RivrsMc/CoreProtect](https://github.com/RivrsMc/CoreProtect) (lui-même basé sur [PlayPro/CoreProtect](https://github.com/PlayPro/CoreProtect)), avec une fonctionnalité ajoutée : le **traçage des renommages d'objets**.
>
> Il enregistre qui a renommé un objet, l'ancien nom, le nouveau nom, où et quand — que ce soit via une enclume vanilla ou via une commande tierce contenant "rename" (compatible EssentialsX, EpicRename, CMI). Consultable via `/co itemrenames [joueur] [limite]`.
>
> Voir [CHANGELOG-FORK.md](CHANGELOG-FORK.md) pour le détail des changements par rapport à l'upstream.

![CoreProtect](https://userfolio.com/uploads/coreprotect-banner-v19.png)

[![Artistic License 2.0](https://img.shields.io/github/license/PlayPro/CoreProtect?&logo=github)](LICENSE)
[![GitHub Workflows](https://github.com/YoannFm/CoreProtect-item-renames/actions/workflows/build.yml/badge.svg)](https://github.com/YoannFm/CoreProtect-item-renames/actions)
[![Netlify Status](https://img.shields.io/netlify/c1d26a0f-65c5-4e4b-95d7-e08af671ab67)](https://app.netlify.com/sites/coreprotect/deploys)
[![CodeFactor](https://www.codefactor.io/repository/github/playpro/coreprotect/badge)](https://www.codefactor.io/repository/github/playpro/coreprotect)
[![Join us on Discord](https://img.shields.io/discord/348680641560313868.svg?label=&logo=discord&logoColor=ffffff&color=7389D8&labelColor=6A7EC2)](https://discord.gg/b4DZ4jy)

CoreProtect
===========

CoreProtect is a blazing fast data logging and anti-griefing tool for Minecraft servers.

For a detailed description of the plugin, please visit [coreprotect.net](https://coreprotect.net).

| Quick Links |  |
| --- | --- |
| CoreProtect Discord: | [discord.gg/b4DZ4jy](https://discord.gg/b4DZ4jy) |
| CoreProtect Patreon: | [patreon.com/coreprotect](https://www.patreon.com/coreprotect) |
| CoreProtect Documentation: | [docs.coreprotect.net](https://docs.coreprotect.net) |
| Downloads for MC 1.14 - 26.1: | [coreprotect.net/latest](https://coreprotect.net/latest/) |
| Downloads for MC 1.8 - 1.12: | [coreprotect.net/legacy](https://coreprotect.net/legacy/) |

bStats
------
[![bStats Graph Data](https://bstats.org/signatures/bukkit/CoreProtect.svg)](https://bstats.org/plugin/bukkit/CoreProtect)

API
------
### [API Documentation](https://docs.coreprotect.net/api/)

### Dependency Information
Maven
```xml
<repository>
    <id>playpro-repo</id>
    <url>https://maven.playpro.com</url>
</repository>
```
```xml
<dependency>
    <groupId>net.coreprotect</groupId>
    <artifactId>coreprotect</artifactId>
    <version>23.2</version>
    <scope>provided</scope>
</dependency>
```

Contributing
------
CoreProtect is an open source project, and gladly accepts community contributions.

If you'd like to contribute, please read our contributing guidelines here: [CONTRIBUTING.md](CONTRIBUTING.md)

[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-2.0-4baaaa.svg)](CONTRIBUTING.md#code-of-conduct) 