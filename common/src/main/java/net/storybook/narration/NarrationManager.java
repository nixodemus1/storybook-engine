package net.storybook.narration;

import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.UUID;

/**
 * Handles all DM-originated text output to players.
 *
 * The DM never appears as a player in chat. Narration goes out either as
 * a distinct "narrator" voice or as a named NPC voice.
 *
 * Usage:
 *   NarrationManager.getInstance().broadcast(server, "The torches flicker.", NarrationStyle.STORY);
 *   NarrationManager.getInstance().whisper(player, "You notice something the others missed.", NarrationStyle.AMBIENT);
 *
 * TODO: add title/subtitle display option for dramatic scene openings
 * TODO: add sound cue trigger alongside narration lines
 * TODO: route messages to a dedicated in-game "story log" book or screen
 */
public class NarrationManager {

    private static NarrationManager instance;

    private NarrationManager() {}

    public static NarrationManager getInstance() {
        if (instance == null) {
            instance = new NarrationManager();
        }
        return instance;
    }

    // -------------------------------------------------------------------------
    // Broadcast: all players
    // -------------------------------------------------------------------------

    /**
     * Send a narration message to every player in the session.
     *
     * @param server the running server
     * @param text   the message content
     * @param style  visual style to apply
     */
    public void broadcast(MinecraftServer server, String text, NarrationStyle style) {
        Text message = format(null, text, style);
        server.getPlayerManager().broadcastChatMessage(message, MessageType.SYSTEM, UUID.randomUUID());
    }

    // -------------------------------------------------------------------------
    // Whisper: single player
    // -------------------------------------------------------------------------

    /**
     * Send a private narration message to one player only.
     *
     * @param player the recipient
     * @param text   the message content
     * @param style  visual style to apply
     */
    public void whisper(ServerPlayerEntity player, String text, NarrationStyle style) {
        Text message = format(null, text, style);
        player.sendMessage(message, false);
    }

    // -------------------------------------------------------------------------
    // NPC voice: message appears attributed to a named NPC
    // -------------------------------------------------------------------------

    /**
     * Broadcast chat as if spoken by a named NPC.
     * Used for live DM voice-acting over text.
     *
     * @param server  the running server
     * @param npcName the in-world NPC name to appear as sender
     * @param text    what the NPC says
     */
    public void speakAsNPC(MinecraftServer server, String npcName, String text) {
        MutableText message = new LiteralText("")
                .append(new LiteralText("[" + npcName + "] ").formatted(Formatting.GOLD))
                .append(new LiteralText(text).formatted(Formatting.WHITE));
        server.getPlayerManager().broadcastChatMessage(message, MessageType.CHAT, UUID.randomUUID());
    }

    // -------------------------------------------------------------------------
    // Internal formatting
    // -------------------------------------------------------------------------

    private Text format(String prefix, String text, NarrationStyle style) {
        Formatting colour;
        switch (style) {
            case DRAMATIC: colour = Formatting.RED; break;
            case AMBIENT:  colour = Formatting.GRAY; break;
            case SYSTEM:   colour = Formatting.YELLOW; break;
            default:       colour = Formatting.LIGHT_PURPLE; break; // STORY
        }

        MutableText out = new LiteralText("");
        if (prefix != null) {
            out.append(new LiteralText("[" + prefix + "] ").formatted(Formatting.DARK_GRAY));
        }
        out.append(new LiteralText(text).formatted(colour));
        return out;
    }
}
