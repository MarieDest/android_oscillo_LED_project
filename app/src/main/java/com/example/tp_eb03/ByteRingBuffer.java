package com.example.tp_eb03;

import android.util.Log;

/**
 * classe du buffer circulaire
 * c'est un buffer dont le début et la fin sont cote à cote
 * et se suivent.
 * Il y a deux indices, un d'écriture et un de lecture, et une capacité.
 */
public class ByteRingBuffer {
    private byte[] buffer;
    private int readerIndex;

    public int getReaderIndex() {
        return readerIndex;
    }

    public int getWriteIndex() {
        return writeIndex;
    }

    private int writeIndex;

    public int getCapacité() {
        return capacité;
    }

    private int capacité;



    private enum dépassement {VIDE,PLEIN,OK}

    /**
     * constructeur
     * @param indiceMax
     */
    public ByteRingBuffer(int indiceMax){
        buffer = new byte[indiceMax];
        capacité = indiceMax;
        readerIndex = 0;
        writeIndex = 0;
    }

    /**
     * méthode qui ajoute un byte dans le buffer
     * @param b
     */
      public void put(byte b){
        if(isOverflow()!=dépassement.PLEIN) {
            buffer[writeIndex] = b;
            writeIndex = (writeIndex + 1) % capacité;
           // System.out.println(writeIndex);

        }
    }

    /**
     * méthode qui ajoute une série de byte dans le buffer
     * @param buf
     */
    public void put(byte[] buf){
        for (int i=0; i<=buf.length -1 ;i++) {
            put(buf[i]);
        }
    }

    /**
     * méthode qui sort un élément du buffer (s'il n'est pas vide )
     * @return
     */
    public byte get(){
        if(isOverflow()!=dépassement.VIDE) {
            byte element = buffer[readerIndex];
            readerIndex = (readerIndex + 1) % capacité;
            return element;
        }else{
            return -1;
        }
    }

    /**
     * méthode qui sort tous les éléments du buffer (s'il n'est pas vide)
     * @return
     */
    public byte[] getAll(){
        int taille = bytesToRead();
        byte[] buf = new byte[taille];
        for (int i=0; i<=taille-1;i++) {
            buf[i]=get();
        }
        return buf;
    }


    /**
     * méthode qui retourne le nombre d'éléments à lire dans le buffer
     * @return
     */
    public int bytesToRead(){
        int taille = writeIndex - readerIndex;
        if(taille<0){
            taille = capacité +taille;
        }
        return taille;
    }

    /**
     * méthode qui détermine s'il y a dépassement de la taille du buffer ou si le buffer est vide
     * @return
     */
    public dépassement isOverflow(){
       if (bytesToRead()<0){ // le  buffer est vide
           //System.out.print("le buffer est vide!");
           return dépassement.VIDE;
       }
       else if (bytesToRead()>capacité){ //le buffer est plein (la taille du buffer est supérieur a la capacité)
         //  System.out.print("le buffer est plein!");

           return dépassement.PLEIN;
       }else{
           return dépassement.OK;
       }

    }


    public String toString(){
            return "buffer circulaire  : \n capacité : "+capacité+" \n valeur courante index Read :   "+readerIndex+"\n valeur courante index Write :  "+writeIndex + "\n le buffer est "+isOverflow();
    }
}
