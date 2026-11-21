package litebans.chat;

/**
 * Parses JSON chat components using the BungeeCord chat API that ships with Spigot.
 * The 1.8.x chat API has no versioned serializer, so every component is parsed
 * with the legacy parser.
 */
public class BungeeComponentParser {
   public static net.md_5.bungee.api.chat.BaseComponent[] getBaseComponentArray(CharSequence var0) {
      return net.md_5.bungee.chat.ComponentSerializer.parse(var0.toString());
   }
}
