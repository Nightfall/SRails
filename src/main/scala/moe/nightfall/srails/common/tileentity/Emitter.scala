package moe.nightfall.srails.common.tileentity

import javax.vecmath.Vector3d

import moe.nightfall.srails.SRails
import moe.nightfall.srails.common.block.property.PropertyRotatable
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.tileentity.TileEntity
import net.minecraft.util._
import net.minecraft.world.World
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.{ClientTickEvent, WorldTickEvent}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.util.Random

object Emitter {
  final val g = 0.08D * 0.9800000190734863D
  //0.0784000015258789 //Gravity

  @SideOnly(Side.CLIENT)
  private final val boundsSetClient = mutable.Set.empty[AxisAlignedBB]

  private final val boundsSet = mutable.Set.empty[AxisAlignedBB]

  def maxVelo: Double = 0.25;

  //soft motion limit

  def addAABB(aabb: AxisAlignedBB, remote: Boolean) {
    //SRails.log.info(s"before size: ${boundsSet(remote).size} ")
    boundsSet(remote) += aabb
    //SRails.log.info(s"added ${aabb.hashCode()} size: ${boundsSet(remote).size} ")
  }

  def removeAABB(aabb: AxisAlignedBB, remote: Boolean) {
    //SRails.log.info(s"before ${boundsSet(remote).size} contains? ${boundsSet(remote).contains(aabb)}")
    boundsSet(remote) -= aabb
    //SRails.log.info(s"removed ${aabb.hashCode()} size: ${boundsSet(remote).size} ")
  }

  def boundsSet(remote: Boolean): mutable.Set[AxisAlignedBB] = {
    if (remote) boundsSetClient else boundsSet
  }


  SRails.log.info(s"created companion object $this")

  //called from Proxy
  def onTickEvent(event: TickEvent) {
    if ((event.`type` == TickEvent.Type.CLIENT || event.`type` == TickEvent.Type.WORLD || event.`type` == TickEvent.Type.SERVER)
      && event.phase == TickEvent.Phase.END) {

      //SRails.log.debug(s"event ${event.`type`} side ${event.side}")

      var world: World = null
      val entitySet = mutable.Set.empty[Entity]
      var set: mutable.Set[AxisAlignedBB] = null
      event match {
        case worldTickEvent: WorldTickEvent =>
          //maybe useless
          world = worldTickEvent.world
          set = boundsSet
        case clientTickEvent: ClientTickEvent =>
          val player: EntityPlayerSP = FMLClientHandler.instance.getClientPlayerEntity
          set = boundsSetClient
          if (player != null) {
            world = player.worldObj
          } else return
        case _ => return
      }

      //SRails.log.info(s"tick for ${world}")
      if (world != null) {
        set.foreach(aabb => if (aabb != null) {
          entitySet ++= world.getEntitiesWithinAABB(classOf[Entity], aabb).asScala
        })
        entitySet.foreach(reverseGravity)
      }
    }
  }

  def reverseGravity(e: Entity) {
    //SRails.log.info(s"reverseGravity for ${e}")
    //reverse gravity
    e match {
      case p: EntityPlayerSP =>
        if (!p.capabilities.isFlying && Minecraft.getMinecraft.inGameHasFocus) {
          //counteract gravity I hope this doesnt break
          p.motionY += g
        }
      case _ =>
        e.motionY += g
    }

    //SRails.log.info(s"motionY ${e.motionY}")
  }
}

import moe.nightfall.srails.common.tileentity.Emitter._

class Emitter extends TileEntity with ITickable {
  SRails.log.info("called constructor of tileentity.Emitter")

  def facing: EnumFacing = {
    if (getWorld != null) {
      getWorld.getBlockState(getPos).getValue(PropertyRotatable.FACING)
    } else {
      SRails.log.info("facing return default value")
      EnumFacing.UP
    }
  }

  def isReverse: Boolean = {
    if(getWorld != null) {
      getWorld.getBlockState(getPos).getValue(PropertyRotatable.REVERSE)
    } else {
      SRails.log.info("isReverse return default value")
      false
    }
  }


  var bounds: AxisAlignedBB = null
  //lazy val bounds: AxisAlignedBB = makeBounds(facing)

  private def makeBounds(enumFacing: EnumFacing): AxisAlignedBB = {
    SRails.log.info(s"makeBounds $enumFacing")
    val baseOffsets: Array[Int] = Array(-1, 32, 2, 2, 2, 2)

    def rotateAll(faceArray: Array[EnumFacing], axis: EnumFacing.Axis*): Array[EnumFacing] = {
      val result: Array[EnumFacing] = new Array[EnumFacing](faceArray.size)
      for (i: Int <- faceArray.indices) {
        var temp: EnumFacing = faceArray(i)
        axis.foreach(axis => {
          temp = temp.rotateAround(axis)
        })
        result(i) = temp
      }
      result
    }

    val index: Array[EnumFacing] = enumFacing match {
      case EnumFacing.UP =>
        EnumFacing.VALUES
      case EnumFacing.DOWN =>
        rotateAll(EnumFacing.VALUES, EnumFacing.Axis.X, EnumFacing.Axis.X)
      case EnumFacing.NORTH =>
        rotateAll(EnumFacing.VALUES, EnumFacing.Axis.Y, EnumFacing.Axis.Y, EnumFacing.Axis.X)
      case EnumFacing.EAST =>
        rotateAll(EnumFacing.VALUES, EnumFacing.Axis.Y, EnumFacing.Axis.X)
      case EnumFacing.SOUTH =>
        rotateAll(EnumFacing.VALUES, EnumFacing.Axis.X)
      case EnumFacing.WEST =>
        rotateAll(EnumFacing.VALUES, EnumFacing.Axis.Y, EnumFacing.Axis.Y, EnumFacing.Axis.Y, EnumFacing.Axis.X)
    }

    new AxisAlignedBB(
      pos.getX - baseOffsets(index(EnumFacing.WEST.getIndex).getIndex),
      pos.getY - baseOffsets(index(EnumFacing.DOWN.getIndex).getIndex),
      pos.getZ - baseOffsets(index(EnumFacing.NORTH.getIndex).getIndex),
      pos.getX + baseOffsets(index(EnumFacing.EAST.getIndex).getIndex) + 1,
      pos.getY + baseOffsets(index(EnumFacing.UP.getIndex).getIndex) + 1,
      pos.getZ + baseOffsets(index(EnumFacing.SOUTH.getIndex).getIndex) + 1
    )
  }


