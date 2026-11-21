package litebans.sync;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import litebans.chat.ChatColorCode;
import litebans.command.ArgParser;
import litebans.command.CommandRunnable;
import litebans.core.LiteBansCore;
import litebans.core.NotificationManager;
import litebans.lib.kotlin.collections.IntIterator;
import litebans.lib.kotlin.ranges.IntRange;
import litebans.platform.LiteBansPlatform;
import litebans.platform.Sender;
import litebans.platform.SimpleSender;
import litebans.randomid.LongBitSet;
import litebans.util.DecimalFormats;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.jetbrains.annotations.NotNull;

public final class EventProcessor extends CommandRunnable {
   private final String[] stringArray;

   public EventProcessor(@NotNull String[] var1, @NotNull LiteBansPlatform var2) {
      super(var2);
      this.stringArray = var1;
   }

   @Override
   public void run() {
      Sender var1 = this.getCurrentSender();
      LongBitSet var2 = new LongBitSet(0L, 1, null);
      int var3 = ArgParser.getInt(this.stringArray, 1, 512);
      int var4 = ArgParser.getInt(this.stringArray, 2, 4096);
      int var5 = ArgParser.getInt(this.stringArray, 3, 6);
      List var7 = new ArrayList(var3);

      for (int var8 = 0; var8 < var3; var8++) {
         UUID var9 = new UUID(var2.getLong(), var2.getLong());
         int var10 = var2.getInt(252) + 2;
         int var11 = var2.getInt(252) + 2;
         int var12 = var2.getInt(252) + 2;
         int var13 = var2.getInt(252) + 2;
         String var14 = "" + var10 + '.' + var11 + '.' + var12 + '.' + var13;
         String var15 = String.valueOf(var2.getInt(999999999));
         var7.add(new SimpleSender(var15, var14, var9));
      }

      CommandRunnable var16 = this;
      ((LiteBansCore)var16.getPlatform().getModule(LiteBansCore.class)).setDebugLevel((byte)0);
      Iterable var17 = new IntRange(1, var5);
      Iterator var18 = var17.iterator();

      while (var18.hasNext()) {
         int var19 = ((IntIterator)var18).getInt();
         this.getPlatform().execute(() -> handleSender(var1, var19, var7, var2, this));
      }
   }

   private static final void handleSender(Sender var0, int var1, List var2, LongBitSet var3, EventProcessor var4) {
      long var9 = System.nanoTime();
      Iterable var12 = new IntRange(1, var1);
      Iterator var14 = var12.iterator();

      while (var14.hasNext()) {
         ((IntIterator)var14).getInt();
         Sender var18 = (Sender)var2.get(var3.getInt(var2.size()));
         CommandRunnable var19 = var4;

         try {
            ((NotificationManager)var19.getPlatform().getModule(NotificationManager.class))
               .getLoginCheck()
               .handleObject(new AsyncPlayerPreLoginEvent(var18.getName(), InetAddress.getByName(var18.getIp()), var18.getUuid()));
         } catch (UnknownHostException var20) {
            throw new RuntimeException(var20);
         }
      }

      long var6 = System.nanoTime() - var9;
      var0.sendMessage(
         ChatColorCode.GREEN
            + " -> "
            + ChatColorCode.GOLD
            + DecimalFormats.getString2((double)var6 / 1000.0 / 1000.0)
            + ' '
            + ChatColorCode.GREEN
            + "ms to process "
            + var1
            + " events."
      );
   }
}
