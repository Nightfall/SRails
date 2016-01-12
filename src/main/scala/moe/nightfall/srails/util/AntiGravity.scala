package moe.nightfall.srails.util

import moe.nightfall.srails.SRails
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.Entity
import net.minecraft.util.AxisAlignedBB
import net.minecraft.world.World
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.{ClientTickEvent, WorldTickEvent}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import scala.collection.JavaConverters._
import scala.collection.mutable

object AntiGravity {
  final val g = 0.08D * 0.9800000190734863D
  //0.0784000015258789 //Gravity

  //need that here but maybe move it later
  @SideOnly(Side.CLIENT)
  lazy val gameSettings = FMLClientHandler.instance.getClient.gameSettings


  @SideOnly(Side.CLIENT)
  private final val boundsSetClient = mutable.Set.empty[AxisAlignedBB]

  private final val boundsSet = mutable.Set.empty[AxisAlignedBB]

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

  def boundsSet(remote: Boolean): scala.collection.mutable.Set[AxisAlignedBB] = {
    if (remote) boundsSetClient else boundsSet
  }

  SRails.log.debug(s"created utility $this")

  //called from Proxy
  def onTickEvent(event: TickEvent) {
    if ((event.`type` == TickEvent.Type.CLIENT || event.`type` == TickEvent.Type.WORLD || event.`type` == TickEvent.Type.SERVER)
      && event.phase == TickEvent.Phase.END) {

      //SRails.log.debug(s"event ${event.`type`} side ${event.side}")

      var world: World = null
      val entitySet = mutable.Set.empty[Entity]
      var set: scala.collection.mutable.Set[AxisAlignedBB] = null
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

    def reverseGravity(e: Entity) {

      //reverse gravity
      e match {
        case p: EntityPlayerSP =>
          if (!p.capabilities.isFlying && Minecraft.getMinecraft.inGameHasFocus) {
            //counteract gravity I hope this doesnt break
            p.motionY += g
            /*
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
              //add forward motion
              heading.scale(0.01 * p.movementInput.moveForward)
              motion.add(heading)
            }

            p.motionX = motion.x
            p.motionY = motion.y
            p.motionZ = motion.z
            */
          }
        case _ =>
          e.motionY += g
      }

      //SRails.log.info(s"motionY ${e.motionY}")
    }
  }
}