  /*
    override def validate() {
      super.validate()
      addAABB(bounds, getWorld.isRemote)
      SRails.log.info(s"validate ${bounds.hashCode()} $bounds")
    }
  */

  override def invalidate() {
    super.invalidate()
    removeAABB(bounds, getWorld.isRemote)
    SRails.log.info(s"invalidate ${bounds.hashCode()} $bounds")
  }

  /*
    override def onLoad() {
      srails.SRails.log.info("onLoad")
      super.onLoad()
      if(bounds == null) bounds = makeBounds(facing)
      addAABB(bounds, getWorld.isRemote)
      SRails.log.info(s"onLoad ${bounds.hashCode()} $bounds")
    }
  */

  override def onChunkUnload() {
    removeAABB(bounds, getWorld.isRemote)
    SRails.log.info(s"onChunkUnLoad ${bounds.hashCode()} $bounds")
    super.onChunkUnload()
  }

  /*
    def updateRotations() {
      removeAABB(bounds, getWorld.isRemote)
      bounds = makeBounds(facing)
      addAABB(bounds, getWorld.isRemote)
    }
  */
  @SideOnly(Side.CLIENT)
  def jumpKeyDown = {
    lazy val gameSettings = FMLClientHandler.instance.getClient.gameSettings
    gameSettings.keyBindJump.isKeyDown
  }


  override def update() {
    if (bounds == null) {
      SRails.log.info("makeBounds in update")
      bounds = makeBounds(facing)
      addAABB(bounds, getWorld.isRemote)
    }
    val buf: Seq[Entity] = getWorld.getEntitiesWithinAABB(classOf[Entity], bounds).asScala

    buf.foreach(onEntityIntersect)

    val pos1 = new BlockPos(bounds.minX, bounds.minY, bounds.minZ)
    val pos2 = new BlockPos(bounds.maxX, bounds.maxY, bounds.maxZ)

    val r = new Random()

    if (getWorld.isRemote) {
      for (x: Double <- bounds.minX to bounds.maxX by 0.5;
           y: Double <- bounds.minY to bounds.maxY by 0.5;
           z: Double <- bounds.minZ to bounds.maxZ by 0.5) {
        if(r.nextDouble() < 0.05)
        getWorld.spawnParticle(EnumParticleTypes.PORTAL, x, y, z, 0, 0, 0, 0, 3, 0)
      }
    }


  }

  def onEntityIntersect(e: Entity) {
    if (e.canBePushed) {

      //make sure to create after gravity magic
      val motion: Vector3d = new Vector3d(e.motionX, e.motionY, e.motionZ)

      //--entity controlled movement--//

      if (e.isSneaking) {
        motion.y -= 0.01
      }

      e match {
        case p: EntityPlayerSP =>
          if (jumpKeyDown) motion.y += 0.01

        case l: EntityLivingBase =>

        case _ =>
      }

      //--add velocity.--//

      val vec: Vec3i = {if(isReverse) facing.getOpposite else facing}.getDirectionVec
      val motionDirection: Vector3d = new Vector3d(vec.getX, vec.getY, vec.getZ)
      motionDirection.normalize()
      motionDirection.scale(0.01)
      motion.add(motionDirection)

      //--limit velocity--//

      if (motion.length > maxVelo) {
        val scale = 1 - (((motion.length - maxVelo) / 2) / motion.length()) //TODO simplify if possible
        motion.scale(scale)

        if (e.isInstanceOf[EntityPlayerSP]) {
          //SRails.log.info(s"scale: $scale")
        }
      }

      //--slow down--//

      motion.scale(1d - 1d / 64d) //general drag inside the field

      //--print debug--//
      /*
      if (e.isInstanceOf[EntityPlayerSP]) {
        SRails.log.info(e.toString)
        SRails.log.info(s"motion: ${e.motionX} ; ${e.motionY} ; ${e.motionZ}")
        SRails.log.info(s"velocity : ${motion.length}")//--check velocity--//
        if (e.isInstanceOf[EntityPlayerSP] && motion.length < Emitter.maxVelo) {
          SRails.log.info(s"speed limited to ${motion.length()} \n")
          //motion.scale(0) //indicate when it slowed down enough --debug-only :p
        }
      }
      */

      //--set-velocity--

      e.motionX = motion.x
      e.motionY = motion.y
      e.motionZ = motion.z
    }
  }

  override def shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState): Boolean = {
    if (oldState.getBlock == newSate.getBlock) {

      if (oldState.getValue(PropertyRotatable.FACING) != newSate.getValue(PropertyRotatable.FACING)) {
        removeAABB(bounds, getWorld.isRemote)
        bounds = makeBounds(newSate.getValue(PropertyRotatable.FACING))
        addAABB(bounds, world.isRemote)
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
