CREATE OR REPLACE FUNCTION "ALMACEN".saldototal(empresa integer, moneda integer, fecha character varying)
 RETURNS double precision
 LANGUAGE plpgsql
AS $function$

	DECLARE total double precision default 0.00;
	DECLARE totalNA double precision default 0.00;
	DECLARE total_retiro double precision default 0.00;
	DECLARE bna int default 0;
	DECLARE estatus int; 
	DECLARE monto int; 
	DECLARE operacion int;
	DECLARE tipo_operacion int;
	DECLARE remesa int;

	DECLARE cursor_remesas CURSOR FOR select rd.id_estatus_remesa, rd.monto, r.id_operacion, op.id_tipo_operacion, r.id_remesa FROM "ALMACEN"."REMESA" r, "ALMACEN"."REMESA_DETALLE" rd, "ALMACEN"."SUCURSAL" s, "ALMACEN"."OPERACION" op
		where rd.id_estatus_remesa=(SELECT MAX(d.id_estatus_remesa) FROM "ALMACEN"."REMESA_DETALLE" d WHERE d.id_remesa=rd.id_remesa 
		and d.fecha < TO_TIMESTAMP($3, 'DD-MM-YYYY HH24:MI:SS')) 
		and r.id_sucursal = s.id_sucursal
		and r.id_operacion = op.id_operacion
		and rd.id_remesa = r.id_remesa
		and r.id_operacion not in (7,9,12) 
		and rd.id_estatus_remesa in (2,4) 
		and s.id_empresa  = $1 
		and rd.id_moneda = $2   
		order by rd.fecha,r.id_remesa asc;
		

	
BEGIN
OPEN cursor_remesas;
loop
	FETCH cursor_remesas INTO estatus, monto, operacion, tipo_operacion, remesa;
	EXIT WHEN NOT FOUND;
	IF (estatus != 5) then
		if(tipo_operacion = 1) then		
			total = total + monto;
			select "ALMACEN".monto_bna(remesa) into bna;
			totalNA = totalNA + bna;			
		else
			if(operacion != 7) then
				 total_retiro = total_retiro + monto;				 
			end if;
		end if;
	end if;
end loop;
	total = total-(totalNA+total_retiro);
return total;
END
$function$;
