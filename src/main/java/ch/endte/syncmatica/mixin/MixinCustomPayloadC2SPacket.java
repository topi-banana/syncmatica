package ch.endte.syncmatica.mixin;

import ch.endte.syncmatica.util.DummyClass;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;

@Restriction(require = @Condition(value = "minecraft", versionPredicates = ">=1.20.2"))
@Mixin(DummyClass.class)
public class MixinCustomPayloadC2SPacket {
}
