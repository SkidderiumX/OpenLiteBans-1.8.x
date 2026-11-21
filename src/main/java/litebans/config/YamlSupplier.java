package litebans.config;

import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

public final class YamlSupplier implements Supplier {
   private final Reader reader;
   private final Map map;

   public YamlSupplier(@NotNull Reader var1) {
      this.reader = var1;
      DumperOptions var2 = new DumperOptions();
      var2.setDefaultFlowStyle(FlowStyle.BLOCK);
      Representer var3 = new YamlConfigRepresenter(var2);
      Yaml var4 = new Yaml((BaseConstructor)(new Constructor()), var3, var2);
      Map var10001 = (Map)var4.loadAs(this.reader, LinkedHashMap.class);
      if (var10001 == null) {
         var10001 = new LinkedHashMap();
      }

      this.map = var10001;
   }

   public final Reader getReader() {
      return this.reader;
   }

   public Map getMap() {
      return this.map;
   }

   @Override
   public Object get() {
      return this.getMap();
   }
}
