/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.builders;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import buildcraft.builders.render.RenderArchitect;
import buildcraft.builders.render.RenderBuilderTile;
import buildcraft.builders.render.RenderConstructionMarker;
import buildcraft.builders.render.RenderFiller;
import buildcraft.builders.render.RenderQuarry;
import buildcraft.core.lib.EntityResizableCuboid;
import buildcraft.core.lib.client.render.RenderVoid;

public class BuilderProxyClient extends BuilderProxy {
    public static TextureAtlasSprite drillTexture;
    public static TextureAtlasSprite drillHeadTexture;

    @Override
    public void registerClientHook() {

    }

    @Override
    public void registerBlockRenderers() {
        super.registerBlockRenderers();

        ClientRegistry.bindTileEntitySpecialRenderer(TileArchitect.class, new RenderArchitect());
        ClientRegistry.bindTileEntitySpecialRenderer(TileFiller.class, new RenderFiller());
        ClientRegistry.bindTileEntitySpecialRenderer(TileBuilder.class, new RenderBuilderTile());
        ClientRegistry.bindTileEntitySpecialRenderer(TileConstructionMarker.class, new RenderConstructionMarker());
        ClientRegistry.bindTileEntitySpecialRenderer(TileQuarry.class, new RenderQuarry());

        RenderingRegistry.registerEntityRenderingHandler(EntityMechanicalArm.class, new RenderVoid<EntityMechanicalArm>());
    }

    @Override
    public EntityResizableCuboid newDrill(World w, double i, double j, double k, double l, double d, double e) {
        EntityResizableCuboid cuboid = super.newDrill(w, i, j, k, l, d, e);
        cuboid.texture = drillTexture;
        cuboid.makeClient();
        // Special casing for the arms
        if (l == 1) {// X-arm (East - West)
            cuboid.textureOffsetX = 8;
            // Don't render the caps
            cuboid.textures[EnumFacing.WEST.ordinal()] = null;
            cuboid.textures[EnumFacing.EAST.ordinal()] = null;
        } else if (e == 1) {// Z-arm (North - South)
            cuboid.textureOffsetZ = 8;
            // Don't render the caps
            cuboid.textures[EnumFacing.NORTH.ordinal()] = null;
            cuboid.textures[EnumFacing.SOUTH.ordinal()] = null;
        }
        return cuboid;
    }

    @Override
    public EntityResizableCuboid newDrillHead(World w, double i, double j, double k, double l, double d, double e) {
        EntityResizableCuboid cuboid = super.newDrillHead(w, i, j, k, l, d, e);
        cuboid.texture = drillHeadTexture;
        cuboid.makeClient();
        cuboid.textureFlips[2] = 2;
        cuboid.textureFlips[3] = 2;
        cuboid.textureFlips[4] = 2;
        cuboid.textureFlips[5] = 2;
        return cuboid;
    }
}
