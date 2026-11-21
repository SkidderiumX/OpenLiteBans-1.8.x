package litebans.chat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.lang.reflect.Type;

public class VersionedComponentSerializer implements JsonDeserializer {
   private final Gson gson;
   private final ChatComponentVersion chatComponentVersion;
   private static final VersionedComponentSerializer INSTANCE = new VersionedComponentSerializer(ChatComponentVersion.V1_16);
   private static final VersionedComponentSerializer INSTANCE2 = new VersionedComponentSerializer(ChatComponentVersion.V1_21_5);
   public static final ThreadLocal threadLocal = new ThreadLocal();

   public VersionedComponentSerializer(ChatComponentVersion var1) {
      this.chatComponentVersion = var1;
      this.gson = new GsonBuilder()
         .registerTypeAdapter(BaseComponent.class, this)
         .registerTypeAdapter(TextComponent.class, new TextComponentSerializer(this))
         .registerTypeAdapter(ChatStyle.class, new ChatStyleSerializer())
         .registerTypeAdapter(HoverText.class, new HoverContentSerializer())
         .create();
   }

   public static VersionedComponentSerializer getVersionedComponentSerializer(ChatComponentVersion var0) {
      switch (VersionedComponentSerializerWhenMappings.switchMap[var0.ordinal()]) {
         case 1:
            return INSTANCE;
         case 2:
            return INSTANCE2;
         default:
            throw new IllegalArgumentException("Unknown version " + var0);
      }
   }

   @Deprecated
   public static VersionedComponentSerializer getInstance() {
      return INSTANCE;
   }

   public BaseComponent[] getBaseComponentArray(String var1) {
      JsonElement var2 = new JsonParser().parse(var1);
      return var2.isJsonArray()
         ? (BaseComponent[])this.gson.fromJson(var2, BaseComponent[].class)
         : new BaseComponent[]{(BaseComponent)this.gson.fromJson(var2, BaseComponent.class)};
   }

   public String getString(BaseComponent[] var1) {
      return var1.length == 1 ? this.gson.toJson(var1[0]) : this.gson.toJson(new TextComponent(var1));
   }

   public BaseComponent getBaseComponent(JsonElement var1, Type var2, JsonDeserializationContext var3) {
      return (BaseComponent)(var1.isJsonPrimitive() ? new TextComponent(var1.getAsString()) : (BaseComponent)var3.deserialize(var1, TextComponent.class));
   }

   public ChatComponentVersion getChatComponentVersion() {
      return this.chatComponentVersion;
   }

   public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) {
      return this.getBaseComponent(var1, var2, var3);
   }
}
