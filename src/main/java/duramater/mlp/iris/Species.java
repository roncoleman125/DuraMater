package duramater.mlp.iris;

import org.jetbrains.annotations.NotNull;

public class Species implements Comparable<Species> {
    int category;
    public Species(int category) {
        this.category = category;
    }

    public static Species parseSpecies(String s) {
        if(s.equals("setosa"))
         return new Species(0);
        else if(s.equals("virginica"))
            return new Species(1);
        else
            return new Species(2);
    }

    @Override
    public int compareTo(@NotNull Species o) {
        return 0;
    }
}
