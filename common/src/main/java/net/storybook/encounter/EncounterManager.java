package net.storybook.encounter;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Manages encounter presets and handles DM-triggered encounter spawning.
 *
 * Use {@link #getInstance()} to access the singleton.
 *
 * Lifecycle:
 *  1. DM authors or imports presets.
 *  2. During a session, DM triggers a preset via control panel or command.
 *  3. EncounterManager spawns entities and tracks them for cleanup.
 *
 * TODO: persist presets as JSON resource files in the mod's data folder
 * TODO: add a "cleanup last encounter" action that despawns tracked entities
 * TODO: support entity target tagging (e.g., guard, villain, minion roles)
 */
public class EncounterManager {

    private static EncounterManager instance;

    private final Map<String, EncounterPreset> presets = new HashMap<>();

    /**
     * Tracks entities spawned by the most recent triggered encounter
     * so they can be cleaned up on demand.
     */
    private final List<UUID> lastSpawnedEntities = new ArrayList<>();

    private EncounterManager() {}

    public static EncounterManager getInstance() {
        if (instance == null) {
            instance = new EncounterManager();
        }
        return instance;
    }

    // -------------------------------------------------------------------------
    // Preset management
    // -------------------------------------------------------------------------

    public void registerPreset(EncounterPreset preset) {
        presets.put(preset.getId(), preset);
    }

    public EncounterPreset getPreset(String id) {
        return presets.get(id);
    }

    public Map<String, EncounterPreset> getAllPresets() {
        return presets;
    }

    // -------------------------------------------------------------------------
    // Spawning
    // -------------------------------------------------------------------------

    /**
     * Trigger an encounter preset at a given position in the given world.
     *
     * @param presetId the ID of the preset to trigger
     * @param world    the server world to spawn into
     * @param pos      the origin position for spawns
     * @return true if the preset was found and spawning was attempted
     */
    public boolean triggerPreset(String presetId, ServerWorld world, Vec3d pos) {
        EncounterPreset preset = presets.get(presetId);
        if (preset == null) return false;

        lastSpawnedEntities.clear();

        for (EncounterPreset.SpawnEntry entry : preset.getSpawns()) {
            for (int i = 0; i < entry.count; i++) {
                spawnEntity(entry.entityId, world, pos);
            }
        }

        return true;
    }

    /**
     * Spawn a single entity by registry ID at the given position.
     * Tracks it for later cleanup.
     */
    public void spawnEntity(Identifier entityId, ServerWorld world, Vec3d pos) {
        EntityType<?> type = Registry.ENTITY_TYPE.get(entityId);
        if (type == null) return;

        Entity entity = type.create(world);
        if (entity == null) return;

        entity.refreshPositionAndAngles(pos.x, pos.y, pos.z, 0f, 0f);
        world.spawnEntity(entity);
        lastSpawnedEntities.add(entity.getUuid());
    }

    /**
     * Despawn all entities from the most recently triggered encounter.
     */
    public void cleanupLastEncounter(ServerWorld world) {
        for (UUID uuid : lastSpawnedEntities) {
            Entity entity = world.getEntity(uuid);
            if (entity != null) {
                entity.remove();
            }
        }
        lastSpawnedEntities.clear();
    }
}
