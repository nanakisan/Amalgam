package amalgam.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import amalgam.common.Amalgam;
import amalgam.common.Config;
import amalgam.common.fluid.AmalgamStack;
import amalgam.common.fluid.IAmalgamContainerItem;
import amalgam.common.tile.TileCastingTable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCastingTable extends BlockContainer implements ITileEntityProvider {

    private IIcon iconBottomSide;
    private IIcon iconBottom;
    private IIcon iconTop;
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

    private boolean interactWithAmalgamContainerItem(TileEntity te, IAmalgamContainerItem container, ItemStack stack, EntityPlayer player) {
        TileCastingTable crucible = (TileCastingTable) te;

        if (player.isSneaking()) {
            int drainAmount = Math.min(container.getEmptySpace(stack), Config.BASE_AMOUNT);
            AmalgamStack fluidStack = (AmalgamStack) crucible.drain(ForgeDirection.UNKNOWN, drainAmount, true);

            if (fluidStack != null) {
                int result = container.fill(stack, fluidStack, true);
                fluidStack.amount -= result;

                if (fluidStack.amount > 0) {
                    crucible.fill(ForgeDirection.UNKNOWN, fluidStack, true);
                }
            }
        } else if (container.getFluid(stack).amount == 0) {
            AmalgamStack fluidStack = (AmalgamStack) crucible.drain(ForgeDirection.UNKNOWN, container.getEmptySpace(stack), true);

            if (fluidStack != null) {
                int result = container.fill(stack, fluidStack, true);
                fluidStack.amount -= result;

                if (fluidStack.amount > 0) {
                    crucible.fill(ForgeDirection.UNKNOWN, fluidStack, true);
                }
            }
        } else {
            AmalgamStack newStack = (AmalgamStack) container.drain(stack, container.getCapacity(stack), true);
            int filled = crucible.fill(ForgeDirection.UNKNOWN, newStack, true);
            newStack.amount -= filled;
            container.fill(stack, newStack, true);
        }

        return true;
    }

    private boolean onCastPickup(TileCastingTable table, EntityPlayer player) {
        table.setTankFluid(null);
        boolean matsRemain = true;

        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = table.getStackInSlot(slot);

            if (stack != null) {
                matsRemain = table.decrStackSize(slot, 1);

                if (stack.getItem().hasContainerItem(stack)) {
                    ItemStack itemstack2 = stack.getItem().getContainerItem(stack);

                    if (itemstack2 != null && itemstack2.isItemStackDamageable() && itemstack2.getItemDamage() > itemstack2.getMaxDamage()) {
                        MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, itemstack2));
                        continue;
                    }

                    if (!stack.getItem().doesContainerItemLeaveCraftingGrid(stack) || !player.inventory.addItemStackToInventory(itemstack2)) {
                        if (table.getStackInSlot(slot) == null) {
                            table.setStackInSlot(slot, itemstack2);
                        } else {
                            player.dropPlayerItemWithRandomChoice(itemstack2, false);
                        }
                    }
                }
            }
        }

        return matsRemain;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.inventory.getCurrentItem();
        TileCastingTable table = (TileCastingTable) world.getTileEntity(x, y, z);

        if (stack == null) {
            if (player.isSneaking()) {
                ItemStack resultStack = table.getStackInSlot(9);

                if (resultStack != null && table.tankIsFull()) {
                    if (!onCastPickup(table, player)) {
                        table.setStackInSlot(9, null);
                    }
                    player.setCurrentItemOrArmor(0, resultStack);
                }

                return false;
            }

            player.openGui(Amalgam.instance, Config.CASTING_GUI_ID, world, x, y, z);

            return true;
        }

        if (stack.getItem() instanceof IAmalgamContainerItem) {
            return interactWithAmalgamContainerItem(table, (IAmalgamContainerItem) stack.getItem(), stack, player);
        }

        player.openGui(Amalgam.instance, Config.CASTING_GUI_ID, world, x, y, z);
        return true;
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
