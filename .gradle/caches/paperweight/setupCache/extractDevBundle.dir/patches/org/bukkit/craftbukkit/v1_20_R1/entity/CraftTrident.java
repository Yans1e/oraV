package org.bukkit.craftbukkit.v1_20_R1.entity;

import net.minecraft.world.entity.projectile.ThrownTrident;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.Trident;
import org.bukkit.inventory.ItemStack;

public class CraftTrident extends CraftArrow implements Trident {

    public CraftTrident(CraftServer server, ThrownTrident entity) {
        super(server, entity);
    }

    @Override
    public ThrownTrident getHandle() {
        return (ThrownTrident) super.getHandle();
    }

    @Override
    public ItemStack getItem() {
        return CraftItemStack.asBukkitCopy(this.getHandle().tridentItem);
    }

    @Override
    public void setItem(ItemStack itemStack) {
        this.getHandle().tridentItem = CraftItemStack.asNMSCopy(itemStack);
    }

    @Override
    public String toString() {
        return "CraftTrident";
    }

    // Paper start
    @Override
    public boolean hasGlint() {
        return this.getHandle().isFoil();
    }

    @Override
    public void setGlint(boolean glint) {
        this.getHandle().setFoil(glint);
    }

    @Override
    public int getLoyaltyLevel() {
        return this.getHandle().getLoyalty();
    }

    @Override
    public void setLoyaltyLevel(int loyaltyLevel) {
        com.google.common.base.Preconditions.checkArgument(loyaltyLevel >= 0 && loyaltyLevel <= 127, "The loyalty level has to be between 0 and 127");
        this.getHandle().setLoyalty((byte) loyaltyLevel);
    }

    @Override
    public boolean hasDealtDamage() {
        return this.getHandle().dealtDamage;
    }

    @Override
    public void setHasDealtDamage(boolean hasDealtDamage) {
        this.getHandle().dealtDamage = hasDealtDamage;
    }
    // Paper end
}
