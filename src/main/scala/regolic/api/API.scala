package regolic.api

import regolic.asts.core.Trees._
import regolic.asts.fol.Trees._
import regolic.asts.fol.Manip._

import regolic.sat.Solver
import regolic.sat.Solver.Results._
import regolic.sat.Solver.Clause
import regolic.sat.ConjunctiveNormalForm
import regolic.sat.Literal

object API {

  implicit class FormulaWrapper(f: Formula) {
    def &&(f2: Formula): Formula = And(f, f2)
    def ||(f2: Formula): Formula = Or(f, f2)
    def unary_!(): Formula = Not(f)
  }
  implicit class TermWrapper(t: Term)

  def boolVar(): Formula = freshPropositionalVariable("v")

  def solve(f: Formula, assumptions: List[Formula]): Option[Map[Formula, Boolean]] = {
    val (clauses, nbVars, mapping) = ConjunctiveNormalForm(f) // TODO nbVars not needed because it would be out of date when there are assumptions

    val assumps = assumptions.map{ lit =>
      if(mapping.contains(lit))
        mapping(lit)
      else
        ConjunctiveNormalForm.nextId
    }.toArray
    
    println("cnf form computed")

    Solver.solve(clauses.map(lits => new Clause(lits.toList)).toList,
      ConjunctiveNormalForm.literalCounter + 1, assumps) match {
      case Satisfiable(model) =>
        Some(mapping.map(p => (p._1, model(p._2))))
      case Unsatisfiable => None
      case Unknown =>
        sys.error("shouldn't be unknown")
    }

  }

}
