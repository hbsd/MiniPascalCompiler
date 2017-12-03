program main(input, output);
    var p: integer;
    procedure f(a: integer; x: boolean);
        var q: array[1..2] of integer;
        procedure g;
            var q, r: integer; {hiding main.f.q}
            begin
                q := 30; {main.f.g.q}
                r := 40; {main.f.g.r}
                writeln('g     :',p,',',a,',',q,',',r); {4}{50,-10,30,40}
                f(90, false)
            end;
        procedure h;
            var r: integer;
            begin
                p := 50; {main.p}
                r := 60; {main.f.h.r}
                writeln('h     :',p,',',a,',',q[1],',',q[2],',',r); {3}{50,80,81,82,60}
                a := -10;
                q[1] := 70; {main.f.q}
                q[2] := 71; {main.f.q}
                g
            end;
        procedure i;
            begin
                p := p {main.p}
            end;
        begin
            q[1] := a +1; {main.f.q}
            q[2] := a +2; {main.f.q}
            if x then begin
                writeln('f     :',p,',',a,',',q[1],',',q[2],',true'); {2}{10,80,81,82,true}
                h
            end else begin
                writeln('f     :',p,',',a,',',q[1],',',q[2],',false'); {5}{50,90,91,92,false}
                i
            end
        end;
    begin
        p := 10; {main.p}
        writeln('start :',p); {1}{10}
        f(80,true);
        writeln('finish:',p) {6}{50}
    end.
