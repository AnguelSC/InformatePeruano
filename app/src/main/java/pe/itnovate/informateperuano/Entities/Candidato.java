package pe.itnovate.informateperuano.Entities;

/**
 * Created by Angel Sirlopu C on 06/12/2015.
 */
public class Candidato {
    private Integer id;
    private String nombres;
    private String apellidos;
    private String dni;
    private Double popularidad;
    private Integer votos;
    private String foto;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public Double getPopularidad() {
        return popularidad;
    }

    public void setPopularidad(Double popularidad) {
        this.popularidad = popularidad;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Integer getVotos() {
        return votos;
    }

    public void setVotos(Integer votos) {
        this.votos = votos;
    }
}
