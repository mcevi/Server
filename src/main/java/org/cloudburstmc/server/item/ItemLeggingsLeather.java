package org.cloudburstmc.server.item;

import org.cloudburstmc.server.utils.Identifier;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemLeggingsLeather extends ItemColorArmor {

    public ItemLeggingsLeather(Identifier id) {
        super(id);
    }

    @Override
    public int getTier() {
        return TIER_LEATHER;
    }

    @Override
    public boolean isLeggings() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 2;
    }

    @Override
    public int getMaxDurability() {
        return 76;
    }
}
