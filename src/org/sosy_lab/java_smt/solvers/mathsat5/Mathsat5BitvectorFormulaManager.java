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
package org.sosy_lab.java_smt.solvers.mathsat5;

import static org.sosy_lab.java_smt.solvers.mathsat5.Mathsat5NativeApi.msat_make_bv_and;
import static org.sosy_lab.java_smt.solvers.mathsat5.Mathsat5NativeApi.msat_make_bv_ashr;
import static org.sosy_lab.java_smt.solvers.mathsat5.Mathsat5NativeApi.msat_make_bv_concat;
import static org.sosy_lab.java_smt.solvers.mathsat5.Mathsat5NativeApi.msat_make_bv_extract;
import static org.sosy_lab.java_smt.solvers.mathsat5.Mathsat5NativeApi.msat_make_bv_lshl;
import static org.sosy_lab.java_smt.solvers.mathsat5.Mathsat5NativeApi.msat_make_bv_lshr;
import static org.sosy_lab.java_smt.solvers.mathsat5.Mathsat5NativeApi.msat_make_bv_minus;
import static org.sosy_lab.java_smt.solvers.mathsat5.Mathsat5NativeApi.msat_make_bv_neg;
import static org.sosy_lab.java_smt.solvers.mathsat5.Mathsat5NativeApi.msat_make_bv_not;
import static org.sosy_lab.java_smt.solvers.mathsat5.Mathsat5NativeApi.msat_make_bv_number;
import static org.sosy_lab.java_smt.solvers.mathsat5.Mathsat5NativeApi.msat_make_bv_or;
import static org.sosy_lab.java_smt.solvers.mathsat5.Mathsat5NativeApi.msat_make_bv_plus;
import static org.sosy_lab.java_smt.solvers.mathsat5.Mathsat5NativeApi.msat_make_bv_sdiv;
import static org.sosy_lab.java_smt.solvers.mathsat5.Mathsat5NativeApi.msat_make_bv_sext;
import static org.sosy_lab.java_smt.solvers.mathsat5.Mathsat5NativeApi.msat_make_bv_sleq;
import static org.sosy_lab.java_smt.solvers.mathsat5.Mathsat5NativeApi.msat_make_bv_slt;
import static org.sosy_lab.java_smt.solvers.mathsat5.Mathsat5NativeApi.msat_make_bv_srem;
import static org.sosy_lab.java_smt.solvers.mathsat5.Mathsat5NativeApi.msat_make_bv_times;
import static org.sosy_lab.java_smt.solvers.mathsat5.Mathsat5NativeApi.msat_make_bv_udiv;
import static org.sosy_lab.java_smt.solvers.mathsat5.Mathsat5NativeApi.msat_make_bv_uleq;
import static org.sosy_lab.java_smt.solvers.mathsat5.Mathsat5NativeApi.msat_make_bv_ult;
import static org.sosy_lab.java_smt.solvers.mathsat5.Mathsat5NativeApi.msat_make_bv_urem;
import static org.sosy_lab.java_smt.solvers.mathsat5.Mathsat5NativeApi.msat_make_bv_xor;
import static org.sosy_lab.java_smt.solvers.mathsat5.Mathsat5NativeApi.msat_make_bv_zext;
import static org.sosy_lab.java_smt.solvers.mathsat5.Mathsat5NativeApi.msat_make_equal;
import static org.sosy_lab.java_smt.solvers.mathsat5.Mathsat5NativeApi.msat_make_int_from_sbv;
import static org.sosy_lab.java_smt.solvers.mathsat5.Mathsat5NativeApi.msat_make_int_from_ubv;
import static org.sosy_lab.java_smt.solvers.mathsat5.Mathsat5NativeApi.msat_make_int_to_bv;

import java.math.BigInteger;
import org.sosy_lab.java_smt.basicimpl.AbstractBitvectorFormulaManager;

