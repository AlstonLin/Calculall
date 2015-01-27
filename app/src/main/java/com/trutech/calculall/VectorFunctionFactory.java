package com.trutech.calculall;

/**
 * Created by Alston on 1/13/2015.
 */
public class VectorFunctionFactory {
    public VectorFunction makeAbs(){
      return new VectorFunction("abs", VectorFunction.ABS) {
          @Override
          public Token perform(Vector v) {
              double num = 0;
              return new Number(num);
          }
      };
    }

    public VectorFunction makeUnit(){
        return new VectorFunction("unit", VectorFunction.UNIT) {
            @Override
            public Token perform(Vector v) {
                return new Vector(null);
            }
        };
    }

}
