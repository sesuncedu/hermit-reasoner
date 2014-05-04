package rationals.converters;
import rationals.Automaton;
import rationals.converters.analyzers.Parser;

import javax.annotation.Nonnull;

public class Expression implements FromString {
  @Nonnull
  public Automaton fromString(String s) throws ConverterException {
    return new Parser(s).analyze() ;
  }
    
}

