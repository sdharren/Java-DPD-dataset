/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.energy;

import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class EnergyProxy {
//    @SidedProxy(clientSide = "buildcraft.energy.EnergyProxyClient", serverSide = "buildcraft.energy.EnergyProxy")
    public static EnergyProxy proxy;

    public void registerTileEntities() {
        GameRegistry.registerTileEntityWithAlternatives(TileEngineStone.class, "buildcraft.energy.engine.stone",
                "net.minecraft.src.buildcraft.energy.TileEngineStone");
        GameRegistry.registerTileEntityWithAlternatives(TileEngineIron.class, "buildcraft.energy.engine.iron",
                "net.minecraft.src.buildcraft.energy.TileEngineIron");
        GameRegistry.registerTileEntityWithAlternatives(TileEngineCreative.class, "buildcraft.energy.engine.creative",
                "net.minecraft.src.buildcraft.energy.TileEngineCreative");
    }

    public void registerBlockRenderers() {}
}
