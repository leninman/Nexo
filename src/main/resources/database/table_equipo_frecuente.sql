-- CREACION DE LA SEQUENCIA ----------------------------------------------    


CREATE SEQUENCE "SEGURIDAD"."seq_usuario_equipo_frecuente_idUsuarioEquipoFrecuente"
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE "SEGURIDAD"."seq_usuario_equipo_frecuente_idUsuarioEquipoFrecuente"
    OWNER TO postgres;

COMMENT ON SEQUENCE "SEGURIDAD"."seq_usuario_equipo_frecuente_idUsuarioEquipoFrecuente"
    IS 'Secuencia de la tabla usuario_e';

-- CREACION DE LA TABLA ----------------------------------------------    

CREATE TABLE "SEGURIDAD"."USUARIO_EQUIPO_FRECUENTE"
(
    id_usuario_equipo_frecuente integer NOT NULL DEFAULT nextval('"SEGURIDAD"."seq_usuario_equipo_frecuente_idUsuarioEquipoFrecuente"'::regclass),
    id_usuario integer NOT NULL,
    direccion_ip character varying(20) COLLATE pg_catalog."default" NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    CONSTRAINT "PK_USUARIO_EQUIPO_FRECUENTE" PRIMARY KEY (id_usuario_equipo_frecuente),
    CONSTRAINT "FK_USUARIO_EQUIPO_FRECUENTE_USUARIO" FOREIGN KEY (id_usuario)
        REFERENCES "SEGURIDAD"."USUARIO" (id_usuario) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE "SEGURIDAD"."USUARIO_EQUIPO_FRECUENTE"
    OWNER to postgres;

COMMENT ON TABLE "SEGURIDAD"."USUARIO_EQUIPO_FRECUENTE"
    IS 'Tabla de ips de uso frecuente por usuario';

CREATE INDEX "IDX_USUARIO_EQUIPO_FRECUENTE_idUsuario"
    ON "SEGURIDAD"."USUARIO_EQUIPO_FRECUENTE" USING btree
    (id_usuario ASC NULLS LAST)
    TABLESPACE pg_default;    

CREATE UNIQUE INDEX "IDX_USUARIO_EQUIPO_FRECUENTE_idUsuarioEquipoFrecuente"
    ON "SEGURIDAD"."USUARIO_EQUIPO_FRECUENTE" USING btree
    (id_usuario_equipo_frecuente ASC NULLS LAST)
    TABLESPACE pg_default;
    
