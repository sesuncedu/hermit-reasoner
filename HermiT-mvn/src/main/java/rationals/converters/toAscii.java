package rationals.converters;

import java.util.Iterator;

import rationals.Automaton;

import javax.annotation.Nonnull;

public class toAscii implements ToString {
    @Nonnull
    public String toString(@Nonnull Automaton a) {
        StringBuffer sb = new StringBuffer();
        sb.append("A = ").append(a.alphabet().toString()).append("\n");
        sb.append("Q = ").append(a.states().toString()).append("\n");
        sb.append("I = ").append(a.initials().toString()).append("\n");
        sb.append("T = ").append(a.terminals().toString()).append("\n");
        sb.append("delta = [\n");
        Iterator i = a.delta().iterator();
        while (i.hasNext())
            sb.append(i.next()).append("\n");
        sb.append("]\n");
        return sb.toString();
    }
}
