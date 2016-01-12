package moe.nightfall.srails.common.tileentity.effect

import javax.vecmath.Vector3d

import moe.nightfall.srails.common.tileentity.SimpleEmitter
import net.minecraft.entity.Entity

class MotionLimit (maxVelo: Double = 0.25) extends MotionEffectOnIntersect {

  override def onEntityIntersect(motion: Vector3d, position: Vector3d, entity: Entity, emitter: SimpleEmitter): Vector3d = {

    //--limit velocity--//

    if (motion.length > maxVelo) {
      //TODO simplify if possible
      val scale = 1 - (((motion.length - maxVelo) / 2) / motion.length())
      motion.scale(scale)
      //if (e.isInstanceOf[EntityPlayerSP]) {
      //SRails.log.info(s"scale: $scale")
      //}
    }
    motion
  }
}