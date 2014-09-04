package amalgam.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import amalgam.common.Amalgam;
import amalgam.common.Config;
import amalgam.common.fluid.IAmalgamContainerItem;
import amalgam.common.tile.TileCastingTable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCastingTable extends BlockAmalgamContainer implements ITileEntityProvider {

    @SideOnly(Side.CLIENT)
    private IIcon iconBottomSide;
    @SideOnly(Side.CLIENT)
    private IIcon iconBottom;
    @SideOnly(Side.CLIENT)
    private IIcon iconTop;
    @SideOnly(Side.CLIENT)
    private IIcon iconNeck;

    public BlockCastingTable() {
        super(Material.rock);
        this.setHardness(3.0F);
        this.setResistance(5.0F);
        this.setStepSound(soundTypeStone);
        this.setCreativeTab(Config.tab);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.iconNeck = iconRegister.registerIcon("amalgam:castingTableNeck");
        this.iconTop = iconRegister.registerIcon("amalgam:castingTableTop");
        this.iconBottom = iconRegister.registerIcon("amalgam:castingTableBase");
        this.iconBottomSide = iconRegister.registerIcon("amalgam:castingTableBaseSide");
        this.blockIcon = iconRegister.registerIcon("amalgam:castingTableSide");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return side == 1 ? this.iconTop : side == 0 ? this.iconBottom : side == 6 ? this.iconNeck : side == 7 ? this.iconBottomSide : this.blockIcon;
    }

    private boolean onCastPickup(TileCastingTable table, EntityPlayer player) {
        table.setTankFluid(null);
        boolean matsRemain = false;

        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = table.getStackInSlot(slot);

            if (stack != null) {
                /* this will return null if no mates remain in the slot, we check if it is null to set the mats remain */
                matsRemain = null != table.decrStackSize(slot, 1);

                if (stack.getItem().hasContainerItem(stack)) {
                    dealWithContainerItem(table, stack, slot, player);
                }
            }
        }

        return matsRemain;
    }

    private void dealWithContainerItem(TileCastingTable table, ItemStack stack, int slot, EntityPlayer player) {
        ItemStack containerItem = stack.getItem().getContainerItem(stack);

        if (containerItem != null && containerItem.isItemStackDamageable() && containerItem.getItemDamage() > containerItem.getMaxDamage()) {
            MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, containerItem));
            return;
        }

        if (!stack.getItem().doesContainerItemLeaveCraftingGrid(stack) || !player.inventory.addItemStackToInventory(containerItem)) {
            if (table.getStackInSlot(slot) == null) {
                table.setInventorySlotContents(slot, containerItem);
            } else {
                player.dropPlayerItemWithRandomChoice(containerItem, false);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.inventory.getCurrentItem();
        TileCastingTable table = (TileCastingTable) world.getTileEntity(x, y, z);

        if (stack == null) {

            ItemStack resultStack = table.getStackInSlot(9);
            if (player.isSneaking() && resultStack != null && table.getEmptySpace() == 0) {
                if (!onCastPickup(table, player)) {
                    table.onCastMatrixChanged(table.castingInventory);
                }
                player.setCurrentItemOrArmor(0, resultStack);

                return false;
            }

            player.openGui(Amalgam.instance, Config.CASTING_GUI_ID, world, x, y, z);

            return true;
        }

        if (stack.getItem() instanceof IAmalgamContainerItem) {
            interactWithAmalgamContainerItem(table, (IAmalgamContainerItem) stack.getItem(), stack, player);

            return true;
        }

        player.openGui(Amalgam.instance, Config.CASTING_GUI_ID, world, x, y, z);

        return true;
    }

    @Override
    protected void interactWithAmalgableItem(TileEntity te, ItemStack stack) {
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metaData) {
        return new TileCastingTable();
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int metaData) {
        TileCastingTable table = (TileCastingTable) world.getTileEntity(x, y, z);

        if (table != null) {
            table.emptyTank();
        }

        super.breakBlock(world, x, y, z, block, metaData);
        world.removeTileEntity(x, y, z);
    }

    @Override
    public boolean isToolEffective(String type, int metadata) {
        if ("pickaxe".equals(type)) {
            return true;
        }

        return false;
    }

    @Override
    public int getRenderType() {
        return Config.castingTableRID;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public void setBlockBoundsForItemRender() {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }
}
