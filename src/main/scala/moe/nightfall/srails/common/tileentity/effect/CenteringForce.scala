package moe.nightfall.srails.common.tileentity.effect

import javax.vecmath.Vector3d

import moe.nightfall.srails.SRails
import moe.nightfall.srails.common.tileentity.SimpleEmitter
import net.minecraft.entity.Entity
import net.minecraft.util.EnumFacing

object CenteringForce extends EffectOnIntersect{
  //TODO move to utility
  lazy val axisMap: Map[EnumFacing.Axis, Vector3d] = Map(
    EnumFacing.Axis.X -> new Vector3d(1, 0, 0),
    EnumFacing.Axis.Y -> new Vector3d(0, 1, 0),
    EnumFacing.Axis.Z -> new Vector3d(0, 0, 1)
  )


  def getVector(axis: Vector3d, blockPos: Vector3d, entityPos: Vector3d): Vector3d = {

    val relativeVector: Vector3d = blockPos
      relativeVector.sub(entityPos)
    val dot = relativeVector.dot(axis)
    relativeVector.scaleAdd(-dot, axis)

    SRails.log.info(s"relative: $relativeVector dot: $dot blockPos: $blockPos entity: $entityPos")

    relativeVector
  }

  override def onEntityIntersect(e: Entity, em: SimpleEmitter): Unit = {
    val axis: Vector3d = axisMap(em.rotation.getAxis)
    val blockPos: Vector3d = new Vector3d(em.getPos.getX, em.getPos.getY, em.getPos.getZ)
    val entityPos: Vector3d = new Vector3d(e.posX, e.posY, e.posZ)

    val relVector: Vector3d = getVector(axis, blockPos, entityPos)

    e.motionX += relVector.x
    e.motionY += relVector.y
    e.motionZ += relVector.z

    //TODO pull entity towards central axis

  }
}
