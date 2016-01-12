package moe.nightfall.srails.common.tileentity.effect

import javax.vecmath.Vector3d

import moe.nightfall.srails.common.tileentity.SimpleEmitter
import moe.nightfall.srails.common.tileentity.traits.Reversible
import net.minecraft.entity.Entity
import net.minecraft.util.{EnumFacing, Vec3i}

object DirectionalForce extends MotionEffectOnIntersect {

  override def onEntityIntersect(motion: Vector3d, position: Vector3d, entity: Entity, emitter: SimpleEmitter): Vector3d = {
    var rotation = emitter.rotation
    val scale = rotation match {
      case EnumFacing.UP => 0.005
      case EnumFacing.DOWN => 0.1
      case _ => 0.01
    }

    emitter match {
      case reversibleEmitter: Reversible =>
        if(reversibleEmitter.isReverse) rotation = rotation.getOpposite
      case _ =>
    }

    val vec: Vec3i = rotation.getDirectionVec
    val motionDirection: Vector3d = new Vector3d(vec.getX, vec.getY, vec.getZ)
    motionDirection.scale(scale)
    motion.add(motionDirection)
    motion
  }
}
