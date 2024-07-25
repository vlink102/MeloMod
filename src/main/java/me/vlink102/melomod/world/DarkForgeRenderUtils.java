package me.vlink102.melomod.world;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.realms.RealmsBufferBuilder;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class DarkForgeRenderUtils {
    private static final AxisAlignedBB DEFAULT_AABB = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

    public static void drawESP(final AxisAlignedBB bb, final double r, final double g, final double b) {

        Minecraft.getMinecraft().entityRenderer.disableLightmap();
        glPushMatrix();
        glEnable(GL_BLEND);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glLineWidth(1.5f);
        glDisable(GL_LIGHTING);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_LINE_SMOOTH);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glColor4d(r, g, b, 0.1825f);
        drawBoundingBox(bb);
        glColor4d(r, g, b, 1f);
        drawOutlineBoundingBox(bb);
        glLineWidth(2f);
        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_LIGHTING);
        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
//        glDisable(GL_BLEND);
        glPopMatrix();
        Minecraft.getMinecraft().entityRenderer.enableLightmap();
    }

    public static void drawCircle(final double x, final double y, final double r, final int c) {

        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_POLYGON_SMOOTH);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);
        GLUtils.glColor(c);

        glBegin(GL_POLYGON);
        for (int i = 0; i <= 360; i++) {
            final double x2 = Math.sin(((i * 3.141526D) / 180)) * r;
            final double y2 = Math.cos(((i * 3.141526D) / 180)) * r;
            glVertex3d(x + x2, y + y2, 0);
        }

        glEnd();

        glDisable(GL_POLYGON_SMOOTH);
        glEnable(GL_TEXTURE_2D);
