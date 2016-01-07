package moe.nightfall.srails.common.tileentity

import javax.vecmath.Vector3d

import moe.nightfall.srails.SRails
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{AxisAlignedBB, ITickable}
import net.minecraft.world.World
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.{ClientTickEvent, WorldTickEvent}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import scala.collection.JavaConverters._
import scala.collection.mutable

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

  def boundsSet(remote: Boolean): mutable.Set[AxisAlignedBB] = {if(remote) boundsSetClient else boundsSet}


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
        if (!p.capabilities.isFlying) {
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
  //non-static values
  lazy val bounds: AxisAlignedBB = new AxisAlignedBB(pos.getX - 3, pos.getY + 1, pos.getZ - 3, pos.getX + 4, pos.getY + 33, pos.getZ + 4) //creates a 3*3 dome above the Block

  def disable() {
    removeAABB(bounds, getWorld.isRemote)
  }

  override def invalidate() {
    removeAABB(bounds, getWorld.isRemote)
    super.invalidate()
  }

  override def onLoad() {
    super.onLoad()
    addAABB(bounds, getWorld.isRemote)
    SRails.log.info(s"onLoad")
  }


  override def onChunkUnload() {
    removeAABB(bounds, getWorld.isRemote)
    SRails.log.info(s"onLoad")
    super.onChunkUnload()
  }

  @SideOnly(Side.CLIENT)
  def jumpKeyDown = {
    lazy val gameSettings = FMLClientHandler.instance.getClient.gameSettings
    gameSettings.keyBindJump.isKeyDown
  }

  override def update() {
    val buf: Seq[Entity] = getWorld.getEntitiesWithinAABB(classOf[Entity], bounds).asScala

    buf.foreach(onEntityIntersect)
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
}
