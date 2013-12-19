package regolic
package smtlib

import regolic.sexpr
import regolic.sexpr.SExprs._

import scala.collection.Iterator

import Commands._

object Parser {
  class UnknownCommand(msg: String) extends Exception(msg)
}

class Parser(input: java.io.Reader) extends Iterator[Command] {

  import Parser._

  private val l = new sexpr.Lexer(input)
  private val p = new sexpr.Parser(l)

  private var lookAhead: Option[SExpr] = None

  override def hasNext: Boolean = {
    lookAhead match {
      case Some(expr) => expr != null
      case None => {
        val c = p.parse
        lookAhead = Some(c)
        c != null
      }
    }
  }

  override def next: Command = {
    val cmd = lookAhead match {
      case None => p.parse
      case Some(c) => {
        lookAhead = None
        c
      }
    }
    if(cmd == null)
      throw new NoSuchElementException
    val res = cmd match {
      case SList(List(SSymbol("SET-LOGIC"), SSymbol(logic))) => 
        SetLogic(Logic.fromString(logic))
      case SList(SSymbol("SET-INFO") :: attr) =>
        SetInfo(parseAttribute(attr))
      case SList(List(SSymbol("DECLARE-SORT"), s@SSymbol(sort), SInt(arity))) => 
        DeclareSort(s, arity.toInt)
      case SList(List(SSymbol("DECLARE-FUN"), s@SSymbol(fun), SList(sorts), sort)) =>
        DeclareFun(s, sorts, sort)
      case SList(List(SSymbol("ASSERT"), term)) =>
        Assert(term)
      case SList(List(SSymbol("CHECK-SAT"))) =>
        CheckSat
      case SList(List(SSymbol("EXIT"))) =>
        Exit
      case SList(List(SSymbol("PUSH"), SInt(n))) => 
        Push(n.toInt)
      case SList(List(SSymbol("POP"), SInt(n))) => 
        Pop(n.toInt)
      case _ =>
        throw new UnknownCommand("Unknown command: " + cmd)
    }
    res
  }

  //todo: make sure no nested keyword in value
  private def parseAttribute(ss: List[SExpr]): Attribute = ss match {
    case List(SQualifiedSymbol(None, SSymbol(key))) => Attribute(key, None)
    case List(SQualifiedSymbol(None, SSymbol(key)), v) => Attribute(key, Some(v))
    case _ => sys.error("unexpected: " + ss + " when expecting attribute")
  }

}