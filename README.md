![](https://grosfichiers.patte-de-coin.fr/jamailun/quick_party_banner.png)

# Quick Party

Quick parties system for players.

By [jamailun](https://github.com/jamailun).

## Concept

All party plugins are too complicated. If you simply want a basic, **transient** party system : here you go.

No need to name the party or anything : just invite another player and *poof*, you have a party up and running.

## Commands

- `/party invite <player>` : Invite a player in your party. If no party exist, will create one for you.
- `/party accept|refuse [player]` : Accept or refuse a party invitation.
  - You can only have one valid party request at the same time.
- `/party info` : Information about your current party.
- `/party leave` : Leave your current party.
- `/party tp <player>` : Teleport to another player in the party.

**As the party leader :**
- `/party disband` : Disband the party, discard all invitations.
- `/party promote <player>` : make a player the new party leader. Of course, said player must be in the party.
- `/party kick <player>` : kick a player from the party.
- `/party tpall` : Send a "group-teleport request" to all of your members.

## Configuration

For now, only two parameters are configurable :
- `friendlyFire` : if true members of a party can deal damage to each other.
- `maxSize` : maximum amount of players in a party. Less than 2 would be a bad choice.

### Teleport configuration

The `teleport` section of the configuration is as following :
- The global `requestExpirationSecs` value, to set the expiration delay of a teleport request.
- The configuration for all teleportation modes.
  - A default value for all modes,
  - An optional overwrite for every mode.

### Teleportation modes

Four teleportation modes exists :
- `LEADER_TO_MEMBER` : when the party leaders teleport to a member.
- `MEMBER_TO_LEADER` : reversed.
- `MEMBER_TO_MEMBER` : a non-party leaders teleports to another one.
- `ALL_TO_LEADER` : specific case of the `/p tpall`.

### Mode configuration

This specific configuration is configured with:
- `enabled` (boolean) : to set if the teleport mode is allowed or not. Null, empty and `none` will disable it.
- `permission` (string) : optional permission to have for a player to use the mode.
- `costItem` (section) : the cost to use this teleportation
  - `type` (string) : Material of the item.
  - `count` (int) : Item count.
- `teleportWaitSecs` (double) : the teleport delay before actually teleport. If zero of negative, no delay will be applied.
- `needConfirmation` (boolean) : if `true`, a confirmation request will be sent to the target player.

## Placeholders

List of placeholders.

|          Placeholder           | Value                                                                                                                   |
|:------------------------------:|-------------------------------------------------------------------------------------------------------------------------|
|        `qpa_has_party`         | Return `true` if the current player has a party, `false` otherwise.                                                     |
|                                |                                                                                                                         ||
|       `qpa_party_leader`       | Return the name of the current party.                                                                                   |
|     `qpa_party_is_leader`      | Return `true` if the current player is the party leader. `false` otherwise.                                             |
|   `qpa_party_creation_date`    | Return the date-time of creation of th party, in the ISO format..                                                       |
|       `qpa_party_leader`       | Return the **total** size of the party, i.e. members and pending invitations.                                           |
|       `qpa_party_leader`       | Return the amount of **members** in the party.                                                                          |
|       `qpa_party_leader`       | Return the amount of **invitations** of the party.                                                                      |
|   `qpa_party_member_<index>`   | Return the name of the member in the specific <index> position.                                                         |
| `qpa_party_invitation_<index>` | Return the name of the invited player in the specific <index> invitation.                                               |
|   `qpa_party_color_<index>`    | Return the "color" of the member in the specified index. For now, it's `&6` for the leader, and nothing for the others. |
| `qpa_party_is_leader_<index>`  | Return the `true` if the member in the specified index is the leader, or `false` otherwise.                             |

> [!NOTE]
> For all `qpa_party_*` placeholders, if the player is not in a party, an empty string will be returned.

> [!TIP]
> To integrate with [TAB](https://github.com/NEZNAMY/TAB) (which is really common), you can use [this](https://github.com/jamailun/QuickParty/blob/main/documentation/integration_TAB.md) example for the scoreboard.
