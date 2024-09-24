package ch.endte.syncmatica.mixin;

import java.util.function.Consumer;
import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.Reference;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.communication.ClientCommunicationManager;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.network.actor.IClientPlay;
import ch.endte.syncmatica.network.handler.ClientPlayHandler;
import ch.endte.syncmatica.network.SyncmaticaPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

//#if MC >= 12002
import net.minecraft.network.packet.CustomPayload;
//#else
//$$ import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
//#endif

@Mixin(value = ClientPlayNetworkHandler.class, priority = 1001)
public abstract class MixinClientPlayNetworkHandler implements IClientPlay
{
    @Unique
    public ExchangeTarget exTarget = null;
    @Unique
    private ClientCommunicationManager comManager = null;

    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)

    //#if MC >= 12002
    private void syncmatica$handlePacket(CustomPayload packet, CallbackInfo ci)
    //#else
    //$$ private void syncmatica$handlePacket(CustomPayloadS2CPacket packet, CallbackInfo ci)
    //#endif
    {
        if (!MinecraftClient.getInstance().isOnThread())
        {
            return; //only execute packet on main thread
        }

        if (packet.getId().id().getNamespace().equals(Reference.MOD_ID))
        {
            SyncmaticaPacket.Payload payload = (SyncmaticaPacket.Payload) packet;
            ClientPlayHandler.decodeSyncData(payload.data(), (ClientPlayNetworkHandler) (Object) this);

            // Cancel unnecessary processing if a PacketType we own is caught
            if  (ci.isCancellable())
                ci.cancel();

        }
    }

    @Override
    public void syncmatica$operateComms(final Consumer<ClientCommunicationManager> operation)
    {
        if (comManager == null)
        {
            final Context con = Syncmatica.getContext(Syncmatica.CLIENT_CONTEXT);
            if (con != null)
            {
                comManager = (ClientCommunicationManager) con.getCommunicationManager();
            }
        }
        if (comManager != null)
        {
            operation.accept(comManager);
        }
    }

    @Override
    public ExchangeTarget syncmatica$getExchangeTarget()
    {
        if (exTarget == null)
        {
            exTarget = new ExchangeTarget((ClientPlayNetworkHandler) (Object) this);
        }
        return exTarget;
    }
}
