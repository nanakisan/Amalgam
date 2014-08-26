package amalgam.common.item;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Sets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAmalgamPick extends ItemAmalgamTool {

    private static final Set<Block> USABLE_BLOCKS = Sets.newHashSet(new Block[] { Blocks.cobblestone, Blocks.double_stone_slab, Blocks.stone_slab,
            Blocks.stone, Blocks.sandstone, Blocks.mossy_cobblestone, Blocks.iron_ore, Blocks.iron_block, Blocks.coal_ore, Blocks.gold_block,
            Blocks.gold_ore, Blocks.diamond_ore, Blocks.diamond_block, Blocks.ice, Blocks.netherrack, Blocks.lapis_ore, Blocks.lapis_block,
            Blocks.redstone_ore, Blocks.lit_redstone_ore, Blocks.rail, Blocks.detector_rail, Blocks.golden_rail, Blocks.activator_rail });

    public ItemAmalgamPick() {
        super(2.0F, ItemAmalgamTool.TOOL_CLASS_PICK, USABLE_BLOCKS);
        this.setHarvestLevel(ItemAmalgamTool.TOOL_CLASS_PICK, 0);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon("amalgam:amalgamPickBlade");
        this.hilt = iconRegister.registerIcon("amalgam:amalgamPickHilt");
    }

    @Override
    public boolean canHarvestBlock(Block block, ItemStack stack) {
        float harvestLevel = this.getHarvestLevel(stack, block.getHarvestTool(0));
        return block == Blocks.obsidian ? harvestLevel >= 3
                : (block != Blocks.diamond_block && block != Blocks.diamond_ore ? (block != Blocks.emerald_ore && block != Blocks.emerald_block ? (block != Blocks.gold_block
                        && block != Blocks.gold_ore ? (block != Blocks.iron_block && block != Blocks.iron_ore ? (block != Blocks.lapis_block
                        && block != Blocks.lapis_ore ? (block != Blocks.redstone_ore && block != Blocks.lit_redstone_ore ? (block.getMaterial() == Material.rock ? true
                        : (block.getMaterial() == Material.iron ? true : block.getMaterial() == Material.anvil))
                        : harvestLevel >= 2)
                        : harvestLevel >= 1)
                        : harvestLevel >= 1)
                        : harvestLevel >= 2)
                        : harvestLevel >= 2)
                        : harvestLevel >= 2);

    }

    @Override
    public float func_150893_a(ItemStack stack, Block block) {
        return block.getMaterial() != Material.iron && block.getMaterial() != Material.anvil && block.getMaterial() != Material.rock ? super
                .func_150893_a(stack, block) : this.getEfficiency(stack);
    }
}