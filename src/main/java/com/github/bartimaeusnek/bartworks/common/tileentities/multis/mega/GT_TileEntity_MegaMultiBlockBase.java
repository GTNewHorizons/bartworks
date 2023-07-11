package com.github.bartimaeusnek.bartworks.common.tileentities.multis.mega;

import static gregtech.api.enums.GT_HatchElement.Energy;
import static gregtech.api.enums.GT_Values.V;
import static gregtech.api.enums.Mods.TecTech;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.github.bartimaeusnek.bartworks.util.BW_Tooltip_Reference;
import com.github.bartimaeusnek.bartworks.util.BW_Util;
import com.github.bartimaeusnek.crossmod.tectech.TecTechEnabledMulti;
import com.github.bartimaeusnek.crossmod.tectech.helper.TecTechUtils;
import com.github.bartimaeusnek.crossmod.tectech.tileentites.tiered.LowPowerLaser;
import com.github.technus.tectech.thing.metaTileEntity.hatch.GT_MetaTileEntity_Hatch_EnergyMulti;
import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.structurelib.StructureLibAPI;
import com.gtnewhorizon.structurelib.structure.AutoPlaceEnvironment;
import com.gtnewhorizon.structurelib.structure.IStructureElement;

import cpw.mods.fml.common.Optional;
import gregtech.api.enums.Mods;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_ExtendedPowerMultiBlockBase;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_Energy;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_Muffler;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_MultiBlockBase;
import gregtech.api.util.GT_Utility;
import gregtech.api.util.IGT_HatchAdder;

@Optional.Interface(
        iface = "com.github.bartimaeusnek.crossmod.tectech.TecTechEnabledMulti",
        modid = Mods.Names.TECTECH,
        striprefs = true)
