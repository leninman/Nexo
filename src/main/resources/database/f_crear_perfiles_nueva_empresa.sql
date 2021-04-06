
CREATE OR REPLACE FUNCTION "SEGURIDAD".f_crear_perfiles_nueva_empresa(
	empresa integer)
    RETURNS boolean
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
AS $BODY$

/* 
 *  Funcion: f_crear_perfiles_nueva_empresa
 *  Autor: Johanan Penso
 *  Fecha creación: 2021-03-10
 *  Versión: 1.0
 *  Parámetros:  empresa, el id de la nueva empresa
 *  Descripción: Procedimiento que creara los perfiles por defecto para la nueva empresa y asignara las opciones del menu que corresponden a cada uno de ellos
 */
DECLARE
		nombrePerfil VARCHAR;
		idPerfil INTEGER;
BEGIN
	
	INSERT INTO "SEGURIDAD"."PERFIL"(perfil, tipo_perfil, tipo_vista, id_empresa, editable, estado) VALUES ('ADMIN','E','E',empresa,FALSE,'A');
	INSERT INTO "SEGURIDAD"."PERFIL"(perfil, tipo_perfil, tipo_vista, id_empresa, editable, estado) VALUES ('Definidor','E','E',empresa,TRUE,'A');
	INSERT INTO "SEGURIDAD"."PERFIL"(perfil, tipo_perfil, tipo_vista, id_empresa, editable, estado) VALUES ('Solicitante','E','E',empresa,TRUE,'A');
	INSERT INTO "SEGURIDAD"."PERFIL"(perfil, tipo_perfil, tipo_vista, id_empresa, editable, estado) VALUES ('Aprobador','E','E',empresa,TRUE,'A');
	INSERT INTO "SEGURIDAD"."PERFIL"(perfil, tipo_perfil, tipo_vista, id_empresa, editable, estado) VALUES ('Consultor','E','E',empresa,TRUE,'A');		
   
		BEGIN
			--RAISE notice '%', 'INICIO'; 
			FOR idPerfil, nombrePerfil IN SELECT p.id_perfil, p.perfil FROM "SEGURIDAD"."PERFIL" p WHERE p.id_empresa = empresa
             
			LOOP 
				IF nombrePerfil = 'ADMIN' THEN
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,1 );
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,8 );
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,10);
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,12);
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,13);
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,17);
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,18);
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,19);
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,20);
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,21);
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,22);
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,23);
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,24);
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,25);
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,26);
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,27);
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,29);
				ELSIF nombrePerfil = 'Definidor' THEN
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,1 );
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,8 );
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,27);
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,29);
				ELSIF nombrePerfil = 'Solicitante'  THEN
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,1 );
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,10);
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,12);
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,17);
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,27);
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,29);
				ELSIF nombrePerfil = 'Aprobador'  THEN
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,1 );
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,13);
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,27);
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,29);
				ELSIF nombrePerfil = 'Consultor'  THEN
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,1 );
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,18);
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,19);
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,20);
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,21);
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,22);
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,23);
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,24);
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,27);
					INSERT INTO "SEGURIDAD"."PERFIL_MENU"(id_perfil, id_menu) VALUES (idPerfil,29);
				ELSE
				  RAISE notice 'Inexistente: %', nombrePerfil;
				END IF;
			END LOOP;
    		--RAISE notice '%', 'FIN'; 
		END;
		
	RETURN TRUE;
END;
$BODY$;


GRANT EXECUTE ON FUNCTION "SEGURIDAD".f_crear_perfiles_nueva_empresa(integer) TO "BE2809D";
