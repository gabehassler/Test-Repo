
package dr.evolution.tree;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface TreeTraitProvider {

    TreeTrait[] getTreeTraits();

    TreeTrait getTreeTrait(String key);

    public class Helper implements TreeTraitProvider {

        public Helper() {
        }

        public Helper(TreeTrait trait) {
           addTrait(trait);
        }

        public Helper(String key, TreeTrait trait) {
           addTrait(key, trait);
        }

        public Helper(TreeTrait[] traits) {
            addTraits(traits);
        }

        public Helper(Collection<TreeTrait> traits) {
            addTraits(traits);
        }

        public void addTrait(TreeTrait trait) {
            traits.put(trait.getTraitName(), trait);
        }

        public void addTrait(String key, TreeTrait trait) {
            if (traits.containsKey(key)) {
                throw new RuntimeException("All traits must have unique names");
            }
            traits.put(key, trait);
        }

        public void addTraits(TreeTrait[] traits) {
            for (TreeTrait trait : traits) {
                this.traits.put(trait.getTraitName(), trait);
            }
        }

        public void addTraits(Collection<TreeTrait> traits) {
            for (TreeTrait trait : traits) {
                this.traits.put(trait.getTraitName(), trait);
            }
        }


        // Implementation of TreeTraitProvider interface

        public TreeTrait[] getTreeTraits() {
            TreeTrait[] traitArray = new TreeTrait[traits.values().size()];
            return traits.values().toArray(traitArray);
        }

        public TreeTrait getTreeTrait(String key) {
            return traits.get(key);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Helper helper = (Helper) o;

            if (traits != null ? !traits.equals(helper.traits) : helper.traits != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return traits != null ? traits.hashCode() : 0;
        }

        // Private members

        private Map<String, TreeTrait> traits = new HashMap<String, TreeTrait>();
    }
}
