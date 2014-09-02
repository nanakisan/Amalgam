package amalgam.common.container;

public interface ICastingHandler {

    void updateTankCapacity(InventoryCasting inv);

    boolean isCastComplete();

    void onCastMatrixChanged(InventoryCasting inv);

}
