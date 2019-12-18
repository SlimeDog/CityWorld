package me.daddychurchill.CityWorld.Support.Materials;

import org.bukkit.Material;
import org.bukkit.TreeSpecies;

public class Wood_Double_Slab extends Wood {

    public Wood_Double_Slab() {
        super(Material.WOOD_DOUBLE_STEP);
    }

    public Wood_Double_Slab(TreeSpecies species) {
        super(Material.WOOD_DOUBLE_STEP, species);
    }

    @Override
    public Wood_Double_Slab clone() {
        return (Wood_Double_Slab) super.clone();
    }
}
