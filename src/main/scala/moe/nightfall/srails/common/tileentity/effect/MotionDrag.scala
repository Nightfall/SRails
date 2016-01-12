package moe.nightfall.srails.common.tileentity.effect

import javax.vecmath.Vector3d

import moe.nightfall.srails.common.tileentity.SimpleEmitter
import net.minecraft.entity.Entity

object MotionDrag extends MotionEffectOnIntersect {
  override def onEntityIntersect(motion: Vector3d, position: Vector3d, entity: Entity, emitter: SimpleEmitter): Vector3d = {
    motion.scale(1 - (1 / 32))
    motion
  }
}
