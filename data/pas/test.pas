program testBasic(input, output);
    var a: integer;
    begin
        a := a+1+2;
        writeln(a+(1+2)); {a + 3}
        if false <> (true or false) then begin
            writeln('A')
        end;
        
        if (not false) = (true and false) then begin
            writeln('B')
        end else begin
            writeln('C')
        end;

        while -7 * 4 < 12 + 2 do begin
            writeln('D')
        end;

        while ('a'<'c') and ('a'='c') do begin
            writeln('E')
        end
    end.
