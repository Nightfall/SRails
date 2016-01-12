package moe.nightfall.srails.common.tileentity.effect

import moe.nightfall.srails.common.tileentity.SimpleEmitter
import net.minecraft.entity.Entity

abstract class ForceFieldEffect

abstract class EffectOnIntersect extends ForceFieldEffect {
  def onEntityIntersect(entity: Entity, emitter: SimpleEmitter): Unit
}