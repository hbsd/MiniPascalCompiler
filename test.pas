program testBasic(input, output);
var a: integer;
    b: boolean;
    variable: char;
begin
    a := 1+6-(33/4-7 mod 3)*(-4211+3);
    b := true and false or true <> false or false and true;
    writeln(a);
    if b then
    begin
        writeln('THEN')
    end
    else begin
        writeln('ELSE')
    end;
    writeln;
    if false then
    begin
        a := -100;
        while a < 100 do
        begin
            writeln('a = ', a, ', a*a = ', a*a);
            a := a + 1
        end
    end;
    writeln;
    
    if false then
    begin
        writeln('DDD')
    end
    else begin
        writeln('CCC')
    end;
    writeln;
    
    while false do
    begin
        writeln('DDD')
    end;
    writeln('END')
end.
