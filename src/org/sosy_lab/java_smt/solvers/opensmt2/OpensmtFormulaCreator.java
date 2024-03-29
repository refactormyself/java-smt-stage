/*
 *  JavaSMT is an API wrapper for a collection of SMT solvers.
 *  This file is part of JavaSMT.
 *
 *  Copyright (C) 2007-2019  Dirk Beyer
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
package org.sosy_lab.java_smt.solvers.opensmt2;

import java.util.List;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.FormulaType;
import org.sosy_lab.java_smt.api.FormulaType.FloatingPointType;
import org.sosy_lab.java_smt.api.visitors.FormulaVisitor;
import org.sosy_lab.java_smt.basicimpl.FormulaCreator;

class OpensmtFormulaCreator extends FormulaCreator<Long, Long, Long, Long> {

  OpensmtFormulaCreator(final Long opensmtEnv) {
    super( // TODO: 2: IMPLEMENT
        opensmtEnv,
        opensmtEnv, // bool
        opensmtEnv, // int
        opensmtEnv); // rat
  }

  @Override
  public Long getBitvectorType(int pBitwidth) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Long getFloatingPointType(FloatingPointType pType) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Long getArrayType(Long pIndexType, Long pElementType) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Long makeVariable(Long pType, String pVarName) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FormulaType<?> getFormulaType(Long pFormula) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <R> R visit(FormulaVisitor<R> pVisitor, Formula pFormula, Long pF) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Long callFunctionImpl(Long pDeclaration, List<Long> pArgs) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Long declareUFImpl(String pName, Long pReturnType, List<Long> pArgTypes) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected Long getBooleanVarDeclarationImpl(Long pTFormulaInfo) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object convertValue(Long pF) {
    // TODO Auto-generated method stub
    return null;
  }

}
