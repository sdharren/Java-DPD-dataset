/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.factory;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.SidedProxy;

import buildcraft.core.lib.EntityResizableCuboid;

public class FactoryProxy {
//    @SidedProxy(clientSide = "buildcraft.factory.FactoryProxyClient", serverSide = "buildcraft.factory.FactoryProxy")
    public static FactoryProxy proxy;

    public void preInit() {}

    public void fmlInit() {}

    public EntityResizableCuboid newPumpTube(World w) {
        return new EntityResizableCuboid(w);
    }
}
