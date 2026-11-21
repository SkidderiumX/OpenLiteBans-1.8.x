package litebans.config;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.constructor.Constructor;

public class YamlThreadLocal extends ThreadLocal {
   public final YamlConfigFormat yamlConfigFormat;

   public YamlThreadLocal(YamlConfigFormat var1) {
      this.yamlConfigFormat = var1;
   }

   public Yaml getYaml() {
      DumperOptions var1 = new DumperOptions();
      var1.setDefaultFlowStyle(FlowStyle.BLOCK);
      YamlRepresenter var2 = new YamlRepresenter(this, var1);
      return new Yaml(new Constructor(), var2, var1);
   }

   @Override
   public Object initialValue() {
      return this.getYaml();
   }
}
