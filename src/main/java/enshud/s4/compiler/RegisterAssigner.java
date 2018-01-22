package enshud.s4.compiler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class RegisterAssigner
{
    private RegisterAssigner(){}
    
    public static void main(String[] a)
    {
        Link l = new Link("t0", "t1", "t2", "t3", "t4");
        l.setLink("t0", "t1");
        l.setLink("t0", "t2");
        l.setLink("t1", "t3");
        l.setLink("t1", "t4");
        l.setLink("t2", "t4");
        l.setLink("t3", "t4");
        l.setLink("t0", "t3");
        l.setLink("t1", "t2");
        l.setLink("t2", "t3");
        System.out.println(l);
        System.out.println(coloring(l, 3));
    }
    
    static Map<String, Integer> coloring(Link l)
    {
        return coloring(l, l.size());
    }
    
    static Map<String, Integer> coloring(Link l, int max)
    {
        final Map<String, Integer> c = new HashMap<>();
        l.getVars()
            .forEach(k -> c.put(k, -1));
        l.getVars()
            .forEach(k -> {
                Set<Integer> toc = l.linksFrom(k)
                        .stream()
                        .map(c::get)
                        .filter(a -> a >= 0)
                        .collect(Collectors.toSet());
                
                int n = IntStream.range(0, max)
                        .filter(i -> !toc.contains(i))
                        .min()
                        .orElseThrow(() -> new IllegalStateException(k+" conflicted."));
                c.put(k, n);
            });
        return c;
    }
    
    private static class Link
    {
        private Map<String, Set<String>> l = new HashMap<>();
        
        public Link(String... vars)
        {
            Stream.of(vars)
                .forEach(v -> l.put(v, new HashSet<>()));
        }
        
        private void setArrow(String from, String to)
        {
            l.get(from).add(to);
        }
        private void removeArrow(String from, String to)
        {
            l.get(from).remove(to);
        }
        void setLink(String from, String to)
        {
            setArrow(from, to);
            setArrow(to, from);
        }
        void removeLink(String from, String to)
        {
            removeArrow(from, to);
            removeArrow(to, from);
        }
        boolean existsArrow(String from, String to)
        {
            return l.get(from).contains(to);
        }
        boolean existsLink(String from, String to)
        {
            return existsArrow(from, to) || existsArrow(to, from);
        }
        Set<String> linksFrom(String from)
        {
            return l.get(from);
        }
        Set<String> getVars()
        {
            return l.keySet();
        }
        int size()
        {
            return l.size();
        }
        @Override
        public String toString()
        {
            return l.keySet().stream()
                .map(k -> k + ":" + linksFrom(k))
                .collect(Collectors.joining(System.lineSeparator(), "", System.lineSeparator()));
        }
    }
}
