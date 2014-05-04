package rationals.converters;

import rationals.Automaton;

import javax.annotation.Nonnull;


public interface FromString {
  @Nonnull
  public Automaton fromString(String s) throws ConverterException ;
}
