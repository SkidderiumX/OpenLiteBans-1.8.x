package litebans.platform.bukkit;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import litebans.lib.kotlin.jvm.internal.Intrinsics;
import litebans.lib.kotlin.jvm.internal.MetadataAnnotation;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@MetadataAnnotation(
   a = 0
)
public final class ProtocolLibChat {
   public static final ProtocolLibChat INSTANCE = new ProtocolLibChat();

   private ProtocolLibChat() {
   }

   public final void handleObject(@Nullable Object var1, @NotNull String var2) {
      PacketContainer var3 = new PacketContainer(Server.KICK_DISCONNECT);
      var3.getChatComponents().write(0, WrappedChatComponent.fromJson(var2));
      ProtocolManager var10000 = ProtocolLibrary.getProtocolManager();
      Intrinsics.handleObject(var1, "");

      try {
         var10000.sendServerPacket((Player)var1, var3);
      } catch (java.lang.reflect.InvocationTargetException var5) {
         throw new RuntimeException(var5);
      }
   }

   public final boolean check() {
      return ProtocolLibrary.getProtocolManager() == null;
   }
}
