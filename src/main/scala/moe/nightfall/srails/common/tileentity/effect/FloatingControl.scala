package moe.nightfall.srails.common.tileentity.effect

import javax.vecmath.Vector3d

import moe.nightfall.srails.common.tileentity.SimpleEmitter
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.Entity
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import scala.math._

object FloatingControl extends MotionEffectOnIntersect {
  lazy val gameSettings = FMLClientHandler.instance.getClient.gameSettings

  @SideOnly(Side.CLIENT)
  def jumpKeyDown = {
    gameSettings.keyBindJump.isKeyDown
  }

  override def onEntityIntersect(motion: Vector3d, position: Vector3d, entity: Entity, emitter: SimpleEmitter): Vector3d = {
    //TODO Config sneaking for falling in forcefields
    if (entity.isSneaking) {
      motion.y -= 0.01
    }

    entity match {
      case p: EntityPlayerSP =>
        if (!p.capabilities.isFlying) {

          //TODO config heading based movement (only useful when jumpKey + sneakKey are enabled)
          val yaw: Double = toRadians(p.rotationYaw)
          val pitch: Double = toRadians(p.rotationPitch)
          val heading = new Vector3d(-sin(yaw) * cos(pitch), -sin(pitch), cos(yaw) * cos(pitch))

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

          //TODO config jump for raising in forcefields
          if (jumpKeyDown) motion.y += 0.01


        }
      case _ =>
    }
    motion
  }
}
