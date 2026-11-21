package litebans.lib.kotlin;

import litebans.lib.kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

public class ExceptionsKt__ExceptionsKt {
   public static final void handleThrowable(@NotNull Throwable throwable, @NotNull Throwable exception) {
      Intrinsics.handleObject3(throwable, "<this>");
      Intrinsics.handleObject3(exception, "exception");
      if (throwable != exception) {
         throwable.addSuppressed(exception);
      }
   }
}
