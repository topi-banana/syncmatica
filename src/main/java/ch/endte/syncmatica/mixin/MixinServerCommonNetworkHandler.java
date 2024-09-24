package ch.endte.syncmatica.mixin;

import ch.endte.syncmatica.Reference;
import ch.endte.syncmatica.network.SyncmaticaPacket;
import ch.endte.syncmatica.network.handler.ServerPlayHandler;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;

@Restriction(require = @Condition(value = "minecraft", versionPredicates = ">=1.20.2"))
@Mixin(ServerCommonNetworkHandler.class)
public class MixinServerCommonNetworkHandler
{
    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
    private void syncmatica$handlePacket(CustomPayloadC2SPacket packet, CallbackInfo ci)
    {
        if (packet.payload().getId().id().getNamespace().equals(Reference.MOD_ID))
        {
            SyncmaticaPacket.Payload payload = (SyncmaticaPacket.Payload) packet.payload();
            Object thiss = this;

            if (thiss instanceof ServerPlayNetworkHandler handler)
            {
                ServerPlayHandler.decodeSyncData(payload.data(), handler);
            }

            // Cancel unnecessary processing if a PacketType we own is caught
            if  (ci.isCancellable())
                ci.cancel();

        }
    }
}
