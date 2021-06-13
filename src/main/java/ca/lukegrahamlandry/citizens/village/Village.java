package ca.lukegrahamlandry.citizens.village;

import ca.lukegrahamlandry.citizens.entity.VillagerBase;
import ca.lukegrahamlandry.citizens.village.buildings.BuildingBase;

import java.util.ArrayList;
import java.util.List;

public class Village {
    // I'll have to save this data somewhere unless i have a clever way not to need it.
    // I could have the markers regenerate the village on world load instead:
    // buildings addOrCreate a village, villagers remember their home and work building in nbt.
    // yeah lets go with that
    // need mixin on item frames or add a Building Marker Frame to map out the village on world load

    public List<BuildingBase> buildings = new ArrayList<>();
    public List<VillagerBase> villagers = new ArrayList<>();

    public Village(){

    }

    // temp
    public static Village INSTANCE = new Village();
}
