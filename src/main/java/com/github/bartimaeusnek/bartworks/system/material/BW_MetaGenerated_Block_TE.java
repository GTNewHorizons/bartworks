/*
 * Copyright (c) 2018-2020 bartimaeusnek Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions: The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package com.github.bartimaeusnek.bartworks.system.material;

import static com.github.bartimaeusnek.bartworks.MainMod.BW_Network_instance;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;

import com.github.bartimaeusnek.bartworks.common.net.MetaBlockPacket;

import gregtech.GT_Mod;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.interfaces.tileentity.ITexturedTileEntity;
import gregtech.api.objects.XSTR;
import gregtech.api.util.GT_OreDictUnificator;

public abstract class BW_MetaGenerated_Block_TE extends TileEntity implements ITexturedTileEntity {

    public short mMetaData;
    protected static boolean shouldFortune = false;
    public boolean mNatural = false;

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound aNBT) {
        super.readFromNBT(aNBT);
        this.mMetaData = aNBT.getShort("m");
    }

    @Override
    public void writeToNBT(NBTTagCompound aNBT) {
        aNBT.setShort("m", this.mMetaData);
        super.writeToNBT(aNBT);
    }

    @Override
    public Packet getDescriptionPacket() {
        if (!this.worldObj.isRemote) BW_Network_instance.sendPacketToAllPlayersInRange(
                this.worldObj,
                new MetaBlockPacket(this.xCoord, (short) this.yCoord, this.zCoord, this.mMetaData),
                this.xCoord,
                this.zCoord);
        return null;
    }

    protected abstract Block GetProperBlock();

    public ArrayList<ItemStack> getDrops(int aFortune) {
        ArrayList<ItemStack> rList = new ArrayList<>();
        if (this.mMetaData <= 0) {
            rList.add(new ItemStack(Blocks.cobblestone, 1, 0));
            return rList;
        }
        Materials aOreMaterial = Werkstoff.werkstoffHashMap.get(this.mMetaData).getBridgeMaterial();
        switch (GT_Mod.gregtechproxy.oreDropSystem) {
            case Item -> {
                rList.add(GT_OreDictUnificator.get(OrePrefixes.rawOre, aOreMaterial, 1));
            }
            case FortuneItem -> {
                // if shouldFortune and isNatural then get fortune drops
                // if not shouldFortune or not isNatural then get normal drops
                // if not shouldFortune and isNatural then get normal drops
                // if shouldFortune and not isNatural then get normal drops
                if (shouldFortune && this.mNatural) {
                    Random tRandom = new XSTR(this.xCoord ^ this.yCoord ^ this.zCoord);
                    long amount = (long) Math.max(1, tRandom.nextInt((1 + Math.min(3, aFortune))));
                    rList.add(GT_OreDictUnificator.get(OrePrefixes.rawOre, aOreMaterial, amount));
                } else {
                    rList.add(GT_OreDictUnificator.get(OrePrefixes.rawOre, aOreMaterial, 1));
                }
            }
            case UnifiedBlock -> {
                // Unified ore
                rList.add(new ItemStack(this.GetProperBlock(), 1, this.mMetaData));
            }
            case PerDimBlock -> {
                // Per Dimension ore
                rList.add(new ItemStack(this.GetProperBlock(), 1, this.mMetaData));
            }
            case Block -> {
                // Regular ore
                rList.add(new ItemStack(this.GetProperBlock(), 1, this.mMetaData));
            }
        }
        return rList;
    }
}
