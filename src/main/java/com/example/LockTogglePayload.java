package com.example;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record LockTogglePayload(int syncId, int slotIndex) implements CustomPayload {
    public static final CustomPayload.Id<LockTogglePayload> ID = new CustomPayload.Id<>(Identifier.of("inventory-sort-mod","lock_toggle"));

    public static final PacketCodec<net.minecraft.network.RegistryByteBuf, LockTogglePayload> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.VAR_INT, LockTogglePayload::syncId,
                    PacketCodecs.VAR_INT, LockTogglePayload::slotIndex,
                    LockTogglePayload::new
            );
    @Override
    public Id<? extends CustomPayload> getId(){
        return ID;
    }
}
