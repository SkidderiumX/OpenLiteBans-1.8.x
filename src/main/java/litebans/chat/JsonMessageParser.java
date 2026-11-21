package litebans.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import litebans.command.TextArgs;
import litebans.core.LiteBansCore;
import litebans.core.PluginModule;
import litebans.lib.json.JSONArray;
import litebans.lib.json.JSONObject;
import litebans.lib.json.JSONParser;
import litebans.lib.kotlin.Pair;
import litebans.lib.kotlin.TuplesKt;
import litebans.lib.kotlin.collections.CollectionsKt;
import litebans.lib.kotlin.collections.IntIterator;
import litebans.lib.kotlin.jvm.internal.Intrinsics;
import litebans.lib.kotlin.ranges.RangesKt;
import litebans.lib.kotlin.text.StringsKt;
import litebans.platform.LiteBansPlatform;
import litebans.platform.Sender;
import litebans.update.Version;
import litebans.update.VersionParser;
import litebans.util.FormatSplitter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class JsonMessageParser extends PluginModule {
   public static final FormatSplitter formatSplitter = new FormatSplitter(null);
   private static boolean flag = true;
   public static final boolean flag2 = false;

   public JsonMessageParser(@NotNull LiteBansPlatform var1) {
      super(var1);
   }

   public final String getString(@NotNull Sender var1, @NotNull String var2) {
      Object var23;
      JsonMessageTemplate var3 = this.getJsonMessageTemplate(var2, var1);
      JsonMessagePart[] var5 = var3.getJsonMessagePartArray();
      int var7 = 0;
      int var8 = var5.length;

      boolean var10000;
      while (true) {
         if (var7 >= var8) {
            var10000 = false;
            break;
         }

         JsonMessagePart var9 = var5[var7];
         if (var9.isFlag()) {
            var10000 = true;
            break;
         }

         var7++;
      }

      if (var10000 && var3.getJsonObject() != null) {
         if (var1.isConsole()) {
            JsonMessagePart[] var17 = var3.getJsonMessagePartArray();
            var8 = 0;
            int var21 = var17.length;

            while (true) {
               if (var8 >= var21) {
                  var23 = null;
                  break;
               }

               JsonMessagePart var10 = var17[var8];
               if (var10.isFlag() && Intrinsics.checkObject(var10.getString2(), this.getString(var1))) {
                  var23 = var10;
                  break;
               }

               var8++;
            }

            JsonMessagePart var14 = /* cast-fix */(JsonMessagePart)(var23);
            if (var14 != null) {
               var1.sendMessage(var3.getString());
               var1.sendMessage("=> " + var14.getString4());
               return null;
            }
         }

         LiteBansCore var15 = (LiteBansCore)this.platform.getModule(LiteBansCore.class);
         if (var15.isDebugEnabled()) {
            var15.debug(var3.getJsonObject());
         }

         label101: {
            if (var1.isPlayer()) {
               var5 = var3.getJsonMessagePartArray();
               var7 = 0;
               var8 = var5.length;

               while (true) {
                  if (var7 >= var8) {
                     throw new NoSuchElementException("Array contains no element matching the predicate.");
                  }

                  JsonMessagePart var22 = var5[var7];
                  if (Intrinsics.checkObject(var22.getString2(), this.getString2(var1))) {
                     if (!var22.isFlag()) {
                        break label101;
                     }

                     String var6 = "litebans.json.hover_text";
                     if (var1.hasPermission(var6)) {
                        break label101;
                     }
                     break;
                  }

                  var7++;
               }
            }

            return var3.getString();
         }

         var1.sendJsonMessage(var3.getJsonObject().toString());
         return null;
      } else {
         return formatSplitter.getString(var2);
      }
   }

   public final JsonMessagePart getJsonMessagePart(@NotNull String var1, @NotNull String var2, @NotNull String var3, @Nullable JsonMessagePart var4) {
      String var5 = '{' + var2 + ':';
      if (var4 != null
         && !StringsKt.checkCharSequence3(var1, var5, false, 2, null)
         && StringsKt.checkCharSequence3(var1, '}', false, 2, null)
         && var4.isFlag2()
         && Intrinsics.checkObject(var4.getString(), var2)) {
         String var39 = var5 + ' ' + var1;
         return this.getJsonMessagePart(var39, var2, var3, null);
      } else if (!StringsKt.checkCharSequence3(var1, var5, false, 2, null)) {
         return new JsonMessagePart(var2, var3, var1, null, false, false, 56, null);
      } else {
         CharSequence var10000 = var1;
         char[] var7 = new char[]{' '};
         List var6 = StringsKt.getList(var10000, var7, false, 0, 6, null);
         List var40 = CollectionsKt.getList(var6);
         int var8 = 0;
         int var9 = 1;
         boolean var10 = false;
         boolean var11 = false;
         boolean var12 = false;
         ArrayList var13 = new ArrayList(var40.size());
         Iterable var17 = var6;
         int var19 = 0;

         for (Object var21 : var17) {
            int var22 = var19++;
            if (var22 < 0) {
               CollectionsKt.run();
            }

            String var23 = (String)var21;
            if (!var10 && StringsKt.checkString(var23, var5, false, 2, null)) {
               var10 = true;
               var11 = true;
               var8 = var22;
            } else if (var11) {
               var9++;
               if (StringsKt.checkCharSequence3(var23, '}', false, 2, null) && !StringsKt.checkCharSequence3(var23, '{', false, 2, null)) {
                  String var26 = StringsKt.getString3(var23, '}', null, 2, null);
                  if (var26.length() > 0) {
                     var13.add(var26);
                  }

                  String var27 = StringsKt.getString4(var23, '}', null, 2, null);
                  if (var27.length() > 0) {
                     int var28 = var22 + 1;
                     if (var40.size() > var28) {
                        var40.set(var28, var27 + (String)var40.get(var28));
                     } else {
                        var40.add(var27);
                     }
                  }

                  var11 = false;
                  var12 = true;
                  break;
               }

               var13.add(var23);
            }
         }

         var8 = Math.min(var40.size() - 1, var8);
         Iterable var14 = RangesKt.getIntRange(0, var9);
         Iterator var16 = var14.iterator();

         while (var16.hasNext()) {
            ((IntIterator)var16).getInt();
            var40.remove(var8);
         }

         String var48 = var2;
         String var10001 = var3;
         String var10002 = CollectionsKt.getString(var40, " ", null, null, 0, null, null, 62, null);
         List var10003;
         if (Intrinsics.checkObject(var2, "url")) {
            var14 = var13;
            String var31 = var10002;
            Collection var43 = new ArrayList(CollectionsKt.getInt(var14, 10));

            for (Object var45 : var14) {
               String var46 = (String)var45;
               var43.add(StringsKt.getString(var46, '§', null, 2, null));
            }

            List var32 = (List)var43;
            var48 = var2;
            var10001 = var3;
            var10002 = var31;
            var10003 = var32;
         } else {
            var10003 = var13;
         }

         String var35 = CollectionsKt.getString(var10003, " ", null, null, 0, null, null, 62, null);
         String var36 = var10002;
         String var37 = var10001;
         String var38 = var48;
         return new JsonMessagePart(var38, var37, var36, var35, var12, var11);
      }
   }

   public static JsonMessagePart getJsonMessagePartDefault(
      JsonMessageParser var0, String var1, String var2, String var3, JsonMessagePart var4, int var5, Object var6
   ) {
      if ((var5 & 8) != 0) {
         var4 = null;
      }

      return var0.getJsonMessagePart(var1, var2, var3, var4);
   }

   public final JsonMessageTemplate getJsonMessageTemplate(@NotNull String var1, @NotNull Sender var2) {
      String var3 = formatSplitter.getString(var1);
      String var4 = this.getString2(var2);
      String var5 = this.getString(var2);
      JsonMessagePart var6 = getJsonMessagePartDefault(this, var3, "hoverText", var4, null, 8, null);
      JsonMessagePart var7 = getJsonMessagePartDefault(this, var6.getString3(), "run", var5, null, 8, null);
      JsonMessagePart var8 = getJsonMessagePartDefault(this, var7.getString3(), "url", var5, null, 8, null);
      JsonMessagePart var9 = getJsonMessagePartDefault(this, var8.getString3(), "suggest", var5, null, 8, null);
      JsonMessagePart var10 = getJsonMessagePartDefault(this, var9.getString3(), "copy", var5, null, 8, null);
      if (!var6.isFlag() && !var7.isFlag() && !var8.isFlag() && !var9.isFlag() && !var10.isFlag()) {
         return new JsonMessageTemplate(var3, null, new JsonMessagePart[0]);
      } else {
         JSONObject var11 = null;
         JSONObject var12 = null;
         int var13 = 0;
         if (var6.isFlag()) {
            JSONArray var14 = new JSONArray();
            String var15 = var6.getString4();
            JSONObject var16 = getJSONObjectDefault(this, var15, var2, null, null, null, 28, null);
            var14.add(var16);
            var12 = new JSONObject();
            var12.put("action", "show_text");
            var12.put("value", var14);
            var13++;
         }

         if (var7.isFlag()) {
            var11 = new JSONObject();
            var11.put("action", "run_command");
            if (this.checkSender(var2)) {
               var11.put("command", var7.getString4());
            } else {
               var11.put("value", var7.getString4());
            }

            var13++;
         }

         if (var9.isFlag()) {
            var11 = new JSONObject();
            var11.put("action", "suggest_command");
            if (this.checkSender(var2)) {
               var11.put("command", var9.getString4());
            } else {
               var11.put("value", var9.getString4());
            }

            var13++;
         }

         if (var10.isFlag()) {
            var11 = new JSONObject();
            var11.put("action", "copy_to_clipboard");
            var11.put("value", var9.getString4());
            var13++;
         }

         if (var8.isFlag() && StringsKt.checkString(var8.getString4(), "https:", false, 2, null)) {
            var11 = new JSONObject();
            var11.put("action", "open_url");
            if (this.checkSender(var2)) {
               var11.put("url", var8.getString4());
            } else {
               var11.put("value", var8.getString4());
            }

            var13++;
         }

         JSONObject var22 = null;
         String var23 = var3;
         Pair[] var17 = new Pair[]{
            TuplesKt.getPair(var6, var12),
            TuplesKt.getPair(var7, var11),
            TuplesKt.getPair(var8, var11),
            TuplesKt.getPair(var9, var11),
            TuplesKt.getPair(var10, var11)
         };
         Pair[] var24 = var17;
         int var26 = 0;

         for (int var18 = var17.length; var26 < var18; var26++) {
            Pair var19 = var24[var26];
            JsonMessagePart var20 = (JsonMessagePart)var19.getObject();
            JSONObject var21 = (JSONObject)var19.getObject2();
            if (var20.isFlag()) {
               var22 = this.getJSONObject(var20.getString3(), var2, var21, var20.getString2(), var22);
               var23 = var20.getString3();
            }
         }

         if (var13 > 1 && var22 != null) {
            var22 = this.getJSONObject(var22, var2);
         }

         JsonMessagePart[] var25 = new JsonMessagePart[]{var6, var7, var8, var9, var10};
         return new JsonMessageTemplate(var23, var22, var25);
      }
   }

   public final String getString(@NotNull Sender var1) {
      return this.checkSender(var1) ? "click_event" : "clickEvent";
   }

   public final String getString2(@NotNull Sender var1) {
      return this.checkSender(var1) ? "hover_event" : "hoverEvent";
   }

   public final JSONObject getJSONObject(@NotNull String var1, @NotNull Sender var2, @Nullable JSONObject var3, @NotNull String var4, @Nullable JSONObject var5) {
      JSONObject var6 = var5;
      Object var7 = null;

      try {
         boolean var8 = var5 == null;
         if (var8) {
            ChatComponentVersion var9 = ChatComponentVersion.V1_21_5;
            int var10000 = !this.checkSender(var2) ? 0 : 770;
            int var10 = var10000;
            if (var10 < 770) {
               var9 = ChatComponentVersion.V1_16;
            }

            VersionedComponentSerializer var26 = VersionedComponentSerializer.getVersionedComponentSerializer(var9);
            BaseComponent[] var12 = LegacyTextParser.getBaseComponentArray(var1);
            String var11 = var26.getString(Arrays.copyOf(var12, var12.length));
            JSONObject var29 = (JSONObject) new JSONParser().getObject(var11);
            Intrinsics.handleObject(var29, "");
            var6 = var29;
            if (!this.checkJSONObject(var3, var6, var4)) {
               Object var13 = var6.get("contents");
               if (Intrinsics.checkObject(var13, "null")) {
                  LiteBansCore var17 = (LiteBansCore)this.platform.getModule(LiteBansCore.class);
                  Intrinsics.handleObject(var17);
                  Object var10003 = var6.get("contents");
                  Intrinsics.handleObject(var10003, "");
                  String var19 = (String)var10003;
                  if (Intrinsics.checkObject(var19, "sort")) {
                     String[] var21 = new String[]{"00"};
                     var10003 = var21;
                  } else {
                     String[] var25 = new String[]{"01:" + var19};
                     var10003 = var25;
                  }

                  String[] var22 = (String[])var10003;
                  TextArgs var27 = new TextArgs(var17, Arrays.copyOf(var22, var22.length));
                  if (var27.getInt() == var17.hashCode()) {
                     throw new IllegalArgumentException(var6.toString());
                  }
               } else if (Intrinsics.checkObject(var13, "true") || Intrinsics.checkObject(var13, "false")) {
                  this.getJSONObject(var6, var2);
               }
            }
         } else {
            JSONObject var28 = var6;
            if (var6 == null) {
               var28 = new JSONObject();
            }

            var6 = var28;
            if (var8) {
               var6.put("text", var1);
            }

            if (var3 != null) {
               var6.put(var4, var3);
            }
         }

         return var6;
      } catch (Exception var23) {
         throw new RuntimeException("Failed to build JSON: \"" + var6 + "\"; last result: " + var7, var23);
      }
   }

   public static JSONObject getJSONObjectDefault(
      JsonMessageParser var0, String var1, Sender var2, JSONObject var3, String var4, JSONObject var5, int var6, Object var7
   ) {
      if ((var6 & 4) != 0) {
         var3 = null;
      }

      if ((var6 & 8) != 0) {
         var4 = "";
      }

      if ((var6 & 16) != 0) {
         var5 = null;
      }

      return var0.getJSONObject(var1, var2, var3, var4, var5);
   }

   private final boolean checkJSONObject(JSONObject var1, JSONObject var2, String var3) {
      if (var1 == null) {
         return false;
      } else {
         JSONArray var4 = (JSONArray)var2.get("extra");
         if (var4 != null) {
            Iterable var5 = var4;
            Collection var8 = new ArrayList();

            for (Object var11 : var5) {
               if (var11 instanceof JSONObject) {
                  var8.add(var11);
               }
            }

            for (Object var13 : (List)var8) {
               JSONObject var9 = (JSONObject)var13;
               var9.put(var3, var1);
            }
         }

         var2.put(var3, var1);
         return true;
      }
   }

   public final JSONObject getJSONObject(@NotNull JSONObject var1, @NotNull Sender var2) {
      if (!Intrinsics.checkObject(var1.get("text"), "")) {
         Map var3 = var1;
         String var4 = "text";
         JsonMessagePart var10000 = getJsonMessagePartDefault(this, (String)var1.get("text"), null, 2, null);
         String var5 = var10000 != null ? var10000.getString3() : null;
         var3.put(var4, var5);
      }

      JSONArray var16 = (JSONArray)var1.get("extra");
      JsonMessagePart var17 = null;
      if (var16 != null) {
         Iterable var6 = var16;
         Collection var9 = new ArrayList();

         for (Object var12 : var6) {
            if (var12 instanceof JSONObject) {
               var9.add(var12);
            }
         }

         for (Object var19 : (List)var9) {
            JSONObject var10 = (JSONObject)var19;
            JsonMessagePart var20 = this.getJsonMessagePart((String)var10.get("text"), var17);
            var17 = var20;
            var10.put("text", var20 != null ? var20.getString3() : null);
            String var13 = "click_event";
            String var14 = "clickEvent";
            if (!this.checkSender(var2)) {
               String var15 = var13;
               var13 = var14;
               var14 = var15;
            }

            if (var10.containsKey(var13) && var10.containsKey(var14)) {
               var10.remove(var14);
            }

            var13 = "hover_event";
            var14 = "hoverEvent";
            if (!this.checkSender(var2)) {
               String var23 = var13;
               var13 = var14;
               var14 = var23;
            }

            if (var10.containsKey(var13) && var10.containsKey(var14)) {
               var10.remove(var14);
            }
         }
      }

      return var1;
   }

   private final JsonMessagePart getJsonMessagePart(String var1, JsonMessagePart var2) {
      if (var1 == null) {
         return null;
      } else {
         JsonMessagePart var3 = this.getJsonMessagePart(var1, "hoverText", "", var2);
         JsonMessagePart var4 = this.getJsonMessagePart(var3.getString3(), "run", "", var2);
         JsonMessagePart var5 = this.getJsonMessagePart(var4.getString3(), "url", "", var2);
         JsonMessagePart var6 = this.getJsonMessagePart(var5.getString3(), "suggest", "", var2);
         JsonMessagePart var7 = this.getJsonMessagePart(var6.getString3(), "copy", "", var2);
         if (var7.isFlag() || var7.isFlag2()) {
            return var7;
         } else if (var6.isFlag() || var6.isFlag2()) {
            return var6;
         } else if (var5.isFlag() || var5.isFlag2()) {
            return var5;
         } else {
            return !var4.isFlag() && !var4.isFlag2() ? var3 : var4;
         }
      }
   }

   public static JsonMessagePart getJsonMessagePartDefault(JsonMessageParser var0, String var1, JsonMessagePart var2, int var3, Object var4) {
      if ((var3 & 2) != 0) {
         var2 = null;
      }

      return var0.getJsonMessagePart(var1, var2);
   }

   @Override
   public void onEnable() {
   }

   public final boolean checkSender(@NotNull Sender var1) {
      if (this.platform.getPlatformId() == 4) {
         return flag;
      } else {
         Version var2 = VersionParser.getVersionDefault(Version.versionParser, this.platform.createBridge().getServerVersion(), null, 2, null);
         return var2.checkInt(1, 21, 5);
      }
   }
}
