package net.minecraft.client.renderer;

import baritone.Baritone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.ListedRenderChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockRenderLayer;
import optifine.Config;
import static org.lwjgl.opengl.GL11.*;


public class RenderList extends ChunkRenderContainer
{
    public void renderChunkLayer(BlockRenderLayer layer)
    {
        if (initialized)
        {
            if (renderChunks.size() == 0)
            {
                return;
            }

            for (RenderChunk renderchunk : renderChunks)
            {
                ListedRenderChunk listedrenderchunk = (ListedRenderChunk)renderchunk;
                GlStateManager.pushMatrix();
                preRenderChunk(renderchunk);
                GlStateManager.callList(listedrenderchunk.getDisplayList(layer, listedrenderchunk.getCompiledChunk()));
                if (Baritone.settings().renderCachedChunks.value && !Minecraft.getMinecraft().isSingleplayer()) {
                    // reset the blend func to normal (not dependent on constant alpha)
                    GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
                }
                GlStateManager.popMatrix();
            }

            if (Config.isMultiTexture())
            {
                GlStateManager.bindCurrentTexture();
            }

            GlStateManager.resetColor();
            renderChunks.clear();
        }
    }
}
