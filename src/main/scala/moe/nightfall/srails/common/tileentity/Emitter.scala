package moe.nightfall.srails.common.tileentity

import moe.nightfall.srails.common.tileentity.effect._
import moe.nightfall.srails.common.tileentity.traits.Reversible

import scala.collection.mutable.ArrayBuffer

class Emitter extends SimpleEmitter with Reversible{
  override val instance = this
  override lazy val fieldOffsets: Array[Int] = Array(-1, 32, 2, 2, 2, 2)

  def maxVelo: Double = 0.25

  override lazy val effectsList: ArrayBuffer[ForceFieldEffect] = ArrayBuffer(AntiGravity, FloatingControl, CenteringForce, DirectionalForce, new MotionLimit(0.3), MotionDrag)

}
