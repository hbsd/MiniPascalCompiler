program testBasic(input, output);
var i   : integer;
    memo: array [0..99] of integer;

procedure fibo(n: integer);
begin
    if memo[n] = -1 then begin
        if (n = 0) or (n = 1) then begin
            memo[n] := 1
        end
        else begin
            fibo(n - 1);
            fibo(n - 2);
            memo[n] := memo[n - 1] + memo[n - 2]
        end
    end
end;

begin
    i := 0;
    while i < 100 do
    begin
        memo[i] := -1;
        i := i + 1
    end;
    fibo(99);

    i := 0;
    while i < 100 do
    begin
        writeln(memo[i]);
        i := i + 1
    end
end.
