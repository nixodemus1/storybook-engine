package net.storybook.dialogue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Holds the DM-authored persona for one NPC.
 *
 * This is the "character sheet" the DM fills out before or during a session.
 * It drives both live-typed responses and (future) AI-generated dialogue.
 *
 * Usage:
 *   NPCPersona p = new NPCPersona("elder_rowan", "Elder Rowan");
 *   p.setBackground("A wise but secretive village elder who knows about the ruins.");
 *   p.addTopic("the ruins", "He will hint but never reveal the full truth.");
 *   p.addSecret("He was once the cultist leader.");
 *
 * TODO: persist personas to JSON so they survive restarts
 * TODO: bind persona to a specific MCA villager UUID for entity tie-in
 */
public class NPCPersona {

    /** Internal ID, matches the entity or a DM-defined label */
    private final String personaId;

    /** Display name shown in chat when this NPC speaks */
    private final String displayName;

    /** What the NPC knows, who they are, how they behave */
    private String background = "";

    /** Topics the DM has marked as relevant this session */
    private final List<String> knownTopics = new ArrayList<>();

    /**
     * Secrets not shared with players but available to DM for reference.
     * Used to stay consistent under questioning.
     */
    private final List<String> dmSecrets = new ArrayList<>();

    /**
     * If bound, this persona is tied to a specific in-world entity by UUID.
     * Set when the DM selects an NPC from the entity list in the control panel.
     */
    private UUID boundEntityUUID;

    public NPCPersona(String personaId, String displayName) {
        this.personaId = personaId;
        this.displayName = displayName;
    }

    public String getPersonaId() { return personaId; }
    public String getDisplayName() { return displayName; }

    public String getBackground() { return background; }
    public void setBackground(String background) { this.background = background; }

    public List<String> getKnownTopics() { return knownTopics; }
    public void addTopic(String topic, String guidance) {
        knownTopics.add(topic + ": " + guidance);
    }

    public List<String> getDmSecrets() { return dmSecrets; }
    public void addSecret(String secret) { dmSecrets.add(secret); }

    public UUID getBoundEntityUUID() { return boundEntityUUID; }
    public void bindToEntity(UUID entityUUID) { this.boundEntityUUID = entityUUID; }

    /**
     * Build a context prompt string for AI-assisted dialogue.
     *
     * The output format is designed to be prepended to an AI system prompt so
     * the AI knows who it is playing, what it knows, and what to keep secret.
     *
     * TODO: wire this into an LLM API call when AI dialogue is implemented
     */
    public String buildAIContext() {
        StringBuilder sb = new StringBuilder();
        sb.append("You are ").append(displayName).append(", an NPC in a Minecraft storytelling session.\n");
        if (!background.isEmpty()) {
            sb.append("Background: ").append(background).append("\n");
        }
        if (!knownTopics.isEmpty()) {
            sb.append("Topics you can discuss:\n");
            knownTopics.forEach(t -> sb.append("- ").append(t).append("\n"));
        }
        sb.append("Stay in character. Do not reveal secrets. Do not break the fourth wall.\n");
        return sb.toString();
    }
}