public abstract class GT_TileEntity_MegaMultiBlockBase<T extends GT_TileEntity_MegaMultiBlockBase<T>>
        extends GT_MetaTileEntity_ExtendedPowerMultiBlockBase<T> implements TecTechEnabledMulti {

    protected GT_TileEntity_MegaMultiBlockBase(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_TileEntity_MegaMultiBlockBase(String aName) {
        super(aName);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        // Migration code
        if (aNBT.hasKey("lEUt")) {
            this.lEUt = aNBT.getLong("lEUt");
        }
    }

    @SuppressWarnings("rawtypes")
    @Optional.Method(modid = Mods.Names.TECTECH)
    boolean areLazorsLowPowa() {
        Collection collection = this.getTecTechEnergyTunnels();
        if (!collection.isEmpty()) for (Object tecTechEnergyMulti : collection)
            if (!(tecTechEnergyMulti instanceof LowPowerLaser)) return false;
        return true;
    }

    @Override
    @Optional.Method(modid = Mods.Names.TECTECH)
    public List<GT_MetaTileEntity_Hatch_Energy> getVanillaEnergyHatches() {
        return this.mEnergyHatches;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Optional.Method(modid = Mods.Names.TECTECH)
    public List getTecTechEnergyTunnels() {
        return mExoticEnergyHatches;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Optional.Method(modid = Mods.Names.TECTECH)
    public List getTecTechEnergyMultis() {
        return mExoticEnergyHatches;
    }

    @Deprecated
    @Override
    protected void calculateOverclockedNessMulti(int aEUt, int aDuration, int mAmperage, long maxInputVoltage) {
        calculateOverclockedNessMultiInternal(aEUt, aDuration, maxInputVoltage, false);
    }

    @Deprecated
    @Override
    protected void calculatePerfectOverclockedNessMulti(int aEUt, int aDuration, int mAmperage, long maxInputVoltage) {
        calculateOverclockedNessMultiInternal(aEUt, aDuration, maxInputVoltage, true);
    }

    @Override
    public String[] getInfoData() {
        return TecTech.isModLoaded() ? this.getInfoDataArray(this) : super.getInfoData();
    }

    protected String[] getExtendedInfoData() {
        return new String[0];
    }

    @Override
    public String[] getInfoDataArray(GT_MetaTileEntity_MultiBlockBase multiBlockBase) {
        int mPollutionReduction = 0;

        for (GT_MetaTileEntity_Hatch_Muffler tHatch : this.mMufflerHatches) {
            if (GT_MetaTileEntity_MultiBlockBase.isValidMetaTileEntity(tHatch)) {
                mPollutionReduction = Math.max(tHatch.calculatePollutionReduction(100), mPollutionReduction);
            }
        }

        long[] ttHatches = getCurrentInfoData();
        long storedEnergy = ttHatches[0];
        long maxEnergy = ttHatches[1];

        for (GT_MetaTileEntity_Hatch_Energy tHatch : this.mEnergyHatches) {
            if (GT_MetaTileEntity_MultiBlockBase.isValidMetaTileEntity(tHatch)) {
                storedEnergy += tHatch.getBaseMetaTileEntity().getStoredEU();
                maxEnergy += tHatch.getBaseMetaTileEntity().getEUCapacity();
            }
        }

        long nominalV = TecTech.isModLoaded() ? TecTechUtils.getnominalVoltageTT(this)
                : BW_Util.getnominalVoltage(this);
        String tName = BW_Util.getTierNameFromVoltage(nominalV);
        if (tName.equals("MAX+")) tName = EnumChatFormatting.OBFUSCATED + "MAX+";

        String[] extendedInfo = getExtendedInfoData();

        String[] baseInfo = new String[] {
                StatCollector.translateToLocal("GT5U.multiblock.Progress") + ": "
                        + EnumChatFormatting.GREEN
                        + GT_Utility.formatNumbers(this.mProgresstime / 20)
                        + EnumChatFormatting.RESET
                        + " s / "
                        + EnumChatFormatting.YELLOW
                        + GT_Utility.formatNumbers(this.mMaxProgresstime / 20)
                        + EnumChatFormatting.RESET
                        + " s",
                StatCollector.translateToLocal("GT5U.multiblock.energy") + ": "
                        + EnumChatFormatting.GREEN
                        + GT_Utility.formatNumbers(storedEnergy)
                        + EnumChatFormatting.RESET
                        + " EU / "
                        + EnumChatFormatting.YELLOW
                        + GT_Utility.formatNumbers(maxEnergy)
                        + EnumChatFormatting.RESET
                        + " EU",
                StatCollector.translateToLocal("GT5U.multiblock.usage") + ": "
                        + EnumChatFormatting.RED
                        + GT_Utility.formatNumbers(-this.lEUt)
                        + EnumChatFormatting.RESET
                        + " EU/t",
                StatCollector.translateToLocal("GT5U.multiblock.mei") + ": "
                        + EnumChatFormatting.YELLOW
                        + GT_Utility.formatNumbers(this.getMaxInputVoltage())
                        + EnumChatFormatting.RESET
                        + " EU/t(*"
                        + GT_Utility.formatNumbers(TecTechUtils.getMaxInputAmperage(this))
                        + "A) = "
                        + EnumChatFormatting.YELLOW
                        + GT_Utility.formatNumbers(nominalV)
                        + EnumChatFormatting.RESET,
                StatCollector.translateToLocal("GT5U.machines.tier") + ": "
                        + EnumChatFormatting.YELLOW
                        + tName
                        + EnumChatFormatting.RESET,
                StatCollector.translateToLocal("GT5U.multiblock.problems") + ": "
                        + EnumChatFormatting.RED
                        + (this.getIdealStatus() - this.getRepairStatus())
                        + EnumChatFormatting.RESET
                        + " "
                        + StatCollector.translateToLocal("GT5U.multiblock.efficiency")
                        + ": "
                        + EnumChatFormatting.YELLOW
                        + (float) this.mEfficiency / 100.0F
                        + EnumChatFormatting.RESET
                        + " %",
                StatCollector.translateToLocal("GT5U.multiblock.pollution") + ": "
                        + EnumChatFormatting.GREEN
                        + mPollutionReduction
                        + EnumChatFormatting.RESET
                        + " %" };

        String[] combinedInfo = Arrays.copyOf(baseInfo, baseInfo.length + extendedInfo.length + 1);

        System.arraycopy(extendedInfo, 0, combinedInfo, baseInfo.length, extendedInfo.length);

        combinedInfo[combinedInfo.length - 1] = BW_Tooltip_Reference.BW;

        return combinedInfo;
    }

    /**
     * Calculates the overclock for megas. Will set this.mMaxProgressTime and this.lEUt automatically
     *
     * @deprecated Use GT_OverclockCalculator instead
     *
     * @param aEUt            EUt of the recipe
     * @param aDuration       Duration of the recipe
     * @param maxInputVoltage Max input voltage of the mega (nominal, so 1A)
     * @param perfectOC       Flag if the multi has perfect OC
     * @return Number of performed overclocks
     */
    @Deprecated
    protected byte calculateOverclockedNessMultiInternal(long aEUt, int aDuration, long maxInputVoltage,
            boolean perfectOC) {
        byte mTier = (byte) Math.max(0, BW_Util.getTier(maxInputVoltage)), overclockCount = 0;
        if (mTier == 0) {
            // Long time calculation
            long xMaxProgresstime = ((long) aDuration) << 1;
            if (xMaxProgresstime > Integer.MAX_VALUE - 1) {
                // make impossible if too long
                this.lEUt = Integer.MAX_VALUE - 1;
                this.mMaxProgresstime = Integer.MAX_VALUE - 1;
            } else {
                this.lEUt = aEUt >> 2;
                this.mMaxProgresstime = (int) xMaxProgresstime;
            }
        } else {
            // Long EUt calculation
            long xEUt = aEUt;
            // Isnt too low EUt check?
            long tempEUt = Math.max(xEUt, V[1]);

            this.mMaxProgresstime = aDuration;

            while (tempEUt <= BW_Util.getTierVoltage(mTier - 1)) {
                tempEUt <<= 2; // this actually controls overclocking
                // xEUt *= 4;//this is effect of everclocking
                this.mMaxProgresstime >>= perfectOC ? 2 : 1; // this is effect of overclocking
                xEUt = this.mMaxProgresstime <= 0 ? xEUt >> 1 : xEUt << 2; // U know, if the time is less than 1 tick
                                                                           // make the machine use less power
                overclockCount++;
            }

            while (xEUt > maxInputVoltage && xEUt >= aEUt) {
                // downclock one notch until we are good again, we have overshot.
                xEUt >>= 2;
                this.mMaxProgresstime <<= perfectOC ? 2 : 1;
                overclockCount--;
            }

            if (xEUt < aEUt) {
                xEUt <<= 2;
                this.mMaxProgresstime >>= perfectOC ? 2 : 1;
                overclockCount++;
            }

            this.lEUt = xEUt;
            if (this.lEUt == 0) this.lEUt = 1;
            if (this.mMaxProgresstime <= 0) this.mMaxProgresstime = 1; // set time to 1 tick
        }
        return overclockCount;
    }

    /**
     * Calculates the overclock for megas. Will set this.mMaxProgressTime and this.lEUt automatically
     *
     * @deprecated Use GT_OverclockCalculator instead
     *
     * @param aEUt            EUt of the recipe
     * @param aDuration       Duration of the recipe
     * @param maxInputVoltage Max input voltage of the mega (nominal, so 1A)
     */
    protected void calculateOverclockedNessMulti(long aEUt, int aDuration, long maxInputVoltage) {
        calculateOverclockedNessMultiInternal(aEUt, aDuration, maxInputVoltage, false);
    }

    /**
     * Calculates the overclock for megas. Will set this.mMaxProgressTime and this.lEUt automatically
     *
     * @deprecated Use GT_OverclockCalculator instead
     *
     * @param aEUt            EUt of the recipe
     * @param aDuration       Duration of the recipe
     * @param maxInputVoltage Max input voltage of the mega (nominal, so 1A)
     */
    @Deprecated
    protected void calculatePerfectOverclockedNessMulti(long aEUt, int aDuration, long maxInputVoltage) {
        calculateOverclockedNessMultiInternal(aEUt, aDuration, maxInputVoltage, true);
    }

    @Override
    public boolean isCorrectMachinePart(ItemStack itemStack) {
        return true;
    }

    @Override
    public int getDamageToComponent(ItemStack itemStack) {
        return 0;
    }

    @Override
    public boolean explodesOnComponentBreak(ItemStack itemStack) {
        return false;
    }

    @Override
    public int getMaxEfficiency(ItemStack itemStack) {
        return 10000;
    }

    @Override
    protected void setProcessingLogicPower(ProcessingLogic logic) {
        logic.setAvailableVoltage(getMaxInputEu());
        logic.setAvailableAmperage(1);
    }

    /**
     * @deprecated Since the multi now utilized {@link GT_MetaTileEntity_ExtendedPowerMultiBlockBase}, just use
     *             Energy.or(ExoticEnergy)
     */
    @Deprecated
    protected enum TTEnabledEnergyHatchElement implements IHatchElement<GT_TileEntity_MegaMultiBlockBase<?>> {

        INSTANCE;

        private static final List<? extends Class<? extends IMetaTileEntity>> mteClasses;

        static {
            ImmutableList.Builder<Class<? extends IMetaTileEntity>> builder = ImmutableList
                    .<Class<? extends IMetaTileEntity>>builder().addAll(Energy.mteClasses());
            if (TecTech.isModLoaded()) builder.add(GT_MetaTileEntity_Hatch_EnergyMulti.class);
            mteClasses = builder.build();
        }

        @Override
        public List<? extends Class<? extends IMetaTileEntity>> mteClasses() {
            return mteClasses;
        }

        @Override
        public IGT_HatchAdder<? super GT_TileEntity_MegaMultiBlockBase<?>> adder() {
            return GT_TileEntity_MegaMultiBlockBase::addEnergyInputToMachineList;
        }

        @Override
        public long count(GT_TileEntity_MegaMultiBlockBase<?> t) {
            return t.mEnergyHatches.size() + t.mExoticEnergyHatches.size();
        }
    }

    protected static class StructureElementAirNoHint<T> implements IStructureElement<T> {

        private static final StructureElementAirNoHint<?> INSTANCE = new StructureElementAirNoHint<>();

        @SuppressWarnings("unchecked")
        public static <T> IStructureElement<T> getInstance() {
            return (IStructureElement<T>) INSTANCE;
        }

        private StructureElementAirNoHint() {}

        @Override
        public boolean check(T o, World world, int x, int y, int z) {
            return world.isAirBlock(x, y, z);
        }

        @Override
        public boolean spawnHint(T o, World world, int x, int y, int z, ItemStack trigger) {
            if (world.blockExists(x, y, z) && !world.isAirBlock(x, y, z))
                // hint if this is obstructed. in case *someone* ever finish the transparent rendering
                StructureLibAPI.hintParticle(
                        world,
                        x,
                        y,
                        z,
                        StructureLibAPI.getBlockHint(),
                        StructureLibAPI.HINT_BLOCK_META_AIR);
            return true;
        }

        @Override
        public boolean placeBlock(T o, World world, int x, int y, int z, ItemStack trigger) {
            world.setBlockToAir(x, y, z);
            return true;
        }

        @Override
        public BlocksToPlace getBlocksToPlace(T t, World world, int x, int y, int z, ItemStack trigger,
                AutoPlaceEnvironment env) {
            return BlocksToPlace.createEmpty();
        }

        @Override
        public PlaceResult survivalPlaceBlock(T o, World world, int x, int y, int z, ItemStack trigger,
                AutoPlaceEnvironment env) {
            if (check(o, world, x, y, z)) return PlaceResult.SKIP;
            if (!StructureLibAPI.isBlockTriviallyReplaceable(world, x, y, z, env.getActor())) return PlaceResult.REJECT;
            world.setBlock(x, y, z, Blocks.air, 0, 2);
            return PlaceResult.ACCEPT;
        }
    }
}
