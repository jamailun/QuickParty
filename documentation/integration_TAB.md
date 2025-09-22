# TAB integration

In this example, we'll use TAB conditions and TAB scoreboard.

## 1) Prepare the QP config

In the QuickParty plugin config file, here is what I use.

Each prefix and suffix will be used in the TAB plugin.

```yaml
# ...
placeholders:
  prefix:
    leader:
      online: '&6&l☉ &6'
      offline: '&e☉ &7'
      self: '&2&l☉ &6'
    member:
      online: '&a▪ &f'
      offline: '&a▫ &7'
      self: '&2▪ &b'
  suffix:
    leader:
      online: ' &c%player_health_rounded% ❤'
      offline: ''
      self: ' &c%player_health_rounded% ❤'
    member:
      online: ' &c%player_health_rounded% ❤'
      offline: ''
      self: ' &c%player_health_rounded% ❤'
# ...
```

## 2) Scoreboard

This is the `scoreboards` section of `TAB/config.yml`.

The principle is really simple. When the condition returns an empty string, the sidebar line will not be displayed.
As such, we dynamically display party members / pending invitations.

```yaml
scoreboard:
  # ... Existing configuration, don't remove it ;-)
  #
  scoreboards:
    # ... Other scoreboards you may have ...
    QuickParty_SB:
      title: "&7====&9 [ Party ] &7===="
      display-condition: '%qpa_has_party%=true'
      lines:
      - ''
      - '&eMembers :'
      - '%qpa_party_nice_member_1%'
      - '%qpa_party_nice_member_2%'
      - '%qpa_party_nice_member_3%'
      - '%qpa_party_nice_member_4%'
      - ''
      - '%condition:party_invits%'
      - '%condition:party_invits_1%'
      - '%condition:party_invits_2%'
      - '%condition:party_invits_3%'
      - '%condition:party_invits_footer%'
```

## 2) Conditions

This is the `conditions` section of `TAB/config.yml`.

Obviously, you can keep your other `condition` entries.

```yaml
conditions:
  # ... Existing conditions...
  
  # Get the party invitation nicely.
  party_invits:
    conditions:
    - '%qpa_party_size_invitations%>0'
    yes: "&f&lInvitations :"
    no: ""
  party_invits_footer:
    conditions:
    - '%qpa_party_size_invitations%>0'
    yes: "&7"
    no: ""
  party_invits_1:
    conditions:
    - '%qpa_party_size_invitations%>0'
    yes: "&7▪ %qpa_party_invitation_1%"
    no: ""
  party_invits_2:
    conditions:
    - '%qpa_party_size_invitations%>1'
    yes: "&7▪ %qpa_party_invitation_2%"
    no: ""
  party_invits_3:
    conditions:
    - '%qpa_party_size_invitations%>2'
    yes: "&7▪ %qpa_party_invitation_3%"
    no: ""
```

