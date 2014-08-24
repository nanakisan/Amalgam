package amalgam.common.item;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import amalgam.common.casting.ICastItem;

import com.google.common.collect.Sets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAmalgamShovel extends ItemAmalgamTool implements ICastItem {

    private static final Set<Block> USABLE_BLOCKS = Sets.newHashSet(new Block[] { Blocks.grass, Blocks.dirt, Blocks.sand, Blocks.gravel,
            Blocks.snow_layer, Blocks.snow, Blocks.clay, Blocks.farmland, Blocks.soul_sand, Blocks.mycelium });

    public ItemAmalgamShovel() {
        super(1.0F, ItemAmalgamTool.TOOL_CLASS_SHOVEL, USABLE_BLOCKS);
        this.setHarvestLevel(ItemAmalgamTool.TOOL_CLASS_SHOVEL, 0);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon("amalgam:amalgamShovelBlade");
        this.hilt = iconRegister.registerIcon("amalgam:amalgamShovelHilt");
    }
}
