package litebans.config;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.representer.Representer;

public class YamlRepresenter extends Representer {
   public final YamlThreadLocal yamlThreadLocal;

   public YamlRepresenter(YamlThreadLocal var1, DumperOptions var2) {
      super();
      this.yamlThreadLocal = var1;
      this.representers.put(ConfigNode.class, new YamlRepresent(this));
   }
}
