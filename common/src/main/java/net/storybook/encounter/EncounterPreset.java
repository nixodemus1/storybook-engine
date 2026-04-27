package net.storybook.encounter;

import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes a reusable encounter preset.
 *
 * A preset is a named, categorized bundle of entity spawns the DM can
 * trigger from the control panel or a command at any time during a session.
 *
 * Example preset: "Goblin Ambush" spawns 3 zombies and 1 skeleton at
 * the target location with optional equipment overrides.
 *
 * TODO: serialize presets to JSON so DMs can author them outside the game
 * TODO: support spawn offsets relative to a target position
 * TODO: support variant loot/equipment randomization
 */
public class EncounterPreset {

    private final String id;
    private final String displayName;
    private final EncounterType type;
    private final List<SpawnEntry> spawns = new ArrayList<>();

    public EncounterPreset(String id, String displayName, EncounterType type) {
        this.id = id;
        this.displayName = displayName;
        this.type = type;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public EncounterType getType() { return type; }
    public List<SpawnEntry> getSpawns() { return spawns; }

    public EncounterPreset addSpawn(Identifier entityId, int count) {
        spawns.add(new SpawnEntry(entityId, count));
        return this;
    }

    // -------------------------------------------------------------------------
    // Inner: one spawn entry inside a preset
    // -------------------------------------------------------------------------

    public static class SpawnEntry {
        public final Identifier entityId;
        public final int count;

        public SpawnEntry(Identifier entityId, int count) {
            this.entityId = entityId;
            this.count = count;
        }
    }
}
