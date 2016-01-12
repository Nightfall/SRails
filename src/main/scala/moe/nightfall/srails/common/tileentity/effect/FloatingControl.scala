package moe.nightfall.srails.common.tileentity.effect

import javax.vecmath.Vector3d

import moe.nightfall.srails.common.tileentity.SimpleEmitter
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.Entity

import scala.math._

object FloatingControl extends EffectOnIntersect {
  override def onEntityIntersect(entity: Entity, emitter: SimpleEmitter): Unit = {

    entity match {
      case p: EntityPlayerSP =>
        if (!p.capabilities.isFlying) {

          //TODO config heading based movement (only useful when jumpKey + sneakKey are enabled)
          val yaw: Double = toRadians(p.rotationYaw)
          val pitch: Double = toRadians(p.rotationPitch)
          val heading = new Vector3d(-sin(yaw) * cos(pitch), -sin(pitch), cos(yaw) * cos(pitch))
          val motion: Vector3d = new Vector3d(p.motionX, p.motionY, p.motionZ)

          p.movementInput.updatePlayerMoveState()
          if (p.movementInput.moveForward != 0) {
            //reverse default forward motion
            val forward = new Vector3d(-sin(yaw), 0, cos(yaw))
            forward.scale(0.02 * -p.movementInput.moveForward)
            motion.add(forward)
            //add forward motion with heading
            heading.scale(0.01 * p.movementInput.moveForward)
            motion.add(heading)
          }

          p.motionX = motion.x
          p.motionY = motion.y
          p.motionZ = motion.z
        }
      case _ =>
    }
  }
}
