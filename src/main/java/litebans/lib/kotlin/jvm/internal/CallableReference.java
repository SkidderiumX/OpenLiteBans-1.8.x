package litebans.lib.kotlin.jvm.internal;

import java.io.Serializable;

public abstract class CallableReference implements KCallable, Serializable {
   private transient KCallable kCallable;
   public final Object object;
   private final Class classValue;
   private final String string;
   private final String string2;
   private final boolean flag;
   public static final Object object2 = CallableReferenceNoReceiver.getInstance();

   public CallableReference() {
      this(object2);
   }

   public CallableReference(Object var1) {
      this(var1, null, null, null, false);
   }

   public CallableReference(Object var1, Class var2, String var3, String var4, boolean var5) {
      this.object = var1;
      this.classValue = var2;
      this.string = var3;
      this.string2 = var4;
      this.flag = var5;
   }

   public abstract KCallable getKCallable();

   public Object getObject() {
      return this.object;
   }

   public KCallable getKCallable2() {
      KCallable var1 = this.kCallable;
      if (var1 == null) {
         var1 = this.getKCallable();
         this.kCallable = var1;
      }

      return var1;
   }

   public KDeclarationContainer getKDeclarationContainer() {
      return null;
   }

   @Override
   public String getName() {
      return this.string;
   }

   public String getString2() {
      return this.string2;
   }
}
