program testExpr(output);
var i, j, k, l, m, n: integer;
    ca, cb, cc, cd: char;
    ba, bb, bt, bf: boolean;
    str: array[-2..10] of char;
    a0, a1, a2, a3, a4, a5, a6, a7, a8, a9: integer;

begin
    i := 6;
    j := -7;
    k := -8;
    ca := 'A';
    cb := 'b';
    bt := true;
    bf := false;

    l  := i;
    l  := l - 6;
    m := 8+j;
    n  := 3+i+2-j+k-8;
    a0 := l;
    a1 := m;
    a2  := n;

    if j < i then
    begin
        writeln('a');
        l  := 3
    end;
    if k <= i - 7 then
    begin
        writeln('b');
        m  := 4
    end;
    if -k-2=6 then
    begin
        writeln('c');
        n := 5
    end;
    a3 := l;
    a4 := m;
    a5  := n;

    if (i > j) then
    begin
        writeln('d');
        l := 6
    end;
    if i >= k+7 then
    begin
        writeln('e');
        m := 7
    end;
    if -k+2 <> 5 then
    begin
        writeln('f');
        n := 8
    end;
    a6 := l;
    a7 := m;
    a8 := n;

    if -i*j+j*1+(-2)*i-((-i div 3 -j/7+(-k)mod(-((1+(2+(k+i)))-2)))) <> 24
    then
    begin
        writeln('g');
        l := 0
    end
    else
    begin
        writeln('h');
        l := 9
    end;
    a9 := l;
    writeln(a0, a1, a2, a3, a4, a5, a6, a7, a8, a9);


    ba := j < i;
    if ba and (k < 0) then
    begin
        writeln('i');
        l  := 0
    end;
    if ba = (k < 0) then
    begin
        writeln('j');
        m  := 1
    end;
    if true  <= ba then
    begin
        writeln('k');
        n  := 2
    end;
    a0 := l;
    a1 := m;
    a2  := n;

    if bf < bt then
    begin
        writeln('l');
        l  := 3
    end;
    if not(not(ba and bt)or bf) then
    begin
        writeln('m');
        m := 4
    end;
    if (true or false)and(true<>false) then
    begin
        writeln('n');
        n := 5
    end;
    a3 := l;
    a4 := m;
    a5  := n;

    if ca <= cb then
    begin
        writeln('o');
        l := 6
    end;
    if ca = 'A' then
    begin
        writeln('p');
        m := 7
    end;
    if not (cb<>'b') then
    begin
        writeln('q');
        n := 8
    end;
    a6 := l;
    a7 := m;
    a8 := n;
    writeln(a0, a1, a2, a3, a4, a5, a6, a7, a8, '9');

    n  := -2;
    while n <= 10 do
    begin
        str[n] := cb;
        n := n+1
    end;

    n := -2;

    while n <= 10 do
    begin
        if n <2 then
        begin
            if n <0 then
            begin
                str[n] := ca
            end
            else
            begin
                str[n] := 'B'
            end
        end
        else
        begin
            str[n] := 'C'
        end;

        cd := 'D';

        if (n >= 4) then
        begin
            if n <8 then
            begin
                if n <6 then
                begin
                    str[n] := cd
                end
                else
                begin
                    str[n] := 'E'
                end
            end
            else
            begin
                if n <= 9 then
                begin
                    str[n] := 'f'
                end
                else
                begin
                    str[n] := 'g'
                end
            end
        end;
        n := n+1
    end;

    writeln('AABBCCDDEEffg');
    writeln(str)
end.
