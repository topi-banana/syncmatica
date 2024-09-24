package ch.endte.syncmatica.network.handler;

import javax.annotation.Nonnull;
import java.util.Objects;
import ch.endte.syncmatica.network.SyncmaticaPacket;
import ch.endte.syncmatica.network.actor.ActorClientPlayHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;

//#if MC>=12002
import net.minecraft.network.packet.CustomPayload;
//#else
//$$ import net.minecraft.network.packet.CustomPayload;
//#endif

/**
 * Network packet senders / receivers (Client Context)
 */
public class ClientPlayHandler
{
    public static void decodeSyncData(@Nonnull SyncmaticaPacket data, @Nonnull ClientPlayNetworkHandler handler)
    {
        CallbackInfo ci = new CallbackInfo("receiveSyncPacket", false);
        ActorClientPlayHandler.getInstance().packetEvent(data.getType(), data.getPacket(), handler, ci);
    }

    public static void encodeSyncData(@Nonnull SyncmaticaPacket data, ClientPlayNetworkHandler handler)
    {
        SyncmaticaPacket.Payload payload = new SyncmaticaPacket.Payload(data);
        if (handler != null)
        {
            sendSyncPacket(payload, handler);
        }
        else
        {
            sendSyncPacket(payload);
        }
    }

    public static void receiveSyncPayload(@Nonnull SyncmaticaPacket data)
    {
        CallbackInfo ci = new CallbackInfo("receiveSyncPacket", false);
        ActorClientPlayHandler.getInstance().packetEvent(data.getType(), data.getPacket(), MinecraftClient.getInstance().getNetworkHandler(), ci);
    }

    public static void receiveSyncPayload(SyncmaticaPacket.Payload payload, ClientPlayNetworking.Context context)
    {
        decodeSyncData(payload.data(), Objects.requireNonNull(context.client().getNetworkHandler()));
    }

    public static <T extends CustomPayload> void sendSyncPacket(@Nonnull T payload)
    {
        if (ClientPlayNetworking.canSend(payload.getId()))
        {
            ClientPlayNetworking.send(payload);
        }
    }

    public static <T extends CustomPayload> void sendSyncPacket(@Nonnull T payload, @Nonnull ClientPlayNetworkHandler handler)
    {
        Packet<?> packet = new CustomPayloadC2SPacket(payload);
        if (handler.accepts(packet))
        {
            handler.sendPacket(packet);
        }
    }
}
