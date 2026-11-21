package litebans.punishment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import litebans.command.ArgUtils;
import litebans.command.CommandFlags;
import litebans.command.CommandRunnable;
import litebans.config.Config;
import litebans.config.Message;
import litebans.config.Template;
import litebans.config.TemplateData;
import litebans.config.TemplateGroup;
import litebans.core.LiteBansCore;
import litebans.core.MessageSender;
import litebans.core.NotificationManager;
import litebans.database.DatabaseManager;
import litebans.database.EntryReader;
import litebans.database.PunishmentStorage;
import litebans.hook.LuckPermsHook;
import litebans.lib.compress.archivers.tar.TargetParser;
import litebans.lib.kotlin.NoWhenBranchMatchedException;
import litebans.lib.kotlin.collections.CollectionsKt;
import litebans.lib.kotlin.jvm.internal.DefaultConstructorMarker;
import litebans.lib.kotlin.jvm.internal.Intrinsics;
import litebans.lib.kotlin.text.StringsKt;
import litebans.platform.LiteBansPlatform;
import litebans.platform.PlatformBridge;
import litebans.platform.Sender;
import litebans.platform.bukkit.BukkitBridge;
import litebans.util.SortComparator2;
import litebans.util.SortComparator4;
import litebans.util.SortComparator8;
import litebans.util.Unreachable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PunishmentRequest {
   private PunishmentType punishmentType;
   private boolean ipBan;
   private boolean silent;
   private boolean extraSilent;
   private boolean temporary;
   private boolean modify;
   private boolean delete;
   private boolean flag7;
   private boolean noQueue;
   private boolean customSender;
   private boolean confirm;
   private boolean skip;
   private boolean hidden;
   private boolean flag13;
   private final long dateStart;
   private long dateEnd;
   private long duration;
   private byte stack;
   private Template template;
   private TemplateGroup templateGroup;
   private boolean flag14;
   private TemplateData templateData;
   private int intValue;
   private CharSequence reason;
   private PlayerHistory playerHistory;
   private PunishmentEntry punishmentEntry;
   private PunishmentEntry punishmentEntry2;
   private final Sender sender;
   private String senderName;
   private String senderUuid;
   private String serverScope;
   private String serverOrigin;
   private String string5;
   private final StringBuilder stringBuilder;
   private final String[] stringArray;
   private final ArrayList args;
   private final List list;
   private PunishmentStage punishmentStage;
   private final CommandFlags commandFlags;

   public PunishmentRequest(
      @NotNull PunishmentType var1,
      boolean var2,
      boolean var3,
      boolean var4,
      boolean var5,
      boolean var6,
      boolean var7,
      boolean var8,
      boolean var9,
      boolean var10,
      boolean var11,
      boolean var12,
      boolean var13,
      boolean var14,
      long var15,
      long var17,
      long var19,
      byte var21,
      @NotNull Template var22,
      @Nullable TemplateGroup var23,
      boolean var24,
      @NotNull TemplateData var25,
      int var26,
      @NotNull CharSequence var27,
      @Nullable PlayerHistory var28,
      @Nullable PunishmentEntry var29,
      @Nullable PunishmentEntry var30,
      @NotNull Sender var31,
      @NotNull String var32,
      @NotNull String var33,
      @NotNull String var34,
      @Nullable String var35,
      @NotNull String var36,
      @NotNull StringBuilder var37,
      @NotNull String[] var38,
      @NotNull ArrayList var39,
      @NotNull List var40
   ) {
      this.punishmentType = var1;
      this.ipBan = var2;
      this.silent = var3;
      this.extraSilent = var4;
      this.temporary = var5;
      this.modify = var6;
      this.delete = var7;
      this.flag7 = var8;
      this.noQueue = var9;
      this.customSender = var10;
      this.confirm = var11;
      this.skip = var12;
      this.hidden = var13;
      this.flag13 = var14;
      this.dateStart = var15;
      this.dateEnd = var17;
      this.duration = var19;
      this.stack = var21;
      this.template = var22;
      this.templateGroup = var23;
      this.flag14 = var24;
      this.templateData = var25;
      this.intValue = var26;
      this.reason = var27;
      this.playerHistory = var28;
      this.punishmentEntry = var29;
      this.punishmentEntry2 = var30;
      this.sender = var31;
      this.senderName = var32;
      this.senderUuid = var33;
      this.serverScope = var34;
      this.serverOrigin = var35;
      this.string5 = var36;
      this.stringBuilder = var37;
      this.stringArray = var38;
      this.args = var39;
      this.list = var40;
      this.punishmentStage = PunishmentStage.INIT;
      this.commandFlags = new CommandFlags(this);
      CommandFlags.parseDefault(this.commandFlags, null, false, false, 7, null);
   }

   public PunishmentRequest(
      PunishmentType var1,
      boolean var2,
      boolean var3,
      boolean var4,
      boolean var5,
      boolean var6,
      boolean var7,
      boolean var8,
      boolean var9,
      boolean var10,
      boolean var11,
      boolean var12,
      boolean var13,
      boolean var14,
      long var15,
      long var17,
      long var19,
      byte var21,
      Template var22,
      TemplateGroup var23,
      boolean var24,
      TemplateData var25,
      int var26,
      CharSequence var27,
      PlayerHistory var28,
      PunishmentEntry var29,
      PunishmentEntry var30,
      Sender var31,
      String var32,
      String var33,
      String var34,
      String var35,
      String var36,
      StringBuilder var37,
      String[] var38,
      ArrayList var39,
      List var40,
      int var41,
      int var42,
      DefaultConstructorMarker var43
   ) {
      this(var1, ((var41 & 2) != 0 ? false : var2), ((var41 & 4) != 0 ? false : var3), ((var41 & 8) != 0 ? false : var4), ((var41 & 16) != 0 ? false : var5), ((var41 & 32) != 0 ? false : var6), ((var41 & 64) != 0 ? false : var7), ((var41 & 128) != 0 ? false : var8), ((var41 & 256) != 0 ? false : var9), ((var41 & 512) != 0 ? false : var10), ((var41 & 1024) != 0 ? false : var11), ((var41 & 2048) != 0 ? false : var12), ((var41 & 4096) != 0 ? false : var13), ((var41 & 8192) != 0 ? false : var14), var15, ((var41 & 32768) != 0 ? -1L : var17), ((var41 & 65536) != 0 ? 0L : var19), ((var41 & 131072) != 0 ? 0 : var21), ((var41 & 262144) != 0 ? Template.Companion.getTemplate() : var22), ((var41 & 524288) != 0 ? null : var23), ((var41 & 1048576) != 0 ? false : var24), ((var41 & 2097152) != 0 ? Template.Companion.getTemplateData() : var25), ((var41 & 4194304) != 0 ? -1 : var26), ((var41 & 8388608) != 0 ? Message.FOREVER : var27), ((var41 & 16777216) != 0 ? null : var28), ((var41 & 33554432) != 0 ? null : var29), ((var41 & 67108864) != 0 ? null : var30), var31, var32, ((var41 & 536870912) != 0 ? var31.isPlayer() ? var31.getUuidString() : var31.getName() : var33), var34, var35, ((var42 & 1) != 0 ? "" : var36), ((var42 & 2) != 0 ? new StringBuilder() : var37), ((var42 & 4) != 0 ? new String[0] : var38), ((var42 & 8) != 0 ? new ArrayList(var38.length) : var39), ((var42 & 16) != 0 ? new ArrayList() : var40));
   }

   public final PunishmentType getPunishmentType() {
      return this.punishmentType;
   }

   public final void setPunishmentType(@NotNull PunishmentType var1) {
      this.punishmentType = var1;
   }

   public final boolean isIpBan() {
      return this.ipBan;
   }

   public final void setIpBan(boolean var1) {
      this.ipBan = var1;
   }

   public final boolean isSilent() {
      return this.silent;
   }

   public final void setSilent(boolean var1) {
      this.silent = var1;
   }

   public final boolean isExtraSilent() {
      return this.extraSilent;
   }

   public final void setExtraSilent(boolean var1) {
      this.extraSilent = var1;
   }

   public final boolean isTemporary() {
      return this.temporary;
   }

   public final void setTemporary(boolean var1) {
      this.temporary = var1;
   }

   public final boolean isModify() {
      return this.modify;
   }

   public final void setModify(boolean var1) {
      this.modify = var1;
   }

   public final boolean isDelete() {
      return this.delete;
   }

   public final void setDelete(boolean var1) {
      this.delete = var1;
   }

   public final boolean isFlag7() {
      return this.flag7;
   }

   public final void setFlag7(boolean var1) {
      this.flag7 = var1;
   }

   public final boolean isNoQueue() {
      return this.noQueue;
   }

   public final void setNoQueue(boolean var1) {
      this.noQueue = var1;
   }

   public final boolean isCustomSender() {
      return this.customSender;
   }

   public final void setCustomSender(boolean var1) {
      this.customSender = var1;
   }

   public final boolean isConfirm() {
      return this.confirm;
   }

   public final void setConfirm(boolean var1) {
      this.confirm = var1;
   }

   public final boolean isSkip() {
      return this.skip;
   }

   public final void setSkip(boolean var1) {
      this.skip = var1;
   }

   public final void setHidden(boolean var1) {
      this.hidden = var1;
   }

   public final long getDateStart() {
      return this.dateStart;
   }

   public final long getDateEnd() {
      return this.dateEnd;
   }

   public final void setDateEnd(long var1) {
      this.dateEnd = var1;
   }

   public final long getDuration() {
      return this.duration;
   }

   public final void setDuration(long var1) {
      this.duration = var1;
   }

   public final byte getStack() {
      return this.stack;
   }

   public final void setStack(byte var1) {
      this.stack = var1;
   }

   public final Template getTemplate() {
      return this.template;
   }

   public final void setTemplate(@NotNull Template var1) {
      this.template = var1;
   }

   public final TemplateGroup getTemplateGroup() {
      return this.templateGroup;
   }

   public final void setTemplateGroup(@Nullable TemplateGroup var1) {
      this.templateGroup = var1;
   }

   public final boolean isFlag14() {
      return this.flag14;
   }

   public final void setFlag14(boolean var1) {
      this.flag14 = var1;
   }

   public final TemplateData getTemplateData() {
      return this.templateData;
   }

   public final void setTemplateData(@NotNull TemplateData var1) {
      this.templateData = var1;
   }

   public final int getIntValue() {
      return this.intValue;
   }

   public final void setIntValue(int var1) {
      this.intValue = var1;
   }

   public final CharSequence getReason() {
      return this.reason;
   }

   public final void setReason(@NotNull CharSequence var1) {
      this.reason = var1;
   }

   public final PlayerHistory getPlayerHistory() {
      return this.playerHistory;
   }

   public final void setPlayerHistory(@Nullable PlayerHistory var1) {
      this.playerHistory = var1;
   }

   public final PunishmentEntry getPunishmentEntry() {
      return this.punishmentEntry;
   }

   public final void setPunishmentEntry(@Nullable PunishmentEntry var1) {
      this.punishmentEntry = var1;
   }

   public final PunishmentEntry getPunishmentEntry2() {
      return this.punishmentEntry2;
   }

   public final void setPunishmentEntry2(@Nullable PunishmentEntry var1) {
      this.punishmentEntry2 = var1;
   }

   public final Sender getSender() {
      return this.sender;
   }

   public final String getSenderName() {
      return this.senderName;
   }

   public final void setSenderName(@NotNull String var1) {
      this.senderName = var1;
   }

   public final String getSenderUuid() {
      return this.senderUuid;
   }

   public final void setSenderUuid(@NotNull String var1) {
      this.senderUuid = var1;
   }

   public final String getServerScope() {
      return this.serverScope;
   }

   public final void setServerScope(@NotNull String var1) {
      this.serverScope = var1;
   }

   public final String getServerOrigin() {
      return this.serverOrigin;
   }

   public final void setServerOrigin(@Nullable String var1) {
      this.serverOrigin = var1;
   }

   public final String getString5() {
      return this.string5;
   }

   public final void setString5(@NotNull String var1) {
      this.string5 = var1;
   }

   public final StringBuilder getStringBuilder() {
      return this.stringBuilder;
   }

   public final String[] getStringArray() {
      return this.stringArray;
   }

   public final ArrayList getArgs() {
      return this.args;
   }

   public final void fireStage(@NotNull PunishmentStage var1) {
      this.punishmentStage = var1;

      for (Object var5 : this.list) {
         Consumer var6 = (Consumer)var5;
         var6.accept(this);
      }

      if (this.punishmentStage == PunishmentStage.CANCELLED) {
         MessageSender var7 = CommandRunnable.messageSender;
         var7.getVoid("");
         throw new NoWhenBranchMatchedException();
      }
   }

   public final boolean hasTemplate() {
      Template var1 = this.template;
      return Intrinsics.getInt(var1.getId() & 255, -1 & 255) < 0;
   }

   public final boolean check2() {
      boolean var10000;
      switch (PunishmentRequestWhenMappings.switchMap[this.punishmentType.ordinal()]) {
         case 1:
         case 2:
         case 3:
            var10000 = true;
            break;
         default:
            var10000 = false;
      }

      return var10000;
   }

   public final void applyTemplateDefaults(@NotNull PunishmentStorage var1) {
      List var2 = this.templateData.getList2();
      if (var2.isEmpty()) {
         var2 = this.template.getDefaults().getList2();
      }

      if (!var2.isEmpty()) {
         Collection var3 = var2;
         this.commandFlags.parse((String[]) var3.toArray(new String[0]), false, true);
         this.handlePunishmentStorage(var1, CollectionsKt.getList(var2), false);
      }
   }

   public final void run() {
      PunishmentType var10001;
      switch (PunishmentRequestWhenMappings.switchMap[this.punishmentType.ordinal()]) {
         case 4:
            var10001 = PunishmentType.UNBAN;
            break;
         case 5:
            var10001 = PunishmentType.UNMUTE;
            break;
         case 6:
            var10001 = PunishmentType.UNWARN;
            break;
         default:
            var10001 = this.punishmentType;
      }

      this.punishmentType = var10001;
   }

   public final void handlePunishmentStorage(@NotNull PunishmentStorage var1, @NotNull String var2, @NotNull String var3) {
      boolean var4 = this.isExempt(var1, var2, var3);
      if (!this.check2() && var4 && !this.isExemptByGroupWeight(var1, var3)) {
         PunishmentType var5 = this.punishmentType;
         CharSequence var10002;
         switch (EntryPlaceholdersWhenMappings.switchMap[var5.getPunishmentType().ordinal()]) {
            case 1:
               var10002 = Message.BAN_EXEMPT;
               break;
            case 2:
               var10002 = Message.MUTE_EXEMPT;
               break;
            case 3:
               var10002 = Message.WARN_EXEMPT;
               break;
            case 4:
               var10002 = Message.KICK_EXEMPT;
               break;
            default:
               Unreachable.getVoid();
               throw new NoWhenBranchMatchedException();
         }

         this.fail(var1, var10002);
         throw new NoWhenBranchMatchedException();
      }
   }

   public final void checkCooldown(@NotNull PunishmentStorage var1, @NotNull Sender var2, @NotNull PunishmentEntry var3, @Nullable PlayerHistory var4) {
      boolean var38;
      boolean var39;
      LiteBansCore var7 = (LiteBansCore)var1.getPlatform().getModule(LiteBansCore.class);
      long var8 = var3.getDateStart();
      if (var2.isConsole()) {
         long var10 = var7.getConfig().getDurationsConsoleCooldown();
         if (var10 > 0L) {
            PunishmentService.handlePunishmentStorage(var1, var1, var3, var8, var10, 0L, var4, false);
         }
      } else {
         Iterable var12 = var7.getConfig().getDurationsLimits();
         List var14 = CollectionsKt.getList(var12, new SortComparator4(var3));
         if (var7.isDebugEnabled()) {
            var7.debug(var14);
         }

         Iterable var16 = var14;
         Iterator var17 = var16.iterator();

         GroupLimits var10000;
         while (true) {
            if (var17.hasNext()) {
               Object var18 = var17.next();
               GroupLimits var19 = (GroupLimits)var18;
               if (var7.isDebugEnabled()) {
                  StringBuilder varDebugSb = new StringBuilder().append("for ").append(var19.getString()).append(' ');
                  String var26 = var19.getString();
                  var7.debug(varDebugSb.append(var2.hasPermission(var26)).toString());
               }

               label66: {
                  if (!Intrinsics.checkObject(var19.getString(), "none")) {
                     String var22 = var19.getString();
                     if (!var2.hasPermission(var22)) {
                        var38 = false;
                        break label66;
                     }
                  }

                  var38 = true;
               }

               if (!var38) {
                  continue;
               }

               var10000 = var19;
               break;
            }

            var10000 = null;
            break;
         }

         GroupLimits var33 = var10000;
         if (var33 != null) {
            long var28 = var33.getCooldown().getLong(var3);
            byte var34 = 5;
            if (var7.isDebugLevel(var34)) {
               var7.debug(
                  var2.getName()
                     + "'s "
                     + var3.getType()
                     + " cooldown is "
                     + DurationParser.getStringDefault(DurationUnit.durationParser, var28, false, 2, null)
                     + '.'
               );
            }

            if (var28 > 0L) {
               String[] var35 = new String[]{"litebans.cooldown.bypass", "litebans.cooldown.bypass." + var3.getType()};
               Sender var20 = var2;
               String[] var21 = Arrays.copyOf(var35, var35.length);
               String[] var23 = var21;
               int var25 = 0;
               int var36 = var21.length;

               while (true) {
                  if (var25 >= var36) {
                     var39 = false;
                     break;
                  }

                  String var27 = var23[var25];
                  if (var20.hasPermission(var27)) {
                     var39 = true;
                     break;
                  }

                  var25++;
               }

               if (!var39) {
                  PunishmentService.handlePunishmentStorageDefault(var1, var1, var3, var8, var28, var33.getCooldown().getRedo(), var4, false, 64, null);
               }
            }
         }
      }
   }

   public final boolean isExempt(@NotNull PunishmentStorage var1, @NotNull Sender var2) {
      return this.isExempt(var1, var2.getName(), var2.getUuidString());
   }

   public final boolean isExempt(@NotNull PunishmentStorage var1, @NotNull String var2, @NotNull String var3) {
      boolean var29;
      NotificationManager var4 = (NotificationManager)var1.getPlatform().getModule(NotificationManager.class);
      LiteBansCore var5 = (LiteBansCore)var1.getPlatform().getModule(LiteBansCore.class);
      String var7 = this.punishmentType.toString();
      String var6 = TargetParser.getString2(var3);
      if (var5.getToggleState().isFlag2() || !var5.getConfig().isExemptEnable()) {
         var5.debug(1, "Exempting is disabled.");
         return false;
      } else if (this.isExempt(var1, var2, var6, this.punishmentType)) {
         return true;
      } else {
         byte var9 = 1;
         if (var5.isDebugLevel(var9)) {
            var5.debug(var2 + " is not in exempt_players.");
         }

         boolean var8 = false;
         Object var23 = var4.getLuckPerms();
         byte var10 = var1.getPlatform().getPlatformId();
         if (var10 == 0) {
            UUID var11;
            try {
               var11 = UUID.fromString(var6);
            } catch (IllegalArgumentException var21) {
               var5.handleObject(var21, 0);
               return false;
            }

            PlatformBridge var10000 = var1.getPlatform().createBridge();
            Intrinsics.handleObject(var10000, "");
            BukkitBridge var12 = (BukkitBridge)var10000;
            var8 = var12.getVaultHook().getObject() != null
               && var12.getVaultHook()
                  .checkString(var6, var11, var7, "litebans.exempt", var5.getConfig().getExemptPermissionWorld(), var5.getConfig().getExemptExemptGroups());
         } else {
            if (var10 != 3 && var10 != 1 && var10 != 2) {
               Unreachable.getVoid();
               throw new NoWhenBranchMatchedException();
            }

            Sender var25 = var4.findOnlineByUuid(var6);
            if (var25 != null) {
               var8 = var25.hasPermission("litebans.exempt") || var25.hasPermission("litebans.exempt." + var7);
            }
         }

         if (!var8 && var23 != null) {
            Object var24 = LuckPermsHook.INSTANCE.getObject(var23, var6);
            if (var24 != null) {
               var8 = LuckPermsHook.INSTANCE.checkObject(var24, "litebans.exempt") || LuckPermsHook.INSTANCE.checkObject(var24, "litebans.exempt." + var7);
            }
         }

         byte var26 = 1;
         if (var5.isDebugLevel(var26)) {
            var5.debug(var2 + " exempt? " + var8);
         }

         return var8;
      }
   }

   public final boolean isExempt(@NotNull PunishmentStorage var1, @NotNull String var2, @NotNull String var3, @NotNull PunishmentType var4) {
      LiteBansCore var5 = (LiteBansCore)var1.getPlatform().getModule(LiteBansCore.class);
      String var6 = var4.toString();
      String[] var7 = var5.getConfig().getExemptExemptPlayers();
      int var8 = 0;

      for (int var9 = var7.length; var8 < var9; var8++) {
         String var10 = var7[var8];
         CharSequence var10000 = var10;
         char[] var12 = new char[]{':'};
         Collection var18 = StringsKt.getList(var10000, var12, false, 2, 2, null);
         String[] var11 = /* cast-fix */(String[])(var18.toArray(new String[0]));
         String var19 = var11[0];
         if ((Intrinsics.checkObject(var19, var3) || StringsKt.checkString(var19, var2, true))
            && (var11.length == 1 || StringsKt.checkString(var11[1], var6, true))) {
            byte var14 = 1;
            if (var5.isDebugLevel(var14)) {
               var5.debug(var2 + " exempt? true");
            }

            return true;
         }
      }

      return false;
   }

   public final boolean isExemptByGroupWeight(@NotNull PunishmentStorage var1, @Nullable String var2) {
      NotificationManager var3 = (NotificationManager)var1.getPlatform().getModule(NotificationManager.class);
      LiteBansCore var4 = (LiteBansCore)var1.getPlatform().getModule(LiteBansCore.class);
      if (this.sender.isConsole()) {
         return true;
      } else {
         if (this.sender.isPlayer()) {
            if (var4.getConfig().isExemptUseGroupWeights()) {
               Object var5 = var3.getLuckPerms();
               if (var5 != null && var2 != null) {
                  Object var6 = LuckPermsHook.INSTANCE.getObject(var5, this.sender.getUuidString());
                  Object var7 = LuckPermsHook.INSTANCE.getObject(var5, var2);
                  if (var6 != null && var7 != null) {
                     int var8 = LuckPermsHook.INSTANCE.getInt(var5, var6);
                     int var22 = LuckPermsHook.INSTANCE.getInt(var5, var7);
                     if (var8 >= var22) {
                        byte var24 = 1;
                        if (var4.isDebugLevel(var24)) {
                           var4.debug("Group weight of " + this.sender.getName() + " (" + var8 + ") >= target weight of " + var22);
                        }

                        if (var8 == var22 && !var4.getConfig().isExemptPermitSameWeight()) {
                           return false;
                        }

                        return true;
                     }

                     byte var23 = 1;
                     if (var4.isDebugLevel(var23)) {
                        var4.debug("Group weight of " + this.sender.getName() + " (" + var8 + ") does not exceed " + var22);
                     }

                     return false;
                  }

                  var4.debug(1, "Missing user!");
               }
            }

            if (!var4.getConfig().isAllowExemptBypass()) {
               byte var19 = 1;
               if (var4.isDebugLevel(var19)) {
                  var4.debug("allow_exempt_bypass is false.");
               }

               return false;
            }
         }

         Sender var18 = this.sender;
         String[] var20 = new String[]{"litebans.exempt.bypass", "litebans.exempt.bypass." + this.punishmentType};
         String[] var9 = var20;
         int var11 = 0;
         int var12 = var20.length;

         boolean var10000;
         while (true) {
            if (var11 >= var12) {
               var10000 = false;
               break;
            }

            String var13 = var9[var11];
            if (var18.hasPermission(var13)) {
               var10000 = true;
               break;
            }

            var11++;
         }

         boolean var17 = var10000;
         if (var17) {
            byte var21 = 1;
            if (var4.isDebugLevel(var21)) {
               var4.debug(this.sender.getName() + " bypasses exempt permissions.");
            }
         }

         return var17;
      }
   }

   public final void reply(@NotNull PunishmentStorage var1, @NotNull CharSequence var2) {
      Sender var3 = (Sender)CommandRunnable.messageSender.getThreadLocal().get();
      if (var3 != null) {
         MessageSender.handleSenderDefault(
            CommandRunnable.messageSender,
            var3,
            EntryReader.getCharSequence(var1, var2, this.punishmentEntry, this.playerHistory, false, 4, null),
            null,
            4,
            null
         );
      }
   }

   public final Void fail(@NotNull PunishmentStorage var1, @Nullable Object var2) {
      CommandRunnable.messageSender.getVoid(EntryReader.getCharSequence(var1, String.valueOf(var2), this.punishmentEntry, this.playerHistory, false, 4, null));
      throw new NoWhenBranchMatchedException();
   }

   public final void handlePunishmentStorage(@NotNull PunishmentStorage var1, @NotNull List var2, boolean var3) {
      DatabaseManager var4 = (DatabaseManager)var1.getPlatform().getModule(DatabaseManager.class);
      ServerScope var5 = ServerScope.serverScopeParser.getServerScope(this.sender, var2, var4.getString(), var3);
      CharSequence var8 = var5.getString();
      String var10001 = var8 == null || var8.length() == 0 ? null : var5.getString();
      if (var10001 == null) {
         var10001 = this.serverScope;
      }

      this.serverScope = var10001;
      this.serverScope = ServerScope.serverScopeParser.getString(this.serverScope, var4.getString(), this.sender);
   }

   public static void handlePunishmentStorageDefault(PunishmentRequest var0, PunishmentStorage var1, List var2, boolean var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var2 = var0.args;
      }

      if ((var4 & 2) != 0) {
         var3 = true;
      }

      var0.handlePunishmentStorage(var1, var2, var3);
   }

   public final void applyDurationLimits(@NotNull PunishmentStorage var1) {
      LiteBansCore var2 = (LiteBansCore)var1.getPlatform().getModule(LiteBansCore.class);
      Config var3 = var2.getConfig();
      PunishmentType var4 = this.punishmentType.getPunishmentType();
      Sender var5 = this.sender;
      String[] var6 = new String[]{"litebans.unlimited", "litebans.unlimited." + var4, "litebans.group.unlimited"};
      Sender var8 = var5;
      String[] var9 = Arrays.copyOf(var6, var6.length);
      String[] var11 = var9;
      int var13 = 0;
      int var14 = var9.length;

      boolean var10000;
      while (true) {
         if (var13 >= var14) {
            var10000 = false;
            break;
         }

         String var15 = var11[var13];
         if (var8.hasPermission(var15)) {
            var10000 = true;
            break;
         }

         var13++;
      }

      if (!var10000) {
         if (!this.check2()) {
            Iterable var19 = var3.getDurationsLimits();
            if (var19 instanceof Collection && ((Collection)var19).isEmpty()) {
               var10000 = false;
            } else {
               Iterator var7 = var19.iterator();

               while (true) {
                  if (!var7.hasNext()) {
                     var10000 = false;
                     break;
                  }

                  GroupLimits var31 = (GroupLimits) var7.next();
                  if (var31.getOther().isTemplateOnly()) {
                     var10000 = true;
                     break;
                  }
               }
            }

            if (var10000) {
               MessageSender var26;
               label90: {
                  Iterable var22 = var3.getDurationsLimits();
                  List var20 = CollectionsKt.getList(var22, new SortComparator2());
                  GroupLimits var23 = GroupLimitResolver.getGroupLimits(var20, this.sender);
                  var26 = CommandRunnable.messageSender;
                  if (var23 != null) {
                     GroupLimitOther var35 = var23.getOther();
                     if (var35 != null) {
                        var10000 = var35.isTemplateOnly();
                        break label90;
                     }
                  }

                  var10000 = false;
               }

               label85: {
                  if (var10000) {
                     Template var29 = this.template;
                     if (Intrinsics.getInt(var29.getId() & 255, -1 & 255) >= 0) {
                        var10000 = true;
                        break label85;
                     }
                  }

                  var10000 = false;
               }

               boolean var30 = var10000;
               CharSequence var32 = Message.ERROR_NO_TEMPLATE;
               if (var30) {
                  var26.getVoid(var32);
                  throw new NoWhenBranchMatchedException();
               }
            }
         }

         if (var4 == PunishmentType.BAN || var4 == PunishmentType.MUTE) {
            Iterable var24 = var3.getDurationsLimits();
            List var21 = CollectionsKt.getList(var24, new SortComparator8(var4));
            if (var2.isDebugEnabled()) {
               var2.debug(var21);
            }

            GroupLimits var38 = GroupLimitResolver.getGroupLimits(var21, this.sender);
            if (var38 == null) {
               return;
            }

            GroupLimits var25 = var38;
            long var27 = var25.getDuration().getLong(var4);
            if (var27 > 0L && var27 < Long.MAX_VALUE) {
               long var33 = DatabaseManager.currentTimeMillisDefault(var1.getDatabaseManager(), false, 1, null) + var27;
               if (this.dateEnd > var33 || this.dateEnd <= 0L) {
                  if (!var3.isDurationsReduceToLimit()) {
                     this.fail(
                        var1,
                        Message.ERROR_DURATION_LIMIT.getString("duration", DurationParser.getStringDefault(DurationUnit.durationParser, var27, false, 2, null))
                     );
                     throw new NoWhenBranchMatchedException();
                  }

                  this.dateEnd = var33;
                  this.reason = DurationParser.getStringDefault(DurationUnit.durationParser, var27, false, 2, null);
               }
            }
         }
      }
   }

   public final CharSequence readReason(@NotNull PunishmentStorage var1) {
      Config var2 = ((LiteBansCore)var1.getPlatform().getModule(LiteBansCore.class)).getConfig();
      boolean var3 = this.punishmentType == PunishmentType.KICK
         ? var2.isRequireKickReason()
         : (this.punishmentType == PunishmentType.WARN ? true : (this.check2() ? var2.isRequireUnbanUnmuteReason() : var2.isRequireBanMuteReason()));
      CharSequence var5 = this.stringBuilder;
      int var7 = 0;
      int var8 = var5.length() - 1;
      boolean var9 = false;

      while (var7 <= var8) {
         int var10 = !var9 ? var7 : var8;
         char var11 = var5.charAt(var10);
         boolean var34 = Intrinsics.getInt(var11, 32) <= 0;
         if (!var9) {
            if (!var34) {
               var9 = true;
            } else {
               var7++;
            }
         } else {
            if (!var34) {
               break;
            }

            var8--;
         }
      }

      CharSequence var4 = var5.subSequence(var7, var8 + 1);
      if (StringsKt.checkCharSequence3(var4, '{', false, 2, null) || StringsKt.checkCharSequence3(var4, '}', false, 2, null)) {
         Sender var19 = this.sender;
         String var6 = "litebans.admin";
         if (!var19.hasPermission(var6)) {
            var4 = StringsKt.getString(
               StringsKt.getString(
                  StringsKt.getString(StringsKt.getString(var4.toString(), "{hoverText:", "", false, 4, null), "{run:", "", false, 4, null),
                  "{url:",
                  "",
                  false,
                  4,
                  null
               ),
               "}",
               "",
               false,
               4,
               null
            );
         }
      }

      if (StringsKt.checkCharSequence3(var4, '\n', false, 2, null)) {
         Sender var20 = this.sender;
         String var23 = "litebans.newline";
         if (!var20.hasPermission(var23)) {
            var4 = StringsKt.getString(var4.toString(), "\n", " ", false, 4, null);
         }
      }

      if (!this.check2()) {
         if (StringsKt.checkCharSequence(var4)) {
            if (this.hasTemplate()) {
               String var10000 = this.templateData.getString2();
               CharSequence var40;
               if (var10000 != null) {
                  var40 = var10000;
               } else {
                  var10000 = this.template.getDefaults().getString2();
                  if (var10000 != null) {
                     var40 = var10000;
                  } else {
                     PunishmentType var29 = this.punishmentType;
                     switch (EntryPlaceholdersWhenMappings.switchMap[var29.getPunishmentType().ordinal()]) {
                        case 1:
                           var40 = Message.DEFAULT_BAN_REASON;
                           break;
                        case 2:
                           var40 = Message.DEFAULT_MUTE_REASON;
                           break;
                        default:
                           var40 = null;
                     }

                     if (var40 == null) {
                        PunishmentType var30 = this.punishmentType;
                        Message var12 = Message.BAN_USAGE;
                        Message var42;
                        switch (PunishmentRequestWhenMappings2.intArray[var30.ordinal()]) {
                           case 1:
                              var42 = var12 == Message.BAN_EXAMPLE ? Message.WARN_EXAMPLE : Message.WARN_USAGE;
                              break;
                           case 2:
                              var42 = var12 == Message.BAN_EXAMPLE ? Message.MUTE_EXAMPLE : Message.MUTE_USAGE;
                              break;
                           case 3:
                              var42 = Message.KICK_USAGE;
                              break;
                           default:
                              var42 = var12;
                        }

                        CharSequence var14 = var42;
                        MessageSender var36 = CommandRunnable.messageSender;
                        boolean var13 = var30 == PunishmentType.KICK;
                        if (var13) {
                           var36.getVoid(var14);
                           throw new NoWhenBranchMatchedException();
                        }

                        Message var15 = Message.BAN_EXAMPLE;
                        Message var43;
                        switch (PunishmentRequestWhenMappings2.intArray[var30.ordinal()]) {
                           case 1:
                              var43 = var15 == Message.BAN_EXAMPLE ? Message.WARN_EXAMPLE : Message.WARN_USAGE;
                              break;
                           case 2:
                              var43 = var15 == Message.BAN_EXAMPLE ? Message.MUTE_EXAMPLE : Message.MUTE_USAGE;
                              break;
                           case 3:
                              var43 = Message.KICK_USAGE;
                              break;
                           default:
                              var43 = var15;
                        }

                        CharSequence var37 = var43;
                        CommandRunnable.messageSender.getVoid(ArgUtils.getCharSequence(var14, true, var37));
                        throw new NoWhenBranchMatchedException();
                     }
                  }
               }

               var4 = var40;
            } else {
               MessageSender var21 = CommandRunnable.messageSender;
               CharSequence var24 = Message.ERROR_NO_REASON;
               if (var3) {
                  var21.getVoid(var24);
                  throw new NoWhenBranchMatchedException();
               }

               PunishmentType var25 = this.punishmentType;
               CharSequence var44;
               switch (EntryPlaceholdersWhenMappings.switchMap[var25.getPunishmentType().ordinal()]) {
                  case 1:
                     var44 = Message.DEFAULT_BAN_REASON;
                     break;
                  case 2:
                     var44 = Message.DEFAULT_MUTE_REASON;
                     break;
                  default:
                     var44 = null;
               }

               if (var44 == null) {
                  var44 = "";
               }

               var4 = var44;
            }
         }
      } else if (StringsKt.checkCharSequence(var4)) {
         MessageSender var22 = CommandRunnable.messageSender;
         CharSequence var26 = Message.ERROR_NO_REASON_REMOVAL;
         if (var3) {
            var22.getVoid(var26);
            throw new NoWhenBranchMatchedException();
         }

         PunishmentType var27 = this.punishmentType;
         CharSequence var45;
         switch (EntryPlaceholdersWhenMappings.switchMap[var27.getPunishmentType().ordinal()]) {
            case 1:
               var45 = Message.DEFAULT_UNBAN_REASON;
               break;
            case 2:
               var45 = Message.DEFAULT_UNMUTE_REASON;
               break;
            case 3:
               var45 = Message.DEFAULT_UNWARN_REASON;
               break;
            default:
               var45 = null;
         }

         if (var45 == null) {
            PunishmentType var28 = this.punishmentType;
            Message var31 = Message.BAN_USAGE;
            Message var46;
            switch (PunishmentRequestWhenMappings2.intArray[var28.ordinal()]) {
               case 1:
                  var46 = var31 == Message.BAN_EXAMPLE ? Message.WARN_EXAMPLE : Message.WARN_USAGE;
                  break;
               case 2:
                  var46 = var31 == Message.BAN_EXAMPLE ? Message.MUTE_EXAMPLE : Message.MUTE_USAGE;
                  break;
               case 3:
                  var46 = Message.KICK_USAGE;
                  break;
               default:
                  var46 = var31;
            }

            CharSequence var38 = var46;
            MessageSender var32 = CommandRunnable.messageSender;
            boolean var35 = var28 == PunishmentType.KICK;
            if (var35) {
               var32.getVoid(var38);
               throw new NoWhenBranchMatchedException();
            }

            Message var39 = Message.BAN_EXAMPLE;
            Message var47;
            switch (PunishmentRequestWhenMappings2.intArray[var28.ordinal()]) {
               case 1:
                  var47 = var39 == Message.BAN_EXAMPLE ? Message.WARN_EXAMPLE : Message.WARN_USAGE;
                  break;
               case 2:
                  var47 = var39 == Message.BAN_EXAMPLE ? Message.MUTE_EXAMPLE : Message.MUTE_USAGE;
                  break;
               case 3:
                  var47 = Message.KICK_USAGE;
                  break;
               default:
                  var47 = var39;
            }

            CharSequence var33 = var47;
            CommandRunnable.messageSender.getVoid(ArgUtils.getCharSequence(var38, true, var33));
            throw new NoWhenBranchMatchedException();
         }

         var4 = var45;
      }

      return EntryPlaceholders.getString(var1, var4, this.template, this.intValue);
   }

   public final CharSequence getCharSequence(@NotNull PunishmentStorage var1, @NotNull ExecutorInfo var2) {
      PunishmentEntry var10000 = this.punishmentEntry;
      Intrinsics.handleObject(this.punishmentEntry);
      PunishmentEntry var3 = var10000;
      String var8 = this.templateData.getString3();
      CharSequence var9;
      if (var8 != null) {
         var9 = var8;
      } else {
         switch (PunishmentRequestWhenMappings.switchMap[var3.getType().ordinal()]) {
            case 4:
               var9 = var3.isIpban() && !var3.isPermanent()
                  ? Message.BAN_BROADCAST_TEMP_IP
                  : (
                     var3.isIpban() && var3.isPermanent()
                        ? Message.BAN_BROADCAST_IP
                        : (!var3.isPermanent() ? Message.BAN_BROADCAST_TEMP : Message.BAN_BROADCAST)
                  );
               break;
            case 5:
               var9 = var3.isIpban() && !var3.isPermanent()
                  ? Message.MUTE_BROADCAST_TEMP_IP
                  : (
                     var3.isIpban() && var3.isPermanent()
                        ? Message.MUTE_BROADCAST_IP
                        : (!var3.isPermanent() ? Message.MUTE_BROADCAST_TEMP : Message.MUTE_BROADCAST)
                  );
               break;
            case 6:
               var9 = Message.WARN_BROADCAST;
               break;
            case 7:
               var9 = this.stringBuilder.length() == 0 ? Message.KICK_BROADCAST_REASON_EMPTY : Message.KICK_BROADCAST;
               break;
            default:
               Unreachable.getVoid();
               throw new NoWhenBranchMatchedException();
         }
      }

      CharSequence var7 = var9;
      return this.getCharSequence(var1, var7, var2);
   }

   public final void handlePunishmentStorage(@NotNull PunishmentStorage var1, @Nullable Sender var2, @Nullable ExecutorInfo var3, boolean var4) {
      PunishmentRequest var5 = this;
      if (!var5.hidden) {
         ExecutorInfo var10002 = var3;
         if (var3 == null) {
            Intrinsics.handleObject(var2);
            String var7 = var2.getName();
            String var8 = var2.getUuidString();
            String var9 = var1.getString(var2);
            String var10 = var2.getIp();
            var10002 = new ExecutorInfo(var7, null, var9, var8, var10, var2, null, 66, null);
         }

         CharSequence var11 = var5.getCharSequence(var1, var10002);
         PlayerHistory var12 = var5.playerHistory;
         LiteBansPlatform var13 = var1.getPlatform();
         new PunishmentNotifier(var5, var2, var12, var11, var13, null, var4, false, 160, null).run();
      }
   }

   public static void handlePunishmentStorageDefault(
      PunishmentRequest var0, PunishmentStorage var1, Sender var2, ExecutorInfo var3, boolean var4, int var5, Object var6
   ) {
      if ((var5 & 2) != 0) {
         var3 = null;
      }

      if ((var5 & 4) != 0) {
         var4 = true;
      }

      var0.handlePunishmentStorage(var1, var2, var3, var4);
   }

   public final CharSequence getCharSequence(@NotNull PunishmentStorage var1, @NotNull CharSequence var2, @NotNull ExecutorInfo var3) {
      CharSequence[] var5 = new CharSequence[]{"tempDuration", this.reason};
      CharSequence var4 = ArgUtils.getString(var2, var5);
      return var3.getSender() != null ? var1.getCharSequence2(var4, var3.getSender()) : var1.getCharSequence(var4, var3);
   }

   public final String getString(@NotNull CommandRunnable var1) {
      return PunishmentService.lookupUuid(var1, this.string5);
   }

   public final boolean isWildcardTarget(@Nullable String var1) {
      return this.ipBan
         && var1 != null
         && (
            StringsKt.checkCharSequence3(var1, '*', false, 2, null)
               || StringsKt.checkCharSequence3(var1, '%', false, 2, null)
               || StringsKt.checkCharSequence3(var1, '_', false, 2, null) && TargetParser.INSTANCE.checkString2(var1)
         )
         && !this.commandFlags.isWildcardIp(var1);
   }

   public static boolean isWildcardTargetDefault(PunishmentRequest var0, String var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = var0.string5;
      }

      return var0.isWildcardTarget(var1);
   }
}
