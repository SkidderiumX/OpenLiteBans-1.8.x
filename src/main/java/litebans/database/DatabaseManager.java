package litebans.database;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.ZipException;
import litebans.api.Database;
import litebans.api.impl.DatabaseImpl;
import litebans.command.AbortException;
import litebans.command.CommandAlias;
import litebans.command.CommandRunnable;
import litebans.command.LiteBansCommand;
import litebans.command.TextArgs;
import litebans.config.Config;
import litebans.config.ConfigFile;
import litebans.config.GenericConfigFile;
import litebans.config.Message;
import litebans.core.CoreFieldAccess;
import litebans.core.LiteBansCore;
import litebans.core.MessageSender;
import litebans.core.NotificationManager;
import litebans.core.PluginModule;
import litebans.lib.hikari.CustomDiscardPolicy;
import litebans.lib.hikari.HikariConfig;
import litebans.lib.hikari.HikariDataSource;
import litebans.lib.hikari.HikariLogger;
import litebans.lib.hikari.HikariPoolMXBean;
import litebans.lib.hikari.HikariProxyPreparedStatement;
import litebans.lib.hikari.ProxyPreparedStatement;
import litebans.lib.kotlin.KotlinVersion;
import litebans.lib.kotlin.Lazy;
import litebans.lib.kotlin.LazyKt;
import litebans.lib.kotlin.NoWhenBranchMatchedException;
import litebans.lib.kotlin.ResultFailure;
import litebans.lib.kotlin.UninitializedPropertyAccessException;
import litebans.lib.kotlin.Unit;
import litebans.lib.kotlin.collections.ArraysKt;
import litebans.lib.kotlin.collections.CollectionsKt;
import litebans.lib.kotlin.collections.IntIterator;
import litebans.lib.kotlin.collections.SetsKt;
import litebans.lib.kotlin.io.AutoCloseableKt;
import litebans.lib.kotlin.io.CloseableKt;
import litebans.lib.kotlin.io.FilesKt;
import litebans.lib.kotlin.jvm.internal.Function1;
import litebans.lib.kotlin.jvm.internal.Intrinsics;
import litebans.lib.kotlin.ranges.IntRange;
import litebans.lib.kotlin.text.Charsets;
import litebans.lib.kotlin.text.Regex;
import litebans.lib.kotlin.text.StringsKt;
import litebans.platform.LiteBansPlatform;
import litebans.platform.PlatformBridge;
import litebans.platform.Sender;
import litebans.punishment.Flag;
import litebans.punishment.PlayerInfo;
import litebans.punishment.PunishmentCheckTask;
import litebans.randomid.AlphabetCodec;
import litebans.sync.AsyncQueue;
import litebans.sync.SyncManager;
import litebans.update.BuildInfo;
import litebans.update.ChecksumMismatchException;
import litebans.update.Updater;
import litebans.util.DecimalFormats;
import litebans.util.FileUtils;
import litebans.util.Unreachable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DatabaseManager extends PluginModule {
   public static final DatabaseDriver databaseDriver = new DatabaseDriver(null);
   private HikariDataSource hikariDataSource;
   private final ReentrantLock reentrantLock = new ReentrantLock();
   private final Lazy lazy = LazyKt.getLazy(() -> getLiteBansCore(this));
   private final Lazy lazy2 = LazyKt.getLazy(() -> getNotificationManager(this));
   private StatementExecutor statementExecutor;
   private CommandAlias commandAlias;
   private HashMap hashMap;
   private AtomicLong atomicLong;
   private final AtomicBoolean atomicBoolean;
   private final AtomicBoolean atomicBoolean2;
   private final AtomicBoolean atomicBoolean3;
   private boolean flag;
   private boolean flag2;
   private boolean flag3;
   private byte byteValue;
   private long longValue;
   private long longValue2;
   private int intValue;
   private String string;
   private int intValue2;
   public static final int intValue3 = 3000;
   private static final String string2 = "at " + ProxyPreparedStatement.class.getName() + ".executeQuery(";
   private static final String string3 = "at " + HikariProxyPreparedStatement.class.getName() + ".executeQuery(";
   private static final String string4 = "at " + QueryStatement.class.getName() + '.';

   public DatabaseManager(@NotNull LiteBansPlatform var1) {
      super(var1);
      this.statementExecutor = new StatementExecutor(var1);
      this.hashMap = new HashMap();
      this.atomicLong = new AtomicLong(0L);
      this.atomicBoolean = new AtomicBoolean();
      this.atomicBoolean2 = new AtomicBoolean();
      this.atomicBoolean3 = new AtomicBoolean();
      this.flag = true;
      this.string = "UTC";
   }

   public final HikariDataSource getHikariDataSource() {
      return this.hikariDataSource;
   }

   public final LiteBansCore getCore() {
      Lazy var1 = this.lazy;
      return (LiteBansCore)var1.getValue();
   }

   public final NotificationManager getNotifications() {
      Lazy var1 = this.lazy2;
      return (NotificationManager)var1.getValue();
   }

   public final CommandAlias getCommandAlias() {
      return this.commandAlias;
   }

   public final String getString() {
      if (this.commandAlias == null) {
         return this.getCore().getConfig().getString2();
      } else {
         CommandAlias var1 = this.commandAlias;
         return var1.toString();
      }
   }

   public final HashMap getHashMap() {
      return this.hashMap;
   }

   public final boolean isConnected() {
      return (this.hikariDataSource != null ? !this.hikariDataSource.check() : false) && this.atomicBoolean.get();
   }

   public final void setFlag(boolean var1) {
      this.flag = var1;
   }

   public final void setFlag2(boolean var1) {
      this.flag2 = var1;
   }

   public final boolean isFlag3() {
      return this.flag3;
   }

   public final void setFlag3(boolean var1) {
      this.flag3 = var1;
   }

   public final byte getByteValue() {
      return this.byteValue;
   }

   public final void setByteValue(byte var1) {
      this.byteValue = var1;
   }

   public final long getLongValue() {
      return this.longValue;
   }

   public final void setLongValue(long var1) {
      this.longValue = var1;
   }

   public final long getLongValue2() {
      return this.longValue2;
   }

   public final void setLongValue2(long var1) {
      this.longValue2 = var1;
   }

   public final int getIntValue() {
      return this.intValue;
   }

   public final String getString2() {
      return this.string;
   }

   public final void setString(@NotNull String var1) {
      this.string = var1;
   }

   public final int getIntValue2() {
      return this.intValue2;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final Connection getConnection() {
      long var1 = System.nanoTime();
      Connection var3 = null;
      boolean var9 = false /* VF: Semaphore variable */;

      label89: {
         try {
            var9 = true;
            HikariDataSource var12 = this.hikariDataSource;
            var3 = var12 != null && !var12.check() ? var12.getConnection() : null;
            var9 = false;
            break label89;
         } catch (Exception var10) {
            this.reportError(var10);
            var9 = false;
         } finally {
            if (var9) {
               long var5 = System.nanoTime() - var1;
               if (var5 > 1000000000L) {
               }
            }
         }

         long var4 = System.nanoTime() - var1;
         if (var4 > 1000000000L) {
         }

         return var3;
      }

      long var13 = System.nanoTime() - var1;
      if (var13 > 1000000000L && var3 != null && !this.isPoolExhausted()) {
         int var6 = (int)((double)var13 / 1.0E9);
         if (var6 > 12) {
            this.platform.getLogger().warning("Database is slow! Establishing connection took " + var6 + " seconds.");
         }
      }

      return var3;
   }

   public final String getSenderServer(@NotNull Sender var1) {
      byte var2 = this.platform.getPlatformId();
      String var10000;
      if (var2 != 0 && var2 != 3) {
         if (var2 != 1 && var2 != 2) {
            Unreachable.getVoid();
            throw new NoWhenBranchMatchedException();
         }

         var10000 = var1.getServerName();
      } else {
         var10000 = null;
      }

      return var10000;
   }

   public final boolean isSyncEnabled() {
      return this.getCore().getConfig().isSync() && (this.commandAlias != null ? this.commandAlias.check() : false);
   }

   public final boolean checkTables(@NotNull PunishmentStorage var1, @Nullable TableCreator var2, boolean var3, boolean var4) {
      LiteBansCore var5 = (LiteBansCore)this.platform.getModule(LiteBansCore.class);
      File var7 = this.platform.getDataFolder();
      String var8 = ".version";
      File var6 = new File(var7, var8);
      String var36 = String.valueOf(currentTimeMillisDefault(this, false, 1, null));
      boolean var37 = false;
      boolean var9 = !var6.exists();

      try {
         label135: {
            File var10 = FileUtils.getFile(var6);
            short var12 = 128;
            Charset var11 = Charsets.charset;
            Reader var13 = new InputStreamReader(new FileInputStream(var10), var11);
            Closeable var38 = var13 instanceof BufferedReader ? (BufferedReader)var13 : new BufferedReader(var13, var12);
            Throwable var40 = null;

            boolean var23;
            try {
               BufferedReader var42 = (BufferedReader)var38;
               String var14 = var42.readLine();
               Config var15 = var5.getConfig();

               try {
                  MessageDigest var16 = MessageDigest.getInstance("SHA-512");
                  if (var16 != null) {
                     var16.reset();
                     String[] var43 = new String[]{
                        BuildInfo.getString3(),
                        var15.getSqlDriver(),
                        var15.getSqlAddress(),
                        var15.getString3(),
                        var15.getString4(),
                        var15.getString5(),
                        var15.getString6()
                     };
                     String var18 = ArraysKt.getString(var43, "\u0001", null, null, 0, null, null, 62, null);
                     byte[] var44 = var16.digest(var18.getBytes(Charsets.charset));
                     var16.reset();
                     var36 = AlphabetCodec.getString(var44, false);
                  }
               } catch (NoSuchAlgorithmException var32) {
                  LiteBansCore.handleObjectDefault(var5, var32, 0, 2, null);
                  Intrinsics.handleObject(var5);
                  String var10003 = var32.getMessage();
                  if (var10003 == null) {
                     var10003 = "";
                  }

                  String var20 = var10003;
                  String[] var46;
                  if (Intrinsics.checkObject(var20, "sort")) {
                     String[] var22 = new String[]{"00"};
                     var46 = var22;
                  } else {
                     String[] var45 = new String[]{"01:" + var20};
                     var46 = var45;
                  }

                  String[] var17 = var46;
                  TextArgs var10000 = new TextArgs(var5, Arrays.copyOf(var17, var17.length));
                  var10000.getInt();
               }

               if (!Intrinsics.checkObject(var36, var14) || var3) {
                  break label135;
               }

               var5.debug(2, "Table upgrade checks skipped.");
               var23 = true;
            } catch (Throwable var33) {
               var40 = var33;
               throw var33;
            } finally {
               CloseableKt.handleCloseable(var38, var40);
            }

            return var23;
         }
      } catch (Exception var35) {
         this.reportError(var35);
         var37 = true;
      }

      if (var2 != null) {
         if (!var1.isOpen()) {
            String var41 = "Connection is closed. Only open connections can be used to upgrade tables.";
            throw new IllegalArgumentException(var41.toString());
         }

         try {
            Intrinsics.handleObject(var1, "");
            SqlStorage var39 = (SqlStorage)var1;
            Intrinsics.handleObject(var5);
            var39.upgradeTables(var4, var5, var2, var9);
         } catch (Throwable var31) {
            this.reportError(var31);
            var37 = true;
         }

         if (!var37) {
            try {
               FilesKt.handleFile(var6, var36, null, 2, null);
            } catch (Throwable var30) {
               this.reportError(var30);
            }
         }
      }

      return false;
   }

   public static boolean checkTablesDefault(DatabaseManager var0, PunishmentStorage var1, TableCreator var2, boolean var3, boolean var4, int var5, Object var6) {
      if ((var5 & 4) != 0) {
         var3 = false;
      }

      if ((var5 & 8) != 0) {
         var4 = false;
      }

      return var0.checkTables(var1, var2, var3, var4);
   }

   @Override
   public void onDisable() {
      Lock var1 = this.reentrantLock;
      var1.lock();

      try {
         AsyncQueue var5 = (AsyncQueue)this.platform.getModule(AsyncQueue.class);
         if (!var5.getQueue().isEmpty()) {
            DatabaseManager var6 = this;

            try {
               PunishmentStorage var8 = var6.openStorage();
               Closeable var9 = var8;
               Throwable var10 = null;

               try {
                  PunishmentStorage var13 = var8;

                  for (Object var18 : var5.getQueue()) {
                     PlayerInfo var19 = (PlayerInfo)var18;
                     EntryReader.handlePunishmentStorage(var13, var19.getString(), var19.getUuid(), var19.getIp(), false, false, 24, null);
                  }
               } catch (Throwable var28) {
                  var10 = var28;
                  throw var28;
               } finally {
                  CloseableKt.handleCloseable(var9, var10);
               }
            } catch (Exception var30) {
               if (!this.isConnectionError(var30)) {
                  throw var30;
               }
            }
         }

         AtomicBoolean var3 = this.atomicBoolean;
         var3.set(false);
         if (this.hikariDataSource != null) {
            this.hikariDataSource.close();
         }

         this.hikariDataSource = null;
      } finally {
         var1.unlock();
      }
   }

   public final ResultSet executeQuery(@NotNull QueryStatement var1) {
      return this.statementExecutor.executeQuery(var1);
   }

   public final int[] executeBatch(@NotNull Statement var1) {
      return this.statementExecutor.executeBatch(var1);
   }

   public final ResultSet execute(@NotNull QueryStatement var1) {
      return this.statementExecutor.execute(var1);
   }

   public final int executeUpdate(@NotNull QueryStatement var1) {
      return this.statementExecutor.executeUpdate(var1);
   }

   public final EmptyResultSet getEmptyResultSet(@NotNull QueryStatement var1) {
      this.statementExecutor.executeUpdate(var1);
      return EmptyResultSetHolder.getEmptyResultSet();
   }

   public final List getDriverLibraries(@NotNull LiteBansCore var1) {
      Config var2 = var1.getConfig();
      String[] var3 = var2.getSqlDrivers();
      String[] var5 = var3;
      Collection var6 = new ArrayList(var3.length);
      int var8 = 0;

      for (int var9 = var3.length; var8 < var9; var8++) {
         String var10 = var5[var8];
         CharSequence var13 = var10;
         Regex var14 = new Regex(":(?!(//))");
         byte var15 = 5;
         Collection var19 = var14.getList(var13, var15);
         String[] var16 = /* cast-fix */(String[])(var19.toArray(new String[0]));
         String var20 = (String)ArraysKt.getObject(var16, 2);
         String var21 = (String)ArraysKt.getObject(var16, 3);
         if (var21 != null) {
            var20 = var20 != null ? StringsKt.getString(var20, "{}", var21, false, 4, null) : null;
         }

         String var22 = (String)ArraysKt.getObject(var16, 4);
         String var17 = var16[0];
         var6.add(new DriverLibrary(var17.toLowerCase(Locale.ENGLISH), var16[1], var20, var21, var22));
      }

      return (List)var6;
   }

   public final CommandAlias getCommandAlias(@NotNull PunishmentStorage var1, int var2, boolean var3) {
      CommandAlias var4 = (CommandAlias)this.hashMap.get(var2);
      if (var4 == null && var3) {
         cleanupServersDefault(this, var1, false, 2, null);
         return this.getCommandAlias(var1, var2, false);
      } else {
         return var4;
      }
   }

   public final boolean hasFreeConnections(int var1) {
      int var2 = this.getCore().getConfig().getSqlPoolMaxConnections();
      HikariDataSource var10000 = this.getHikariDataSource();
      if (var10000 != null) {
         HikariPoolMXBean var4 = var10000.getHikariPool2();
         if (var4 != null) {
            return var4.getInt2() >= var2 - var1 && var2 >= 3;
         }
      }

      return 0 >= var2 - var1 && var2 >= 3;
   }

   public static boolean hasFreeConnectionsDefault(DatabaseManager var0, int var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = 1;
      }

      return var0.hasFreeConnections(var1);
   }

   public final void reportError(@NotNull Throwable var1) {
      try {
         LiteBansCore var2 = (LiteBansCore)this.platform.getModule(LiteBansCore.class);
         String var10000 = var1.getMessage();
         if (var10000 == null) {
            var10000 = "";
         }

         String var16 = var10000;
         if (var1 instanceof ChecksumMismatchException) {
            PluginModule var19 = this;
            var19.platform.getLogger().severe(var16);
            return;
         }

         if (var1 instanceof UninitializedPropertyAccessException
            && Intrinsics.checkObject(CoreFieldAccess.getObject(var2), Message.getString(var2, false, true))) {
            return;
         }

         if (!this.platform.isEnabled()) {
            if (var1 instanceof SQLException && StringsKt.checkCharSequence3(var16, "Interrupted during connection acquisition", false, 2, null)) {
               return;
            }

            var1.printStackTrace();
            return;
         }

         if (var1 instanceof VirtualMachineError) {
            throw var1;
         }

         StringWriter var18 = new StringWriter();
         var1.printStackTrace(new PrintWriter(var18));
         String var5 = var18.toString();
         boolean var6 = false;
         if (var1 instanceof SQLException) {
            Throwable var7 = var1;
            int var8 = 4;

            while (var8-- > 0) {
               Throwable var29 = var7.getCause();
               if (var29 == null) {
                  break;
               }

               var7 = var29;
            }

            if (!(var7 instanceof SocketTimeoutException) && (!(var1 instanceof SQLTransientConnectionException) || !this.atomicBoolean3.get())) {
               PluginModule var20 = this;
               String var22 = "An unexpected database error has occurred.";
               var20.platform.getLogger().severe(var22);
            } else {
               this.atomicBoolean3.set(true);
               var6 = true;
               PluginModule var9 = this;
               String var10 = "Unfortunately, a request has failed because your database server did not respond within "
                  + DecimalFormats.getString((double)var2.getConfig().getSqlPoolTimeout() / 1000.0)
                  + " seconds. (Socket timeout)";
               var9.platform.getLogger().severe(var10);
            }

            String var21 = var2.getConfig().getSqlDriver();
            Throwable var23 = this.platform.getMainConfig().getThrowable();
            int var11 = var2.getConfig().getSqlPoolMaxConnections();
            if (var11 < 5) {
               PluginModule var12 = this;
               String var13 = "max_connections in config.yml is smaller than 5 (max="
                  + var11
                  + ")! This can cause connection pool exhaustion (\"Connection is not available, request timed out ...\").";
               var12.platform.getLogger().warning(var13);
            }

            if (var1 instanceof SQLTransientConnectionException && !this.atomicBoolean3.get() && this.isPoolExhausted()) {
               this.reportPoolExhaustion();
            }

            if (StringsKt.checkCharSequence3(var5, "Unknown column '", false, 2, null) && StringsKt.checkCharSequence3(var5, "' in '", false, 2, null)
               || StringsKt.checkCharSequence3(var5, "Column '", false, 2, null) && StringsKt.checkCharSequence3(var5, "' not found.", false, 2, null)
               || StringsKt.checkCharSequence3(var5, ": Column \"", false, 2, null) && StringsKt.checkCharSequence3(var5, "\" not found", false, 2, null)) {
               this.runFullUpgradeCheck();
            }

            if (StringsKt.checkCharSequence3(var5, "ALTER command denied to user ", false, 2, null)) {
               this.warnMissingAlterPermission();
               return;
            }

            if (var23 != null) {
               PluginModule var24 = this;
               String var26 = "WARNING: config.yml was not loaded successfully, errors were encountered. Check startup logs for more information.";
               var24.platform.getLogger().severe(var26);
            } else if (Intrinsics.checkObject(var21, "h2") && StringsKt.checkCharSequence3(var5, "The database is read only", false, 2, null)) {
               PluginModule var25 = this;
               String var27 = "[!!!] Database is read-only. This indicates that LiteBans does not have write access to its own database!";
               var25.platform.getLogger().severe(var27);
            }
         }

         if (var6 && !var2.isDebugLevel(1)) {
            return;
         }

         var2.logStackTrace(var5);
      } catch (Throwable var14) {
         PluginModule var3 = this;
         String var4 = "Exception in exception handler";
         var3.platform.getLogger().severe(var4);
         var14.printStackTrace();
         var3 = this;
         var4 = "Original exception:";
         var3.platform.getLogger().severe(var4);
         var1.printStackTrace();
      }
   }

   private final boolean isPoolExhausted() {
      HikariDataSource var10000 = this.getHikariDataSource();
      if (var10000 != null) {
         HikariPoolMXBean var2 = var10000.getHikariPool2();
         if (var2 != null) {
            return var2.getInt2() >= this.getCore().getConfig().getSqlPoolMaxConnections();
         }
      }

      return 0 >= this.getCore().getConfig().getSqlPoolMaxConnections();
   }

   private final void warnIfSslDisabled() {
      if (StringsKt.checkCharSequence3(this.getCore().getConfig().getSqlOptions(), "useSSL=false", false, 2, null)) {
         PluginModule var1 = this;
         String var2 = "Database SSL connection support is disabled. Enabling SSL should fix this error.";
         var1.platform.getLogger().severe(var2);
         var1 = this;
         var2 = "Change \"useSSL=false\" to \"useSSL=true\" in config.yml, and then run \"litebans reload\".";
         var1.platform.getLogger().severe(var2);
      }
   }

   private final void warnMissingAlterPermission() {
      PluginModule var1 = this;
      String var2 = "Your database user ("
         + this.getCore().getConfig().getString3()
         + ") is lacking ALTER permission for the "
         + this.getCore().getConfig().getString5()
         + " database!";
      var1.platform.getLogger().severe(var2);
      var1 = this;
      var2 = "Without this permission, LiteBans cannot upgrade the database.";
      var1.platform.getLogger().severe(var2);
      var1 = this;
      var2 = "Please add the necessary permissions!";
      var1.platform.getLogger().severe(var2);
      var1 = this;
      var2 = "The required permissions are SELECT, CREATE, INSERT, UPDATE, DELETE, ALTER, DROP.";
      var1.platform.getLogger().severe(var2);
   }

   private final void runFullUpgradeCheck() {
      AtomicBoolean var1 = this.atomicBoolean2;
      if (var1.compareAndSet(false, true)) {
         PluginModule var3 = this;
         String var2 = "Executing full upgrade check in response to 'Unknown column' exception.";
         var3.platform.getLogger().warning(var2);
         LiteBansCommand var4 = (LiteBansCommand)this.platform.getCommandRegistry().getCommand("litebans");
         if (var4 != null) {
            var4.handleSender(this.platform.getConsole());
         }
      } else {
         PluginModule var5 = this;
         String var6 = "[!!!] An 'unknown column' exception likely indicates that tables could not be upgraded successfully - consider the following exception as a consequence of a startup error, not the original cause.";
         var5.platform.getLogger().severe(var6);
      }
   }

   private final void reportPoolExhaustion() {
      AsyncQueue var2;
      int var3;
      int var17;
      label23: {
         LiteBansCore var1 = (LiteBansCore)this.platform.getModule(LiteBansCore.class);
         var2 = (AsyncQueue)this.platform.getModule(AsyncQueue.class);
         var3 = var1.getConfig().getSqlPoolMaxConnections();
         HikariDataSource var10000 = this.getHikariDataSource();
         if (var10000 != null) {
            HikariPoolMXBean var16 = var10000.getHikariPool2();
            if (var16 != null) {
               var17 = var16.getInt2();
               break label23;
            }
         }

         var17 = 0;
      }

      int var4 = var17;
      Database var18 = Database.get();
      Intrinsics.handleObject(var18, "");
      int var5 = ((DatabaseImpl)var18).getIntValue();
      Lazy var6 = LazyKt.getLazy(() -> getList(this));
      int var7 = var2.getQueue().size();
      PluginModule var8 = this;
      String var9 = "**Connection pool exhaustion detected** (open=" + var4 + ", max=" + var3 + ", logins=" + var7 + ')';
      var8.platform.getLogger().severe(var9);
      if (var5 > 0) {
         var8 = this;
         var9 = "There have been " + var5 + " API calls to prepareStatement().";
         var8.platform.getLogger().severe(var9);
         var8 = this;
         var9 = "Be aware that plugins which use the LiteBans API incorrectly can create a connection leak, which inevitably leads to exhaustion after "
            + var3
            + " connections have been used.";
         var8.platform.getLogger().severe(var9);
         if (!getList(var6).isEmpty()) {
            var8 = this;
            var9 = "Plugin(s) which may be using LiteBans API: [" + CollectionsKt.getString(getList(var6), ", ", null, null, 0, null, null, 62, null) + ']';
            var8.platform.getLogger().severe(var9);
         }
      }
   }

   @Override
   public void onEnable() {
      Lock var1 = this.reentrantLock;
      var1.lock();

      try {
         this.getCore().onEnable();

         try {
            if (!this.flag2) {
               connectDefault(this, this.getCore().getConfig().getSqlDriver(), 0L, 2, null);
               new PunishmentCheckTask(this.platform).run();
               return;
            }
         } catch (Throwable var6) {
            this.reportError(var6);
            return;
         }
      } finally {
         var1.unlock();
      }
   }

   public final Object loadDriver(@NotNull DriverLibrary var1, boolean var2, boolean var3) {
      LiteBansCore var4 = (LiteBansCore)this.platform.getModule(LiteBansCore.class);
      String var5 = var1.getString();
      String var7 = "Loading: " + var1;
      if (!getAtomicBoolean(this).get()) {
         PluginModule var9 = this;
         var9.platform.getLogger().info(var7);
      }

      if (var4.isDebugEnabled()) {
         StringBuilder var10000 = new StringBuilder().append("> ");
         String var10001;
         if (var1.getString3() == null) {
            var10001 = var1.getString() + ".jar";
         } else {
            int var11 = StringsKt.getInt2(var1.getString3(), '/', 0, false, 6, null);
            var10001 = var11 >= 0 && var11 <= var1.getString3().length() - 1 ? var1.getString3().substring(var11 + 1) : var1.getString() + ".jar";
         }

         var4.debug(var10000.append(var10001).toString());
      }

      File var20 = this.platform.getDataFolder();
      Object var8 = "lib";
      File var6 = FileUtils.getFile3(new File(var20, "lib"));
      String var45;
      if (var1.getString3() == null) {
         var45 = var1.getString() + ".jar";
      } else {
         int var32 = StringsKt.getInt2(var1.getString3(), '/', 0, false, 6, null);
         var45 = var32 >= 0 && var32 <= var1.getString3().length() - 1 ? var1.getString3().substring(var32 + 1) : var1.getString() + ".jar";
      }

      String var23 = var45;
      File var21 = new File(var6, var23);

      try {
         var8 = null;

         try {
            var8 = this.loadDriverJar(var1, var21, var3);
         } catch (ChecksumMismatchException var17) {
            this.reportError(var17);
         }

         if (var8 == null) {
            var23 = var1.getString3();
            if (var23 != null) {
               String var33 = "Download URL for " + var5 + ": " + var23;
               if (!getAtomicBoolean(this).get()) {
                  PluginModule var13 = this;
                  var13.platform.getLogger().info(var33);
               }

               var33 = "Downloading " + var5 + "...";
               if (!getAtomicBoolean(this).get()) {
                  PluginModule var43 = this;
                  var43.platform.getLogger().info(var33);
               }

               try {
                  Updater.handleStringDefault(LiteBansCore.updater, var21.toString(), var23, null, null, 12, null);
               } catch (Exception var16) {
                  PluginModule var35 = this;
                  String var41 = "Failed to download " + var5 + '.';
                  var35.platform.getLogger().severe(var41);
                  this.reportError(var16);
                  return null;
               }

               if (var21.exists()) {
                  var33 = "Downloaded " + var5 + " successfully.";
                  if (!getAtomicBoolean(this).get()) {
                     PluginModule var44 = this;
                     var44.platform.getLogger().info(var33);
                  }
               } else {
                  PluginModule var26 = this;
                  var33 = "Something happened.";
                  var26.platform.getLogger().warning(var33);
               }
            }
         }

         if (var8 == null) {
            try {
               var8 = this.loadDriverJar(var1, var21, var3);
               if (var8 == null) {
                  PluginModule var25 = this;
                  String var31 = "What happened?";
                  var25.platform.getLogger().severe(var31);
                  return null;
               }

               String var30 = "Loaded " + var5 + '!';
               if (!getAtomicBoolean(this).get()) {
                  PluginModule var42 = this;
                  var42.platform.getLogger().info(var30);
               }
            } catch (Throwable var18) {
               PluginModule var27 = this;
               String var38 = "Failed to load " + var1 + '.';
               var27.platform.getLogger().severe(var38);
               this.reportError(var18);
               if (var18 instanceof ZipException && var2) {
                  var27 = this;
                  var38 = "Attempting to recover from this error...";
                  var27.platform.getLogger().severe(var38);
                  if (var21.exists() && var21.delete()) {
                     var27 = this;
                     var38 = "File removed: " + var21;
                     var27.platform.getLogger().severe(var38);
                  }

                  return loadDriverDefault(this, var1, false, false, 4, null);
               }

               return null;
            }
         }

         return var8;
      } catch (Exception var19) {
         String var10 = "Failed to load from " + var21 + ':';
         if (!getAtomicBoolean(this).get()) {
            PluginModule var12 = this;
            var12.platform.getLogger().info(var10);
         }

         this.reportError(var19);
         return null;
      }
   }

   public static Object loadDriverDefault(DatabaseManager var0, DriverLibrary var1, boolean var2, boolean var3, int var4, Object var5) {
      if ((var4 & 2) != 0) {
         var2 = true;
      }

      if ((var4 & 4) != 0) {
         var3 = true;
      }

      return var0.loadDriver(var1, var2, var3);
   }

   public final void cleanupServers(@NotNull PunishmentStorage var1, boolean var2) {
      if (System.currentTimeMillis() - this.atomicLong.get() >= 3000L || var2) {
         ArrayList var3 = new ArrayList();
         Config var4 = this.getCore().getConfig();
         boolean var5 = false;
         Table var6 = Table.SERVERS;
         CharSequence var7 = "*";
         Closeable var33 = var1.prepare(SqlString.getSqlString(SqlString.getString2("SELECT " + var7 + " FROM " + var6)));
         Throwable var35 = null;

         try {
            QueryStatement var8 = (QueryStatement)var33;
            AutoCloseable var10 = this.execute(var8);
            Throwable var11 = null;

            try {
               ResultSet var12 = (ResultSet)var10;

               while (var12.next()) {
                  CommandAlias var14 = new CommandAlias(KotlinVersion.getInt2((int)var12.getLong("id")), var12.getString("name"), var12.getString("uuid"), null);
                  var3.add(var14);
                  if (!var5 && Intrinsics.checkObject(var14.getString2(), var4.getString())) {
                     var5 = true;
                     this.commandAlias = var14;
                     if (Integer.compareUnsigned(var14.getIntValue(), 65000) >= 0) {
                        int var16 = var14.getIntValue();
                        SyncManager var18 = (SyncManager)this.platform.getModule(SyncManager.class);
                        PluginModule var19 = this;
                        String var20 = "[Sync] Server limit reached (" + KotlinVersion.getString(var16) + " servers). Resetting server information.";
                        var19.platform.getLogger().severe(var20);
                        Table var37 = Table.SERVERS;
                        QueryRunner.getInt(var1.prepare(SqlString.getSqlString(SqlString.getString2("DROP TABLE " + var37))));
                        createTablesDefault(this, 0L, false, false, false, 9, null);
                        SyncManager.publishDefault(var18, Flag.SERVERS_RELOAD, var1, null, 65535, 4, null);
                        this.platform.runAsyncLater(new ServerResetTask(this), this.getCore().getConfig().getSyncPollTicks() * (long)3);
                        return;
                     }
                  }
               }
            } catch (Throwable var29) {
               var11 = var29;
               throw var29;
            } finally {
               AutoCloseableKt.handleAutoCloseable(var10, var11);
            }
         } catch (Throwable var31) {
            var35 = var31;
            throw new RuntimeException(var31);
         } finally {
            CloseableKt.handleCloseable(var33, var35);
         }

         this.hashMap.clear();

         for (Object var36o : var3) {
            CommandAlias var36 = (CommandAlias) var36o;
            this.hashMap.put(var36.getIntValue(), var36);
         }

         this.atomicLong.set(System.currentTimeMillis());
      }
   }

   public static void cleanupServersDefault(DatabaseManager var0, PunishmentStorage var1, boolean var2, int var3, Object var4) {
      if ((var3 & 2) != 0) {
         var2 = false;
      }

      var0.cleanupServers(var1, var2);
   }

   public final PunishmentStorage openStorage() {
      if (this.platform.isPrimaryThread() && this.platform.isFullyEnabled()) {
         throw new AssertionError("Main server thread is not permitted to perform database queries.");
      } else {
         Connection var1 = this.getConnection();
         return var1 != null && this.isConnected() && !this.flag2 ? new SqlStorage(this.platform, var1) : new NullStorage(this.platform);
      }
   }

   public final void runWithStorageAsync(@NotNull Function1 var1) {
      Sender var10000 = (Sender)CommandRunnable.messageSender.getThreadLocal().get();
      if (var10000 == null) {
         var10000 = this.platform.getConsole();
      }

      Sender var2 = var10000;
      this.platform.execute(() -> handleSender(var2, this, var1));
   }

   public final boolean isConnectionError(@NotNull Throwable var1) {
      String var10000 = var1.getMessage();
      if (var10000 == null) {
         var10000 = "";
      }

      String var2 = var10000;
      LiteBansPlatform var3 = this.platform;
      return (!var3.isEnabled() || var3.isDisabling())
         && (
            StringsKt.checkCharSequence3(var2, "Interrupted during connection", false, 2, null)
               || StringsKt.checkCharSequence3(var2, "SQLSTATE(08S01)", false, 2, null)
               || StringsKt.checkCharSequence3(var2, "Socket closed", false, 2, null)
               || StringsKt.checkCharSequence3(var2, "Connection is closed", false, 2, null)
               || StringsKt.checkCharSequence3(var2, "closed connection", false, 2, null)
         );
   }

   public final void createTables(long var1, boolean var3, boolean var4, boolean var5) {
      Config var6 = this.getCore().getConfig();
      Connection var10000 = this.getConnection();
      if (var10000 != null) {
         AutoCloseable var7 = var10000;
         Throwable var8 = null;

         try {
            Connection var9 = (Connection)var7;
            AutoCloseable var11 = var9.createStatement();
            Throwable var12 = null;

            try {
               Statement var13 = (Statement)var11;
               Closeable var15 = new SqlStorage(this.platform, var9);
               Throwable var16 = null;

               try {
                  SqlStorage var17 = (SqlStorage)var15;
                  TableCreator var21 = var17.getTableCreator();
                  boolean var22 = databaseDriver.checkString2(var6.getSqlDriver());
                  DatabaseManager var23 = this;
                  SqlStorage var24 = var17;
                  Intrinsics.handleObject(var13);
                  Statement var25 = var13;
                  Statement var27 = var13;
                  if (var3) {
                     Iterable var29 = Table.Companion.getList();
                     Collection var32 = new ArrayList(CollectionsKt.getInt(var29, 10));

                     for (Object var35 : var29) {
                        Table var36 = (Table)var35;
                        var32.add(var24.getCreateTableSql(var36, var21));
                     }

                     for (Object var80 : (List)var32) {
                        CharSequence var33 = (CharSequence)var80;
                        var27.addBatch(var33.toString());
                     }
                  }

                  if (var4) {
                     byte var30 = var24.getLiteBansCore().getConfig().getUsernameMaxLength();
                     Table var10001 = Table.HISTORY;
                     String[] var81 = new String[6];
                     String var102 = "id";
                     var81[0] = Intrinsics.checkObject(var21.getSqlColumnFormat().getString(), "sqlite")
                        ? var102 + " INTEGER PRIMARY KEY AUTOINCREMENT"
                        : var102 + " SERIAL";
                     String var87 = "date";
                     var102 = " DEFAULT CURRENT_TIMESTAMP";
                     var81[1] = var87 + " TIMESTAMP" + var102;
                     var87 = "name";
                     int var104 = var30 & 255;
                     String var38 = "";
                     String var39 = var21.getSqlColumnFormat().getCharset();
                     var81[2] = databaseDriver.checkString(var21.getSqlColumnFormat().getString())
                        ? var87 + " VARCHAR(" + var104 + ')' + var39 + var38
                        : var87 + " VARCHAR(" + var104 + ')' + var38;
                     var87 = "uuid";
                     byte var105 = 36;
                     var38 = "";
                     var39 = var21.getSqlColumnFormat().getCharset();
                     var81[3] = databaseDriver.checkString(var21.getSqlColumnFormat().getString())
                        ? var87 + " VARCHAR(" + var105 + ')' + var39 + var38
                        : var87 + " VARCHAR(" + var105 + ')' + var38;
                     var87 = "ip";
                     byte var106 = 45;
                     var38 = "";
                     var39 = var21.getSqlColumnFormat().getCharset();
                     var81[4] = databaseDriver.checkString(var21.getSqlColumnFormat().getString())
                        ? var87 + " VARCHAR(" + var106 + ')' + var39 + var38
                        : var87 + " VARCHAR(" + var106 + ')' + var38;
                     var102 = "id";
                     var81[5] = "PRIMARY KEY(" + var102 + ')';
                     SqlString var74 = SqlString.getSqlString(var21.getString(var10001, var81));
                     var27.addBatch(var74.toString());
                  }

                  if (var5 && var22) {
                     Table var159 = Table.SERVERS;
                     String[] var82 = new String[5];
                     String var108 = "id";
                     var82[0] = Intrinsics.checkObject(var21.getSqlColumnFormat().getString(), "sqlite")
                        ? var108 + " INTEGER PRIMARY KEY AUTOINCREMENT"
                        : var108 + " SERIAL";
                     String var91 = "name";
                     byte var109 = 32;
                     String var133 = "";
                     String var143 = var21.getSqlColumnFormat().getCharset();
                     String var42 = var133 + " NOT NULL";
                     var82[1] = databaseDriver.checkString(var21.getSqlColumnFormat().getString())
                        ? var91 + " VARCHAR(" + var109 + ')' + var143 + var42
                        : var91 + " VARCHAR(" + var109 + ')' + var42;
                     var91 = "uuid";
                     byte var110 = 32;
                     var133 = "";
                     var143 = var21.getSqlColumnFormat().getCharset();
                     var42 = var133 + " NOT NULL";
                     var82[2] = databaseDriver.checkString(var21.getSqlColumnFormat().getString())
                        ? var91 + " VARCHAR(" + var110 + ')' + var143 + var42
                        : var91 + " VARCHAR(" + var110 + ')' + var42;
                     var91 = "date";
                     var108 = " DEFAULT CURRENT_TIMESTAMP";
                     var82[3] = var91 + " TIMESTAMP" + var108;
                     var108 = "id";
                     var82[4] = "PRIMARY KEY(" + var108 + ')';
                     SqlString var75 = SqlString.getSqlString(var21.getString(var159, var82));
                     var27.addBatch(var75.toString());
                     var159 = Table.SYNC;
                     var82 = new String[5];
                     var108 = "id";
                     var82[0] = Intrinsics.checkObject(var21.getSqlColumnFormat().getString(), "sqlite")
                        ? var108 + " INTEGER PRIMARY KEY AUTOINCREMENT"
                        : var108 + " SERIAL";
                     var108 = "info";
                     var82[1] = var108 + " INT" + var21.getSqlColumnFormat().getUnsigned() + " NOT NULL";
                     var91 = "msg";
                     short var115 = 4096;
                     var133 = "";
                     var143 = var21.getSqlColumnFormat().getCharset();
                     var42 = var133 + " NOT NULL";
                     var82[2] = databaseDriver.checkString(var21.getSqlColumnFormat().getString())
                        ? var91 + " VARCHAR(" + var115 + ')' + var143 + var42
                        : var91 + " VARCHAR(" + var115 + ')' + var42;
                     var91 = "time";
                     var108 = " DEFAULT CURRENT_TIMESTAMP";
                     var82[3] = var91 + " TIMESTAMP" + var108;
                     var108 = "id";
                     var82[4] = "PRIMARY KEY(" + var108 + ')';
                     var75 = SqlString.getSqlString(var21.getString(var159, var82));
                     var27.addBatch(var75.toString());
                  }

                  Table var161 = Table.CACHE8K;
                  String[] var84 = new String[3];
                  String var96 = "id";
                  String var118 = "";
                  String var146 = var21.getSqlColumnFormat().getString();
                  var84[0] = !Intrinsics.checkObject(var146, "pgsql") && !Intrinsics.checkObject(var146, "h2")
                     ? var96 + " TINYINT" + var21.getSqlColumnFormat().getUnsigned() + " NOT NULL" + var118
                     : var96 + " SMALLINT NOT NULL" + var118;
                  var118 = "b";
                  short var136 = 8192;
                  String var10004;
                  if (Intrinsics.checkObject(var21.getSqlColumnFormat().getString(), "pgsql")) {
                     var10004 = var118 + " BYTEA NOT NULL";
                  } else {
                     String var40 = "BLOB";
                     var10004 = var118 + ' ' + var40 + '(' + var136 + ")NOT NULL";
                  }

                  var84[1] = var10004;
                  var118 = "id";
                  var84[2] = "PRIMARY KEY(" + var118 + ')';
                  SqlString var77 = SqlString.getSqlString(var21.getString(var161, var84));
                  var27.addBatch(var77.toString());
                  var161 = Table.CONFIG;
                  var84 = new String[6];
                  var118 = "id";
                  var84[0] = Intrinsics.checkObject(var21.getSqlColumnFormat().getString(), "sqlite")
                     ? var118 + " INTEGER PRIMARY KEY AUTOINCREMENT"
                     : var118 + " SERIAL";
                  var96 = "version";
                  short var122 = 128;
                  String var137 = "";
                  var146 = var21.getSqlColumnFormat().getCharset();
                  String var155 = var137 + " NOT NULL";
                  var84[1] = databaseDriver.checkString(var21.getSqlColumnFormat().getString())
                     ? var96 + " VARCHAR(" + var122 + ')' + var146 + var155
                     : var96 + " VARCHAR(" + var122 + ')' + var155;
                  var96 = "build";
                  short var123 = 128;
                  String var138 = "";
                  var146 = var21.getSqlColumnFormat().getCharset();
                  var155 = var138 + " NOT NULL";
                  var84[2] = databaseDriver.checkString(var21.getSqlColumnFormat().getString())
                     ? var96 + " VARCHAR(" + var123 + ')' + var146 + var155
                     : var96 + " VARCHAR(" + var123 + ')' + var155;
                  var96 = "timezone";
                  byte var124 = 64;
                  String var139 = " DEFAULT '+00:00'";
                  var146 = var21.getSqlColumnFormat().getCharset();
                  var155 = var139 + " NOT NULL";
                  var84[3] = databaseDriver.checkString(var21.getSqlColumnFormat().getString())
                     ? var96 + " VARCHAR(" + var124 + ')' + var146 + var155
                     : var96 + " VARCHAR(" + var124 + ')' + var155;
                  var118 = "accept";
                  String var140 = " DEFAULT 0";
                  String var152 = var21.getSqlColumnFormat().getString();
                  var84[4] = !Intrinsics.checkObject(var152, "pgsql") && !Intrinsics.checkObject(var152, "h2")
                     ? var118 + " TINYINT" + var21.getSqlColumnFormat().getUnsigned() + " NOT NULL" + var140
                     : var118 + " SMALLINT NOT NULL" + var140;
                  var118 = "id";
                  var84[5] = "PRIMARY KEY(" + var118 + ')';
                  var77 = SqlString.getSqlString(var21.getString(var161, var84));
                  var27.addBatch(var77.toString());
                  var161 = Table.ALLOW;
                  var84 = new String[4];
                  var118 = "id";
                  var84[0] = Intrinsics.checkObject(var21.getSqlColumnFormat().getString(), "sqlite")
                     ? var118 + " INTEGER PRIMARY KEY AUTOINCREMENT"
                     : var118 + " SERIAL";
                  var96 = "uuid";
                  byte var128 = 16;
                  if (Intrinsics.checkObject(var21.getSqlColumnFormat().getString(), "pgsql")) {
                     var10004 = var96 + " BYTEA NOT NULL";
                  } else {
                     var146 = "BINARY";
                     var10004 = var96 + ' ' + var146 + '(' + var128 + ")NOT NULL";
                  }

                  var84[1] = var10004;
                  var96 = "type";
                  var118 = "";
                  var146 = var21.getSqlColumnFormat().getString();
                  var84[2] = !Intrinsics.checkObject(var146, "pgsql") && !Intrinsics.checkObject(var146, "h2")
                     ? var96 + " TINYINT" + var21.getSqlColumnFormat().getUnsigned() + " NOT NULL" + var118
                     : var96 + " SMALLINT NOT NULL" + var118;
                  var118 = "id";
                  var84[3] = "PRIMARY KEY(" + var118 + ')';
                  var77 = SqlString.getSqlString(var21.getString(var161, var84));
                  var27.addBatch(var77.toString());

                  try {
                     var23.executeBatch(var25);
                  } catch (Exception var64) {
                     this.reportError(var64);
                  }

                  boolean var72 = checkTablesDefault(this, var17, var21, false, false, 12, null);
                  this.run9();
                  var17.getPlatform().runAsyncLater(() -> logDatabaseInfo(this, true), 200L);
                  this.commandAlias = null;
                  if (var22 && var6.isSync()) {
                     updateServerInfoDefault(this, var17, 0L, true, 2, null);
                  }

                  String var19 = DecimalFormats.getString((double)(System.nanoTime() - var1) / 1000.0 / 1000.0);
                  PluginModule var20 = this;
                  String var71 = "Database connection fully initialized (" + var19 + " ms).";
                  var20.platform.getLogger().info(var71);
               } catch (Throwable var65) {
                  var16 = var65;
                  throw var65;
               } finally {
                  CloseableKt.handleCloseable(var15, var16);
               }
            } catch (Throwable var67) {
               var12 = var67;
               throw var67;
            } finally {
               AutoCloseableKt.handleAutoCloseable(var11, var12);
            }
         } catch (Throwable var69) {
            var8 = var69;
            throw new RuntimeException(var69);
         } finally {
            AutoCloseableKt.handleAutoCloseable(var7, var8);
         }
      } else {
         var10000 = null;
      }
   }

   public static void createTablesDefault(DatabaseManager var0, long var1, boolean var3, boolean var4, boolean var5, int var6, Object var7) {
      if ((var6 & 1) != 0) {
         var1 = System.nanoTime();
      }

      if ((var6 & 2) != 0) {
         var3 = true;
      }

      if ((var6 & 4) != 0) {
         var4 = true;
      }

      if ((var6 & 8) != 0) {
         var5 = true;
      }

      var0.createTables(var1, var3, var4, var5);
   }

   public final long currentTimeMillis(boolean var1) {
      long var2 = System.currentTimeMillis();
      if (var1) {
         return var2;
      } else {
         long var4 = this.getCore().getConfig().getDatabaseTimeOffset();
         if (this.getCore().getConfig().isUseDatabaseTime()) {
            TimeZone var6 = TimeZone.getTimeZone(this.string);
            Calendar var7 = Calendar.getInstance(var6);
            long var8 = var7.getTimeInMillis();
            return var4 + var8 + (long)var6.getOffset(var8);
         } else {
            return var2 + var4;
         }
      }
   }

   public static long currentTimeMillisDefault(DatabaseManager var0, boolean var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = false;
      }

      return var0.currentTimeMillis(var1);
   }

   public final void updateServerInfo(@NotNull PunishmentStorage var1, long var2, boolean var4) {
      Config var5 = this.getCore().getConfig();
      String var6 = var5.getString2();
      String var7 = var5.getString();
      Table var9 = Table.SERVERS;
      CharSequence var10 = "*";
      String var29 = SqlString.getString2("SELECT " + var10 + " FROM " + var9);
      var10 = "uuid=?";
      CharSequence[] var12 = new CharSequence[]{SqlString.getSqlString(var29), var10};
      CharSequence[] var14 = var12;
      int var16 = 0;

      for (int var17 = var12.length; var16 < var17; var16++) {
         CharSequence var18 = var14[var16];
         if (var18.length() <= 0) {
            String var21 = "Failed requirement.";
            throw new IllegalArgumentException(var21.toString());
         }
      }

      QueryStatement var30 = var1.prepare(SqlString.getSqlString(SqlString.getString2(SqlString.getString(var29) + " WHERE " + var10)));
      var30.getQueryStatement(var7);
      ResultSet var8 = QueryRunner.getResultSet(var30);

      try {
         if (!var8.next()) {
            var9 = Table.SERVERS;
            var10 = "name,uuid";
            byte var46 = 44;
            CharSequence var57 = var10;
            int var64 = 0;

            for (int var68 = 0; var68 < var57.length(); var68++) {
               char var73 = var57.charAt(var68);
               if (var73 == var46) {
                  var64++;
               }
            }

            String var44 = "?";
            if (var64 > 0) {
               StringBuilder var47 = new StringBuilder(var64 * 2);
               Iterable var52 = new IntRange(0, var64);
               Iterator var60 = var52.iterator();

               while (var60.hasNext()) {
                  ((IntIterator)var60).getInt();
                  var47.append("?,");
               }

               var44 = StringsKt.getString(var47.toString(), 1);
            }

            QueryStatement var33 = var1.prepare(SqlString.getSqlString(SqlString.getString2("INSERT INTO " + var9 + '(' + var10 + ")VALUES(" + var44 + ')')));
            String[] var40 = new String[]{var6, var7};
            String[] var58 = var40;
            var64 = 0;

            for (int var69 = var40.length; var64 < var69; var64++) {
               String var74 = var58[var64];
               var33.getQueryStatement(var74);
            }

            QueryRunner.getResultSet(var33);
         } else {
            String var31 = var8.getString("name");
            if (!Intrinsics.checkObject(var6, var31)) {
               this.getCore().debug("[Sync] Updating stored server name: \"" + var31 + "\" -> \"" + var6 + '"');
               Table var35 = Table.SERVERS;
               String var36 = SqlString.getString4(SqlString.getString2("UPDATE " + var35), "name=?");
               CharSequence var11 = "uuid=?";
               CharSequence[] var13 = new CharSequence[]{SqlString.getSqlString(var36), var11};
               CharSequence[] var15 = var13;
               int var61 = 0;

               for (int var66 = var13.length; var61 < var66; var61++) {
                  CharSequence var19 = var15[var61];
                  if (var19.length() <= 0) {
                     String var22 = "Failed requirement.";
                     throw new IllegalArgumentException(var22.toString());
                  }
               }

               QueryStatement var37 = var1.prepare(SqlString.getSqlString(SqlString.getString2(SqlString.getString(var36) + " WHERE " + var11)));
               String[] var41 = new String[]{var6, var7};
               String[] var59 = var41;
               int var67 = 0;

               for (int var70 = var41.length; var67 < var70; var67++) {
                  String var20 = var59[var67];
                  var37.getQueryStatement(var20);
               }

               QueryRunner.getResultSet(var37);
               Table[] var38 = Table.values();
               int var42 = 0;

               for (int var43 = var38.length; var42 < var43; var42++) {
                  Table var45 = var38[var42];
                  if (var45.check()) {
                     String var48 = SqlString.getString4(SqlString.getString2("UPDATE " + var45), "server_scope=?");
                     CharSequence var53 = "server_scope=?";
                     CharSequence[] var62 = new CharSequence[]{SqlString.getSqlString(var48), var53};
                     CharSequence[] var71 = var62;
                     int var77 = 0;

                     for (int var79 = var62.length; var77 < var79; var77++) {
                        CharSequence var23 = var71[var77];
                        if (var23.length() <= 0) {
                           String var26 = "Failed requirement.";
                           throw new IllegalArgumentException(var26.toString());
                        }
                     }

                     QueryStatement var49 = var1.prepare(SqlString.getSqlString(SqlString.getString2(SqlString.getString(var48) + " WHERE " + var53)));
                     String[] var54 = new String[]{var6, var31};
                     String[] var75 = var54;
                     int var80 = 0;

                     for (int var83 = var54.length; var80 < var83; var80++) {
                        String var24 = var75[var80];
                        var49.getQueryStatement(var24);
                     }

                     QueryRunner.getResultSet(var49);
                     String var50 = SqlString.getString4(SqlString.getString2("UPDATE " + var45), "server_origin=?");
                     CharSequence var55 = "server_origin=?";
                     CharSequence[] var63 = new CharSequence[]{SqlString.getSqlString(var50), var55};
                     var71 = var63;
                     var77 = 0;

                     for (int var81 = var63.length; var77 < var81; var77++) {
                        CharSequence var84 = var71[var77];
                        if (var84.length() <= 0) {
                           String var87 = "Failed requirement.";
                           throw new IllegalArgumentException(var87.toString());
                        }
                     }

                     QueryStatement var51 = var1.prepare(SqlString.getSqlString(SqlString.getString2(SqlString.getString(var50) + " WHERE " + var55)));
                     String[] var56 = new String[]{var6, var31};
                     var75 = var56;
                     var80 = 0;

                     for (int var85 = var56.length; var80 < var85; var80++) {
                        String var86 = var75[var80];
                        var51.getQueryStatement(var86);
                     }

                     QueryRunner.getResultSet(var51);
                  }
               }
            }
         }

         if (var2 <= 0L) {
            this.cleanupServers(var1, var4);
         } else {
            this.platform.runAsyncLater(() -> cleanupServers(var1, var4), var2);
         }
      } catch (Exception var28) {
         if (var28 instanceof SQLException) {
            this.reportError(var28);
         } else {
            LiteBansCore.handleObjectDefault(this.getCore(), var28, 0, 2, null);
         }
      }
   }

   public static void updateServerInfoDefault(DatabaseManager var0, PunishmentStorage var1, long var2, boolean var4, int var5, Object var6) {
      if ((var5 & 2) != 0) {
         var2 = 0L;
      }

      if ((var5 & 4) != 0) {
         var4 = false;
      }

      var0.updateServerInfo(var1, var2, var4);
   }

   private final void connect(String var1, long var2) {
      boolean var4 = false;
      if (Intrinsics.checkObject(var1, "sqlite") && this.flag) {
         var4 = SqlDateFormat.sqliteToH2Converter.checkDatabaseManager(this);
      }

      if (!var4) {
         this.createDataSource(this.getCore());
         createTablesDefault(this, var2, false, false, false, 14, null);
      }
   }

   public static void connectDefault(DatabaseManager var0, String var1, long var2, int var4, Object var5) {
      if ((var4 & 2) != 0) {
         var2 = System.nanoTime();
      }

      var0.connect(var1, var2);
   }

   public final void createDataSource(@NotNull LiteBansCore var1) {
      long var2 = System.nanoTime();
      this.hikariDataSource = null;
      Config var4 = var1.getConfig();
      String var5 = var4.getSqlDriver();
      List var6 = this.getDriverLibraries(var1);
      DriverLibrary var7 = this.findDriverLibrary(var1, var5);
      if (var7 == null) {
         PluginModule var45 = this;
         StringBuilder var116 = new StringBuilder().append("Unknown SQL driver '").append(var5).append("'. Please use one of these drivers: ");
         Iterable var47 = var6;
         StringBuilder var40 = var116;
         Collection var51 = new ArrayList(CollectionsKt.getInt(var47, 10));

         for (Object var56 : var47) {
            DriverLibrary var16 = (DriverLibrary)var56;
            var51.add(var16.getString());
         }

         String var48 = var40.append((List)var51).toString();
         var45.platform.getLogger().severe(var48);
      } else {
         Throwable var10000 = null; Object driverInstance = loadDriverDefault(this, var7, false, false, 6, null);
         if (driverInstance != null) {
            Object var8 = driverInstance;
            String var9 = var4.getSqlAddress();
            String[] var11 = new String[]{"h2", "sqlite"};
            boolean var10 = SetsKt.getSet(var11).contains(var5);
            if (!StringsKt.checkCharSequence3(var9, ':', false, 2, null)) {
               String var49 = this.getCore().getConfig().getSqlDriver();
               var9 = databaseDriver.checkString(var49)
                  ? var9 + ":3306"
                  : (Intrinsics.checkObject(var49, "pgsql") || Intrinsics.checkObject(var49, "postgresql") ? var9 + ":5432" : var9);
            }

            switch (var5.hashCode()) {
               case -894935028:
                  if (var5.equals("sqlite")) {
                     File var52 = this.platform.getDataFolder();
                     String var53 = "litebans.sqlite";
                     var9 = new File(var52, var53).getPath();
                     this.statementExecutor = new LockingStatementExecutor(this.platform);
                  }
                  break;
               case 3274:
                  if (var5.equals("h2")) {
                     File var13 = this.platform.getDataFolder();
                     String var14 = "litebans";
                     var9 = new File(var13, var14).getAbsolutePath();
                  }
                  break;
               case 106616951:
                  if (var5.equals("pgsql")) {
                     var5 = "postgresql";
                  }
            }

            var9 = this.statementExecutor.getString(var9);
            String var50 = "jdbc:" + var5 + "://" + var9;
            if (!var10) {
               var50 = var50 + '/' + var4.getString5();
            }

            String var12 = var4.getSqlOptions();
            if (var12.length() > 0) {
               if (!var10 && !StringsKt.checkCharSequence(var12, '?', false, 2, null)) {
                  var12 = '?' + var12;
               }

               if ((
                     !Intrinsics.checkObject(var5, "h2")
                        || !Intrinsics.checkObject(var12, "useSSL=false") && !Intrinsics.checkObject(var12, "useSSL=false&serverTimezone=UTC")
                  )
                  && !Intrinsics.checkObject(var5, "sqlite")) {
                  var50 = var50 + var12;
               }
            }

            String var54 = StringsKt.getString(var50, "?useSSL=", ";useSSL=", false, 4, null);
            if (Intrinsics.checkObject(var5, "h2")) {
               var50 = var50 + ";TRACE_LEVEL_FILE=0;TRACE_LEVEL_SYSTEM_OUT=0";
            }

            long var15 = Math.max(2000L, var4.getSqlPoolTimeout());
            boolean var17 = checkTablesDefault(this, new NullStorage(this.platform), null, false, false, 12, null);
            long var18 = var17 ? var15 : 2147483647L;
            if (Intrinsics.checkObject(var5, "pgsql") || Intrinsics.checkObject(var5, "postgresql")) {
               var18 = Math.min(var18, 2147483L);
            }

            if (var1.isDebugEnabled()) {
               var1.debug(var50);
            }

            if (var1.isDebugEnabled()) {
               var1.debug(this.platform.createBridge().getServerVersion());
            }

            String var21 = "Connecting to database...";
            if (!getAtomicBoolean(this).get()) {
               PluginModule var23 = this;
               var23.platform.getLogger().info(var21);
            }

            try {
               HikariLogger.handleLogger(this.platform.getLogger());
               HikariConfig var20 = new HikariConfig();
               var20.handleString4("litebans-pool");
               var20.handleString3(var50);
               String var66 = "user";
               String var82 = var4.getString3();
               var20.handleString(var66, var82);
               var66 = "password";
               var82 = var4.getString4();
               var20.handleString(var66, var82);
               var66 = "connectTimeout";
               Number var84 = var15;
               String var97 = var84.toString();
               var20.handleString(var66, var97);
               var66 = "socketTimeout";
               Number var85 = var18;
               var97 = var85.toString();
               var20.handleString(var66, var97);
               var66 = "timezone";
               var82 = "UTC";
               var20.handleString(var66, var82);
               var66 = "utf-8";
               var82 = "utf8mb4_unicode_ci";
               String var99 = "useUnicode";
               String var105 = "true";
               var20.handleString(var99, var105);
               var99 = "characterEncoding";
               var20.handleString(var99, var66);
               var99 = "characterSetResults";
               var20.handleString(var99, var66);
               var99 = "connectionCollation";
               var20.handleString(var99, var82);
               var66 = "cachePrepStmts";
               String var94 = "true";
               var20.handleString(var66, var94);
               HikariConfig var61 = var20;
               String[] var73 = new String[]{
                  "useServerPrepStmts",
                  "cacheResultSetMetadata",
                  "cacheServerConfiguration",
                  "elideSetAutoCommits",
                  "useLocalSessionState",
                  "useLocalTransactionState"
               };
               String[] var103 = var73;
               int var29 = 0;

               for (int var30 = var73.length; var29 < var30; var29++) {
                  String var31 = var103[var29];
                  String var35 = "true";
                  var61.handleString(var31, var35);
               }

               HikariConfig var62 = var20;
               String[] var74 = new String[]{"maintainTimeStats", "alwaysSendSetIsolation"};
               String[] var104 = var74;
               var29 = 0;

               for (int var107 = var74.length; var29 < var107; var29++) {
                  String var108 = var104[var29];
                  String var109 = "false";
                  var62.handleString(var108, var109);
               }

               var20.handleLong(var15);
               var20.handleLong4(Math.min(30000L, var15));
               if (var4.getSqlPoolMinConnections() != var4.getSqlPoolMaxConnections()) {
                  var20.handleLong3(var4.getSqlPoolIdleTimeout());
               }

               var20.handleInt(Math.max(0, var4.getSqlPoolMinConnections()));
               var20.handleInt2(Math.max(1, var4.getSqlPoolMaxConnections()));
               var20.setLongValue13(Math.max(0L, var4.getSqlPoolKeepalive()));
               ThreadPoolExecutor var88 = this.platform.getExecutor();
               int var95 = Math.max(20, var20.getIntValue2() + 8);
               var88.setMaximumPoolSize(var95);
               var88.setRejectedExecutionHandler(new CustomDiscardPolicy(this));
               var88.setCorePoolSize(Math.min(var95, var20.getIntValue3() + 1));
               var88.setKeepAliveTime(4L, TimeUnit.MINUTES);
               if (Intrinsics.checkObject(var5, "sqlite")) {
                  var20.handleString("SELECT 1");
               } else if (Intrinsics.checkObject(var5, "h2")) {
                  var20.handleString2("SET IGNORECASE TRUE");
               }

               var20.handleDataSource(new DriverManagerDataSource((Driver)var8, var20));
               this.hikariDataSource = new HikariDataSource(var20);
            } catch (Exception var44) {
               PluginModule var58 = this;
               String var22 = "Failed to connect to [" + var54 + "].";
               var58.platform.getLogger().severe(var22);
               if (!Intrinsics.checkObject(var5, "h2") && StringsKt.checkCharSequence(var4.getString3()) && StringsKt.checkCharSequence(var4.getString4())) {
                  PluginModule var59 = this;
                  var22 = "[!!] No username and password has been provided in the configuration!";
                  var59.platform.getLogger().severe(var22);
               }

               var21 = var44.getMessage();
               var10000 = var44.getCause();
               var22 = var10000 != null ? var10000.getMessage() : null;
               if (var22 == null || var21 == null || !StringsKt.checkCharSequence3(var21, var22, false, 2, null)) {
                  PluginModule var80 = this;
                  String var24 = "Error: " + var44.getMessage();
                  var80.platform.getLogger().severe(var24);
               }

               String var81 = "";
               Throwable var91 = var44;
               int var25 = 16;

               while (var25-- > 0) {
                  var10000 = var91.getCause();
                  if (var10000 == null) {
                     break;
                  }

                  var91 = var10000;
                  String var113 = var91.getLocalizedMessage();
                  if (var113 == null) {
                     var113 = "";
                  }

                  String var26 = var113;
                  if (var26.length() <= 0
                     || !StringsKt.checkCharSequence3(var26, "Connection is not available, request timed out", false, 2, null)
                        && !StringsKt.checkCharSequence3(var26, "The driver has not received any packets from the server.", false, 2, null)) {
                     if (var26.length() == 0) {
                        String var114 = var91.getMessage();
                        if (var114 == null) {
                           var114 = "";
                        }

                        var26 = var114;
                     }

                     if (var91 instanceof UnknownHostException) {
                        var26 = "Unknown host: " + var26;
                     }

                     if (!Intrinsics.checkObject(var26, "Communications link failure")
                        && var26.length() > 0
                        && !StringsKt.checkCharSequence3(var81, var26, false, 2, null)) {
                        PluginModule var27 = this;
                        String var28 = "Error: " + var26;
                        var27.platform.getLogger().severe(var28);
                     }

                     String var115 = var91.getMessage();
                     if (var115 == null) {
                        var115 = "";
                     }

                     var26 = var115;
                     if (var26.length() > 0) {
                        if (StringsKt.checkString(var26, "Access denied for user", false, 2, null)) {
                           this.explainAccessDenied(var4);
                           break;
                        }

                        if (StringsKt.checkCharSequence3(var26, "Public Key Retrieval is not allowed", false, 2, null)) {
                           this.warnIfSslDisabled();
                           break;
                        }

                        var81 = var26;
                     }
                  }
               }

               if (var1.isDebugLevel(1)) {
                  var44.printStackTrace();
               }

               return;
            }

            String var57 = DecimalFormats.getString((double)(System.nanoTime() - var2) / 1000.0 / 1000.0);
            if (var1.isDebugLevel(1)) {
               String var75 = "Connected to [" + var54 + "] successfully (" + var57 + " ms).";
               if (!getAtomicBoolean(this).get()) {
                  PluginModule var92 = this;
                  var92.platform.getLogger().info(var75);
               }
            } else {
               String var76 = "Connected to " + databaseDriver.getString(var5) + " database successfully (" + var57 + " ms).";
               if (!getAtomicBoolean(this).get()) {
                  PluginModule var93 = this;
                  var93.platform.getLogger().info(var76);
               }
            }

            this.atomicBoolean.set(true);
            if (Intrinsics.checkObject(var5, "h2")) {
               try {
                  File var77 = this.platform.getDataFolder();
                  String var89 = "h2.yml";
                  File var63 = new File(var77, var89);
                  if (!var63.exists() || GenericConfigFile.getConfigFormat3() == null) {
                     var77 = this.platform.getDataFolder();
                     var89 = "h2.json";
                     var63 = new File(var77, var89);
                  }

                  ConfigFile var79 = this.platform.loadConfig(var63).getConfigFile4();
                  var79.getConfigFile("user", var4.getString3()).getConfigFile("password", var4.getString4()).getConfigFile2();
               } catch (Exception var43) {
                  var43.printStackTrace();
               }
            }
         }
      }
   }

   private final void explainAccessDenied(Config var1) {
      PluginModule var2 = this;
      String var3 = "[!] Access to the database \"" + var1.getString5() + "\" was denied.";
      var2.platform.getLogger().severe(var3);
      var2 = this;
      var3 = "[!] Ensure that the database \"" + var1.getString5() + "\" has been created and that you have granted permission to access this database.";
      var2.platform.getLogger().severe(var3);
      if (Intrinsics.checkObject(var1.getString5(), "litebans")) {
         var2 = this;
         var3 = "[!!] Your database is named \"litebans\"; this is the default database name. Ensure that you have defined the correct database name.";
         var2.platform.getLogger().severe(var3);
      }
   }

   public final DriverLibrary findDriverLibrary(@NotNull LiteBansCore var1, @NotNull String var2) {
      Iterable var3 = this.getDriverLibraries(var1);
      Iterator var4 = var3.iterator();

      Object var10000;
      while (true) {
         if (var4.hasNext()) {
            Object var5 = var4.next();
            DriverLibrary var6 = (DriverLibrary)var5;
            if (!Intrinsics.checkObject(var6.getString(), var2)) {
               continue;
            }

            var10000 = var5;
            break;
         }

         var10000 = null;
         break;
      }

      return (DriverLibrary)var10000;
   }

   private final void createIndexes(String[] var1, Statement var2, Table var3, String var4) {
      String var5 = "CREATE INDEX IF NOT EXISTS";
      if (StringsKt.checkString(var4, "5.", false, 2, null)
         || StringsKt.checkString(var4, "8.", false, 2, null)
         || StringsKt.checkString(var4, "9.", false, 2, null)) {
         var5 = "CREATE INDEX";
      }

      String[] var8 = var1;
      Collection var9 = new ArrayList(var1.length);
      int var11 = 0;

      for (int var12 = var1.length; var11 < var12; var11++) {
         String var13 = var8[var11];
         var9.add(var5 + " idx_" + var3 + '_' + var13 + " ON " + var3 + " (" + var13 + ')');
      }

      for (Object var18 : (List)var9) {
         String var10 = (String)var18;
         try {
            var2.execute(var10);
         } catch (SQLException var19) {
            throw new RuntimeException(var19);
         }
      }
   }

   public final void run9() {
      this.runWithStorageAsync(st -> checkVersion(this, (PunishmentStorage) st));
   }

   public final Object loadDriverJar(@NotNull DriverLibrary var1, @NotNull File var2, boolean var3) {
      if (!var2.exists()) {
         return null;
      } else {
         String var4 = LiteBansCore.updater.getString(FilesKt.getByteArray(var2));
         if (var1.getString5() != null && !Intrinsics.checkObject(var1.getString5(), var4)) {
            var2.delete();
            throw new ChecksumMismatchException(var2, var1.getString5(), var4);
         } else {
            try {
               URL[] var6 = new URL[]{var2.toURI().toURL(), DatabaseManager.class.getProtectionDomain().getCodeSource().getLocation().toURI().toURL()};
               URLClassLoader var5 = new URLClassLoader(var6, Driver.class.getClassLoader());
               Class var7 = var5.loadClass(var1.getString2());
               return var3 ? var7.getConstructor().newInstance() : var7;
            } catch (Exception var8) {
               throw new RuntimeException(var8);
            }
         }
      }
   }

   private static final LiteBansCore getLiteBansCore(DatabaseManager var0) {
      return (LiteBansCore)var0.platform.getModule(LiteBansCore.class);
   }

   private static final NotificationManager getNotificationManager(DatabaseManager var0) {
      return (NotificationManager)var0.platform.getModule(NotificationManager.class);
   }

   private static final List getList(DatabaseManager var0) {
      return var0.platform.createBridge().getPluginNames(var0.platform.getName());
   }

   private static final List getList(Lazy var0) {
      return (List)var0.getValue();
   }

   private static final void handleSender(Sender var0, DatabaseManager var1, Function1 var2) {
      MessageSender var3 = CommandRunnable.messageSender;
      Intrinsics.handleObject(var0);
      Sender var4 = var0;

      try {
         var3.getThreadLocal().set(var4);

         try {
            DatabaseManager var7 = var1;

            try {
               PunishmentStorage var33 = var7.openStorage();
               Closeable var10 = var33;
               Throwable var11 = null;

               try {
                  var2.invoke(var33);
               } catch (Throwable var27) {
                  var11 = var27;
                  throw var27;
               } finally {
                  CloseableKt.handleCloseable(var10, var11);
               }
            } catch (Exception var29) {
               if (!var1.isConnectionError(var29)) {
                  throw var29;
               }
            }
         } catch (AbortException var30) {
            CharSequence var9 = String.valueOf(var30.getObject());
            MessageSender.handleSenderDefault(CommandRunnable.messageSender, var0, var9, null, 4, null);
         } catch (Exception var31) {
            var1.reportError(var31);
         }
      } finally {
         var3.getThreadLocal().remove();
      }
   }

   private static final void logDatabaseInfo(DatabaseManager var0, boolean var1) {
      DatabaseManager var2 = var0;

      try {
         DatabaseManager var4 = var2;

         try {
            PunishmentStorage var6 = var4.openStorage();
            Closeable var7 = var6;
            Throwable var8 = null;

            try {
               if (var6.isOpen()) {
                  Intrinsics.handleObject(var6, "");
                  AutoCloseable var13 = ((SqlStorage)var6).getStatement();
                  Throwable var14 = null;

                  try {
                     Statement var15 = (Statement)var13;
                     if (!var1) {
                        DatabaseManager var17 = var2;
                        if (var2.getIntValue() <= 674) {
                           ResultFailure var19 = new ResultFailure();
                           var19.object = "";
                           if (databaseDriver.checkString(var2.getCore().getConfig().getSqlDriver())) {
                              try {
                                 DatabaseManager var20 = var17;

                                 try {
                                    PunishmentStorage var22 = var20.openStorage();
                                    Closeable var23 = var22;
                                    Throwable var24 = null;

                                    try {
                                       ResultSet var29 = QueryRunner.getResultSet(var22.prepare("SELECT @@version"));
                                       byte var31 = 1;
                                       String var10000;
                                       if (var29.next()) {
                                          ResultSet var33 = var29;

                                          String var35;
                                          try {
                                             var35 = var33.getString(var31);
                                          } catch (SQLException var102) {
                                             var35 = null;
                                          }

                                          var10000 = var35;
                                       } else {
                                          var10000 = null;
                                       }

                                       String var10001 = var10000;
                                       if (var10000 == null) {
                                          var10001 = "";
                                       }

                                       var19.object = var10001;
                                    } catch (Throwable var103) {
                                       var24 = var103;
                                       throw var103;
                                    } finally {
                                       CloseableKt.handleCloseable(var23, var24);
                                    }
                                 } catch (SQLException var108) {
                                    if (!var17.isConnectionError(var108)) {
                                       throw var108;
                                    }
                                 }
                              } catch (SQLException var109) {
                                 var2.reportError(var109);
                              }
                           }

                           String[] var21 = new String[]{"template", "ipban_wildcard", "uuid", "ip", "banned_by_uuid", "time", "until", "ipban", "active"};
                           String[] var120 = var21;
                           String[] var122 = new String[]{"uuid", "name", "ip"};
                           var21 = var122;

                           try {
                              Table[] var123 = Table.values();
                              int var124 = 0;

                              for (int var125 = var123.length; var124 < var125; var124++) {
                                 Table var25 = var123[var124];
                                 if (var25.check()) {
                                    handleDatabaseManager(var17, var120, var15, var25, (String)var19.object);
                                 } else if (var25 == Table.HISTORY) {
                                    handleDatabaseManager(var17, var21, var15, var25, (String)var19.object);
                                 }
                              }
                           } catch (Exception var107) {
                              LiteBansCore.handleObjectDefault(var17.getCore(), var107, 0, 2, null);
                           }
                        }
                     }
                  } catch (Throwable var110) {
                     var14 = var110;
                     throw var110;
                  } finally {
                     AutoCloseableKt.handleAutoCloseable(var13, var14);
                  }
               }
            } catch (Throwable var112) {
               var8 = var112;
               throw var112;
            } finally {
               CloseableKt.handleCloseable(var7, var8);
            }
         } catch (Exception var114) {
            if (!var2.isConnectionError(var114)) {
               throw var114;
            }
         }
      } catch (Exception var115) {
         var0.reportError(var115);
      }

      try {
         if (!var1) {
            DatabaseManager var116 = var2;

            try {
               PunishmentStorage var117 = var116.openStorage();
               Closeable var118 = var117;
               Throwable var119 = null;

               try {
                  var117.recordLogin("CONSOLE", "CONSOLE", "#", false, false);
               } catch (Throwable var100) {
                  var119 = var100;
                  throw var100;
               } finally {
                  CloseableKt.handleCloseable(var118, var119);
               }
            } catch (Exception var105) {
               if (!var2.isConnectionError(var105)) {
                  throw var105;
               }
            }
         }
      } catch (Throwable var106) {
         var0.reportError(var106);
      }
   }

   private static final void handleDatabaseManager(DatabaseManager var0, PunishmentStorage var1, boolean var2) {
      var0.cleanupServers(var1, var2);
   }

   private static final Unit checkVersion(DatabaseManager var0, PunishmentStorage var1) {
      try {
         if (var1.isClosed()) {
            return Unit.INSTANCE;
         }

         ResultSet var2 = QueryRunner.getResultSet(var1.prepare("SELECT NOW() as now"));
         var2.next();
         Timestamp var3 = var2.getTimestamp("now");
         Calendar var4 = Calendar.getInstance();
         var4.setTimeZone(TimeZone.getTimeZone("UTC"));
         var4.setTime(var3);
         var0.intValue2 = (int)(var0.currentTimeMillis(true) - var4.getTimeInMillis());
         String var5 = BuildInfo.getString();
         String var6 = BuildInfo.getString3();
         String var7 = "UTC";
         Table var9 = Table.CONFIG;
         CharSequence var10 = "*";
         ResultSet var8 = QueryRunner.getResultSet2(
            var1.prepare(SqlString.getSqlString(SqlString.getString(SqlString.getString2("SELECT " + var10 + " FROM " + var9), 1)))
         );
         if (var8.next()) {
            var0.flag3 = var8.getInt("accept") > 0;
            var0.string = var8.getString("timezone");
            if (!Intrinsics.checkObject(var6, var8.getString("build"))) {
               var9 = Table.CONFIG;
               QueryRunner.getInt(
                  var1.prepare(
                     SqlString.getSqlString(SqlString.getString4(SqlString.getString2("UPDATE " + var9), "version='" + var5 + "',build='" + var6 + '\''))
                  )
               );
            }
         } else {
            var9 = Table.CONFIG;
            var10 = "version,build,timezone";
            byte var13 = 44;
            CharSequence var15 = var10;
            int var17 = 0;

            for (int var18 = 0; var18 < var15.length(); var18++) {
               char var19 = var15.charAt(var18);
               if (var19 == var13) {
                  var17++;
               }
            }

            String var12 = "?";
            if (var17 > 0) {
               StringBuilder var32 = new StringBuilder(var17 * 2);
               Iterable var14 = new IntRange(0, var17);
               Iterator var16 = var14.iterator();

               while (var16.hasNext()) {
                  ((IntIterator)var16).getInt();
                  var32.append("?,");
               }

               var12 = StringsKt.getString(var32.toString(), 1);
            }

            QueryStatement var29 = var1.prepare(SqlString.getSqlString(SqlString.getString2("INSERT INTO " + var9 + '(' + var10 + ")VALUES(" + var12 + ')')));
            String[] var31 = new String[]{var5, var6, var7};
            String[] var33 = var31;
            var17 = 0;

            for (int var35 = var31.length; var17 < var35; var17++) {
               String var36 = var33[var17];
               var29.getQueryStatement(var36);
            }

            QueryRunner.getInt(var29);
         }
      } catch (Exception var24) {
         var0.reportError(var24);
      }

      if (var0.getCore().getConfig().getBanAlts() > 0 && !var0.flag3) {
         Sender var25 = var1.getPlatform().getConsole();
         CharSequence var26 = Message.BAN_ALTS_GUARDRAIL_DISCLAIMER;
         MessageSender.handleSenderDefault(CommandRunnable.messageSender, var25, var26, null, 4, null);
      }

      return Unit.INSTANCE;
   }

   public static final void handleDatabaseManager(DatabaseManager var0, String[] var1, Statement var2, Table var3, String var4) {
      var0.createIndexes(var1, var2, var3, var4);
   }

   public static final AtomicBoolean getAtomicBoolean(DatabaseManager var0) {
      return var0.atomicBoolean;
   }

   public static final String getString22() {
      return string2;
   }

   public static final String getString3() {
      return string3;
   }

   public static final String getString4() {
      return string4;
   }
}
