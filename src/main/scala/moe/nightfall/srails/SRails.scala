/*
 * Copyright (c) 2015, Nightfall Group
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package moe.nightfall.srails

import moe.nightfall.srails.common.Proxy
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.{Mod, SidedProxy}
import org.apache.logging.log4j.LogManager
;

@Mod(modid = SRails.ID, name = SRails.Name, modLanguage = "scala")
object SRails {

  final val ID = "srails"

  final val Name = "Steve on Rails"

  var log = LogManager.getLogger(Name)

  @SidedProxy(serverSide = "moe.nightfall.srails.server.Proxy", clientSide = "moe.nightfall.srails.client.Proxy")
  var proxy: Proxy = null

  @EventHandler
  def preInit(e: FMLPreInitializationEvent) {
    log = e.getModLog
    proxy.preInit(e)
    log.info("Done with pre init phase.")
  }

  @Mod.EventHandler
  def init(e: FMLInitializationEvent) {
    proxy.init(e)
    log.info("Done with init phase.")
  }

  @Mod.EventHandler
  def postInit(e: FMLPostInitializationEvent) {
    proxy.postInit(e)
    log.info("Done with pre post init phase.")
  }
}
