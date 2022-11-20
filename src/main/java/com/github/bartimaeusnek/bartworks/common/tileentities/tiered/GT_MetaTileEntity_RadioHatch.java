/*
 * Copyright (c) 2018-2020 bartimaeusnek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.bartimaeusnek.bartworks.common.tileentities.tiered;

import static gregtech.api.enums.GT_Values.ticksBetweenSounds;

import com.github.bartimaeusnek.bartworks.API.modularUI.BW_UITextures;
import com.github.bartimaeusnek.bartworks.MainMod;
import com.github.bartimaeusnek.bartworks.util.BWRecipes;
import com.github.bartimaeusnek.bartworks.util.BW_ColorUtil;
import com.github.bartimaeusnek.bartworks.util.BW_Tooltip_Reference;
import com.github.bartimaeusnek.bartworks.util.MathUtils;
import com.gtnewhorizons.modularui.api.drawable.shapes.Rectangle;
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.api.math.Color;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.ProgressBar;
import com.gtnewhorizons.modularui.common.widget.ProgressBar.Direction;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GT_UIInfos;
import gregtech.api.gui.modularui.GT_UITextures;
import gregtech.api.gui.modularui.GUITextureSet;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.modularui.IAddGregtechLogo;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Utility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class GT_MetaTileEntity_RadioHatch extends GT_MetaTileEntity_Hatch implements IAddGregtechLogo {

    private final int cap;
    public int sievert;
    private long timer = 1;
    private long decayTime = 1;
    private short[] colorForGUI;
    private byte mass;
    private String material;
    private byte coverage;
    private ItemStack lastUsedItem = null;
    private boolean lastFail = false;
    private GT_Recipe lastRecipe = null;

    public GT_MetaTileEntity_RadioHatch(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier, 1, new String[] {
            StatCollector.translateToLocal("tooltip.tile.radhatch.0.name"),
            StatCollector.translateToLocal("tooltip.tile.tiereddsc.3.name") + " " + (aTier - 2) + " "
                    + ((aTier - 2) >= 2
                            ? StatCollector.translateToLocal("tooltip.bw.kg.1.name")
                            : StatCollector.translateToLocal("tooltip.bw.kg.0.name")),
            StatCollector.translateToLocal("tooltip.tile.radhatch.1.name"),
            BW_Tooltip_Reference.ADDED_BY_BARTIMAEUSNEK_VIA_BARTWORKS.get()
        });
        this.cap = aTier - 2;
    }

    public GT_MetaTileEntity_RadioHatch(String aName, int aTier, String aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, 1, aDescription, aTextures);
        this.cap = aTier - 2;
    }

    public GT_MetaTileEntity_RadioHatch(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, 1, aDescription, aTextures);
        this.cap = aTier - 2;
    }

    public int getSievert() {
        return this.sievert - MathUtils.ceilInt((float) this.sievert / 100f * (float) this.coverage);
    }

    public short[] getColorForGUI() {
        if (this.colorForGUI != null) return this.colorForGUI;
        return colorForGUI = new short[] {0xFA, 0xFA, 0xFF};
    }

    public byte getMass() {
        return this.mass;
    }

    public byte getCoverage() {
        return this.coverage;
    }

    public long getDecayTime() {
        return this.decayTime;
    }

    public void setCoverage(short coverage) {
        byte nu;
        if (coverage > 100) nu = 100;
        else if (coverage < 0) nu = 0;
        else nu = (byte) coverage;
        this.coverage = nu;
    }

    public ITexture[] getTexturesActive(ITexture aBaseTexture) {
        return new ITexture[] {aBaseTexture, TextureFactory.of(Textures.BlockIcons.OVERLAY_PIPE_IN)};
    }

    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        return new ITexture[] {aBaseTexture, TextureFactory.of(Textures.BlockIcons.OVERLAY_PIPE_IN)};
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new GT_MetaTileEntity_RadioHatch(this.mName, this.mTier, this.mDescriptionArray, this.mTextures);
    }

    @Override
    public void onScrewdriverRightClick(byte aSide, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        aPlayer.openGui(
                MainMod.MOD_ID,
                2,
                this.getBaseMetaTileEntity().getWorld(),
                this.getBaseMetaTileEntity().getXCoord(),
                this.getBaseMetaTileEntity().getYCoord(),
                this.getBaseMetaTileEntity().getZCoord());
    }

    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        GT_UIInfos.openGTTileEntityUI(aBaseMetaTileEntity, aPlayer);
        return true;
    }

    public void updateSlots() {
        if (this.mInventory[0] != null && this.mInventory[0].stackSize <= 0) this.mInventory[0] = null;
    }

    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTimer) {
        BaseMetaTileEntity myMetaTileEntity = ((BaseMetaTileEntity) this.getBaseMetaTileEntity());
        if (myMetaTileEntity.isServerSide()) {

            if (this.mass > 0) {
                ++this.timer;
            }

            if (this.mass > 0) {
                if (this.decayTime == 0 || (this.decayTime > 0 && this.timer % this.decayTime == 0)) {
                    this.mass--;
                    if (this.mass == 0) {
                        this.material = StatCollector.translateToLocal("tooltip.bw.empty.name");
                        this.sievert = 0;
                    }
                    this.timer = 1;
                }
            }

            if (myMetaTileEntity.mTickTimer > (myMetaTileEntity.mLastSoundTick + ticksBetweenSounds)) {
                if (this.sievert > 0) {
                    sendLoopStart((byte) 1);
                    myMetaTileEntity.mLastSoundTick = myMetaTileEntity.mTickTimer;
                }
            }

            if (this.mass == 0) {
                ItemStack lStack = this.mInventory[0];

                if (lStack == null) {
                    return;
                }

                if (this.lastFail && GT_Utility.areStacksEqual(this.lastUsedItem, lStack, true)) {
                    return;
                }

                if (!this.lastFail && this.lastUsedItem != null && this.lastRecipe != null) {
                    if (GT_Utility.areStacksEqual(this.lastUsedItem, lStack, true)) {
                        this.mass = (byte) this.lastRecipe.mDuration;
                        this.decayTime = this.lastRecipe.mSpecialValue;
                        this.sievert = this.lastRecipe.mEUt;
                        this.material = this.lastUsedItem.getDisplayName();
                        lStack.stackSize--;
                        updateSlots();
                    } else {
                        this.lastRecipe = null;
                    }
                }

                if (this.lastRecipe == null || this.lastFail) {
                    this.lastRecipe = BWRecipes.instance
                            .getMappingsFor(BWRecipes.RADHATCH)
                            .findRecipe(
                                    this.getBaseMetaTileEntity(), false, Integer.MAX_VALUE - 7, null, mInventory[0]);
                    if (this.lastRecipe == null) {
                        this.lastFail = true;
                        this.lastUsedItem = this.mInventory[0] == null ? null : this.mInventory[0].copy();
                    } else {
                        if (this.lastRecipe.mDuration > this.cap) {
                            this.lastFail = true;
                            this.lastUsedItem = this.mInventory[0].copy();
                            return;
                        }
                        this.lastFail = false;
                        this.lastUsedItem = this.mInventory[0].copy();
                        this.mass = (byte) this.lastRecipe.mDuration;
                        this.decayTime = this.lastRecipe.mSpecialValue;
                        this.sievert = this.lastRecipe.mEUt;
                        Materials mat = GT_OreDictUnificator.getAssociation(lStack).mMaterial.mMaterial;
                        this.colorForGUI = new short[] {mat.getRGBA()[0], mat.getRGBA()[1], mat.getRGBA()[2]};
                        this.material = lStack.getDisplayName();
                        lStack.stackSize--;
                        updateSlots();
                    }
                }
            }
        }
    }

    @Override
    public boolean isGivingInformation() {
        return true;
    }

    @Override
    public String[] getInfoData() {
        if (this.sievert != 0)
            return new String[] {
                StatCollector.translateToLocal("tooltip.tile.radhatch.2.name") + " "
                        + StatCollector.translateToLocal(this.material),
                StatCollector.translateToLocal("tooltip.tile.radhatch.3.name") + " " + this.sievert,
                StatCollector.translateToLocal("tooltip.tile.radhatch.4.name") + " " + this.mass,
                StatCollector.translateToLocal("tooltip.tile.radhatch.5.name") + " "
                        + (this.decayTime - this.timer % (this.decayTime * 60))
                        + StatCollector.translateToLocal("tooltip.tile.radhatch.6.name")
                        + "/"
                        + (this.decayTime - this.timer % this.decayTime) / 20
                        + StatCollector.translateToLocal("tooltip.tile.radhatch.7.name")
                        + "/"
                        + (this.decayTime - this.timer % this.decayTime) / 20 / 60
                        + StatCollector.translateToLocal("tooltip.tile.radhatch.8.name")
                        + "/"
                        + (this.decayTime - this.timer % this.decayTime) / 20 / 60 / 60
                        + StatCollector.translateToLocal("tooltip.tile.radhatch.9.name")
            };
        else
            return new String[] {
                StatCollector.translateToLocal("tooltip.tile.radhatch.2.name") + " "
                        + StatCollector.translateToLocal("tooltip.bw.empty.name"),
                StatCollector.translateToLocal("tooltip.tile.radhatch.3.name") + " " + "0",
                StatCollector.translateToLocal("tooltip.tile.radhatch.4.name") + " " + "0"
            };
    }

    public boolean isSimpleMachine() {
        return true;
    }

    public boolean isFacingValid(byte aFacing) {
        return true;
    }

    public boolean isAccessAllowed(EntityPlayer aPlayer) {
        return true;
    }

    public boolean isValidSlot(int aIndex) {
        return true;
    }

    public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
        return false;
    }

    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
        return aSide == this.getBaseMetaTileEntity().getFrontFacing()
                && BWRecipes.instance.getMappingsFor(BWRecipes.RADHATCH).containsInput(aStack);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        aNBT.setByte("mMass", this.mass);
        aNBT.setByte("mSv", (byte) (this.sievert - 100));
        aNBT.setByte("mCoverage", this.coverage);
        aNBT.setInteger("mTextColor", BW_ColorUtil.getColorFromRGBArray(this.getColorForGUI()));
        if (this.material != null && !this.material.isEmpty()) aNBT.setString("mMaterial", this.material);
        aNBT.setLong("timer", this.timer);
        aNBT.setLong("decay", this.decayTime);
        super.saveNBTData(aNBT);
    }

    public long getTimer() {
        return this.timer;
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        this.timer = aNBT.getLong("timer");
        this.mass = aNBT.getByte("mMass");
        this.sievert = aNBT.getByte("mSv") + 100;
        this.coverage = aNBT.getByte("mCoverage");
        this.colorForGUI = BW_ColorUtil.splitColorToRBGArray(aNBT.getInteger("mTextColor"));
        this.material = aNBT.getString("mMaterial");
        this.decayTime = aNBT.getLong("decay");
        super.loadNBTData(aNBT);
    }

    @Override
    public void startSoundLoop(byte aIndex, double aX, double aY, double aZ) {
        super.startSoundLoop(aIndex, aX, aY, aZ);
        ResourceLocation rl = new ResourceLocation(MainMod.MOD_ID, "hatch.RadOn");
        if (aIndex == 1) {
            GT_Utility.doSoundAtClient(rl, 10, 1.0F, aX, aY, aZ);
        }
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        getBaseMetaTileEntity().add1by1Slot(builder);
        addGregTechLogo(builder);
        builder.widget(new DrawableWidget()
                        .setBackground(BW_UITextures.PICTURE_SIEVERTS_CONTAINER)
                        .setPos(61, 9)
                        .setSize(56, 24))
                .widget(new ProgressBar()
                        .setProgress(() -> getSievert() / 148f)
                        .setDirection(Direction.RIGHT)
                        .setTexture(BW_UITextures.PICTURE_SIEVERTS_PROGRESS, 24)
                        .setPos(65, 13)
                        .setSize(48, 16))
                .widget(
                        new DrawableWidget() {
                            @Override
                            public void draw(float partialTicks) {
                                if (decayTime > 0) {
                                    int height = MathUtils.ceilInt(
                                            48 * ((decayTime - timer % decayTime) / (float) decayTime));
                                    new Rectangle()
                                            .setColor(Color.argb(colorForGUI[0], colorForGUI[1], colorForGUI[2], 1))
                                            .draw(new Pos2d(0, 48 - height), new Size(16, height), partialTicks);
                                }
                            }
                        }.setPos(124, 20)
                                .setSize(16, 48)
                                .attachSyncer(
                                        new FakeSyncWidget.ShortSyncer(
                                                () -> colorForGUI[0], val -> colorForGUI[0] = val),
                                        builder)
                                .attachSyncer(
                                        new FakeSyncWidget.ShortSyncer(
                                                () -> colorForGUI[1], val -> colorForGUI[1] = val),
                                        builder)
                                .attachSyncer(
                                        new FakeSyncWidget.ShortSyncer(
                                                () -> colorForGUI[2], val -> colorForGUI[2] = val),
                                        builder))
                .widget(new DrawableWidget()
                        .setBackground(BW_UITextures.PICTURE_DECAY_TIME_CONTAINER)
                        .setPos(120, 16)
                        .setSize(24, 56))
                .widget(new FakeSyncWidget.LongSyncer(() -> decayTime, val -> decayTime = val))
                .widget(new FakeSyncWidget.LongSyncer(() -> timer, val -> timer = val))
                .widget(new TextWidget("Mass: " + mass + " kg")
                        .setTextAlignment(Alignment.Center)
                        .attachSyncer(new FakeSyncWidget.ByteSyncer(() -> mass, val -> mass = val), builder)
                        .setPos(50, 62)
                        .setSize(80, 10))
                .widget(new TextWidget("Sievert: " + sievert + " Sv")
                        .setTextAlignment(Alignment.Center)
                        .attachSyncer(new FakeSyncWidget.IntegerSyncer(() -> sievert, val -> sievert = val), builder)
                        .setPos(55, 72)
                        .setSize(80, 10));
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(new DrawableWidget()
                .setDrawable(BW_UITextures.PICTURE_BW_LOGO_47X20)
                .setSize(47, 20)
                .setPos(11, 55));
    }

    @Override
    public GUITextureSet getGUITextureSet() {
        return new GUITextureSet()
                .setMainBackground(GT_UITextures.BACKGROUND_SINGLEBLOCK_DEFAULT)
                .setGregTechLogo(GT_UITextures.PICTURE_GT_LOGO_17x17_TRANSPARENT);
    }

    @Override
    public boolean useModularUI() {
        return true;
    }
}
