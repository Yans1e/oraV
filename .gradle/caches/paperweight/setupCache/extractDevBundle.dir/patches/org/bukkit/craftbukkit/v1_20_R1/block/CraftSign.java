package org.bukkit.craftbukkit.v1_20_R1.block;

import com.google.common.base.Preconditions;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.bukkit.DyeColor;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.craftbukkit.v1_20_R1.block.sign.CraftSignSide;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerSignOpenEvent;
import org.jetbrains.annotations.NotNull;

public class CraftSign<T extends SignBlockEntity> extends CraftBlockEntityState<T> implements Sign {

    private final CraftSignSide front;
    private final CraftSignSide back;

    public CraftSign(World world, T tileEntity) {
        super(world, tileEntity);
        this.front = new CraftSignSide(this.getSnapshot().getFrontText());
        this.back = new CraftSignSide(this.getSnapshot().getBackText());
    }

    // Paper start
    @Override
    public java.util.@NotNull List<net.kyori.adventure.text.Component> lines() {
        return this.front.lines();
    }

    @Override
    public net.kyori.adventure.text.@NotNull Component line(int index) {
        return this.front.line(index);
    }

    @Override
    public void line(int index, net.kyori.adventure.text.@NotNull Component line) {
        this.front.line(index, line);
    }
    // Paper end

    @Override
    public String[] getLines() {
        return this.front.getLines();
    }

    @Override
    public String getLine(int index) throws IndexOutOfBoundsException {
        return this.front.getLine(index);
    }

    @Override
    public void setLine(int index, String line) throws IndexOutOfBoundsException {
        this.front.setLine(index, line);
    }

    @Override
    public boolean isEditable() {
        return !this.isWaxed();
    }

    @Override
    public void setEditable(boolean editable) {
        this.setWaxed(!editable);
    }

    @Override
    public boolean isWaxed() {
        return getSnapshot().isWaxed();
    }

    @Override
    public void setWaxed(boolean waxed) {
        getSnapshot().setWaxed(waxed);
    }

    @Override
    public boolean isGlowingText() {
        return this.front.isGlowingText();
    }

    @Override
    public void setGlowingText(boolean glowing) {
        this.front.setGlowingText(glowing);
    }

    @NotNull
    @Override
    public SignSide getSide(Side side) {
        Preconditions.checkArgument(side != null, "side == null");

        switch (side) {
            case FRONT:
                return this.front;
            case BACK:
                return this.back;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public DyeColor getColor() {
        return this.front.getColor();
    }

    @Override
    public void setColor(DyeColor color) {
        this.front.setColor(color);
    }

    @Override
    public void applyTo(T sign) {
        getSnapshot().setText(this.front.applyLegacyStringToSignSide(), true);
        getSnapshot().setText(this.back.applyLegacyStringToSignSide(), false);

        super.applyTo(sign);
    }

    public static void openSign(Sign sign, Player player, Side side) {
        Preconditions.checkArgument(sign != null, "sign == null");
        Preconditions.checkArgument(side != null, "side == null");
        Preconditions.checkArgument(sign.isPlaced(), "Sign must be placed");
        Preconditions.checkArgument(sign.getWorld() == player.getWorld(), "Sign must be in same world as Player");

        // Paper start
        io.papermc.paper.event.player.PlayerOpenSignEvent event = new io.papermc.paper.event.player.PlayerOpenSignEvent((Player) player, sign, side, io.papermc.paper.event.player.PlayerOpenSignEvent.Cause.PLUGIN);
        if (!event.callEvent()) return;
        if (PlayerSignOpenEvent.getHandlerList().getRegisteredListeners().length > 0) {
            // Paper end
        if (!CraftEventFactory.callPlayerSignOpenEvent(player, sign, side, PlayerSignOpenEvent.Cause.PLUGIN)) {
            return;
        }
        } // Paper

        SignBlockEntity handle = ((CraftSign<?>) sign).getTileEntity();
        handle.setAllowedPlayerEditor(player.getUniqueId());

        ((CraftPlayer) player).getHandle().openTextEdit(handle, Side.FRONT == side);
    }

    // Paper start
    public static Component[] sanitizeLines(java.util.List<? extends net.kyori.adventure.text.Component> lines) {
        Component[] components = new Component[4];
        for (int i = 0; i < 4; i++) {
            if (i < lines.size() && lines.get(i) != null) {
                components[i] = io.papermc.paper.adventure.PaperAdventure.asVanilla(lines.get(i));
            } else {
                components[i] = net.minecraft.network.chat.Component.literal("");
            }
        }
        return components;
    }
    // Paper end

    // Paper start - side facing API
    @Override
    public Side getInteractableSideFor(final double x, final double z) {
        this.requirePlaced();
        return this.getSnapshot().isFacingFrontText(x, z) ? Side.FRONT : Side.BACK;
    }
    // Paper end

    public static Component[] sanitizeLines(String[] lines) {
        Component[] components = new Component[4];

        for (int i = 0; i < 4; i++) {
            if (i < lines.length && lines[i] != null) {
                components[i] = CraftChatMessage.fromString(lines[i])[0];
            } else {
                components[i] = Component.empty();
            }
        }

        return components;
    }

    public static String[] revertComponents(Component[] components) {
        String[] lines = new String[components.length];
        for (int i = 0; i < lines.length; i++) {
            lines[i] = CraftSign.revertComponent(components[i]);
        }
        return lines;
    }

    private static String revertComponent(Component component) {
        return CraftChatMessage.fromComponent(component);
    }
}
