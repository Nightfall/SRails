package moe.nightfall.srails.common.tileentity.effect

import javax.vecmath.Vector3d

import moe.nightfall.srails.common.tileentity.SimpleEmitter
import net.minecraft.entity.Entity

import scala.collection.mutable.ArrayBuffer

abstract class ForceFieldEffect

object EffectOnIntersect {

  def onEntityIntersect(entities: Seq[Entity], effectsList: ArrayBuffer[ForceFieldEffect], emitter: SimpleEmitter) = {
    for (entity <- entities) {

    }

    val buffer = new ArrayBuffer[MotionEffectOnIntersect]()

    for (effect: ForceFieldEffect <- effectsList.distinct) {
      effect match {
        case m: MotionEffectOnIntersect =>
          buffer += m
        case e: EffectOnIntersect =>
          onEntityIntersectAll(entities, e, emitter)
        case _ =>
      }
    }
    MotionEffectOnIntersect.onEntityIntersect(entities, buffer, emitter)
  }

  private def onEntityIntersectAll(entities: Seq[Entity], effect: EffectOnIntersect, emitter: SimpleEmitter): Unit = {
    for (entity <- entities) {
      effect.onEntityIntersect(entity, emitter)
    }
  }

}

abstract class EffectOnIntersect extends ForceFieldEffect {
  def onEntityIntersect(entity: Entity, emitter: SimpleEmitter): Unit
}


object MotionEffectOnIntersect {
  def onEntityIntersect(entities: Seq[Entity], effectsList: ArrayBuffer[MotionEffectOnIntersect], emitter: SimpleEmitter) = {
    for (entity <- entities) {
      for (effect: MotionEffectOnIntersect <- effectsList.distinct) {
        var motion: Vector3d = new Vector3d(entity.motionX, entity.motionY, entity.motionZ)
        val position = new Vector3d(entity.posX, entity.posY, entity.posZ)
        motion = effect.onEntityIntersect(motion, position, entity, emitter)
        entity.motionX = motion.x
        entity.motionY = motion.y
        entity.motionZ = motion.z
      }
    }
  }
}

abstract class MotionEffectOnIntersect extends EffectOnIntersect {

  def onEntityIntersect(motion: Vector3d, position: Vector3d, entity: Entity, emitter: SimpleEmitter): Vector3d

  //single call wrapper
  override def onEntityIntersect(entity: Entity, emitter: SimpleEmitter): Unit = {
    var motion: Vector3d = new Vector3d(entity.motionX, entity.motionY, entity.motionZ)
    val position = new Vector3d(entity.posX, entity.posY, entity.posZ)
    motion = onEntityIntersect(motion, position, entity, emitter)
    entity.motionX = motion.x
    entity.motionY = motion.y
    entity.motionZ = motion.z
  }

}