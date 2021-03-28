create table fotbl(
    trdate varchar(30),
    symbol varchar(30),
    expirydt varchar(30),
    instrument varchar(10),
    optiontyp varchar(10),
    strikepr decimal,
    closepr decimal,
    settlepr decimal,
    contracts int,
    valinlakh decimal,
    openint int,
    choi int,
    primary key(trdate, symbol, expirydt, instrument, optiontyp, strikepr )
);
create table fotblwdts(
        trdate varchar(30),
    symbol varchar(30),
    expirydt varchar(30),
    instrument varchar(10),
    optiontyp varchar(10),
    strikepr decimal,
    closepr decimal,
    settlepr decimal,
    contracts int,
    valinlakh decimal,
    openint int,
    choi int,
    primary key(trdate, symbol, expirydt, instrument, optiontyp, strikepr )
);
