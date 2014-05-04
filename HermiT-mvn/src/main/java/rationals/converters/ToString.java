package rationals.converters ;
import rationals.Automaton;

import javax.annotation.Nonnull;


public interface ToString {
  @Nonnull
  public String toString(Automaton a) ;
}
