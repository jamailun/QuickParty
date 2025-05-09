# TAB integration

In this example, we'll use TAB conditions and TAB scoreboard.

## 1) Scoreboard

This is the `scoreboards` section of `TAB/config.yml`.

The principle is really simple. When the condition returns an empty string, the sidebar line will not be displayed.
As such, we dynamically display party members / pending invitations.

```yaml
scoreboard:
  # ... Existing configuration, don't remove it ;-)
  #
  scoreboards:
    # Other scoreboards you may have.
    #
    QuickParty_SB:
      title: "&7====&9 [ Party ] &7===="
      display-condition: '%qpa_has_party%=true'
      lines:
      - ''
      - '&eMembers :'
      # We always have one member
      - "&b%qpa_party_color_1%- %qpa_party_member_1%"
      # Additional members (up to 4 total)
      - '%condition:party_members_2%'
      - '%condition:party_members_3%'
      - '%condition:party_members_4%'
      - ''
      # Invitations. We only display the section when needed.
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
  #
  # Existing conditions...
  #
  
  # Get the 2nd, 3rd and 4th party member.
  party_members_2:
    conditions:
    - '%qpa_party_size_members%>1'
    yes: "&b%qpa_party_color_2%- %qpa_party_member_2%"
    no: ""
  party_members_3:
    conditions:
    - '%qpa_party_size_members%>2'
    yes: "&b%qpa_party_color_3%- %qpa_party_member_3%"
    no: ""
  party_members_4:
    conditions:
    - '%qpa_party_size_members%>3'
    yes: "&b%qpa_party_color_4%- %qpa_party_member_4%"
    no: ""
  # get the invitations
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
    yes: "&7- %qpa_party_invitation_1%"
    no: ""
  party_invits_2:
    conditions:
    - '%qpa_party_size_invitations%>1'
    yes: "&7- %qpa_party_invitation_2%"
    no: ""
  party_invits_3:
    conditions:
    - '%qpa_party_size_invitations%>2'
    yes: "&7- %qpa_party_invitation_3%"
    no: ""
```