/** Mathsat Bitvector Theory, build out of Bitvector*Operations. */
class Mathsat5BitvectorFormulaManager
    extends AbstractBitvectorFormulaManager<Long, Long, Long, Long> {

  private final long mathsatEnv;

  protected Mathsat5BitvectorFormulaManager(Mathsat5FormulaCreator pCreator) {
    super(pCreator);
    this.mathsatEnv = pCreator.getEnv();
  }

  public static Mathsat5BitvectorFormulaManager create(Mathsat5FormulaCreator creator) {
    return new Mathsat5BitvectorFormulaManager(creator);
  }

  @Override
  public Long concat(Long pFirst, Long pSecond) {
    return msat_make_bv_concat(mathsatEnv, pFirst, pSecond);
  }

  @Override
  public Long extract(Long pFirst, int pMsb, int pLsb, boolean pSigned) {
    return msat_make_bv_extract(mathsatEnv, pMsb, pLsb, pFirst);
  }

  @Override
  public Long extend(Long pNumber, int pExtensionBits, boolean pSigned) {
    if (pSigned) {
      return msat_make_bv_sext(mathsatEnv, pExtensionBits, pNumber);
    } else {
      return msat_make_bv_zext(mathsatEnv, pExtensionBits, pNumber);
    }
  }

  @Override
  public Long makeBitvectorImpl(int pLength, long pI) {
    int i = (int) pI;
    if (i == pI && i > 0) { // fits into an int
      return Mathsat5NativeApi.msat_make_bv_int_number(mathsatEnv, i, pLength);
    }
    return makeBitvectorImpl(pLength, BigInteger.valueOf(pI));
  }

  @Override
  public Long makeBitvectorImpl(int pLength, BigInteger pI) {
    if (pI.signum() < 0) {
      BigInteger max = BigInteger.valueOf(2).pow(pLength - 1);
      if (pI.compareTo(max.negate()) < 0) {
        throw new IllegalArgumentException(
            pI + " is to small for a bitvector with length " + pLength);
      }
      BigInteger n = BigInteger.valueOf(2).pow(pLength);
      pI = pI.add(n);
    }
    return msat_make_bv_number(mathsatEnv, pI.toString(), pLength, 10);
  }

  @Override
  protected Long makeBitvectorImpl(int pLength, Long pIntegerFormula) {
    return msat_make_int_to_bv(mathsatEnv, pLength, pIntegerFormula);
  }

  @Override
  protected Long toIntegerFormulaImpl(Long pBVFormula, boolean pSigned) {
    if (pSigned) {
      return msat_make_int_from_sbv(mathsatEnv, pBVFormula);
    } else {
      return msat_make_int_from_ubv(mathsatEnv, pBVFormula);
    }
  }

  @Override
  public Long makeVariableImpl(int length, String var) {
    long bvType = getFormulaCreator().getBitvectorType(length);
    return getFormulaCreator().makeVariable(bvType, var);
  }

  /**
   * Return a term representing the (arithmetic if signed is true) right shift of number by toShift.
   */
  @Override
  public Long shiftRight(Long number, Long toShift, boolean signed) {
    long t;
    if (signed) {
      t = msat_make_bv_ashr(mathsatEnv, number, toShift);
    } else {
      t = msat_make_bv_lshr(mathsatEnv, number, toShift);
    }
    return t;
  }

  @Override
  public Long shiftLeft(Long number, Long toShift) {
    return msat_make_bv_lshl(mathsatEnv, number, toShift);
  }

  @Override
  public Long not(Long pBits) {
    return msat_make_bv_not(mathsatEnv, pBits);
  }

  @Override
  public Long and(Long pBits1, Long pBits2) {
    return msat_make_bv_and(mathsatEnv, pBits1, pBits2);
  }

  @Override
  public Long or(Long pBits1, Long pBits2) {
    return msat_make_bv_or(mathsatEnv, pBits1, pBits2);
  }

  @Override
  public Long xor(Long pBits1, Long pBits2) {
    return msat_make_bv_xor(mathsatEnv, pBits1, pBits2);
  }

  @Override
  public Long negate(Long pNumber) {
    return msat_make_bv_neg(mathsatEnv, pNumber);
  }

  @Override
  public Long add(Long pNumber1, Long pNumber2) {
    return msat_make_bv_plus(mathsatEnv, pNumber1, pNumber2);
  }

  @Override
  public Long subtract(Long pNumber1, Long pNumber2) {
    return msat_make_bv_minus(mathsatEnv, pNumber1, pNumber2);
  }

  @Override
  public Long divide(Long pNumber1, Long pNumber2, boolean signed) {
    if (signed) {
      return msat_make_bv_sdiv(mathsatEnv, pNumber1, pNumber2);
    } else {
      return msat_make_bv_udiv(mathsatEnv, pNumber1, pNumber2);
    }
  }

  @Override
  public Long modulo(Long pNumber1, Long pNumber2, boolean signed) {
    if (signed) {
      return msat_make_bv_srem(mathsatEnv, pNumber1, pNumber2);
    } else {
      return msat_make_bv_urem(mathsatEnv, pNumber1, pNumber2);
    }
  }

  @Override
  public Long multiply(Long pNumber1, Long pNumber2) {
    return msat_make_bv_times(mathsatEnv, pNumber1, pNumber2);
  }

  @Override
  public Long equal(Long pNumber1, Long pNumber2) {
    return msat_make_equal(mathsatEnv, pNumber1, pNumber2);
  }

  @Override
  public Long lessThan(Long pNumber1, Long pNumber2, boolean signed) {
    if (signed) {
      return msat_make_bv_slt(mathsatEnv, pNumber1, pNumber2);
    } else {
      return msat_make_bv_ult(mathsatEnv, pNumber1, pNumber2);
    }
  }

  @Override
  public Long lessOrEquals(Long pNumber1, Long pNumber2, boolean signed) {
    if (signed) {
      return msat_make_bv_sleq(mathsatEnv, pNumber1, pNumber2);
    } else {
      return msat_make_bv_uleq(mathsatEnv, pNumber1, pNumber2);
    }
  }

  @Override
  public Long greaterThan(Long pNumber1, Long pNumber2, boolean signed) {
    return lessThan(pNumber2, pNumber1, signed);
  }

  @Override
  public Long greaterOrEquals(Long pNumber1, Long pNumber2, boolean signed) {
    return lessOrEquals(pNumber2, pNumber1, signed);
  }
}
