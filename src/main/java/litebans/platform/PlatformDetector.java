package litebans.platform;

import litebans.chat.BungeeLegacyComponentParser;
import org.bukkit.entity.Player;
import org.bukkit.entity.Player.Spigot;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class PlatformDetector {
   private static boolean flag = ClassLoadDiagnostics.checkString("org.bukkit.block.TestBlock");
   private static boolean flag2 = ClassLoadDiagnostics.checkString("net.md_5.bungee.api.chat.BaseComponent");
   private static boolean flag3 = false;
   private static boolean flag4 = false;

   public static boolean isFlag2() {
      return flag2;
   }

   public static boolean isFlag3() {
      return flag3;
   }

   public static boolean isFlag4() {
      return flag4;
   }

   public static void handlePlayer(Player var0, String var1) {
      Spigot var2 = var0.spigot();
      var2.sendMessage(BungeeLegacyComponentParser.getBaseComponentArray2(var1));
   }

   static {
      try {
         AsyncPlayerPreLoginEvent.class.getMethod("kickMessage");
         flag4 = true;
         Class.forName("io.papermc.paper.threadedregions.scheduler.RegionScheduler");
         int var0 = Thread.activeCount();
         Thread[] var1 = new Thread[var0];
         Thread.enumerate(var1);

         for (Thread var5 : var1) {
            if (var5 != null && var5.getName().startsWith("Folia Watchdog")) {
               flag3 = true;
            }
         }
      } catch (ReflectiveOperationException | LinkageError var6) {
      }
   }
}
