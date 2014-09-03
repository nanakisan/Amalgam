package amalgam.common.container;

public interface ICastingHandler {

    void updateTankCapacity(InventoryCasting inv);

    boolean isCastComplete();

    void onCastPickup();

    void onCastMatrixChanged(InventoryCasting inv);

    void updateAmalgamDistribution();

}
