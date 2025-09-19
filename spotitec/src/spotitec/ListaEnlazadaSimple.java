/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spotitec;

/**
 *
 * @author saidn
 */
public class ListaEnlazadaSimple {

    private Nodo cabeza;
    private int tamano;

    public ListaEnlazadaSimple() {
        this.cabeza = null;
        this.tamano = 0;
    }

    public boolean estaVacia() {
        return cabeza == null;
    }

    public int getTamano() {
        return tamano;
    }

    public void agregar(Cancion cancion) {
        Nodo nuevoNodo = new Nodo(cancion);
        if (estaVacia()) {
            cabeza = nuevoNodo;
        } else {
            Nodo actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevoNodo;
        }
        tamano++;
    }

    public Cancion eliminar(int indice) {
        if (indice < 0 || indice >= tamano) {
            return null;
        }
    
        Nodo nodoEliminado;
        if (indice == 0) {
            nodoEliminado = cabeza;
            cabeza = cabeza.siguiente;
        } else {
            Nodo anterior = cabeza;
            for (int i = 0; i < indice - 1; i++) {
                anterior = anterior.siguiente;
            }

            nodoEliminado = anterior.siguiente; 

            anterior.siguiente = nodoEliminado.siguiente;
        }
        tamano--;
        return nodoEliminado.cancion;
    }
    
    public Cancion obtener(int indice) {
        if (indice < 0 || indice >= tamano) {
            return null; 
        }
        Nodo actual = cabeza;
        for (int i = 0; i < indice; i++) {
            actual = actual.siguiente;
        }
        return actual.cancion;
    }
}
