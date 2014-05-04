package rationals.transformations ;

import rationals.Automaton;

import javax.annotation.Nonnull;

public interface UnaryTransformation {
  @Nonnull
  public Automaton transform(Automaton a) ;
}
