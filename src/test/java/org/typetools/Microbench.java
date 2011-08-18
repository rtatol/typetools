/**
 * 
 */
package org.typetools;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Jonathan Halterman
 */
public class Microbench {
  class Foo extends Bar<ArrayList<String>> {
  }

  class Bar<T extends List<String>> implements Baz<HashSet<Integer>, T> {
  }

  interface Baz<T1 extends Set<Integer>, T2 extends List<String>> {
  }

  interface Resolver {
    Class<?>[] resolve();
  }

  static final DecimalFormat format = new DecimalFormat();

  static final Resolver MapBasedResolver = new Resolver() {
    @Override
    public Class<?>[] resolve() {
      return TypeResolver.resolveArguments(Foo.class, Baz.class);
    }
  };

  static final Resolver ArrayBasedResolver = new Resolver() {
    @Override
    public Class<?>[] resolve() {
      return Types.resolveArguments(Baz.class, Foo.class);
    }
  };

  public static void main(String... args) throws Exception {
    // Warmup
    MapBasedResolver.resolve();
    ArrayBasedResolver.resolve();

    System.out.println("With map caching disabled\n");
    TypeResolver.disableCache();

    for (int i2 = 0; i2 < 10; i2++) {
      iterate(MapBasedResolver, "Map Based ");
      iterate(ArrayBasedResolver, "Array Based ");
      System.out.println();
    }

    System.out.println("With map caching enabled\n");
    TypeResolver.enableCache();

    for (int i2 = 0; i2 < 10; i2++) {
      iterate(MapBasedResolver, "Map Based ");
      iterate(ArrayBasedResolver, "Array Based ");
      System.out.println();
    }
  }

  static void iterate(Resolver resolver, String label) {
    int count = 100000;

    long time = System.currentTimeMillis();

    for (int i = 0; i < count; i++) {
      resolver.resolve();
    }

    time = System.currentTimeMillis() - time;

    System.out.println(label + format.format(count * 1000 / time) + " resolutions / second");
  }
}
