package me.vlink102.melomod.world;

import me.vlink102.melomod.mixin.SkyblockUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.util.vector.Vector3f;

public class Render {

    @SubscribeEvent
    public void onDrawBlockHighlight(DrawBlockHighlightEvent event) {
        ItemStack held = Minecraft.getMinecraft().thePlayer.getHeldItem();
        EntityPlayer player = event.player;
        double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) event.partialTicks;
        double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) event.partialTicks;
        double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) event.partialTicks;
        SkyblockUtil.ItemType type =SkyblockUtil.ItemType.parseFromItemStack(held);
        if (type == null) return;
        if (type == SkyblockUtil.ItemType.DRILL || type == SkyblockUtil.ItemType.GAUNTLET || type == SkyblockUtil.ItemType.PICKAXE) {
            RaycastResult result = raycast(Minecraft.getMinecraft().thePlayer, 1f, 4, 0.1f);
            if (result != null) {
                AxisAlignedBB box = result.state.getBlock().getSelectedBoundingBox(
                        Minecraft.getMinecraft().theWorld,
                        result.pos
                );
                AxisAlignedBB bb = box.expand(0.01D, 0.01D, 0.01D).offset(-d0, -d1, -d2);
                RenderUtils.drawFilledBoundingBox(
                        bb,
                        1f,
                        "00:70:156:8:96"
                );
                GlStateManager.disableDepth();
                RenderUtils.drawOutlineBoundingBox(
                        bb,
                        2f,
                        "00:70:156:8:96"
                );
                GlStateManager.enableDepth();

                GlStateManager.depthMask(true);
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
            }
        }
    }

    private static class RaycastResult {
        IBlockState state;
        BlockPos pos;

        public RaycastResult(IBlockState state, BlockPos pos) {
            this.state = state;
            this.pos = pos;
        }
    }

    private RaycastResult raycast(EntityPlayerSP player, float partialTicks, float dist, float step) {
        Vector3f pos = new Vector3f((float) player.posX, (float) player.posY + player.getEyeHeight(), (float) player.posZ);

        Vec3 lookVec3 = player.getLook(partialTicks);

        Vector3f look = new Vector3f((float) lookVec3.xCoord, (float) lookVec3.yCoord, (float) lookVec3.zCoord);
        look.scale(step / look.length());

        int stepCount = (int) Math.ceil(dist / step);

        for (int i = 0; i < stepCount; i++) {
            Vector3f.add(pos, look, pos);

            WorldClient world = Minecraft.getMinecraft().theWorld;
            BlockPos position = new BlockPos(pos.x, pos.y, pos.z);
            IBlockState state = world.getBlockState(position);

            if (state.getBlock() != Blocks.air) {
                //Back-step
                Vector3f.sub(pos, look, pos);
                look.scale(0.1f);

                for (int j = 0; j < 10; j++) {
                    Vector3f.add(pos, look, pos);

                    BlockPos position2 = new BlockPos(pos.x, pos.y, pos.z);
                    IBlockState state2 = world.getBlockState(position2);

                    if (state2.getBlock() != Blocks.air) {
                        return new RaycastResult(state2, position2);
                    }
                }

                return new RaycastResult(state, position);
            }
        }

        return null;
    }
    private static boolean isPickaxe(String internalname) {
        if (internalname == null) return false;

        if (internalname.endsWith("_PICKAXE")) {
            return true;
        } else if (internalname.contains("_DRILL_")) {
            char lastChar = internalname.charAt(internalname.length() - 1);
            return lastChar >= '0' && lastChar <= '9';
        } else return internalname.equals("GEMSTONE_GAUNTLET") || internalname.equals("PICKONIMBUS") || internalname.equals("DIVAN_DRILL");
    }
    /*

    public static void drawBoundingBoxAtBlockPos(PoseStack matrixStackIn, AABB aabbIn, float red, float green, float blue, float alpha, BlockPos pos, BlockPos aimed) {
        Vec3 cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        double camX = cam.x, camY = cam.y, camZ = cam.z;

        matrixStackIn.pushPose();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        drawShapeOutline(matrixStackIn, Shapes.create(aabbIn), pos.getX() - camX, pos.getY() - camY, pos.getZ() - camZ, red, green, blue, alpha, pos, aimed);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        matrixStackIn.popPose();
    }

    private static void drawShapeOutline(PoseStack matrixStack, VoxelShape voxelShape, double originX, double originY, double originZ, float red, float green, float blue, float alpha, BlockPos pos, BlockPos aimed) {
        PoseStack.Pose pose = matrixStack.last();
        BufferSource.BufferSource renderTypeBuffer = Minecraft.getMinecraft().getFramebuffer();
        VertexConsumer bufferIn = renderTypeBuffer.getBuffer(RenderType.lines());
        voxelShape.forAllEdges((x0, y0, z0, x1, y1, z1) -> {
            if (!pos.equals(aimed)){
                bufferIn.vertex(pose.pose(), (float) (x0 + originX), (float) (y0 + originY), (float) (z0 + originZ))
                        .color(red, green, blue, alpha)
                        .normal(pose.normal(), (float) (x1-x0), (float) (y1-y0), (float) (z1-z0))
                        .endVertex();
                bufferIn.vertex(pose.pose(), (float) (x1 + originX), (float) (y1 + originY), (float) (z1 + originZ))
                        .color(red, green, blue, alpha)
                        .normal(pose.normal(), (float) (x1-x0), (float) (y1-y0), (float) (z1-z0))
                        .endVertex();
            }

        });

        renderTypeBuffer.endBatch(RenderType.lines());
    }

    public static void drawArea(ItemStack gun, Player player, PoseStack matrixStack){
        BlockHitResult ray = WorldUtil.getLookingAt(player.level, player, ClipContext.Fluid.NONE, BurnerGunNBT.getRaycast(gun));
        if (player.level.getBlockState(ray.getBlockPos()) == Blocks.AIR.defaultBlockState())
            return;
        int xRad = BurnerGunNBT.getHorizontal(gun);
        int yRad = BurnerGunNBT.getVertical(gun);
        BlockPos aimedPos = ray.getBlockPos();
        if (ray.getType() != BlockHitResult.Type.BLOCK)
            return;
        Vec3 size = WorldUtil.getDim(ray, xRad, yRad, player);
        float[] color = BurnerGunNBT.getColor(gun);
        AABB test = player.level.getBlockState(aimedPos).getShape(player.getLevel(), aimedPos, CollisionContext.of(player)).bounds();
        drawBoundingBoxAtBlockPos(matrixStack, test, color[0], color[1], color[2], 1.0F, aimedPos.relative(ray.getDirection()), aimedPos.relative(ray.getDirection()));
        drawBoundingBoxAtBlockPos(matrixStack, test, color[0], color[1], color[2], 1.0F, aimedPos, aimedPos.relative(ray.getDirection()));
        if (player.isCrouching())
            return;
        for (int xPos = aimedPos.getX() - (int)size.x(); xPos <= aimedPos.getX() + (int)size.x(); ++xPos){
            for (int yPos = aimedPos.getY() - (int)size.y(); yPos <= aimedPos.getY() + (int)size.y(); ++yPos){
                for (int zPos = aimedPos.getZ() - (int)size.z(); zPos <= aimedPos.getZ() + (int)size.z(); ++zPos){
                    BlockPos thePos = new BlockPos(xPos, yPos, zPos);
                    if (thePos != aimedPos && player.level.getBlockState(thePos) != Blocks.AIR.defaultBlockState() && player.level.getBlockState(thePos) != Blocks.CAVE_AIR.defaultBlockState())
                        drawBoundingBoxAtBlockPos(matrixStack, player.level.getBlockState(thePos).getShape(player.getLevel(), aimedPos, CollisionContext.of(player)).optimize().bounds(), color[0], color[1], color[2], 1.0F, thePos, aimedPos);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderWorldEvent(RenderLevelLastEvent e) {
        final GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
        Player player = Minecraft.getInstance().player;
        ItemStack gun = !BurnerGunMK2.getGun(player).isEmpty() ? BurnerGunMK2.getGun(player) : BurnerGunMK1.getGun(player);
        if (gun.isEmpty())
            return;
        gameRenderer.resetProjectionMatrix(e.getProjectionMatrix());
        if (player.level.isClientSide)
            drawArea(gun, player, e.getPoseStack());

        //drawBoundingBoxAtBlockPos(e.getMatrixStack(), test, 1.0F, 0.0F, 0.0F, 1.0F, new BlockPos(0, 65, 0), new BlockPos(0, 65, 0));
        //drawBoundingBoxAtBlockPos(e.getMatrixStack(), test, 1.0F, 0.0F, 0.0F, 1.0F, new BlockPos(1, 65, 0), new BlockPos(0, 65, 0));
        drawBoundingBoxAtBlockPos(e.getMatrixStack(), test, 1.0F, 0.0F, 0.0F, 1.0F, new BlockPos(0, 65, 1));
        drawBoundingBoxAtBlockPos(e.getMatrixStack(), test, 1.0F, 0.0F, 0.0F, 1.0F, new BlockPos(0, 65, -1));

        drawBoundingBoxAtBlockPos(e.getMatrixStack(), test, 1.0F, 0.0F, 0.0F, 1.0F, new BlockPos(0, 64, 0));
        drawBoundingBoxAtBlockPos(e.getMatrixStack(), test, 1.0F, 0.0F, 0.0F, 1.0F, new BlockPos(0, 64, 1));
        drawBoundingBoxAtBlockPos(e.getMatrixStack(), test, 1.0F, 0.0F, 0.0F, 1.0F, new BlockPos(0, 64, -1));

        drawBoundingBoxAtBlockPos(e.getMatrixStack(), test, 1.0F, 0.0F, 0.0F, 1.0F, new BlockPos(0, 66, 0));
        drawBoundingBoxAtBlockPos(e.getMatrixStack(), test, 1.0F, 0.0F, 0.0F, 1.0F, new BlockPos(0, 66, 1));
        drawBoundingBoxAtBlockPos(e.getMatrixStack(), test, 1.0F, 0.0F, 0.0F, 1.0F, new BlockPos(0, 66, -1));
    }*/
}
