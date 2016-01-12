package moe.nightfall.srails.common.tileentity.effect

import javax.vecmath.Vector3d

import moe.nightfall.srails.common.tileentity.SimpleEmitter
import net.minecraft.entity.Entity
import net.minecraft.util.EnumFacing

object CenteringForce extends MotionEffectOnIntersect{
  //TODO move to utility
  lazy val axisMap: Map[EnumFacing.Axis, Vector3d] = Map(
    EnumFacing.Axis.X -> new Vector3d(1, 0, 0),
    EnumFacing.Axis.Y -> new Vector3d(0, 1, 0),
    EnumFacing.Axis.Z -> new Vector3d(0, 0, 1)
  )


  def getVector(axis: Vector3d, blockPos: Vector3d, entityPos: Vector3d): Vector3d = {

    val relativeVector: Vector3d = blockPos
      relativeVector.sub(entityPos)
    val temp: Vector3d = new Vector3d()

    temp.cross(axis, relativeVector)

    relativeVector.cross(temp, axis)

    relativeVector
  }

  override def onEntityIntersect(motion: Vector3d, position: Vector3d, entity: Entity, emitter: SimpleEmitter): Vector3d = {

    val axis: Vector3d = axisMap(emitter.rotation.getAxis)
    val blockPos: Vector3d = new Vector3d(emitter.getPos.getX + 0.5, emitter.getPos.getY + 0.5, emitter.getPos.getZ + 0.5)

    val relVector: Vector3d = getVector(axis, blockPos, position)

    relVector.scale(1/(relVector.lengthSquared() + 1))
    relVector.scale(0.03)

    motion.add(relVector)
    motion
  }
}
