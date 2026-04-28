package net.storybook;

import net.storybook.dialogue.DialogueManager;
import net.storybook.encounter.EncounterManager;
import net.storybook.narration.NarrationManager;
import net.storybook.session.StorySession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main entrypoint and root coordinator for the Storybook Engine mod.
 *
 * This is the first class loaded by the mod initializer (Forge or Fabric).
 * It wires together the session, encounter, narration, and dialogue subsystems.
 *
 * Loader-specific entrypoints (StorybookForge, StorybookFabric) should
 * call {@link #init()} during their own mod initialization.
 *
 * Package hierarchy:
 *
 *   net.storybook
 *   ├── StorybookEngine          ← you are here (root coordinator)
 *   ├── session/
 *   │   ├── StorySession         ← lifecycle: start, pause, resume, end
 *   │   └── SessionState         ← enum: IDLE, ACTIVE, PAUSED, CINEMATIC
 *   ├── encounter/
 *   │   ├── EncounterManager     ← spawn presets and individual entities
 *   │   ├── EncounterPreset      ← reusable named spawn bundle
 *   │   └── EncounterType        ← enum: COMBAT, BOSS, AMBIENT, CHASE, DISCOVERY
 *   ├── narration/
 *   │   ├── NarrationManager     ← broadcast, whisper, speakAsNPC
 *   │   └── NarrationStyle       ← enum: STORY, DRAMATIC, AMBIENT, SYSTEM
 *   └── dialogue/
 *       ├── DialogueManager      ← registry of NPC personas
 *       └── NPCPersona           ← character sheet: name, background, topics, secrets
 *
 * Future packages to add:
 *   net.storybook.camera         ← cinematic camera control
 *   net.storybook.api            ← HTTP/WebSocket bridge to control panel app
 *   net.storybook.command        ← in-game DM slash commands
 *   net.storybook.preset         ← load/save session presets from JSON
 */
public class StorybookEngine {

    public static final String MOD_ID = "storybook";

    private static final Logger LOGGER = LogManager.getLogger(StorybookEngine.class);

    private static StorybookEngine instance;

    private StorybookEngine() {}

    public static StorybookEngine getInstance() {
        if (instance == null) {
            instance = new StorybookEngine();
        }
        return instance;
    }

    /**
     * Called once at mod startup from the loader-specific entrypoint.
     * Bootstraps all subsystems.
     */
    public void init() {
        // Singletons initialise lazily, but calling get here confirms they load at boot
        StorySession.getInstance();
        EncounterManager.getInstance();
        NarrationManager.getInstance();
        DialogueManager.getInstance();

        LOGGER.info("[StorybookEngine] Initialised. Ready for session.");
        LOGGER.info("[StorybookEngine] Storybook Engine is ALIVE");

        // TODO: register server event listeners for session tick (freeze enforcement etc.)
        // TODO: load default encounter presets from data/storybook/encounters/*.json
        // TODO: start HTTP API server for control panel bridge (Phase 2)
    }
}
