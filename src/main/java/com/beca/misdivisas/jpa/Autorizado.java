package com.beca.misdivisas.jpa;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;


/**
 * The persistent class for the "AUTORIZADO" database table.
 * 
 */
@Entity
@Table(name="\"AUTORIZADO\"", schema ="\"ALMACEN\"")
@NamedQuery(name="Autorizado.findAll", query="SELECT a FROM Autorizado a")
public class Autorizado implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="\"ALMACEN\".\"seq_autorizado_idAutorizado\"", sequenceName ="\"ALMACEN\".\"seq_autorizado_idAutorizado\"", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="\"ALMACEN\".\"seq_autorizado_idAutorizado\"")
	@Column(name="\"id_autorizado\"", unique = true, nullable = false, columnDefinition = "serial")
	private Integer idAutorizado;

	@Column(name="\"cargo_empresa\"")
	private String cargoEmpresa;

	@Column(name="\"documento_identidad\"")
	private String documentoIdentidad;

	@Column(name="email")
	private String email;
	
	@Column(name="estado")
	private String estado;

	@JsonIgnore
	@Column(name="\"imagen_adicional\"")
	private String imagenAdicional;

	@Column(name="\"imagen_documento\"")
	private String imagenDocumento;

	@Column(name="\"imagen_rif\"")
	private String imagenRif;

	@Column(name="\"marca_modelo_color_vehiculo\"")
	private String marcaModeloColorVehiculo;

	@Column(name="\"nombre_completo\"")
	private String nombreCompleto;

	@Column(name="\"nombre_empresa\"")
	private String nombreEmpresa;

	@Column(name="\"placa_vehiculo\"")
	private String placaVehiculo;

	@Column(name="\"rif_empresa\"")
	private String rifEmpresa;

	@Column(name="\"telefono_movil\"")
	private String telefonoMovil;
	
	@Column(name="\"id_empresa\"")
	private Integer idEmpresa;
	
	@Column(name="\"id_empresa_beneficiaria\"")
	private Integer idEmpresaBeneficiaria;
	
	@Column(name="\"id_tipo_autorizado\"")
	private Integer idTipoAutorizado;

	@Column(name="\"id_transportista\"")
	private Integer idTransportista;
	
	@Column(name="\"fecha_creacion\"")
	private Timestamp fechaCreacion;

	@Column(name="\"fecha_actualizacion\"")
	private Timestamp fechaActualizacion;

	//bi-directional many-to-one association to Empresa
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"id_empresa\"", insertable = false, updatable = false)
	})
	private Empresa empresa;

	//bi-directional many-to-one association to Empresa
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"id_empresa_beneficiaria\"", insertable = false, updatable = false)
	})
	private Empresa empresaBeneficiaria;

	//bi-directional many-to-one association to TipoAutorizado
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"id_tipo_autorizado\"", insertable = false, updatable = false)
	})
	private TipoAutorizado tipoAutorizado;

	//bi-directional many-to-one association to Transportista
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"id_transportista\"", insertable = false, updatable = false)
	})
	private Transportista transportista;

	//bi-directional many-to-one association to SolicitudRetiro
	@JsonBackReference
	@OneToMany(mappedBy="autorizado")
	private List<SolicitudRetiro> solicitudRetiros;

	public Autorizado() {
	}

	public Integer getIdAutorizado() {
		return this.idAutorizado;
	}

	public void setIdAutorizado(Integer idAutorizado) {
		this.idAutorizado = idAutorizado;
	}

	public String getCargoEmpresa() {
		return this.cargoEmpresa;
	}

	public void setCargoEmpresa(String cargoEmpresa) {
		this.cargoEmpresa = cargoEmpresa;
	}

	public String getDocumentoIdentidad() {
		return this.documentoIdentidad;
	}

	public void setDocumentoIdentidad(String documentoIdentidad) {
		this.documentoIdentidad = documentoIdentidad;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getMarcaModeloColorVehiculo() {
		return this.marcaModeloColorVehiculo;
	}

	public void setMarcaModeloColorVehiculo(String marcaModeloColorVehiculo) {
		this.marcaModeloColorVehiculo = marcaModeloColorVehiculo;
	}

	public String getNombreCompleto() {
		return this.nombreCompleto;
	}

	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}

	public String getNombreEmpresa() {
		return this.nombreEmpresa;
	}

	public void setNombreEmpresa(String nombreEmpresa) {
		this.nombreEmpresa = nombreEmpresa;
	}

	public String getPlacaVehiculo() {
		return this.placaVehiculo;
	}

	public void setPlacaVehiculo(String placaVehiculo) {
		this.placaVehiculo = placaVehiculo;
	}

	public String getRifEmpresa() {
		return this.rifEmpresa;
	}

	public void setRifEmpresa(String rifEmpresa) {
		this.rifEmpresa = rifEmpresa;
	}

	public String getTelefonoMovil() {
		return this.telefonoMovil;
	}

	public void setTelefonoMovil(String telefonoMovil) {
		this.telefonoMovil = telefonoMovil;
	}

	public Integer getIdEmpresa() {
		return idEmpresa;
	}

	public void setIdEmpresa(Integer idEmpresa) {
		this.idEmpresa = idEmpresa;
	}

	public Integer getIdEmpresaBeneficiaria() {
		return idEmpresaBeneficiaria;
	}

	public void setIdEmpresaBeneficiaria(Integer idEmpresaBeneficiaria) {
		this.idEmpresaBeneficiaria = idEmpresaBeneficiaria;
	}

	public Integer getIdTipoAutorizado() {
		return idTipoAutorizado;
	}

	public void setIdTipoAutorizado(Integer idTipoAutorizado) {
		this.idTipoAutorizado = idTipoAutorizado;
	}

	public Integer getIdTransportista() {
		return idTransportista;
	}

	public void setIdTransportista(Integer idTransportista) {
		this.idTransportista = idTransportista;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public Empresa getEmpresaBeneficiaria() {
		return empresaBeneficiaria;
	}

	public void setEmpresaBeneficiaria(Empresa empresaBeneficiaria) {
		this.empresaBeneficiaria = empresaBeneficiaria;
	}

	public TipoAutorizado getTipoAutorizado() {
		return this.tipoAutorizado;
	}

	public void setTipoAutorizado(TipoAutorizado tipoAutorizado) {
		this.tipoAutorizado = tipoAutorizado;
	}

	public Transportista getTransportista() {
		return this.transportista;
	}

	public void setTransportista(Transportista transportista) {
		this.transportista = transportista;
	}

	public Timestamp getFechaActualizacion() {
		return this.fechaActualizacion;
	}

	public void setFechaActualizacion(Timestamp fechaActualizacion) {
		this.fechaActualizacion = fechaActualizacion;
	}

	public Timestamp getFechaCreacion() {
		return this.fechaCreacion;
	}

	public void setFechaCreacion(Timestamp fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
	
	public List<SolicitudRetiro> getSolicitudRetiros() {
		return this.solicitudRetiros;
	}

	public void setSolicitudRetiros(List<SolicitudRetiro> solicitudRetiros) {
		this.solicitudRetiros = solicitudRetiros;
	}

	public SolicitudRetiro addSolicitudRetiro(SolicitudRetiro solicitudRetiro) {
		getSolicitudRetiros().add(solicitudRetiro);
		solicitudRetiro.setAutorizado(this);

		return solicitudRetiro;
	}

	public SolicitudRetiro removeSolicitudRetiro(SolicitudRetiro solicitudRetiro) {
		getSolicitudRetiros().remove(solicitudRetiro);
		solicitudRetiro.setAutorizado(null);

		return solicitudRetiro;
	}

	public String getImagenAdicional() {
		return imagenAdicional;
	}

	public void setImagenAdicional(String imagenAdicional) {
		this.imagenAdicional = imagenAdicional;
	}

	public String getImagenDocumento() {
		return imagenDocumento;
	}

	public void setImagenDocumento(String imagenDocumento) {
		this.imagenDocumento = imagenDocumento;
	}

	public String getImagenRif() {
		return imagenRif;
	}

	public void setImagenRif(String imagenRif) {
		this.imagenRif = imagenRif;
	}

}