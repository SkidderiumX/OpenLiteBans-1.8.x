package litebans.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import litebans.chat.ChatColorCode;
import litebans.config.Config;
import litebans.config.ConfigFile;
import litebans.config.Message;
import litebans.core.LiteBansCore;
import litebans.core.NotificationManager;
import litebans.core.PluginModule;
import litebans.database.DatabaseManager;
import litebans.database.PunishmentStorage;
import litebans.database.QueryStatement;
import litebans.database.SqlStorage;
import litebans.database.Table;
import litebans.hook.UsernamePattern;
import litebans.importer.Importer;
import litebans.lib.hikari.HikariDataSource;
import litebans.lib.hikari.HikariPoolMXBean;
import litebans.lib.kotlin.Triple;
import litebans.platform.LiteBansPlatform;
import litebans.platform.PlatformDetector;
import litebans.platform.Sender;
import litebans.platform.bukkit.BukkitBridge;
import litebans.platform.bukkit.ProtocolLibChat;
import litebans.punishment.MuteManager;
import litebans.punishment.PlayerHistory;
import litebans.punishment.PunishmentEntry;
import litebans.punishment.PunishmentService;
import litebans.sync.EventProcessor;
import litebans.sync.SyncManager;
import litebans.update.BuildConstants;
import litebans.update.Version;
import litebans.util.Formatter;
import org.jetbrains.annotations.NotNull;

public class LiteBansCommand extends BaseCommand {
   public static Object object = "";
   public final Map map = new HashMap();

   @Override
   public boolean isArgsRequired() {
      return false;
   }

   public LiteBansCommand(LiteBansPlatform var1) {
      super("litebans", var1);
   }

   private final void handleSender(Sender var1, CharSequence var2) {
      handleSender(var1, var2, this.getPlatform());
   }

