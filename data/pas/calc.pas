program calc(input);
    var input   : array [0..63] of char;
        pos     : integer;
        rToDigit: integer;
        rExp : integer;
    procedure toDigit(c: char);
        begin
            if c = '0' then begin
                rToDigit := 0
            end else begin if c = '1' then begin
                rToDigit := 1
            end else begin if c = '2' then begin
                rToDigit := 2
            end else begin if c = '3' then begin
                rToDigit := 3
            end else begin if c = '4' then begin
                rToDigit := 4
            end else begin if c = '5' then begin
                rToDigit := 5
            end else begin if c = '6' then begin
                rToDigit := 6
            end else begin if c = '7' then begin
                rToDigit := 7
            end else begin if c = '8' then begin
                rToDigit := 8
            end else begin if c = '9' then begin
                rToDigit := 9
            end end end end end end end end end end
        end;
    procedure exp;
        var c: char;
            op: char;
            rExpImpl: integer;
            rTerm: integer;
        procedure term;
            var c: char;
                op: char;
                rFactor: integer;
            procedure factor;
                var c: char;
                begin
                    writeln('>factor');
                    c := input[pos];
                    writeln(c);
                    if (c >= '0') and (c <= '9') then begin
                        pos := pos + 1;
                        toDigit(c);
                        rFactor := rToDigit
                    end else begin if c = '(' then begin
                        pos := pos + 1;
                        exp;
                        writeln(input[pos]);
                        pos := pos + 1;
                        rFactor := rExpImpl
                    end end;
                    writeln('<factor = ', rFactor)
                end;
            begin
                writeln('>term');
                factor;
                rTerm := rFactor;
                op := input[pos];
                writeln(op);
                while (op = '*') or (op = '/') do
                begin
                    writeln('|term = ', rTerm);
                    pos := pos + 1;
                    factor;
                    if op = '*' then begin
                        rTerm := rTerm * rFactor
                    end else begin
                        rTerm := rTerm div rFactor
                    end;
                    op := input[pos];
                    writeln(op)
                end;
                writeln('<term = ', rTerm)
            end;
        procedure expImpl;
            begin
                writeln('>exp');
                term;
                rExpImpl := rTerm;
                op := input[pos];
                writeln(op);
                while (op = '+') or (op = '-') do
                begin
                    writeln('|exp = ', rExpImpl);
                    pos := pos + 1;
                    term;
                    if op = '+' then begin
                        rExpImpl := rExpImpl + rTerm
                    end else begin
                        rExpImpl := rExpImpl - rTerm
                    end;
                    op := input[pos];
                    writeln(op)
                end;
                writeln('<exp = ', rExpImpl)
            end;
        begin
            expImpl;
            rExp := rExpImpl
        end;
    begin
        readln(input);
        pos := 0;
        exp;
        writeln(input, ' = ', rExp)
    end.
