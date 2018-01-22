package enshud.s4.compiler.tacode;

import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import enshud.pascal.InfixOperator;
import enshud.pascal.QualifiedVariable;
import enshud.s4.compiler.tacode.jmp.JumpFalse;
import enshud.s4.compiler.tacode.jmp.Jump;
import enshud.s4.compiler.tacode.ldst.Copy;
import enshud.s4.compiler.tacode.ldst.LoadLcl;
import enshud.s4.compiler.tacode.ldst.StoreLcl;
import enshud.s4.compiler.tacode.op.Binary;
import enshud.s4.compiler.tacode.proc.Arg;


public class BasicBlock
{
    final String               name;
    int                        offset;
    
    final TACode               code;
    final Set<BasicBlock>      pred  = new HashSet<>();
    final Set<BasicBlock>      succ  = new HashSet<>();
    final Set<BasicBlock>      domin = new HashSet<>();
    
    final Map<String, Integer> gen   = new HashMap<>();
    final Map<String, Integer> kill  = new HashMap<>();
    final Map<String, Integer> use   = new HashMap<>();
    final Map<String, Integer> out   = new HashMap<>();
    final Map<String, Integer> in    = new HashMap<>();
    final Map<String, Integer> live  = new HashMap<>();
    
    BasicBlock(String name, TACode code)
    {
        this.name = name;
        this.code = code;
    }
    
    public static void main(String[] args)
    {
        final TACode code = new TACode();
        code.add(new Copy("t0", TAValue.constant(0)));
        code.add(new StoreLcl(new QualifiedVariable("i", null, 0, null), TAValue.variable("t0")));
        code.add(new LoadLcl("t1", new QualifiedVariable("i", null, 0, null), "L1"));
        code.add(
            new Binary(
                "c",
                TAValue.variable("t1"), InfixOperator.GREATEQUAL, TAValue.constant(10)
            )
        );
        code.add(new JumpFalse(TAValue.variable("c"), "L2"));
        code.add(new Arg(TAValue.variable("c")));
        
        code.add(
            new Binary(
                "t2",
                TAValue.variable("t1"), InfixOperator.ADD, TAValue.constant(1)
            )
        );
        code.add(new StoreLcl(new QualifiedVariable("i", null, 0, null), TAValue.variable("t2")));
        code.add(new Jump("L1"));
        code.add(new Copy("t1", TAValue.variable("t1"), "L2"));
        System.out.println(code);
        /*
         * insts.add(new Copy("t0", TAValue.constant(10))); insts.add( new
         * Binary( "t1", TAValue.variable("t0"), InfixOperator.MOD,
         * TAValue.constant(2) ) ); insts.add( new Binary( "t2",
         * TAValue.variable("t1"), InfixOperator.EQUAL, TAValue.constant(0) ) );
         * insts.add(new ConditionalJump(TAValue.variable("t1"), "L1"));
         * insts.add(new Copy("a", TAValue.constant("odd"))); insts.add(new
         * Jump("L2")); insts.add(new Copy("a", TAValue.constant("even"),
         * "L1")); insts.add(new Copy("a", TAValue.variable("a"), "L2"));
         */
        final BasicBlockList bl = BasicBlockList.fromInsts(code);
        System.out.println(bl);
        bl.findNaturalLoop();
    }
    
    @Override
    public String toString()
    {
        return new StringBuilder()
            .append("<<").append(name).append(">>").append(System.lineSeparator())
            .append("pred:").append(pred.stream().map(b -> b.name).collect(Collectors.toList()))
            .append(System.lineSeparator())
            .append("succ:").append(succ.stream().map(b -> b.name).collect(Collectors.toList()))
            .append(System.lineSeparator())
            .append("dom :").append(domin.stream().map(b -> b.name).collect(Collectors.toList()))
            .append(System.lineSeparator())
            .append("gen :").append(gen).append(System.lineSeparator())
            .append("kill:").append(kill).append(System.lineSeparator())
            .append("use :").append(use).append(System.lineSeparator())
            .append("in  :").append(in).append(System.lineSeparator())
            .append("out :").append(out).append(System.lineSeparator())
            .append("live:").append(live).append(System.lineSeparator())
            .append("inst:").append(System.lineSeparator()).append(code)
            .toString();
    }
    
    public boolean isExitBlock()
    {
        return succ.isEmpty();
    }
}
// call ret jump cjump
