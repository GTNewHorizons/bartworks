package com.github.bartimaeusnek.bartworks.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTech_API;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class BW_EICPistonVisualizer extends EntityFX {

    public BW_EICPistonVisualizer(World world, int x, int y, int z, int age) {
        super(world, (double) x, ((double) y), (double) z);
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.particleMaxAge = age;
    }

    @Override
    public void onUpdate() {
        if (this.particleAge++ >= this.particleMaxAge) this.setDead();
    }

    @Override
    public void renderParticle(
            Tessellator p_70539_1_,
            float p_70539_2_,
            float p_70539_3_,
            float p_70539_4_,
            float p_70539_5_,
            float p_70539_6_,
            float p_70539_7_) {
        Tessellator tessellator = Tessellator.instance;
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(false);
        double f11 = this.prevPosX + (this.posX - this.prevPosX) * (double) p_70539_2_ - interpPosX;
        double f12 = this.prevPosY + (this.posY - this.prevPosY) * (double) p_70539_2_ - interpPosY;
        double f13 = this.prevPosZ + (this.posZ - this.prevPosZ) * (double) p_70539_2_ - interpPosZ;

        // tessellator.draw();
        // tessellator.startDrawingQuads();
        // tessellator.setTranslation(f11 - this.posX, f12 - posY, f13 - posZ);
        tessellator.setColorRGBA(255, 255, 255, 255);
        RenderBlocks.getInstance().blockAccess = this.worldObj;
        /*
        RenderBlocks.getInstance().setRenderFromInside(false);
        RenderBlocks.getInstance().setRenderAllFaces(true);
        RenderBlocks.getInstance()
                .renderBlockUsingTexture(
                        GregTech_API.sBlockMetal5,
                        (int) this.posX,
                        (int) this.posY,
                        (int) this.posZ,
                        GregTech_API.sBlockMetal5.getIcon(0, 2));

         */

        RenderBlocks.getInstance().setRenderFromInside(false);
        RenderBlocks.getInstance()
                .renderFaceXPos(GregTech_API.sBlockMetal5, f11, f12, f13, GregTech_API.sBlockMetal5.getIcon(0, 2));
        RenderBlocks.getInstance()
                .renderFaceXNeg(GregTech_API.sBlockMetal5, f11, f12, f13, GregTech_API.sBlockMetal5.getIcon(0, 2));
        RenderBlocks.getInstance()
                .renderFaceYPos(GregTech_API.sBlockMetal5, f11, f12, f13, GregTech_API.sBlockMetal5.getIcon(0, 2));
        RenderBlocks.getInstance()
                .renderFaceYNeg(GregTech_API.sBlockMetal5, f11, f12, f13, GregTech_API.sBlockMetal5.getIcon(0, 2));
        RenderBlocks.getInstance()
                .renderFaceZPos(GregTech_API.sBlockMetal5, f11, f12, f13, GregTech_API.sBlockMetal5.getIcon(0, 2));
        RenderBlocks.getInstance()
                .renderFaceZNeg(GregTech_API.sBlockMetal5, f11, f12, f13, GregTech_API.sBlockMetal5.getIcon(0, 2));
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(true);
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 2;
    }
}
