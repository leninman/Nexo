CREATE OR REPLACE FUNCTION "ALMACEN".monto_bna(remesa integer)
 RETURNS double precision
 LANGUAGE plpgsql
AS $function$

DECLARE bna int default 0;
declare cursor_bna cursor for  select sum(p.cantidad_no_apta*d.denominacion) from "ALMACEN"."PIEZA" p, "ALMACEN"."DENOMINACION" d 
										where p.id_denominacion = d.id_denominacion
										and p.id_remesa = $1;
BEGIN
	open cursor_bna;
	FETCH cursor_bna INTO bna;
	close cursor_bna;
	return bna;
END
$function$;