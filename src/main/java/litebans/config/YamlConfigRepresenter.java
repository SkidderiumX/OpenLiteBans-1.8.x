package litebans.config;

import java.util.Map;
import litebans.lib.kotlin.jvm.internal.Intrinsics;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

public final class YamlConfigRepresenter extends Representer {
   public YamlConfigRepresenter(DumperOptions var1) {
      super();
      Map var2 = this.representers;
      Class<ConfigNode> var3 = ConfigNode.class;
      Represent var4 = a -> getNode(this, (Object) a);
      var2.put(var3, var4);
   }

   private static final Node getNode(YamlConfigRepresenter var0, Object var1) {
      Intrinsics.handleObject(var1, "");
      return var0.represent(((ConfigNode)var1).map);
   }
}