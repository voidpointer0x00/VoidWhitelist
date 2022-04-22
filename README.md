
<img src="https://user-images.githubusercontent.com/43143315/164706072-0366fc30-c686-4408-9e66-7930eb047273.png" align=right />

# VoidWhitelist [![Tests][TestsBadge]][TestsUrl] [![CodeQL][CodeQLBadge]][CodeQLUrl] [![ReleaseBadge]][ReleaseUrl] [![WTFPL][LicenseBadge]](LICENSE)

VoidWhitelist is an enchanced whitelist plugin for v1.14-1.18 Minecraft servers.
It allows to add both online and offline UUIDs on a white list on a permanent and temporary basis.

## How to use?

### GUI
GUI is accessible through a `/whitelist gui` command. Requires `whitelist.gui` permission. GUI actions _(such as remove or edit)_
don't require any permission.

### Commands
By default the plugin will use configured UUID. _-offline_ and _-online_ parameters explicitly indicate which UUID a command should use.
`whitelist.*` gives you a permission to run all pugin commands.

- `/whitelist add [player] (duration) (-offline|-online)` — adds a _player_ for a _duration_. The duration is specified in a standard
Essentials form `1mon7d30m`. Requires `whitelist.add` permission.
- `/whitelist rem [player] (-offline|-online)` — removes a _player_. Requires `whitelist.remove` permission.
- `/whitelist info [player] (-offline|-online)` — tells whether a _player_ is whitelisted and if so displays the duration.
- Requires `whitelist.info` permission.
- `/whitelist status` — tells whether the whitelist is enabled or not. Requires `whitelist.status` permission.
- `/whitelist on|off` — enables and disabled the whitelist. Requires `whitelist.enable` and `whitelist.disable` permission.
- `/whitelist export-db` — exports the connected database into a new `export-${timestamp}.json` json file. Requires `whitelist.export` permission.
- `/whitelist import-json` — imports whitelist from `whitelist.json` file into the connected database. Requires `whitelist.import` permission.

[TestsBadge]: https://github.com/NyanGuyMF/VoidWhitelist/actions/workflows/tests.yml/badge.svg
[CodeQLBadge]: https://github.com/NyanGuyMF/VoidWhitelist/actions/workflows/codeql-analysis.yml/badge.svg
[ReleaseBadge]: https://img.shields.io/github/v/release/NyanGuyMF/VoidWhitelist.svg?color=orange
[LicenseBadge]: https://img.shields.io/github/license/NyanGuyMF/VoidWhitelist.svg?color=blue

[TestsUrl]: https://github.com/NyanGuyMF/VoidWhitelist/actions/workflows/tests.yml
[CodeQLUrl]: https://github.com/NyanGuyMF/VoidWhitelist/actions/workflows/codeql.yml
[ReleaseUrl]: https://github.com/NyanGuyMF/VoidWhitelist/releases
