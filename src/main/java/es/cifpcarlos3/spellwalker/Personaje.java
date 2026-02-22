package es.cifpcarlos3.spellwalker;

public class Personaje {
    private String nombrePersonaje;
    private String nombreCampana;
    private String escuela;
    private String spells;

    public Personaje(String nombrePersonaje, String nombreCampana, String escuela, String spells) {
        this.nombrePersonaje = nombrePersonaje;
        this.nombreCampana = nombreCampana;
        this.escuela = escuela;
        this.spells = spells;
    }

    public String getNombrePersonaje() { return nombrePersonaje; }
    public String getNombreCampana() { return nombreCampana; }
    public String getEscuela() { return escuela; }
    public String getSpells() { return spells; }
}