//        glDisable(GL_BLEND);

    }

    public static void drawFilledCircle(int cx, int cy, double r, int c) {

        r *= 2.0D;
        cx *= 2;
        cy *= 2;
        float f = (c >> 24 & 0xFF) / 255.0F;
        float f1 = (c >> 16 & 0xFF) / 255.0F;
        float f2 = (c >> 8 & 0xFF) / 255.0F;
        float f3 = (c & 0xFF) / 255.0F;
        GLUtils.enableGL2D();
        glScalef(0.5F, 0.5F, 0.5F);
        glColor4f(f1, f2, f3, f);
        glEnable(GL_POLYGON_SMOOTH);
        glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);
        glBegin(GL_TRIANGLE_FAN);

        for (int i = 0; i <= 360; i++) {
            double x = Math.sin(i * Math.PI / 180.0D) * r;
            double y = Math.cos(i * Math.PI / 180.0D) * r;
            glVertex2d(cx + x, cy + y);
        }

        glEnd();
        glScalef(2.0F, 2.0F, 2.0F);
        GLUtils.disableGL2D();
        glDisable(GL_POLYGON_SMOOTH);
    }

    public static void drawBoundingBox(AxisAlignedBB aa) {

        Tessellator tessellator = Tessellator.getInstance();
        RealmsBufferBuilder vertexBuffer = new RealmsBufferBuilder(tessellator.getWorldRenderer());
        vertexBuffer.begin(3, DefaultVertexFormats.POSITION);
        vertexBuffer.offset(aa.minX, aa.minY, aa.minZ);
        vertexBuffer.offset(aa.minX, aa.maxY, aa.minZ);
        vertexBuffer.offset(aa.maxX, aa.minY, aa.minZ);
        vertexBuffer.offset(aa.maxX, aa.maxY, aa.minZ);
        vertexBuffer.offset(aa.maxX, aa.minY, aa.maxZ);
        vertexBuffer.offset(aa.maxX, aa.maxY, aa.maxZ);
        vertexBuffer.offset(aa.minX, aa.minY, aa.maxZ);
        vertexBuffer.offset(aa.minX, aa.maxY, aa.maxZ);
        tessellator.draw();
        vertexBuffer.begin(3, DefaultVertexFormats.POSITION);
        vertexBuffer.offset(aa.maxX, aa.maxY, aa.minZ);
        vertexBuffer.offset(aa.maxX, aa.minY, aa.minZ);
        vertexBuffer.offset(aa.minX, aa.maxY, aa.minZ);
        vertexBuffer.offset(aa.minX, aa.minY, aa.minZ);
        vertexBuffer.offset(aa.minX, aa.maxY, aa.maxZ);
        vertexBuffer.offset(aa.minX, aa.minY, aa.maxZ);
        vertexBuffer.offset(aa.maxX, aa.maxY, aa.maxZ);
        vertexBuffer.offset(aa.maxX, aa.minY, aa.maxZ);
        tessellator.draw();
        vertexBuffer.begin(3, DefaultVertexFormats.POSITION);
        vertexBuffer.offset(aa.minX, aa.maxY, aa.minZ);
        vertexBuffer.offset(aa.maxX, aa.maxY, aa.minZ);
        vertexBuffer.offset(aa.maxX, aa.maxY, aa.maxZ);
        vertexBuffer.offset(aa.minX, aa.maxY, aa.maxZ);
        vertexBuffer.offset(aa.minX, aa.maxY, aa.minZ);
        vertexBuffer.offset(aa.minX, aa.maxY, aa.maxZ);
        vertexBuffer.offset(aa.maxX, aa.maxY, aa.maxZ);
        vertexBuffer.offset(aa.maxX, aa.maxY, aa.minZ);
        tessellator.draw();
        vertexBuffer.begin(3, DefaultVertexFormats.POSITION);
        vertexBuffer.offset(aa.minX, aa.minY, aa.minZ);
        vertexBuffer.offset(aa.maxX, aa.minY, aa.minZ);
        vertexBuffer.offset(aa.maxX, aa.minY, aa.maxZ);
        vertexBuffer.offset(aa.minX, aa.minY, aa.maxZ);
        vertexBuffer.offset(aa.minX, aa.minY, aa.minZ);
        vertexBuffer.offset(aa.minX, aa.minY, aa.maxZ);
        vertexBuffer.offset(aa.maxX, aa.minY, aa.maxZ);
        vertexBuffer.offset(aa.maxX, aa.minY, aa.minZ);
        tessellator.draw();
        vertexBuffer.begin(3, DefaultVertexFormats.POSITION);
        vertexBuffer.offset(aa.minX, aa.minY, aa.minZ);
        vertexBuffer.offset(aa.minX, aa.maxY, aa.minZ);
        vertexBuffer.offset(aa.minX, aa.minY, aa.maxZ);
        vertexBuffer.offset(aa.minX, aa.maxY, aa.maxZ);
        vertexBuffer.offset(aa.maxX, aa.minY, aa.maxZ);
        vertexBuffer.offset(aa.maxX, aa.maxY, aa.maxZ);
        vertexBuffer.offset(aa.maxX, aa.minY, aa.minZ);
        vertexBuffer.offset(aa.maxX, aa.maxY, aa.minZ);
        tessellator.draw();
        vertexBuffer.begin(3, DefaultVertexFormats.POSITION);
        vertexBuffer.offset(aa.minX, aa.maxY, aa.maxZ);
        vertexBuffer.offset(aa.minX, aa.minY, aa.maxZ);
        vertexBuffer.offset(aa.minX, aa.maxY, aa.minZ);
        vertexBuffer.offset(aa.minX, aa.minY, aa.minZ);
        vertexBuffer.offset(aa.maxX, aa.maxY, aa.minZ);
        vertexBuffer.offset(aa.maxX, aa.minY, aa.minZ);
        vertexBuffer.offset(aa.maxX, aa.maxY, aa.maxZ);
        vertexBuffer.offset(aa.maxX, aa.minY, aa.maxZ);
        tessellator.draw();
    }

    public static void drawOutlineBoundingBox(AxisAlignedBB boundingBox) {

        Tessellator tessellator = Tessellator.getInstance();
        RealmsBufferBuilder vertexBuffer = new RealmsBufferBuilder(tessellator.getWorldRenderer());
        vertexBuffer.begin(3, DefaultVertexFormats.POSITION);
        vertexBuffer.normal((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ).endVertex();
        vertexBuffer.normal((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ).endVertex();
        vertexBuffer.normal((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ).endVertex();
        vertexBuffer.normal((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ).endVertex();
        vertexBuffer.normal((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ).endVertex();
        tessellator.draw();
        vertexBuffer.begin(3, DefaultVertexFormats.POSITION);
        vertexBuffer.normal((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ).endVertex();
        vertexBuffer.normal((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ).endVertex();
        vertexBuffer.normal((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ).endVertex();
        vertexBuffer.normal((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ).endVertex();
        vertexBuffer.normal((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ).endVertex();
        tessellator.draw();
        vertexBuffer.begin(1, DefaultVertexFormats.POSITION);
        vertexBuffer.normal((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ).endVertex();
        vertexBuffer.normal((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ).endVertex();
        vertexBuffer.normal((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ).endVertex();
        vertexBuffer.normal((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ).endVertex();
        vertexBuffer.normal((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ).endVertex();
        vertexBuffer.normal((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ).endVertex();
        vertexBuffer.normal((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ).endVertex();
        vertexBuffer.normal((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ).endVertex();
        tessellator.draw();
    }

    public static void drawTracer(double x, double y, double z, float lineWidth, float red, float green, float blue, float alpha) {

        boolean userViewbobbing = Minecraft.getMinecraft().gameSettings.viewBobbing;
        Minecraft.getMinecraft().gameSettings.viewBobbing = false;

        Minecraft.getMinecraft().gameSettings.viewBobbing = userViewbobbing;

        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(lineWidth);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glColor4f(red, green, blue, alpha);
        Vec3 eyes = new Vec3(0, 0, 1).rotatePitch(-(float) Math.toRadians(Minecraft.getMinecraft().thePlayer.rotationPitch)).rotateYaw(-(float) Math.toRadians(Minecraft.getMinecraft().thePlayer.rotationYaw));
        glBegin(GL_LINES);
        glVertex3d(eyes.xCoord, Minecraft.getMinecraft().thePlayer.getEyeHeight() + eyes.yCoord, eyes.zCoord);
        glVertex3d(x, y, z);
        glEnd();
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
//        glDisable(GL_BLEND);
    }


    public static void drawTag(String s, double d, double d1, double d2, float scale, int color) {

        d += 0.5D;
        d2 += 0.5D;
        FontRenderer fontrenderer = Minecraft.getMinecraft().fontRendererObj;
        RenderManager renderManager1 = Minecraft.getMinecraft().getRenderManager();
        glPushMatrix();
        glTranslatef((float) d, (float) d1 + 1.5F, (float) d2 - 0.5F);
        glNormal3f(0.0F, 1.0F, 0.0F);
        glRotatef(-renderManager1.playerViewY, 0.0F, 1.0F, 0.0F);
        glRotatef(renderManager1.playerViewX, 1.0F, 0.0F, 0.0F);
        glScalef(-scale, -scale, scale);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        GL11.glBlendFunc(770, 771);
        fontrenderer.drawStringWithShadow(s, (float) -fontrenderer.getStringWidth(s) / 2, 0, color);
        fontrenderer.drawStringWithShadow(s, (float) -fontrenderer.getStringWidth(s) / 2, 0, color);
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        GlStateManager.color(1, 1, 1, 1);
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        glPopMatrix();
    }

    public static void drawSolidBox() {

        drawSolidBox(DEFAULT_AABB);
    }

    public static void drawSolidBox(AxisAlignedBB bb) {

        glBegin(GL_QUADS);
        {
            glVertex3d(bb.minX, bb.minY, bb.minZ);
            glVertex3d(bb.maxX, bb.minY, bb.minZ);
            glVertex3d(bb.maxX, bb.minY, bb.maxZ);
            glVertex3d(bb.minX, bb.minY, bb.maxZ);

            glVertex3d(bb.minX, bb.maxY, bb.minZ);
            glVertex3d(bb.minX, bb.maxY, bb.maxZ);
            glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
            glVertex3d(bb.maxX, bb.maxY, bb.minZ);

            glVertex3d(bb.minX, bb.minY, bb.minZ);
            glVertex3d(bb.minX, bb.maxY, bb.minZ);
            glVertex3d(bb.maxX, bb.maxY, bb.minZ);
            glVertex3d(bb.maxX, bb.minY, bb.minZ);

            glVertex3d(bb.maxX, bb.minY, bb.minZ);
            glVertex3d(bb.maxX, bb.maxY, bb.minZ);
            glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
            glVertex3d(bb.maxX, bb.minY, bb.maxZ);

            glVertex3d(bb.minX, bb.minY, bb.maxZ);
            glVertex3d(bb.maxX, bb.minY, bb.maxZ);
            glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
            glVertex3d(bb.minX, bb.maxY, bb.maxZ);

            glVertex3d(bb.minX, bb.minY, bb.minZ);
            glVertex3d(bb.minX, bb.minY, bb.maxZ);
            glVertex3d(bb.minX, bb.maxY, bb.maxZ);
            glVertex3d(bb.minX, bb.maxY, bb.minZ);
        }
        glEnd();
    }

    public static void drawOutlinedBox() {

        drawOutlinedBox(DEFAULT_AABB);
    }

    public static void drawOutlinedBox(AxisAlignedBB bb) {

        glBegin(GL_LINES);
        {
            glVertex3d(bb.minX, bb.minY, bb.minZ);
            glVertex3d(bb.maxX, bb.minY, bb.minZ);

            glVertex3d(bb.maxX, bb.minY, bb.minZ);
            glVertex3d(bb.maxX, bb.minY, bb.maxZ);

            glVertex3d(bb.maxX, bb.minY, bb.maxZ);
            glVertex3d(bb.minX, bb.minY, bb.maxZ);

            glVertex3d(bb.minX, bb.minY, bb.maxZ);
            glVertex3d(bb.minX, bb.minY, bb.minZ);

            glVertex3d(bb.minX, bb.minY, bb.minZ);
            glVertex3d(bb.minX, bb.maxY, bb.minZ);

            glVertex3d(bb.maxX, bb.minY, bb.minZ);
            glVertex3d(bb.maxX, bb.maxY, bb.minZ);

            glVertex3d(bb.maxX, bb.minY, bb.maxZ);
            glVertex3d(bb.maxX, bb.maxY, bb.maxZ);

            glVertex3d(bb.minX, bb.minY, bb.maxZ);
            glVertex3d(bb.minX, bb.maxY, bb.maxZ);

            glVertex3d(bb.minX, bb.maxY, bb.minZ);
            glVertex3d(bb.maxX, bb.maxY, bb.minZ);

            glVertex3d(bb.maxX, bb.maxY, bb.minZ);
            glVertex3d(bb.maxX, bb.maxY, bb.maxZ);

            glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
            glVertex3d(bb.minX, bb.maxY, bb.maxZ);

            glVertex3d(bb.minX, bb.maxY, bb.maxZ);
            glVertex3d(bb.minX, bb.maxY, bb.minZ);
        }
        glEnd();
    }

    public static void drawTri(double x1, double y1, double x2, double y2, double x3, double y3, double width, Color c) {

        glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GLUtils.glColor(c);
        glLineWidth((float) width);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex2d(x1, y1);
        GL11.glVertex2d(x2, y2);
        GL11.glVertex2d(x3, y3);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        glEnable(GL11.GL_TEXTURE_2D);
    }

    public static void drawHLine(float par1, float par2, float par3, int color) {

        if (par2 < par1) {
            float var5 = par1;
            par1 = par2;
            par2 = var5;
        }

        drawRect(par1, par3, par2 + 1, par3 + 1, color);
    }

    public static void drawVLine(float par1, float par2, float par3, int color) {

        if (par3 < par2) {
            float var5 = par2;
            par2 = par3;
            par3 = var5;
        }

        drawRect(par1, par2 + 1, par1 + 1, par3, color);
    }

    public static void drawRect(float left, float top, float right, float bottom, Color color) {

        float var5;

        if (left < right) {
            var5 = left;
            left = right;
            right = var5;
        }

        if (top < bottom) {
            var5 = top;
            top = bottom;
            bottom = var5;
        }

        glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL11.GL_LINE_SMOOTH);
        glPushMatrix();
        GLUtils.glColor(color);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2d(left, bottom);
        GL11.glVertex2d(right, bottom);
        GL11.glVertex2d(right, top);
        GL11.glVertex2d(left, top);
        GL11.glEnd();
        GL11.glPopMatrix();
        glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }

    public static void drawRect(float left, float top, float right, float bottom, int color) {

        float var5;

        if (left < right) {
            var5 = left;
            left = right;
            right = var5;
        }

        if (top < bottom) {
            var5 = top;
            top = bottom;
            bottom = var5;
        }

        glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL11.GL_LINE_SMOOTH);
        glPushMatrix();
        GLUtils.glColor(color);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2d(left, bottom);
        GL11.glVertex2d(right, bottom);
        GL11.glVertex2d(right, top);
        GL11.glVertex2d(left, top);
        GL11.glEnd();
        GL11.glPopMatrix();
        glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }
}
