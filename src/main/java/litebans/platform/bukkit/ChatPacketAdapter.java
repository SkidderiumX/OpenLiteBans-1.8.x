package litebans.platform.bukkit;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import litebans.core.LiteBansCore;
import litebans.core.NotificationManager;
import litebans.database.DatabaseManager;
import litebans.lib.kotlin.Lazy;
import litebans.lib.kotlin.LazyKt;
import litebans.lib.kotlin.jvm.internal.Intrinsics;
import litebans.platform.LiteBansPlatform;
import litebans.platform.PlatformBridge;
import litebans.punishment.MuteManager;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public final class ChatPacketAdapter extends PacketAdapter {
   public static final ChatPacketAdapterCompanion Companion = new ChatPacketAdapterCompanion(null);
   private final LiteBansPlatform liteBansPlatform;
   private final MuteManager muteManager;
   private final NotificationManager notificationManager;
   private final LiteBansCore liteBansCore;
   private final DatabaseManager databaseManager;
   private static final Lazy lazy = LazyKt.getLazy(ChatPacketAdapter::getPacketType);
   private static final Lazy lazy2 = LazyKt.getLazy(ChatPacketAdapter::getPacketType2);

   public ChatPacketAdapter(@NotNull LiteBansPlatform var1) {
      super(createParams(var1));
      this.liteBansPlatform = var1;
      this.muteManager = (MuteManager)this.liteBansPlatform.getModule(MuteManager.class);
      this.notificationManager = (NotificationManager)this.liteBansPlatform.getModule(NotificationManager.class);
      this.liteBansCore = (LiteBansCore)this.liteBansPlatform.getModule(LiteBansCore.class);
      this.databaseManager = (DatabaseManager)this.liteBansPlatform.getModule(DatabaseManager.class);
   }

   private static PacketAdapter.AdapterParameteters createParams(LiteBansPlatform var1) {
      PlatformBridge var10001 = var1.createBridge();
      Intrinsics.handleObject(var10001, "");
      Plugin var25 = BukkitBridge.getPluginDefault((BukkitBridge)var10001, null, 1, null);
      Object[] var4 = new Object[]{ChatPacketAdapterCompanion.getObject(Companion), ChatPacketAdapterCompanion.getObject2(Companion)};
      Plugin var23 = var25;
      Collection var9 = new ArrayList();
      Object[] var11 = var4;
      int var13 = 0;

      for (int var14 = var4.length; var13 < var14; var13++) {
         Object var15 = var11[var13];
         PacketType var10000 = var15 instanceof PacketType ? (PacketType)var15 : null;
         if ((var15 instanceof PacketType ? (PacketType)var15 : null) != null) {
            PacketType var20 = var10000;
            var9.add(var20);
         }
      }

      Collection var6 = (List)var9;
      PacketType[] var24 = (PacketType[])(var6.toArray(new PacketType[0]));
      return PacketAdapter.params(var23, Arrays.copyOf(var24, var24.length)).optionAsync();
   }

   public void onPacketReceiving(@NotNull PacketEvent var1) {
      PacketType var2 = var1.getPacketType();
      if (!var1.isCancelled()
         && !ProtocolLibHook.checkPlayer2(var1.getPlayer())
         && (ProtocolLibHook.getInt(var1.getPlayer()) < 759 || Intrinsics.checkObject(var2, ChatPacketAdapterCompanion.getObject2(Companion)))) {
         String var3 = (Intrinsics.checkObject(var2, ChatPacketAdapterCompanion.getObject2(Companion)) ? "/" : "") + this.getString(var1);
         boolean var4 = ChatPacketAdapterCompanion.getObject2(Companion) != null
            && !Intrinsics.checkObject(var2, ChatPacketAdapterCompanion.getObject2(Companion));
         this.muteManager.getMuteChatHandler().handleObject(var1, var3, this.liteBansPlatform.getSender(var1.getPlayer()), var4);
      }
   }

   public final String getString(@NotNull PacketEvent var1) {
      String var10000 = (String)var1.getPacket().getStrings().readSafely(0);
      if (var10000 == null) {
         var10000 = "";
      }

      return var10000;
   }

   private static final PacketType getPacketType() {
      PacketType var0;
      try {
         var0 = Client.CHAT;
      } catch (Throwable var1) {
         var0 = null;
      }

      return var0;
   }

   private static final PacketType getPacketType2() {
      // 1.8.x has no separate signed chat-command packet; commands arrive through Client.CHAT.
      return null;
   }

   public static final Lazy getLazy() {
      return lazy;
   }

   public static final Lazy getLazy2() {
      return lazy2;
   }
}
