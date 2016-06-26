package es.uoproject.pliskid.util;

import android.content.Context;

import es.uoproject.pliskid.activities.Launcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

/**
 * Clase que gestiona el fichero de persistencia.
 */
public class Serialization {

    /**
     * Método que registra los cambios
     * @param serializableData
     */
    public static void serializeData(SerializableData serializableData){

        FileOutputStream fos;
        try{

            fos = Launcher.getActivity().openFileOutput("data", Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(fos);
            objectOutputStream.writeObject(serializableData);
            objectOutputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que lee la configuración guardada
     * @return
     */
    public static SerializableData loadSerializableData(){

        ObjectInputStream objectInputStream=null;

        try{

            objectInputStream=new ObjectInputStream(Launcher.getActivity().openFileInput("data"));
            Object obj=objectInputStream.readObject();

            if(obj instanceof SerializableData)
                return (SerializableData) obj;
            else
                return null;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            if(objectInputStream!=null)
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        //If everything fails
        return null;
    }

}
