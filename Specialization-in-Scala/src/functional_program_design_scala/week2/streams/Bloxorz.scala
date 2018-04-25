package functional_program_design_scala.week2.streams

/**
 * A main object that can be used to execute the Bloxorz solver
 */
object Bloxorz extends App {

  /**
   * A level constructed using the `InfiniteTerrain` trait which defines
   * the terrain to be valid at every position.
   */
  object InfiniteLevel extends Solver with InfiniteTerrain {
    val startPos = Pos(1,3)
    val goal = Pos(5,8)
  }

  println(InfiniteLevel.solution)

  /**
   * A simple level constructed using the StringParserTerrain
   */
  abstract class Level extends Solver with StringParserTerrain

  object Level0 extends Level {
    val level =
      """------
        |--ST--
        |--oo--
        |--oo--
        |------""".stripMargin
  }
  /*
  private lazy val vector: Vector[Vector[Char]] =
    Vector(Level0.level.split("\n").map(str => Vector(str: _*)): _*) 
  println(vector.take(10).toList)  
  lazy val terrain: Level0.Terrain = Level0.terrainFunction(vector)
  println(terrain.apply(Level0.Pos(1,2)))
  
  def findChar(c: Char, levelVector: Vector[Vector[Char]]): Level0.Pos = {
    val row = levelVector.indexWhere((p: Vector[Char]) => p.contains(c))
    val col = levelVector(row).indexOf(c)
    Level0.Pos(row, col)
  } */ 
  //println(findChar('-', vector)) 
  
  
  println(Level0.solution)      
  

  /**
   * Level 1 of the official Bloxorz game
   */
  object Level1 extends Level {
    val level =
      """ooo-------
        |oSoooo----
        |ooooooooo-
        |-ooooooooo
        |-----ooToo
        |------ooo-""".stripMargin
  }
  //println(Level1.Block(Level1.Pos(1,1),Level1.Pos(1,1)).legalNeighbors)
  //println(Level1.neighborsWithHistory(Level1.Block(Level1.Pos(1,1),Level1.Pos(1,1)), List(Level1.Left,Level1.Up)))
  
  
  println(Level1.solution)
}