   @Override
   public void execute(@NotNull Sender var1, String[] var2) {
      if (var2.length != 0 && !var2[0].equalsIgnoreCase("version")) {
         LiteBansPlatform var3 = this.getPlatform();
         SyncManager var4 = (SyncManager)var3.getModule(SyncManager.class);
         NotificationManager var5 = this.getNotificationManager();
         DatabaseManager var6 = this.getDatabaseManager();
         LiteBansCore var7 = this.getLiteBansCore();
         MuteManager var8 = this.getMuteManager();
         Config var9 = var7.getConfig();
         String var10 = var2[0].toLowerCase(Locale.ENGLISH);
         if (var10.equals("broadcast")) {
            new BroadcastSubcommand(var2, var3).run();
         } else if (var10.equals("allow")) {
            new AllowSubcommand(var2, var3, true).run();
         } else if (!var10.equals("unlink") && !var10.equals("clearhistory") && !var10.equals("cleariphistory")) {
            if (var10.equals("reveal") && var2.length >= 2) {
               new RevealSubcommand(var2, var3).run();
            } else {
               PunishmentService.requirePermission(this, "litebans.admin");

               try {
                  if (object == null) {
                     throw new IOException();
                  }
               } catch (Exception var26) {
                  var7.handleObject(var26, 0);
                  this.handleSender2(var1);
                  return;
               }

               if (var10.equals("reload")) {
                  var7.onDisable();

                  try {
                     for (PluginModule var50 : var3.getModules()) {
                        var50.onReload();
                     }

                     Throwable var35 = var3.getMainConfig().getThrowable();
                     if (var35 != null) {
                        this.handleSender(var1, Message.RELOAD_FAIL);
                        this.handleSender(var1, Message.RELOAD_FAIL_CONFIG);
                        throw var35;
                     }

                     var3.execute(() -> handleDatabaseManager(var6, var5, var7, var1));
                  } catch (Throwable var27) {
                     var6.reportError(var27);
                  }
               } else if (var10.equals("info")) {
                  var3.execute(() -> handleDatabaseManager(var6, var1, var7, var4));
               } else if (var10.equals("servers")) {
                  new ServersSubcommand(var3).run();
               } else if (var10.equals("accept")) {
                  new AcceptDisclaimerSubcommand(var3).run();
               } else if (var10.equals("debug")) {
                  if (var2.length >= 2) {
                     byte var33 = (byte)Integer.parseInt(var2[1]);
                     var7.setDebugLevel(var33);
                     this.handleSender(var1, ChatColorCode.GREEN + "Debug level has been set to: " + ChatColorCode.GOLD + var33);
                  }

                  if (var7.isDebugEnabled()) {
                     this.handleSender(
                        var1,
                        ChatColorCode.GREEN
                           + "Debug levels: "
                           + var7.getDebugLevel()
                           + " (config); "
                           + var7.getLongValue()
                           + "/"
                           + var7.getByteValue4()
                           + " (database)."
                     );
                  }
               } else if (var10.equals("timezone")) {
                  SimpleDateFormat var32 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                  TimeZone var40 = TimeZone.getTimeZone(var6.getString2());
                  Calendar var45 = Calendar.getInstance(var40);
                  var32.setCalendar(var45);
                  if (var2.length == 1) {
                     this.handleSender(
                        var1,
                        ChatColorCode.GREEN
                           + "Current timezone: "
                           + ChatColorCode.GOLD
                           + var6.getString2()
                           + "\n"
                           + ChatColorCode.GREEN
                           + "Current time: "
                           + ChatColorCode.GOLD
                           + var32.format(var45.getTime())
                     );
                  } else {
                     String var49 = var2[1];
                     var3.execute(() -> handleDatabaseManager(var6, var49, var45, var1, var32));
                  }
               } else if (var10.equals("fixhistory") || var10.equals("fixiphistory")) {
                  var3.runAsync(() -> handleDatabaseManager2(var6, var1));
               } else if (var10.startsWith("clearmute")) {
                  if (var2.length >= 2) {
                     UUID var31 = UUID.fromString(var2[1]);
                     PunishmentEntry var39 = (PunishmentEntry)var8.getMuteChatHandler().getConcurrentHashMap().remove(var31);
                     if (var39 != null) {
                        this.handleSender(var1, ChatColorCode.GREEN + "Removed active mute for " + var39 + " successfully.");
                     } else {
                        CommandRunnable.messageSender.getVoid(ChatColorCode.RED + "No mute found for " + var31);
                     }
                  } else {
                     var8.getMuteChatHandler().getConcurrentHashMap().clear();
                     this.handleSender(var1, ChatColorCode.GREEN + "Active mutes have been cleared.");
                  }
               } else if (var10.equals("listmutes") && var1.isConsole()) {
                  this.handleSender(var1, Arrays.toString(var8.getMuteChatHandler().getConcurrentHashMap().entrySet().toArray()));
               } else if (var10.equals("import")) {
                  if (var2.length == 1) {
                     CommandRunnable.messageSender.getVoid(Message.IMPORT_USAGE);
                  }

                  String var30 = var2[1].toLowerCase(Locale.ENGLISH);
                  if (var30.equals("start")) {
                     var3.execute(() -> handleConfig(var7.getConfig(), var6, var1, var3, var7));
                  }
               } else if (var10.equals("reset-database")) {
                  new ResetDatabaseSubcommand(var2, var3).run();
               } else if (var10.equals("reset-templates")) {
                  new TemplateResetSubcommand(var3).run();
               } else if (var10.equals("addhistory") || var10.equals("addiphistory") || var10.equals("add-login")) {
                  new AddLoginSubcommand(var2, var3).run();
               } else if (var10.equals("json-disconnect") && var2.length >= 3) {
                  String var29 = var2[1];
                  String var38 = Formatter.getString(Arrays.copyOfRange(var2, 2, var2.length), " ");
                  if (var7.isDebugEnabled()) {
                     var7.debug("target:" + var29 + ", reason:" + var38);
                  }

                  Sender var44 = PunishmentService.findOnline(this, var29);
                  if (var44 != null) {
                     switch (var3.getPlatformId()) {
                        case 0:
                           ProtocolLibChat.INSTANCE.handleObject(var44.getHandle(), var38);
                           break;
                        case 1:
                           throw new UnsupportedOperationException();
                        default:
                           throw new UnsupportedOperationException();
                     }
                  }
               } else {
                  if (var10.equals("test-vault") && var2.length >= 2) {
                     String var11 = var2[1];
                     UUID var12 = UUID.fromString(var11);
                     String var13;
                     if (var2.length == 3) {
                        var13 = var2[2];
                     } else {
                        var13 = null;
                     }

                     if (var3.getPlatformId() == 0) {
                        var3.execute(() -> handleLiteBansPlatform(var3, var12, var13, var1));
                        return;
                     }

                     CommandRunnable.messageSender.getVoid("Command is not supported on this platform.");
                  }

                  if (var10.equals("refresh") && var2.length >= 2) {
                     String var28 = UsernamePattern.mojangApi.getString3(var2[1]);
                     String var37 = null;
                     if (var2.length >= 3) {
                        var37 = var2[2];
                     }

                     Sender var43 = var5.findOnlineByUuid(var28);
                     String var14 = var7.config.getString2();
                     if (var43 != null) {
                        String var15 = var43.getServerName();
                        if (var15 != null) {
                           var14 = var15;
                        }
                     }

                     String var52 = null;
                     if (var43 != null) {
                        var52 = var43.getName();
                     }

                     var8.syncMute(var52, var28, var37, var14);
                     this.handleSender(var1, ChatColorCode.GREEN + "Refreshed player: '" + var28 + "', server: " + var14);
                  } else if (var10.equals("expiry-update")) {
                     var3.execute(() -> handleDatabaseManager(var6));
                     this.handleSender(var1, ChatColorCode.GREEN + "Expiry update executed.");
                  } else if (var10.equals("upgrade")) {
                     this.handleSender(var1);
                  } else if (var10.equals("benchgen") && var1.isConsole() && var7.isDebugEnabled()) {
                     new TestDataGenerator(var2, var3).run();
                  } else if (var10.equals("benchjoin") && var1.isConsole() && var7.isDebugEnabled()) {
                     new EventProcessor(var2, var3).run();
                  } else {
                     this.handleSender2(var1);
                  }
               }
            }
         } else if (var2.length >= 2) {
            new UnlinkSubcommand(var2, var3).run();
         }
      } else {
         this.handleSender2(var1);
      }
   }

