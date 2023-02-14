package com.github.bartimaeusnek.bartworks.common.loaders;

import static com.github.bartimaeusnek.bartworks.common.tileentities.multis.GT_TileEntity_ElectricImplosionCompressor.eicMap;
import static gregtech.api.enums.GT_Values.M;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import cpw.mods.fml.common.Loader;
import gregtech.api.enums.*;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;

public class ElectricImplosionCompressorRecipes implements Runnable {

    private static void addElectricImplosionRecipe(final ItemStack[] inputItems, final FluidStack[] inputFluids,
            final ItemStack[] outputItems, final FluidStack[] outputFluids, final int durationInTicks,
            final int EUPerTick) {
        eicMap.addRecipe(
                false,
                inputItems,
                outputItems,
                null,
                inputFluids,
                outputFluids,
                durationInTicks,
                EUPerTick,
                1);
    }

    private static final ItemStack[] circuits = new ItemStack[] { ItemList.Circuit_ExoticProcessor.get(1),
            ItemList.Circuit_OpticalAssembly.get(1), ItemList.Circuit_Biowaresupercomputer.get(1),
            ItemList.Circuit_Wetwaremainframe.get(1) };

    @Override
    public void run() {
        // Custom electric implosion compressor recipes. Cannot be overclocked.

        if (Loader.isModLoaded("eternalsingularity")) {

            addElectricImplosionRecipe(
                    // IN.
                    new ItemStack[] { GT_Values.NI },
                    new FluidStack[] { Materials.SpaceTime.getMolten(72L) },
                    // OUT.
                    new ItemStack[] { GT_ModHandler.getModItem("eternalsingularity", "eternal_singularity", 1L) },
                    new FluidStack[] { GT_Values.NF },
                    // Recipe stats.
                    100 * 20,
                    (int) TierEU.RECIPE_UMV);
        }

        addElectricImplosionRecipe(
                // IN.
                new ItemStack[] { GT_ModHandler.getModItem("GoodGenerator", "highDensityPlutoniumNugget", 5L) },
                new FluidStack[] { Materials.Infinity.getMolten(9L) },
                // OUT.
                new ItemStack[] { GT_ModHandler.getModItem("GoodGenerator", "highDensityPlutonium", 1L) },
                new FluidStack[] { GT_Values.NF },
                // Recipe stats.
                1,
                (int) TierEU.RECIPE_UEV);

        addElectricImplosionRecipe(
                // IN.
                new ItemStack[] { GT_ModHandler.getModItem("GoodGenerator", "highDensityUraniumNugget", 5L) },
                new FluidStack[] { Materials.Infinity.getMolten(9L) },
                // OUT.
                new ItemStack[] { GT_ModHandler.getModItem("GoodGenerator", "highDensityUranium", 1L) },
                new FluidStack[] { GT_Values.NF },
                // Recipe stats.
                1,
                (int) TierEU.RECIPE_UEV);

        addElectricImplosionRecipe(
                // IN.
                new ItemStack[] { GT_ModHandler.getModItem("GoodGenerator", "highDensityThoriumNugget", 5L) },
                new FluidStack[] { Materials.Infinity.getMolten(9L) },
                // OUT.
                new ItemStack[] { GT_ModHandler.getModItem("GoodGenerator", "highDensityThorium", 1L) },
                new FluidStack[] { GT_Values.NF },
                // Recipe stats.
                1,
                (int) TierEU.RECIPE_UEV);

        // Magneto material recipe for base fluid.
        addElectricImplosionRecipe(
                // IN.
                new ItemStack[] { GT_OreDictUnificator.get(OrePrefixes.nanite, Materials.WhiteDwarfMatter, 1L),
                        GT_OreDictUnificator.get(OrePrefixes.nanite, Materials.Universium, 1L),
                        GT_OreDictUnificator.get(OrePrefixes.nanite, Materials.BlackDwarfMatter, 1L) },
                new FluidStack[] { Materials.RawStarMatter.getFluid(16 * 144L) },
                // OUT.
                new ItemStack[] { GT_Values.NI },
                new FluidStack[] { Materials.MagnetohydrodynamicallyConstrainedStarMatter.getMolten(4 * 144L) },
                // Recipe stats.
                20 * 4,
                (int) TierEU.RECIPE_UXV);

        addMagnetohydrodynamicallyConstrainedStarMatterPartRecipes();
    }

    private void addMagnetohydrodynamicallyConstrainedStarMatterPartRecipes() {

        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.frameGt, 1);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.nugget, 9);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.ingot, 1);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.plate, 1);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.plateDense, 1);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.stick, 2);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.round, 8);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.bolt, 8);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.screw, 8);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.ring, 4);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.foil, 8);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.itemCasing, 2);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.gearGtSmall, 1);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.rotor, 1);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.stickLong, 1);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.springSmall, 2);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.spring, 1);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.gearGt, 1);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.wireFine, 8);
    }

    private void addWhiteDwarfMagnetoEICRecipe(final OrePrefixes part, final int multiplier) {

        final int partFraction = (int) (144 * part.mMaterialAmount / M);

        for (ItemStack circuit : circuits) {
            addElectricImplosionRecipe(
                    new ItemStack[] { circuit, GT_OreDictUnificator.get(part, Materials.Universium, multiplier) },
                    new FluidStack[] { Materials.MagnetohydrodynamicallyConstrainedStarMatter.getMolten(partFraction) },
                    new ItemStack[] { GT_OreDictUnificator
                            .get(part, Materials.MagnetohydrodynamicallyConstrainedStarMatter, multiplier) },
                    new FluidStack[] { GT_Values.NF },
                    (int) (20 * partFraction / 144.0),
                    (int) TierEU.RECIPE_UXV);
        }
    }
}
