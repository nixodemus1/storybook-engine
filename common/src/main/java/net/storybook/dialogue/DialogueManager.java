package net.storybook.dialogue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Registry of active NPC personas for the current session.
 *
 * The DM creates and edits personas here. The NarrationManager consumes them
 * when broadcasting NPC speech.
 *
 * TODO: load/save personas from JSON session preset files
 * TODO: expose persona list to the control panel API
 */
public class DialogueManager {

    private static DialogueManager instance;

    /** Keyed by personaId */
    private final Map<String, NPCPersona> personas = new HashMap<>();

    /** Reverse index: entity UUID -> personaId, for quick lookup by entity */
    private final Map<UUID, String> entityIndex = new HashMap<>();

    private DialogueManager() {}

    public static DialogueManager getInstance() {
        if (instance == null) {
            instance = new DialogueManager();
        }
        return instance;
    }

    // -------------------------------------------------------------------------
    // Persona management
    // -------------------------------------------------------------------------

    public void registerPersona(NPCPersona persona) {
        personas.put(persona.getPersonaId(), persona);
        if (persona.getBoundEntityUUID() != null) {
            entityIndex.put(persona.getBoundEntityUUID(), persona.getPersonaId());
        }
    }

    public NPCPersona getPersona(String personaId) {
        return personas.get(personaId);
    }

    /**
     * Look up the persona attached to a specific in-world entity.
     * Returns null if no persona is bound to that entity.
     */
    public NPCPersona getPersonaForEntity(UUID entityUUID) {
        String id = entityIndex.get(entityUUID);
        return id != null ? personas.get(id) : null;
    }

    public Collection<NPCPersona> getAllPersonas() {
        return personas.values();
    }

    public void clearSession() {
        personas.clear();
        entityIndex.clear();
    }
}
