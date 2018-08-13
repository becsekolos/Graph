
A program sok beérkező adatra lassan hajtódik végre, ezért "beégetett" adatokat használtam a "teszteléshez"/próbálgatáshoz.

A feladat megoldása során elsősorban a program futási ideje okozott rengeteg problémát, hiszen egy nagy adathalmazt kell feldolgozni.

A sok összehasonlítás, bejárás, művelet (O(n^n) műveletigény) nagyon lelassítja a programot...

Ez volt az első alkalom, hogy tapasztaltam, mennyire fontos az, hogy egy adott kód "költséghatékony" legyen, hogy minél gyorsabban lefusson. (O(n) időben)

A program így közel sem tökéletes, rengeteg javítanivaló lenne rajta. (Pl.: Olyan gráfban, amely kört tartalmaz, valamely élek duplán kerülnek most az eredménybe.)


-----------------------------------------------------------------------------------------------------------


Az adatbázis és a táblák létrehozása,a .CSV fájl beolvasása nem Java kódból történik, hanem pgAdmin (PostgreSQL) segítségével:

-- Table: public.testtable

-- DROP TABLE public.testtable;

CREATE TABLE public.testtable
(
    id bigint NOT NULL,
    point_start bigint,
    point_end bigint,
    CONSTRAINT testdb_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.testtable
    OWNER to postgres;
	
-----------------------------------------------------------------------------------------------------------
	
-- Table: public.outtable

-- DROP TABLE public.outtable;

CREATE TABLE public.outtable
(
    point_start bigint,
    point_end bigint
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.outtable
    OWNER to postgres;
	
-----------------------------------------------------------------------------------------------------------

COPY testdb(id,point_start,point_end) 
FROM 'C:<path>\newGraph.csv' DELIMITER ';' CSV HEADER;
