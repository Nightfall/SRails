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
import net.minecraftforge.fml.common.gameevent.TickEvent.{ClientTickEvent, PlayerTickEvent, ServerTickEvent, WorldTickEvent}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import net.minecraftforge.fml.server.FMLServerHandler

import scala.collection.JavaConverters._
import scala.collection.mutable

object Emitter {
  final val g = 0.08D * 0.9800000190734863D
  //0.0784000015258789 //Gravity
  private val boundsSet = mutable.Set.empty[AxisAlignedBB]

  def maxVelo: Double = 0.25;

  //soft motion limit

  def addAABB(aabb: AxisAlignedBB) {
    boundsSet += aabb
    SRails.log.info(s"added $aabb")
  }

  def removeAABB(aabb: AxisAlignedBB) {
    boundsSet -= aabb
    //SRails.log.info(s"removed $aabb")
  }

  SRails.log.info(s"created companion object $this")

  //called from Proxy
  def onTickEvent(event: TickEvent) {
    if ((event.`type` == TickEvent.Type.CLIENT || event.`type` == TickEvent.Type.WORLD || event.`type` == TickEvent.Type.SERVER)
      && event.phase == TickEvent.Phase.END) {

      SRails.log.info(s"event ${event.`type`} side ${event.side}")

      val set: mutable.Set[AxisAlignedBB] = boundsSet;
      val entitySet = mutable.Set.empty[Entity]
      var world: World = null

      event match {
        case playerTickEvent: PlayerTickEvent =>
          //probably useless
          world = playerTickEvent.player.worldObj
        case worldTickEvent: WorldTickEvent =>
          //maybe useless
          world = worldTickEvent.world
        case clientTickEvent: ClientTickEvent =>
          val player: EntityPlayerSP = FMLClientHandler.instance.getClientPlayerEntity
          if (player != null) {
            world = player.worldObj
          } else return
        case serverTickEvent: ServerTickEvent =>

          //TODO crash here, seems to get stuck on server start

          val server = FMLServerHandler.instance.getServer
          if (server != null && server.serverIsInRunLoop()) {
            world = server.getEntityWorld
          } else return
        case _ => return
      }

      SRails.log.info(s"tick for ${world}")
      if (world != null) {
        set.foreach(aabb => if (aabb != null) entitySet ++= world.getEntitiesWithinAABB(classOf[Entity], aabb).asScala)
        entitySet.foreach(reverseGravity)
      }


    }
  }

  def reverseGravity(e: Entity) {

    SRails.log.info(s"reverseGravity for ${e}")
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
  }
}

import moe.nightfall.srails.common.tileentity.Emitter._

class Emitter extends TileEntity with ITickable {
  //non-static values
  def bounds: AxisAlignedBB = {
    lazy val b = new AxisAlignedBB(pos.getX - 3, pos.getY + 1, pos.getZ - 3, pos.getX + 4, pos.getY + 33, pos.getZ + 4) //creates a 3*3 dome above the Block
    return b
  }

  addAABB(bounds)

  def disable() {
    removeAABB(bounds)
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
      //val isFloating = e.getDataWatcher.getWatchableObjectByte(73)
      /*
            if(array.contains(e)) { //random value
              e match {
                case p: EntityPlayerSP =>
                  if (!p.capabilities.isFlying) {
                    //counteract gravity I hope this doesnt break
                    p.motionY += g
                  }
                case _ =>
                  e.motionY += g
                  //e.onGround = true //non-player entities can stand in the field and jump to move upwards
              }
              //SRails.log.info(s"added to array $e")
            }
      */


      //make sure to create after gravity magic
      val motion: Vector3d = new Vector3d(e.motionX, e.motionY, e.motionZ)

      //--entity controlled movement--//

      if (e.isSneaking) {
        motion.y -= 0.008
      }

      e match {
        case p: EntityPlayerSP =>
          if (jumpKeyDown) motion.y += 0.008

        case l: EntityLivingBase =>


        case _ =>
      }
      /*
      if (e.isInstanceOf[EntityPlayerSP] && jumpKeyDown) {
        motion.y += 0.008
      }
      */

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