   @Override
   public boolean isFlag() {
      return false;
   }

   @Override
   public String getPermission() {
      return null;
   }

   public void handleString(String var1) {
      this.getPlatform().getLogger().info("Begin import from: " + var1);
   }

   public void handleSender(Sender var1) {
      DatabaseManager var2 = (DatabaseManager)this.getPlatform().getModule(DatabaseManager.class);
      this.getPlatform().execute(() -> handleDatabaseManager(var2, var1));
   }

   public void handleDatabaseManager(DatabaseManager var1, Sender var2, boolean var3) {
      try {
         PunishmentStorage var4 = var1.openStorage();

         label96: {
            try {
               if (var4.isClosed()) {
                  break label96;
               }

               for (Table var8 : Table.values()) {
                  if (var8.check()) {
                     long var9 = 0L;
                     if (!var3) {
                        this.handleSender(var2, Message.FIXHISTORY_START.getString("table", var8.toString()));
                     }

                     QueryStatement var11 = var4.prepare("SELECT * FROM " + var8);
                     ResultSet var12 = var1.execute(var11);
                     int var13 = 0;

                     for (Object var16o : var4.readEntries(var12, var8.getPunishmentType())) {
                        PunishmentEntry var16 = (PunishmentEntry) var16o;
                        String var17 = var16.getUuid();
                        if (var17 != null) {
                           if (UsernamePattern.mojangApi.checkString(var17)) {
                              var9++;
                           } else {
                              PlayerHistory var18 = var4.findLatestHistory(var17);
                              if (var18 == null) {
                                 String var19 = UsernamePattern.mojangApi.getString(var17);
                                 if (var19 != null) {
                                    var4.recordLogin(var19, var17, "#", false, true);
                                    var13++;
                                 }
                              }
                           }
                        }
                     }

                     if (!var3) {
                        this.handleSender(var2, Message.FIXHISTORY_END.getString("amount", var13));
                     }

                     if (var9 > 0L && !var3) {
                        this.handleSender(var2, Message.FIXHISTORY_OFFLINE_UUIDS.getString("amount", var9));
                     }
                  }
               }
            } catch (Throwable var21) {
               if (var4 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var20) {
                     var21.addSuppressed(var20);
                  }
               }

               throw var21;
            }

            if (var4 != null) {
               var4.close();
            }

            return;
         }

         if (var4 != null) {
            var4.close();
         }
      } catch (Exception var22) {
         var1.reportError(var22);
      }
   }

   public void handleSender2(Sender var1) {
      LiteBansCore var2 = (LiteBansCore)this.getPlatform().getModule(LiteBansCore.class);
      if (var2.getCommandAlias() != null) {
         this.handleSender(var1, ChatColorCode.RED + "[LiteBans] " + ChatColorCode.DARK_RED + "Plugin access has been disabled!");
         if (var2.getString() != null) {
            this.handleSender(var1, var2.getString());
         }
      }

      String var3;
      switch (this.getPlatform().getPlatformId()) {
         case 0:
            if (PlatformDetector.isFlag3()) {
               var3 = "Folia";
            } else if (PlatformDetector.isFlag4()) {
               var3 = "Paper";
            } else {
               var3 = "Bukkit";

               try {
                  Class.forName("org.spigotmc.SpigotConfig");
                  var3 = "Spigot";
               } catch (ClassNotFoundException var5) {
               }
            }
            break;
         case 1:
            var3 = "BungeeCord";
            break;
         case 2:
            var3 = "Velocity";
            break;
         case 3:
            var3 = "Fabric";
            break;
         default:
            var3 = "Unknown";
      }

      String var4 = Version.versionParser.getVersion(this.getPlatform().createBridge().getServerVersion(), new Integer[]{0, 0, 0}).toString();
      this.handleSender(
         var1,
         ChatColorCode.GREEN
            + "LiteBans version "
            + ChatColorCode.GOLD
            + BuildConstants.string
            + ChatColorCode.GREEN
            + " running on "
            + ChatColorCode.getString(var3)
            + " "
            + ChatColorCode.getString4(var4)
      );
      this.handleSender(
         var1,
         ChatColorCode.GOLD.toString()
            + ChatColorCode.UNDERLINE
            + "https://www.spigotmc.org/resources/litebans.3715/ {url: https://www.spigotmc.org/resources/litebans.3715/}"
      );
   }

   private final void handleDatabaseManager(DatabaseManager var1, Sender var2) {
      try {
         PunishmentStorage var3 = var1.openStorage();

         try {
            var1.checkTables(var3, ((SqlStorage)var3).getTableCreator(), true, true);
            var3.close();
            var1.onDisable();
            var1.onEnable();
            this.handleSender(var2, ChatColorCode.GREEN + "Upgrade check executed successfully.");
         } catch (Throwable var7) {
            if (var3 != null) {
               try {
                  var3.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (var3 != null) {
            var3.close();
         }
      } catch (Exception var8) {
         var1.reportError(var8);
      }
   }

   private static final void handleDatabaseManager(DatabaseManager var0) {
      try {
         PunishmentStorage var1 = var0.openStorage();

         try {
            var1.expireEntries(true);
         } catch (Throwable var5) {
            if (var1 != null) {
               try {
                  var1.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (var1 != null) {
            var1.close();
         }
      } catch (Exception var6) {
         var0.reportError(var6);
      }
   }

   private final void handleLiteBansPlatform(LiteBansPlatform var1, UUID var2, String var3, Sender var4) {
      BukkitBridge var5 = (BukkitBridge)var1.createBridge();
      boolean var6 = var5.getVaultHook().checkObject(var5.getOfflinePlayer(var2), "litebans.test", var3);
      this.handleSender(var4, ChatColorCode.GREEN + "litebans.test: " + (var6 ? ChatColorCode.GREEN + "true" : ChatColorCode.RED + "false"));
   }

   private final void handleConfig(Config var1, DatabaseManager var2, Sender var3, LiteBansPlatform var4, LiteBansCore var5) {
      String var6 = var1.getImportFrom().toLowerCase(Locale.ENGLISH);
      if (var6.contains(" ")) {
         var6 = var6.replace(" ", "");
      }

      boolean var7 = var6.equals("vanilla") || var6.contains("banned-players") || var6.contains("essentials");
      Connection var8 = null;

      try {
         PunishmentStorage var9 = var2.openStorage();

         label382: {
            label383: {
               label384: {
                  label385: {
                     try {
                        label361: {
                           if (var9.isClosed()) {
                              this.handleSender(var3, Message.ERROR_NO_DATABASE);
                              break label384;
                           }

                           if (!var7) {
                              Properties var12 = new Properties();
                              String var13 = null;
                              String var14 = null;
                              String var15 = "sqlite";
                              String var11;
                              if (var1.isImportSqlite()) {
                                 String var16 = var1.getImportSqliteFile();
                                 boolean var17 = var6.equals("litebans");
                                 if (var17) {
                                    var15 = "h2";
                                    var16 = new File(var4.getDataFolder(), "litebans").getAbsolutePath();
                                    var13 = var1.getImportImportMysqlUsername();
                                    var14 = var1.getImportImportMysqlPassword();
                                 } else if (var6.equals("banmanager7") || var6.equals("banmanagerv7")) {
                                    var15 = "h2";
                                    var16 = new File("plugins/BanManager", "local_bans").getAbsolutePath();
                                 }

                                 File var18 = new File(var16);
                                 File var19 = new File(var16 + ".mv.db");
                                 if (!var17 && !var18.exists() && !var19.exists()) {
                                    throw new FileNotFoundException(var16);
                                 }

                                 this.handleSender(var3, Message.IMPORT_START.getString("db", var16));
                                 var11 = "jdbc:" + var15 + "://" + var18.getAbsolutePath();
                              } else {
                                 var15 = "mysql";
                                 var13 = var1.getImportImportMysqlUsername();
                                 var14 = var1.getImportImportMysqlPassword();
                                 var11 = "jdbc:mysql://" + var1.getImportImportMysqlAddress() + '/' + var1.getImportImportMysqlDatabase();
                                 this.handleSender(var3, Message.IMPORT_START.getString("db", var11));
                              }

                              Driver var10 = (Driver)var2.loadDriver(Objects.requireNonNull(var2.findDriverLibrary(var5, var15)), true, true);
                              if (var10 == null) {
                                 break label385;
                              }

                              String var44 = "30000";
                              var12.put("connectTimeout", var44);
                              var12.put("socketTimeout", var44);
                              var12.put("autoReconnect", "true");
                              var12.put("useUnicode", "true");
                              var12.put("connectionCollation", "utf8mb4_unicode_ci");
                              var12.put("characterEncoding", "utf-8");
                              var12.put("characterSetResults", "utf-8");
                              var12.put("serverTimezone", "UTC");
                              var12.put("timezone", "UTC");
                              if (var13 != null && !var13.isEmpty()) {
                                 var12.put("user", var13);
                              }

                              if (var14 != null && !var14.isEmpty()) {
                                 var12.put("password", var14);
                              }

                              var8 = var10.connect(var11, var12);
                           } else {
                              this.handleSender(var3, Message.IMPORT_START.getString("db", "banned-players.json"));
                           }

                           boolean var38 = var1.isImportImportIpbans();
                           List var39 = var4.getDiagnostics().getList(Importer.class, new Class[]{LiteBansPlatform.class}, new Object[]{var4});
                           boolean var40 = false;

                           for (Object var42o : var39) {
                              Importer var42 = (Importer) var42o;
                              if (var42.checkString(var6) && (!var42.check() || var8 != null)) {
                                 this.handleString(var42.getString());
                                 String var43 = var5.config.getImportImportTablePrefix();
                                 if (!var43.equals("default")) {
                                    var42.setString2(var43);
                                 }

                                 var42.handleString(var6, var8, var9, var38);
                                 var40 = true;
                                 var42.handleSender(var3);
                                 if (!var42.check2()) {
                                    break label361;
                                 }
                                 break;
                              }
                           }

                           if (!var40) {
                              this.handleSender(var3, Message.IMPORT_UNSUPPORTED.getString("name", var6));
                              break label382;
                           }

                           this.handleDatabaseManager(var2, var3, true);
                           break label383;
                        }
                     } catch (Throwable var35) {
                        if (var9 != null) {
                           try {
                              var9.close();
                           } catch (Throwable var34) {
                              var35.addSuppressed(var34);
                           }
                        }

                        throw var35;
                     }

                     if (var9 != null) {
                        var9.close();
                     }

                     return;
                  }

                  if (var9 != null) {
                     var9.close();
                  }

                  return;
               }

               if (var9 != null) {
                  var9.close();
               }

               return;
            }

            if (var9 != null) {
               var9.close();
            }

            return;
         }

         if (var9 != null) {
            var9.close();
         }
      } catch (Throwable var36) {
         this.handleSender(var3, Message.IMPORT_FAIL);
         var2.reportError(var36);
         return;
      } finally {
         this.map.clear();
         if (var8 != null) {
            try {
               var8.close();
            } catch (Exception var33) {
               throw new RuntimeException(var33);
            }
         }
      }
   }

   private final void handleDatabaseManager2(DatabaseManager var1, Sender var2) {
      this.handleDatabaseManager(var1, var2, false);
   }

   private final void handleDatabaseManager(DatabaseManager var1, String var2, Calendar var3, Sender var4, SimpleDateFormat var5) {
      try {
         var1.setString(var2);
         var3.setTimeZone(TimeZone.getTimeZone(var2));
         PunishmentStorage var6 = var1.openStorage();

         try {
            QueryStatement var7 = var6.prepare("UPDATE " + Table.CONFIG + " SET timezone=?").getQueryStatement(var2);

            try {
               var1.executeUpdate(var7);
            } catch (Throwable var12) {
               if (var7 != null) {
                  try {
                     var7.close();
                  } catch (Throwable var11) {
                     var12.addSuppressed(var11);
                  }
               }

               throw var12;
            }

            if (var7 != null) {
               var7.close();
            }

            this.handleSender(
               var4,
               ChatColorCode.GREEN
                  + "Timezone updated: "
                  + ChatColorCode.GOLD
                  + var1.getString2()
                  + "\n"
                  + ChatColorCode.GREEN
                  + "Current time: "
                  + ChatColorCode.GOLD
                  + var5.format(var3.getTime())
            );
         } catch (Throwable var13) {
            if (var6 != null) {
               try {
                  var6.close();
               } catch (Throwable var10) {
                  var13.addSuppressed(var10);
               }
            }

            throw var13;
         }

         if (var6 != null) {
            var6.close();
         }
      } catch (Exception var14) {
         var1.reportError(var14);
      }
   }

   private final void handleDatabaseManager(DatabaseManager var1, Sender var2, LiteBansCore var3, SyncManager var4) {
      try {
         PunishmentStorage var6 = var1.openStorage();

         label63: {
            try {
               HikariDataSource var5 = var1.getHikariDataSource();
               if (!var6.isClosed() && var5 != null) {
                  HikariPoolMXBean var7 = var5.getHikariPool2();
                  String var8 = var3.getConfig().getSqlDriver();
                  String var9 = DatabaseManager.databaseDriver.getString(var8);
                  this.handleSender(
                     var2, ChatColorCode.getString2("LiteBans is connected to a ") + ChatColorCode.getString(var9) + ChatColorCode.getString2(" database.")
                  );
                  int var10 = var3.getConfig().getSqlPoolMaxConnections();
                  int var11 = var3.getConfig().getSqlPoolMinConnections();
                  CommandAlias var12 = var1.getCommandAlias();
                  if (var12 != null) {
                     String var13 = "" + ChatColorCode.GOLD + var12.getIntValue2();
                     if (var12.check2()) {
                        var13 = "" + ChatColorCode.RED + var12.getIntValue2() + "!!!";
                     }

                     this.handleSender(var2, ChatColorCode.GREEN + "Server name: " + ChatColorCode.GOLD + var12.getString());
                     this.handleSender(
                        var2,
                        ChatColorCode.GREEN
                           + "Server UUID: "
                           + ChatColorCode.GOLD
                           + var12.getString2()
                           + ChatColorCode.GREEN
                           + " <"
                           + var13
                           + ChatColorCode.GREEN
                           + ">"
                     );
                  }

                  this.handleSender(
                     var2,
                     ChatColorCode.GREEN
                        + "Pooled connections: "
                        + ChatColorCode.GOLD
                        + var7.getInt3()
                        + ChatColorCode.GREEN
                        + "/"
                        + ChatColorCode.GOLD
                        + var10
                        + ChatColorCode.GREEN
                        + " (minimum: "
                        + ChatColorCode.GOLD
                        + var11
                        + ChatColorCode.GREEN
                        + ", idle: "
                        + ChatColorCode.GOLD
                        + var7.getInt()
                        + ChatColorCode.GREEN
                        + ")"
                  );
                  int var23 = var7.getInt4();
                  if (var23 > 0) {
                     this.handleSender(var2, ChatColorCode.GREEN + "Threads awaiting connection: " + ChatColorCode.GOLD + var23);
                  }

                  Triple var14 = var4.getTriple();
                  AtomicLong var15 = (AtomicLong)var14.getObject4();
                  long var16 = ((AtomicLong)var14.getObject22()).get();
                  long var18 = Math.max(0L, ((AtomicLong)var14.getObject32()).get() - var16);
                  this.handleSender(
                     var2,
                     ChatColorCode.GREEN
                        + "Sync events in/out/skipped: "
                        + ChatColorCode.GOLD
                        + var15
                        + ChatColorCode.GREEN
                        + ", "
                        + ChatColorCode.GOLD
                        + var16
                        + ChatColorCode.GREEN
                        + ", "
                        + ChatColorCode.GOLD
                        + var18
                  );
                  break label63;
               }

               this.handleSender(var2, Message.ERROR_NO_DATABASE);
            } catch (Throwable var21) {
               if (var6 != null) {
                  try {
                     var6.close();
                  } catch (Throwable var20) {
                     var21.addSuppressed(var20);
                  }
               }

               throw var21;
            }

            if (var6 != null) {
               var6.close();
            }

            return;
         }

         if (var6 != null) {
            var6.close();
         }
      } catch (Exception var22) {
         var1.reportError(var22);
      }
   }

   private final void handleDatabaseManager(DatabaseManager var1, NotificationManager var2, LiteBansCore var3, Sender var4) {
      boolean var5 = true;

      try {
         var1.onDisable();
         var2.handleString(var3.getConfig().getSqlDriver());
         var1.onEnable();
         PunishmentStorage var6 = var1.openStorage();

         label72: {
            try {
               if (var6.isClosed()) {
                  this.handleSender(var4, Message.RELOAD_FAIL_CONNECT);
                  break label72;
               }

               ConfigFile var7 = var3.getMessagesLoader().getConfigFile();
               if (var3.templateManager.getConfigFile() == null) {
                  this.handleSender(var4, Message.RELOAD_FAIL_CONFIG_TEMPLATES);
                  var5 = false;
               }

               if (var7 != null && var7.getThrowable() != null) {
                  this.handleSender(var4, Message.RELOAD_FAIL.getString(Message.RELOAD_FAIL_CONFIG_MESSAGES));
                  var5 = false;
               }

               if (var5) {
                  this.handleSender(var4, Message.RELOAD_SUCCESS);
               }
            } catch (Throwable var10) {
               if (var6 != null) {
                  try {
                     var6.close();
                  } catch (Throwable var9) {
                     var10.addSuppressed(var9);
                  }
               }

               throw var10;
            }

            if (var6 != null) {
               var6.close();
            }

            return;
         }

         if (var6 != null) {
            var6.close();
         }
      } catch (Exception var11) {
         var1.reportError(var11);
      }
   }
}
