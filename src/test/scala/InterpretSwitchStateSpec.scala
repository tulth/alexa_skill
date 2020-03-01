import org.scalatest._

import org.http4s.Uri

import alexa_skill._

class InterpretSwitchStateSpec extends FlatSpec with Matchers {
  "interpretSwitchState" should "set give true when set to on" in {
    JarvisIntentHandler.interpretSwitchState("ON") should be (Some(true))
    JarvisIntentHandler.interpretSwitchState("on") should be (Some(true))
  }

  "interpretSwitchState" should "set give false when set to off" in {
    JarvisIntentHandler.interpretSwitchState("off") should be (Some(false))
    JarvisIntentHandler.interpretSwitchState("OFF") should be (Some(false))
  }

  "interpretSwitchState" should "set give true when set to active" in {
    JarvisIntentHandler.interpretSwitchState("active") should be (Some(true))
    JarvisIntentHandler.interpretSwitchState("ACTIVE") should be (Some(true))
  }

  "interpretSwitchState" should "set give false when set to inactive" in {
    JarvisIntentHandler.interpretSwitchState("inactive") should be (Some(false))
    JarvisIntentHandler.interpretSwitchState("INACTIVE") should be (Some(false))
  }

  "interpretSwitchState" should "set give true when set to activate" in {
    JarvisIntentHandler.interpretSwitchState("activate") should be (Some(true))
  }

  "interpretSwitchState" should "set give false when set to deactivate" in {
    JarvisIntentHandler.interpretSwitchState("deactivate") should be (Some(false))
  }

  "interpretSwitchState" should "set give true when set to occupied" in {
    JarvisIntentHandler.interpretSwitchState("occupied") should be (Some(true))
  }

  "interpretSwitchState" should "set give false when set to unoccupied" in {
    JarvisIntentHandler.interpretSwitchState("unoccupied") should be (Some(false))
  }

  "interpretSwitchState" should "set give false when set to away" in {
    JarvisIntentHandler.interpretSwitchState("away") should be (Some(false))
  }

  "interpretSwitchState" should "set give none when others arguments are given" in {
    JarvisIntentHandler.interpretSwitchState("onnnn") should be (None)
    JarvisIntentHandler.interpretSwitchState("awayyyy") should be (None)
    JarvisIntentHandler.interpretSwitchState("offf") should be (None)
  }

}
