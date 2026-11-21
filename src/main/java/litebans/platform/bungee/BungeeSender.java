package litebans.platform.bungee;

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.UUID;
import litebans.BungeePlugin;
import litebans.chat.BungeeComponentParser;
import litebans.chat.BungeeLegacyComponentParser;
import litebans.lib.kotlin.jvm.internal.MetadataAnnotation;
import litebans.platform.Sender;
import lombok.NonNull;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import org.jetbrains.annotations.Nullable;

@MetadataAnnotation(
   a = 1
)
public class BungeeSender implements Sender {
   private final BungeePlugin bungeePlugin;
   private final WeakReference weakReference;
   private UUID uuid2 = ZERO_UUID;
   private String string = null;
   private String string2;
   private final String string3;
   private final boolean flag;

   public BungeeSender(BungeePlugin var1, CommandSender var2) {
      this.bungeePlugin = var1;
      this.weakReference = new WeakReference<>(var2);
      this.flag = var2 instanceof ProxiedPlayer;
      this.string3 = var2.getName();
   }

   @Override
   public void dispatchCommand(String var1) {
      CommandSender var2 = (CommandSender)this.weakReference.get();
      if (var2 != null && !var1.isEmpty()) {
         this.bungeePlugin.getProxy().getPluginManager().dispatchCommand(var2, var1);
      }
   }

   @Override
   public String getDisplayName() {
      CommandSender var1 = (CommandSender)this.weakReference.get();
      if (var1 == null) {
         return this.string3;
      } else {
         return this.isPlayer() ? ((ProxiedPlayer)var1).getDisplayName() : this.getName();
      }
   }

   @Override
   public Object getHandle() {
      return this.weakReference.get();
   }

   @Nullable
   @Override
   public String getIp() {
      String var1 = this.string2;
      if (var1 == null) {
         CommandSender var2 = (CommandSender)this.weakReference.get();
         if (var2 != null) {
            this.string2 = var1 = this.getString(var2);
         }
      }

      return var1;
   }

   @Override
   public String getName() {
      return this.string3;
   }

   @Nullable
   @Override
   public String getServerName() {
      CommandSender var1 = (CommandSender)this.weakReference.get();
      if (var1 == null) {
         return null;
      } else if (this.isPlayer()) {
         ProxiedPlayer var2 = (ProxiedPlayer)var1;
         Server var3 = var2.getServer();
         return var3 == null ? null : var3.getInfo().getName();
      } else {
         return null;
      }
   }

   @NonNull
   @Override
   public UUID getUuid() {
      UUID var1 = this.uuid2;
      if (var1.equals(ZERO_UUID) && this.flag) {
         CommandSender var2 = (CommandSender)this.weakReference.get();
         if (var2 != null) {
            UUID var3 = ((ProxiedPlayer)var2).getUniqueId();
            if (var3 != null) {
               this.uuid2 = var3;
               return var3;
            }
         }
      }

      return var1;
   }

   @Override
   public String getUuidString() {
      String var1 = this.string;
      return var1 != null && !var1.equals("00000000-0000-0000-0000-000000000000") ? var1 : (this.string = this.getUuid().toString());
   }

   public int getInt() {
      Object var1 = this.getHandle();
      if (var1 instanceof ProxiedPlayer) {
         PendingConnection var2 = ((ProxiedPlayer)var1).getPendingConnection();
         return var2.getVersion();
      } else {
         return 0;
      }
   }

   @Override
   public boolean equals(Object var1) {
      return this == var1 || var1 instanceof BungeeSender && ((BungeeSender)var1).getUuid().equals(this.getUuid());
   }

   @Override
   public boolean hasPermission(@Nullable String var1) {
      if (var1 == null) {
         return true;
      } else {
         CommandSender var2 = (CommandSender)this.weakReference.get();
         return var2 != null && var2.hasPermission(var1);
      }
   }

   @Override
   public boolean isConsole() {
      return !this.flag;
   }

   @Override
   public boolean isOnline() {
      if (this.flag) {
         CommandSender var1 = (CommandSender)this.weakReference.get();
         if (var1 != null) {
            return ((ProxiedPlayer)var1).isConnected();
         }
      }

      return this.weakReference.get() != null;
   }

   @Override
   public boolean isPlayer() {
      return this.flag;
   }

   @Override
   public void kick(@NonNull String reason) {
      if (reason == null) {
         throw new NullPointerException("reason is marked non-null but is null");
      } else {
         CommandSender var2 = (CommandSender)this.weakReference.get();
         if (var2 != null) {
            if (this.isPlayer()) {
               ProxiedPlayer var3 = (ProxiedPlayer)var2;
               var3.disconnect(BungeeLegacyComponentParser.getBaseComponentArray(reason));
            }
         }
      }
   }

   @Override
   public void sendJsonMessage(String var1) {
      CommandSender var2 = (CommandSender)this.weakReference.get();
      if (var2 != null) {
         var2.sendMessage(BungeeComponentParser.getBaseComponentArray(var1));
      }
   }

   @Override
   public void sendMessage(String var1) {
      CommandSender var2 = (CommandSender)this.weakReference.get();
      if (var2 != null && (!this.flag || this.isOnline())) {
         var2.sendMessage(BungeeLegacyComponentParser.getBaseComponentArray(var1));
      }
   }

   @Override
   public void sendPluginMessage(String var1, byte[] var2) {
      CommandSender var3 = (CommandSender)this.weakReference.get();
      if (var3 != null) {
         if (this.isPlayer()) {
            ((ProxiedPlayer)var3).sendData(var1, var2);
         }
      }
   }

   private final String getString(CommandSender var1) {
      if (var1 instanceof ProxiedPlayer) {
         InetSocketAddress var2 = ((ProxiedPlayer)var1).getAddress();
         if (var2 != null) {
            InetAddress var3 = var2.getAddress();
            if (var3 != null) {
               return var3.getHostAddress();
            }
         }
      }

      return null;
   }
}
