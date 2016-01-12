package moe.nightfall.srails.common.tileentity

import moe.nightfall.srails.SRails
import moe.nightfall.srails.common.block.property.PropertyRotatable
import moe.nightfall.srails.common.tileentity.effect._
import moe.nightfall.srails.common.tileentity.traits.{Effectable, Rotatable}
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.util._
import net.minecraft.world.World

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer
import scala.util.Random

abstract class SimpleEmitter extends TileEntity with ITickable with Rotatable with Effectable {

  lazy val fieldOffsets: Array[Int] = Array(-1, 32, 2, 2, 2, 2)

  def maxVelo: Double = 0.25

  protected var forceFieldBounds: AxisAlignedBB = null

  protected val effectsList: ArrayBuffer[ForceFieldEffect]


  //TODO move into utility
  private def rotated(axis: EnumFacing.Axis*): Map[EnumFacing, Int] = {
    var result: Map[EnumFacing, Int] = Map()
    for (facing <- EnumFacing.VALUES) {
      var temp: EnumFacing = facing
      axis.foreach(axis => {
        temp = temp.rotateAround(axis)
      })
      result += (facing -> temp.getIndex)
    }
    result
  }

  val rotationIndices: Map[EnumFacing, Map[EnumFacing, Int]] = Map(
    EnumFacing.UP ->
      rotated(),
    EnumFacing.DOWN ->
      rotated(EnumFacing.Axis.X, EnumFacing.Axis.X),
    EnumFacing.NORTH ->
      rotated(EnumFacing.Axis.Y, EnumFacing.Axis.Y, EnumFacing.Axis.X),
    EnumFacing.EAST ->
      rotated(EnumFacing.Axis.Y, EnumFacing.Axis.X),
    EnumFacing.SOUTH ->
      rotated(EnumFacing.Axis.X),
    EnumFacing.WEST ->
      rotated(EnumFacing.Axis.Y, EnumFacing.Axis.Y, EnumFacing.Axis.Y, EnumFacing.Axis.X)
  )

  protected def makeBounds(facing: EnumFacing): AxisAlignedBB = {
    //creates a 5 * 32 * 5 field in front of the emitter

    val indices: Map[EnumFacing, Int] = rotationIndices(facing)

    new AxisAlignedBB(
      pos.getX - fieldOffsets(indices(EnumFacing.WEST)),
      pos.getY - fieldOffsets(indices(EnumFacing.DOWN)),
      pos.getZ - fieldOffsets(indices(EnumFacing.NORTH)),
      pos.getX + fieldOffsets(indices(EnumFacing.EAST)) + 1,
      pos.getY + fieldOffsets(indices(EnumFacing.UP)) + 1,
      pos.getZ + fieldOffsets(indices(EnumFacing.SOUTH)) + 1
    )
  }

  /*
    override def validate() {
      super.validate()
      addAABB(bounds, getWorld.isRemote)
      SRails.log.info(s"validate ${bounds.hashCode()} $bounds")
    }

    override def onLoad() {
      srails.SRails.log.info("onLoad")
      super.onLoad()
      if(bounds == null) bounds = makeBounds(facing)
      addAABB(bounds, getWorld.isRemote)
      SRails.log.info(s"onLoad ${bounds.hashCode()} $bounds")
    }
  */
  override def invalidate() {
    super.invalidate()
    unregisterBounds(forceFieldBounds)
  }

  override def onChunkUnload() {
    super.onChunkUnload()
    unregisterBounds(forceFieldBounds)
  }


  override def update() {
    if (forceFieldBounds == null) {
      SRails.log.info("makeBounds in update")
      forceFieldBounds = makeBounds(rotation)
      registerBounds(forceFieldBounds)
    }
    val buf: Seq[Entity] = getWorld.getEntitiesWithinAABB(classOf[Entity], forceFieldBounds).asScala

    buf.foreach(onEntityIntersect)

    val r = new Random()
    if (getWorld.isRemote) {
      for (x: Double <- forceFieldBounds.minX to forceFieldBounds.maxX by 0.5;
           y: Double <- forceFieldBounds.minY to forceFieldBounds.maxY by 0.5;
           z: Double <- forceFieldBounds.minZ to forceFieldBounds.maxZ by 0.5) {
        if (r.nextDouble() < 0.05)
          getWorld.spawnParticle(EnumParticleTypes.PORTAL, x, y, z, 0, 0, 0, 0, 3, 0)
      }
    }
  }

  override def addEffect(effect: ForceFieldEffect): Unit = {
    effectsList += effect
    SRails.log.info(s"effect added: $effect")
  }

  protected def onEntityIntersect(entity: Entity) = {
    for (effect: ForceFieldEffect <- effectsList.distinct) {
      effect match {
        case e: EffectOnIntersect =>
          e.onEntityIntersect(entity, this)
        case _ =>
      }
    }
  }

  protected def registerBounds(aabb: AxisAlignedBB) = {
    for (effect: ForceFieldEffect <- effectsList.distinct) {
      EffectOnTick.registerBounds(effect, aabb)
    }
  }

  protected def unregisterBounds(aabb: AxisAlignedBB) = {
    for (effect: ForceFieldEffect <- effectsList.distinct) {
      EffectOnTick.unregisterBounds(effect, aabb)
    }
  }

  override def shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState): Boolean = {
    if (oldState.getBlock == newSate.getBlock) {

      if (oldState.getValue(PropertyRotatable.ROTATION) != newSate.getValue(PropertyRotatable.ROTATION)) {
        unregisterBounds(forceFieldBounds)
        forceFieldBounds = makeBounds(newSate.getValue(PropertyRotatable.ROTATION))
        registerBounds(forceFieldBounds)
      }
      return false
    }
    true
  }

  /*
  dont need nbt data for now, breaks apperently when trying to set from block
    override def readFromNBT(nbt: NBTTagCompound) {
      super.readFromNBT(nbt)
      _direction = EnumFacing.getFront(nbt.getByte("direction"))
    }

    override def writeToNBT(nbt: NBTTagCompound) {
      super.writeToNBT(nbt)
      nbt.setByte("direction", _direction.ordinal.asInstanceOf[Byte])
    }
  */

}
