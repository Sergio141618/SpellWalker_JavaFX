package es.cifpcarlos3.spellwalker;

public class PersonajesId {
    private String id;
    private String nombre;
    private String idCampana;

    public PersonajesId(String id, String nombre, String idCampana) {
        this.id = id;
        this.nombre = nombre;
        this.idCampana = idCampana;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getIdCampana() { return idCampana; }
}

