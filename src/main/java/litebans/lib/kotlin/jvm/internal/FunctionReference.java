package litebans.lib.kotlin.jvm.internal;

public class FunctionReference extends CallableReference implements FunctionBase, KFunction {
   private final int intValue;
   private final int intValue2;

   public FunctionReference(int var1, Object var2, Class var3, String var4, String var5, int var6) {
      super(var2, var3, var4, var5, (var6 & 1) == 1);
      this.intValue = var1;
      this.intValue2 = var6 >> 1;
   }

   @Override
   public KCallable getKCallable() {
      return this;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof FunctionReference)) {
         return var1 instanceof KFunction ? var1.equals(this.getKCallable2()) : false;
      } else {
         FunctionReference var2 = (FunctionReference)var1;
         return this.getName().equals(var2.getName())
            && this.getString2().equals(var2.getString2())
            && this.intValue2 == var2.intValue2
            && this.intValue == var2.intValue
            && Intrinsics.checkObject(this.getObject(), var2.getObject())
            && Intrinsics.checkObject(this.getKDeclarationContainer(), var2.getKDeclarationContainer());
      }
   }

   @Override
   public int hashCode() {
      return ((this.getKDeclarationContainer() == null ? 0 : this.getKDeclarationContainer().hashCode() * 31) + this.getName().hashCode()) * 31
         + this.getString2().hashCode();
   }

   @Override
   public String toString() {
      KCallable var1 = this.getKCallable2();
      if (var1 != this) {
         return var1.toString();
      } else {
         return "<init>".equals(this.getName())
            ? "constructor (Kotlin reflection is not available)"
            : "function " + this.getName() + " (Kotlin reflection is not available)";
      }
   }
}
