package moe.nightfall.srails.common.tileentity

import moe.nightfall.srails.common.tileentity.effect.{DirectionalForce, CenteringForce, AntiGravity, FloatingControl}
import moe.nightfall.srails.common.tileentity.traits.Reversible
import net.minecraft.util._
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import scala.collection.mutable.ArrayBuffer

class Emitter extends SimpleEmitter with ITickable with Reversible{
  override lazy val fieldOffsets: Array[Int] = Array(-1, 32, 2, 2, 2, 2)

  override def maxVelo: Double = 0.25;

  override lazy val effectsList = ArrayBuffer(AntiGravity, FloatingControl, CenteringForce, new DirectionalForce)

  @SideOnly(Side.CLIENT)
  def jumpKeyDown = {
    lazy val gameSettings = FMLClientHandler.instance.getClient.gameSettings
    gameSettings.keyBindJump.isKeyDown
  }

/*
  override def update() {
    if (bounds == null) {
      SRails.log.info("makeBounds in update")
      bounds = makeBounds(rotation)
      addAABB(bounds)
    }
    val buf: Seq[Entity] = getWorld.getEntitiesWithinAABB(classOf[Entity], bounds).asScala

    buf.foreach(onEntityIntersect)
  }

  def onEntityIntersect(e: Entity) {
    if (e.canBePushed) {

      //make sure to create after gravity magic
      val motion: Vector3d = new Vector3d(e.motionX, e.motionY, e.motionZ)

      //--slow down--//

      motion.scale(1d - 1d / 64d) //general drag inside the field

      //--limit velocity--//

      if (motion.length > maxVelo) {
        //TODO simplify if possible
        val scale = 1 - (((motion.length - maxVelo) / 2) / motion.length())
        motion.scale(scale)

        //if (e.isInstanceOf[EntityPlayerSP]) {
        //SRails.log.info(s"scale: $scale")
        //}
      }

      //--add velocity--//

      val scale = rotation match {
        case EnumFacing.UP => 0.005
        case EnumFacing.DOWN => 0.1
        case _ => 0.01
      }
      val vec: Vec3i = {if(isReverse) rotation.getOpposite else rotation}.getDirectionVec
      val motionDirection: Vector3d = new Vector3d(vec.getX, vec.getY, vec.getZ)
      motionDirection.normalize()
      motionDirection.scale(scale)
      motion.add(motionDirection)




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
*/

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
