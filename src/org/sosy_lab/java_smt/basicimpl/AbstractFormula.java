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
package org.sosy_lab.java_smt.basicimpl;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.errorprone.annotations.Immutable;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.sosy_lab.java_smt.api.ArrayFormula;
import org.sosy_lab.java_smt.api.BitvectorFormula;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.FloatingPointFormula;
import org.sosy_lab.java_smt.api.FloatingPointRoundingModeFormula;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.FormulaType;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;
import org.sosy_lab.java_smt.api.NumeralFormula.RationalFormula;

/**
 * A Formula represented as a TFormulaInfo object.
 *
 * @param <TFormulaInfo> the solver specific type.
 */
@Immutable(containerOf = "TFormulaInfo")
abstract class AbstractFormula<TFormulaInfo> implements Formula {

  private final TFormulaInfo formulaInfo;

  private AbstractFormula(TFormulaInfo formulaInfo) {
    this.formulaInfo = checkNotNull(formulaInfo);
  }

  @Override
  public final boolean equals(@Nullable Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof AbstractFormula)) {
      return false;
    }
    Object otherFormulaInfo = ((AbstractFormula<?>) o).formulaInfo;
    return formulaInfo == otherFormulaInfo || formulaInfo.equals(otherFormulaInfo);
  }

  final TFormulaInfo getFormulaInfo() {
    return formulaInfo;
  }

  @Override
  public final int hashCode() {
    return formulaInfo.hashCode();
  }

  @Override
  public final String toString() {
    return formulaInfo.toString();
  }

  /** Simple ArrayFormula implementation. */
  static final class ArrayFormulaImpl<TI extends Formula, TE extends Formula, TFormulaInfo>
      extends AbstractFormula<TFormulaInfo> implements ArrayFormula<TI, TE> {

    private final FormulaType<TI> indexType;
    private final FormulaType<TE> elementType;

    ArrayFormulaImpl(TFormulaInfo info, FormulaType<TI> pIndexType, FormulaType<TE> pElementType) {
      super(info);
      this.indexType = checkNotNull(pIndexType);
      this.elementType = checkNotNull(pElementType);
    }

    public FormulaType<TI> getIndexType() {
      return indexType;
    }

    public FormulaType<TE> getElementType() {
      return elementType;
    }
  }

  /** Simple BooleanFormula implementation. Just tracing the size and the sign-treatment */
  static final class BitvectorFormulaImpl<TFormulaInfo> extends AbstractFormula<TFormulaInfo>
      implements BitvectorFormula {
    BitvectorFormulaImpl(TFormulaInfo info) {
      super(info);
    }
  }

  /** Simple FloatingPointFormula implementation. */
  static final class FloatingPointFormulaImpl<TFormulaInfo> extends AbstractFormula<TFormulaInfo>
      implements FloatingPointFormula {
    FloatingPointFormulaImpl(TFormulaInfo info) {
      super(info);
    }
  }

  /** Simple FloatingPointRoundingModeFormula implementation. */
  static final class FloatingPointRoundingModeFormulaImpl<TFormulaInfo>
      extends AbstractFormula<TFormulaInfo> implements FloatingPointRoundingModeFormula {
    FloatingPointRoundingModeFormulaImpl(TFormulaInfo info) {
      super(info);
    }
  }

  /** Simple BooleanFormula implementation. */
  static final class BooleanFormulaImpl<TFormulaInfo> extends AbstractFormula<TFormulaInfo>
      implements BooleanFormula {
    BooleanFormulaImpl(TFormulaInfo pT) {
      super(pT);
    }
  }

  /** Simple IntegerFormula implementation. */
  static final class IntegerFormulaImpl<TFormulaInfo> extends AbstractFormula<TFormulaInfo>
      implements IntegerFormula {
    IntegerFormulaImpl(TFormulaInfo pTerm) {
      super(pTerm);
    }
  }

  /** Simple RationalFormula implementation. */
  static final class RationalFormulaImpl<TFormulaInfo> extends AbstractFormula<TFormulaInfo>
      implements RationalFormula {
    RationalFormulaImpl(TFormulaInfo pTerm) {
      super(pTerm);
    }
  }
}
