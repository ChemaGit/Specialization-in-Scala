package functional_program_design_scala_week_3

import org.scalacheck._
import org.scalacheck.commands.Commands
import Arbitrary._
import Gen._
import Prop._
import scala.util.{Try, Success}

  /**
   * Stateful Testing
   */
  /*class Counter {
    private var n = 0
    def inc = n += 1
    def dec = n -= 1
    def get = n
    def reset = n = 0
  }*/
/**
 * OK, our implementation seems to work. But let us introduce a bug:  
 */
class Counter {
  private var n = 0
  def inc = n += 1
  def dec = if(n > 3) n -= 2 else n -= 1  // Bug!
  def get = n
  def reset = n = 0
}  

object StatefulTesting extends Properties("String") with Commands{
  
  type State = Int

  type Sut = Counter

  /** Decides if [[newSut]] should be allowed to be called
   *  with the specified state instance. This can be used to limit the number
   *  of co-existing [[Sut]] instances. The list of existing states represents
   *  the initial states (not the current states) for all [[Sut]] instances
   *  that are active for the moment. If this method is implemented
   *  incorrectly, for example if it returns false even if the list of
   *  existing states is empty, ScalaCheck might hang.
   *
   *  If you want to allow only one [[Sut]] instance to exist at any given time
   *  (a singleton [[Sut]]), implement this method the following way:
   *
   *  {{{
   *  def canCreateNewSut(newState: State, initSuts: Traversable[State]
   *    runningSuts: Traversable[Sut]
   *  ) = {
   *    initSuts.isEmpty && runningSuts.isEmpty
   *  }
   *  }}}
   */
  def canCreateNewSut(newState: State, initSuts: Traversable[State],
    runningSuts: Traversable[Sut]): Boolean = true

  /** The precondition for the initial state, when no commands yet have
   *  run. This is used by ScalaCheck when command sequences are shrinked
   *  and the first state might differ from what is returned from
   *  [[genInitialState]]. */
  def initialPreCondition(state: State): Boolean = {
    // Since the counter implementation doesn't allow initialisation with an
    // arbitrary number, we can only start from zero
    state == 0
  }

  /** Create a new [[Sut]] instance with an internal state that
   *  corresponds to the provided abstract state instance. The provided state
   *  is guaranteed to fulfill [[initialPreCondition]], and
   *  [[newSut]] will never be called if
   *  [[canCreateNewSut]] is not true for the given state. */
  def newSut(state: State): Sut = new Counter

  /** Destroy the system represented by the given [[Sut]]
   *  instance, and release any resources related to it. */
  def destroySut(sut: Sut): Unit = ()

  /** A generator that should produce an initial [[State]] instance that is
   *  usable by [[newSut]] to create a new system under test.
   *  The state returned by this generator is always checked with the
   *  [[initialPreCondition]] method before it is used. */
  def genInitialState: Gen[State] = Gen.const(0)

  /** A generator that, given the current abstract state, should produce
   *  a suitable Command instance. */
  def genCommand(state: State): Gen[Command] = Gen.oneOf(
    Inc, Get, Dec, Reset
  )

  // A UnitCommand is a command that doesn't produce a result
  case object Inc extends UnitCommand {
    def run(sut: Sut): Unit = sut.inc

    def nextState(state: State): State = state + 1

    // This command has no preconditions
    def preCondition(state: State): Boolean = true

    // This command should always succeed (never throw an exception)
    def postCondition(state: State, success: Boolean): Prop = success
  }

  case object Dec extends UnitCommand {
    def run(sut: Sut): Unit = sut.dec
    def nextState(state: State): State = state - 1
    def preCondition(state: State): Boolean = true
    def postCondition(state: State, success: Boolean): Prop = success
  }

  case object Reset extends UnitCommand {
    def run(sut: Sut): Unit = sut.reset
    def nextState(state: State): State = 0
    def preCondition(state: State): Boolean = true
    def postCondition(state: State, success: Boolean): Prop = success
  }

  case object Get extends Command {

    // The Get command returns an Int
    type Result = Int

    def run(sut: Sut): Result = sut.get

    def nextState(state: State): State = state

    def preCondition(state: State): Boolean = true

    // The post condition verifies that the result we get back from the
    // actual system corresponds to our model of the state
    def postCondition(state: State, result: Try[Result]): Prop = {
      result == Success(state)
    }
  }  
  StatefulTesting.property().check
}