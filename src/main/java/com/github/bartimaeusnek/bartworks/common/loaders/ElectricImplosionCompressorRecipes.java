package com.github.bartimaeusnek.bartworks.common.loaders;

import static com.github.bartimaeusnek.bartworks.common.tileentities.multis.GT_TileEntity_ElectricImplosionCompressor.eicMap;
import static gregtech.api.enums.GT_Values.M;
import static java.lang.Math.max;
import static java.lang.Math.min;

import cpw.mods.fml.common.Loader;
import gregtech.api.enums.*;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_Utility;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class ElectricImplosionCompressorRecipes implements Runnable {

    private static void addElectricImplosionRecipe(
            final ItemStack[] inputItems,
            final FluidStack[] inputFluids,
            final ItemStack[] outputItems,
            final FluidStack[] outputFluids,
            final int durationInTicks,
            final int EUPerTick) {
        eicMap.addRecipe(
                false, inputItems, outputItems, null, inputFluids, outputFluids, durationInTicks, EUPerTick, 1);
    }

    private static final ItemStack[] circuits = new ItemStack[] {
        ItemList.Circuit_ExoticProcessor.get(1),
        ItemList.Circuit_OpticalAssembly.get(1),
        ItemList.Circuit_Biowaresupercomputer.get(1),
        ItemList.Circuit_Wetwaremainframe.get(1)
    };

    @Override
    public void run() {
        // Custom electric implosion compressor recipes. Cannot be overclocked.

        if (Loader.isModLoaded("eternalsingularity")) {

            addElectricImplosionRecipe(
                    // IN.
                    new ItemStack[] {GT_Values.NI},
                    new FluidStack[] {Materials.SpaceTime.getMolten(72L)},
                    // OUT.
                    new ItemStack[] {GT_ModHandler.getModItem("eternalsingularity", "eternal_singularity", 1L)},
                    new FluidStack[] {GT_Values.NF},
                    // Recipe stats.
                    100 * 20,
                    (int) TierEU.RECIPE_UMV);
        }

        addElectricImplosionRecipe(
                // IN.
                new ItemStack[] {GT_ModHandler.getModItem("GoodGenerator", "highDensityPlutoniumNugget", 5L)},
                new FluidStack[] {Materials.Infinity.getMolten(9L)},
                // OUT.
                new ItemStack[] {GT_ModHandler.getModItem("GoodGenerator", "highDensityPlutonium", 1L)},
                new FluidStack[] {GT_Values.NF},
                // Recipe stats.
                1,
                (int) TierEU.RECIPE_UEV);

        addElectricImplosionRecipe(
                // IN.
                new ItemStack[] {GT_ModHandler.getModItem("GoodGenerator", "highDensityUraniumNugget", 5L)},
                new FluidStack[] {Materials.Infinity.getMolten(9L)},
                // OUT.
                new ItemStack[] {GT_ModHandler.getModItem("GoodGenerator", "highDensityUranium", 1L)},
                new FluidStack[] {GT_Values.NF},
                // Recipe stats.
                1,
                (int) TierEU.RECIPE_UEV);

        addElectricImplosionRecipe(
                // IN.
                new ItemStack[] {GT_ModHandler.getModItem("GoodGenerator", "highDensityThoriumNugget", 5L)},
                new FluidStack[] {Materials.Infinity.getMolten(9L)},
                // OUT.
                new ItemStack[] {GT_ModHandler.getModItem("GoodGenerator", "highDensityThorium", 1L)},
                new FluidStack[] {GT_Values.NF},
                // Recipe stats.
                1,
                (int) TierEU.RECIPE_UEV);

        // Magneto material recipe for base fluid.
        addElectricImplosionRecipe(
                // IN.
                new ItemStack[] {
                    GT_OreDictUnificator.get(OrePrefixes.nanite, Materials.WhiteDwarfMatter, 1L),
                    GT_OreDictUnificator.get(OrePrefixes.nanite, Materials.Universium, 1L),
                    GT_OreDictUnificator.get(OrePrefixes.nanite, Materials.BlackDwarfMatter, 1L)
                },
                new FluidStack[] {Materials.RawStarMatter.getFluid(16 * 144L)},
                // OUT.
                new ItemStack[] {GT_Values.NI},
                new FluidStack[] {Materials.MagnetohydrodynamicallyConstrainedStarMatter.getMolten(4 * 144L)},
                // Recipe stats.
                20 * 4,
                (int) TierEU.RECIPE_UXV);

        addMagnetohydrodynamicallyConstrainedStarMatterPartRecipes();
    }

    private void addMagnetohydrodynamicallyConstrainedStarMatterPartRecipes() {

        // Magnetohydrodynamically constrained recipes.
        addWhiteDwarfMagnetoEICRecipe(ItemList.White_Dwarf_Shape_Extruder_Ingot.get(0), OrePrefixes.ingot);

        addWhiteDwarfMagnetoEICRecipe(ItemList.White_Dwarf_Shape_Extruder_Plate.get(0), OrePrefixes.plate);

        addWhiteDwarfMagnetoEICRecipe(ItemList.White_Dwarf_Shape_Extruder_Rod.get(0), OrePrefixes.stick);

        addWhiteDwarfMagnetoEICRecipe(ItemList.White_Dwarf_Shape_Extruder_Bolt.get(0), OrePrefixes.bolt);

        addWhiteDwarfMagnetoEICRecipe(ItemList.White_Dwarf_Shape_Extruder_Ring.get(0), OrePrefixes.ring);

        addWhiteDwarfMagnetoEICRecipe(
                ItemList.White_Dwarf_Shape_Extruder_Turbine_Blade.get(0), OrePrefixes.turbineBlade);

        addWhiteDwarfMagnetoEICRecipe(ItemList.White_Dwarf_Shape_Extruder_Casing.get(0), OrePrefixes.itemCasing);

        addWhiteDwarfMagnetoEICRecipe(ItemList.White_Dwarf_Shape_Extruder_Small_Gear.get(0), OrePrefixes.gearGtSmall);

        addWhiteDwarfMagnetoEICRecipe(ItemList.White_Dwarf_Shape_Extruder_Rotor.get(0), OrePrefixes.rotor);

        addWhiteDwarfMagnetoEICRecipe(ItemList.White_Dwarf_Shape_Extruder_Gear.get(0), OrePrefixes.gear);

        addWhiteDwarfMagnetoEICRecipe(ItemList.White_Dwarf_Shape_Extruder_Rotor.get(0), OrePrefixes.rotor);

        // Other reshaping recipes.

        // Dense Plates.
        addOtherMagnetoEICRecipe(
                GT_OreDictUnificator.get(OrePrefixes.plate, Materials.MagnetohydrodynamicallyConstrainedStarMatter, 9L),
                GT_OreDictUnificator.get(
                        OrePrefixes.plateDense, Materials.MagnetohydrodynamicallyConstrainedStarMatter, 1L),
                1);

        // Nuggets.
        addOtherMagnetoEICRecipe(
                GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.MagnetohydrodynamicallyConstrainedStarMatter, 1L),
                GT_OreDictUnificator.get(
                        OrePrefixes.nugget, Materials.MagnetohydrodynamicallyConstrainedStarMatter, 9L),
                1);

        // Rounds.
        addOtherMagnetoEICRecipe(
                GT_OreDictUnificator.get(
                        OrePrefixes.nugget, Materials.MagnetohydrodynamicallyConstrainedStarMatter, 1L),
                GT_OreDictUnificator.get(OrePrefixes.round, Materials.MagnetohydrodynamicallyConstrainedStarMatter, 1L),
                1);

        // Screws.
        addOtherMagnetoEICRecipe(
                GT_OreDictUnificator.get(OrePrefixes.bolt, Materials.MagnetohydrodynamicallyConstrainedStarMatter, 1L),
                GT_OreDictUnificator.get(OrePrefixes.screw, Materials.MagnetohydrodynamicallyConstrainedStarMatter, 1L),
                1);

        // Foil.
        addOtherMagnetoEICRecipe(
                GT_OreDictUnificator.get(OrePrefixes.plate, Materials.MagnetohydrodynamicallyConstrainedStarMatter, 1L),
                GT_OreDictUnificator.get(OrePrefixes.foil, Materials.MagnetohydrodynamicallyConstrainedStarMatter, 4L),
                2);

        // Spring.
        addOtherMagnetoEICRecipe(
                GT_OreDictUnificator.get(
                        OrePrefixes.stickLong, Materials.MagnetohydrodynamicallyConstrainedStarMatter, 1L),
                GT_OreDictUnificator.get(
                        OrePrefixes.spring, Materials.MagnetohydrodynamicallyConstrainedStarMatter, 1L),
                1);

        // Small Spring.
        addOtherMagnetoEICRecipe(
                GT_OreDictUnificator.get(OrePrefixes.stick, Materials.MagnetohydrodynamicallyConstrainedStarMatter, 1L),
                GT_OreDictUnificator.get(
                        OrePrefixes.springSmall, Materials.MagnetohydrodynamicallyConstrainedStarMatter, 2L),
                1);

        // Long Rod.
        addOtherMagnetoEICRecipe(
                GT_OreDictUnificator.get(OrePrefixes.stick, Materials.MagnetohydrodynamicallyConstrainedStarMatter, 2L),
                GT_OreDictUnificator.get(
                        OrePrefixes.stickLong, Materials.MagnetohydrodynamicallyConstrainedStarMatter, 1L),
                2);

        // Frame Box.
        addOtherMagnetoEICRecipe(
                GT_OreDictUnificator.get(OrePrefixes.stick, Materials.MagnetohydrodynamicallyConstrainedStarMatter, 8L),
                GT_OreDictUnificator.get(
                        OrePrefixes.frameGt, Materials.MagnetohydrodynamicallyConstrainedStarMatter, 1L),
                3);
    }

    private static void addWhiteDwarfMagnetoEICRecipe(final ItemStack shape, final OrePrefixes part) {
        final int magnetoBaseTime = (int) (144 * (part.mMaterialAmount / M));

        // Always integer division.
        final double partFraction = part.mMaterialAmount / (double) M;

        for (int i = 0; i < 4; i++) {
            addElectricImplosionRecipe(
                    new ItemStack[] {
                        circuits[i],
                        shape,
                        GT_OreDictUnificator.get(part, Materials.Universium, min(1, (long) (1 / partFraction)))
                    },
                    new FluidStack[] {
                        Materials.MagnetohydrodynamicallyConstrainedStarMatter.getMolten((long) (144 * partFraction))
                    },
                    new ItemStack[] {
                        GT_OreDictUnificator.get(
                                part, Materials.MagnetohydrodynamicallyConstrainedStarMatter, min(1, (long)
                                        (1 / partFraction)))
                    },
                    new FluidStack[] {GT_Values.NF},
                    max(1, (int) (magnetoBaseTime * partFraction)),
                    (int) TierEU.RECIPE_UXV);
        }
    }

    private static void addOtherMagnetoEICRecipe(
            final ItemStack partInput, final ItemStack partOutput, final int circuitNumber) {
        for (int i = 0; i < 4; i++) {
            addElectricImplosionRecipe(
                    new ItemStack[] {circuits[i], partInput, GT_Utility.getIntegratedCircuit(circuitNumber)},
                    new FluidStack[] {GT_Values.NF},
                    new ItemStack[] {partOutput},
                    new FluidStack[] {GT_Values.NF},
                    5 * 20,
                    (int) TierEU.RECIPE_UXV);
        }
    }
}
