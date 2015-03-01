- [x] Basic draw and control
- [x] Movement sets next position in special component
- [x] Movement system sets position to next position
- [x] Collision system is run before movement system
- [x] Collision system sets next position back to original
- [x] Collision system generates collision callback
- [x] Add messages log:
  - [x] Example log message entity
  - [x] Add sidebar div
  - [x] Add log message div inside of sidebar div
  - [x] Log system renders last 15 messages
  - [-] Functionality to remove component
  - [-] Functionality to remove entity
  - [-] LogRotate system preserves only 15 messages
  - [x] Blocked system generates log message entity

- [x] Battling:
  - [x] Example not moving enemy
  - [x] Battle system reacts to collision events
    - [x] Logs damage
    - [x] Inflicts damage to target's health
  - [x] Target dies when health reaches zero - Death system
  - [x] Target drops certain entities when dead
  - [x] Dropped entities are passable
  - [x] Prefer drawing passable objects before other
  - [x] Death is logged

- [x] Melee AI:
  - [x] Enemy has sight radius and damage
  - [x] Player has health
  - [x] Enemy chases player if it is in radius
  - [x] Enemy bumps to the player to damage him
  - [x] Battle system actually can handle inflicting enemy -> player
  - [x] Player can die