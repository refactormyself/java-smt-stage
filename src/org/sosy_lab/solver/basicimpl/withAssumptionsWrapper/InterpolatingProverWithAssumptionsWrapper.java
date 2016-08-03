/*
 *  JavaSMT is an API wrapper for a collection of SMT solvers.
 *  This file is part of JavaSMT.
 *
 *  Copyright (C) 2007-2016  Dirk Beyer
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.sosy_lab.solver.basicimpl.withAssumptionsWrapper;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Lists;

import org.sosy_lab.solver.SolverException;
import org.sosy_lab.solver.api.BooleanFormula;
import org.sosy_lab.solver.api.BooleanFormulaManager;
import org.sosy_lab.solver.api.FormulaManager;
import org.sosy_lab.solver.api.FunctionDeclaration;
import org.sosy_lab.solver.api.FunctionDeclarationKind;
import org.sosy_lab.solver.api.InterpolatingProverEnvironment;
import org.sosy_lab.solver.visitors.BooleanFormulaTransformationVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class InterpolatingProverWithAssumptionsWrapper<T>
    extends BasicProverWithAssumptionsWrapper<T, InterpolatingProverEnvironment<T>>
    implements InterpolatingProverEnvironment<T> {

  private final List<T> solverAssumptionsFromPush;
  private final FormulaManager fmgr;
  private final BooleanFormulaManager bmgr;

  public InterpolatingProverWithAssumptionsWrapper(
      InterpolatingProverEnvironment<T> pDelegate, FormulaManager pFmgr) {
    super(pDelegate);
    solverAssumptionsFromPush = new ArrayList<>();
    fmgr = checkNotNull(pFmgr);
    bmgr = fmgr.getBooleanFormulaManager();
  }

  @Override
  public BooleanFormula getInterpolant(List<T> pFormulasOfA)
      throws SolverException, InterruptedException {
    List<T> completeListOfA = Lists.newArrayList(pFormulasOfA);
    completeListOfA.addAll(solverAssumptionsFromPush);
    BooleanFormula interpolant = delegate.getInterpolant(completeListOfA);

    // remove assumption variables from the rawInterpolant if necessary
    if (!solverAssumptionsAsFormula.isEmpty()) {
      interpolant =
          bmgr.transformRecursively(interpolant, new RemoveAssumptionsFromFormulaVisitor());
    }

    return interpolant;
  }

  @Override
  public List<BooleanFormula> getSeqInterpolants(List<Set<T>> pPartitionedFormulas)
      throws SolverException, InterruptedException {
    if (solverAssumptionsAsFormula.isEmpty()) {
      return delegate.getSeqInterpolants(pPartitionedFormulas);
    } else {
      throw new UnsupportedOperationException();
    }
  }

  @Override
  public List<BooleanFormula> getTreeInterpolants(
      List<Set<T>> pPartitionedFormulas, int[] pStartOfSubTree)
      throws SolverException, InterruptedException {
    if (solverAssumptionsAsFormula.isEmpty()) {
      return delegate.getTreeInterpolants(pPartitionedFormulas, pStartOfSubTree);
    } else {
      throw new UnsupportedOperationException();
    }
  }

  @Override
  public boolean isUnsatWithAssumptions(Collection<BooleanFormula> assumptions)
      throws SolverException, InterruptedException {
    clearAssumptions();

    solverAssumptionsAsFormula.addAll(assumptions);
    for (BooleanFormula formula : assumptions) {
      solverAssumptionsFromPush.add(delegate.push(formula));
    }
    return delegate.isUnsat();
  }

  @Override
  protected void clearAssumptions() {
    super.clearAssumptions();
    solverAssumptionsFromPush.clear();
  }

  class RemoveAssumptionsFromFormulaVisitor extends BooleanFormulaTransformationVisitor {

    private RemoveAssumptionsFromFormulaVisitor() {
      super(fmgr);
    }

    @Override
    public BooleanFormula visitAtom(BooleanFormula atom, FunctionDeclaration<BooleanFormula> decl) {
      if (decl.getKind() == FunctionDeclarationKind.VAR) {
        String varName = decl.getName();
        // TODO is it sound to replace a variable with TRUE?
        if (solverAssumptionsContainsVar(varName)) {
          return bmgr.makeBoolean(true);
        } else {
          return bmgr.makeVariable(varName);
        }
      } else {
        return atom;
      }
    }

    private boolean solverAssumptionsContainsVar(String variableName) {
      for (BooleanFormula solverVar : solverAssumptionsAsFormula) {
        if (fmgr.extractVariables(solverVar).keySet().contains(variableName)) {
          return true;
        }
      }
      return false;
    }
  }
}