package com.example.tp_eb03;

import android.util.Log;

/*
TO DO LIST:
- faire la méthode toString et bytesToRead
-tester avec le fichier des tests unitaires donné par le prof

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

    public ByteRingBuffer(int indiceMax){
        buffer = new byte[indiceMax];
        capacité = indiceMax;
        readerIndex = 0;
        writeIndex = 0;
    }
    // vérifier les conditions de write et read histoire de ne pas avoir d'overflow.
    public void put(byte b){
        if(isOverflow()!=dépassement.PLEIN) {
            buffer[writeIndex] = b;
            writeIndex = (writeIndex + 1) % capacité;
           // System.out.println(writeIndex);

        }
    }
    public void put(byte[] buf){
        for (int i=0; i<=buf.length -1 ;i++) {
            put(buf[i]);
        }
    }
    public byte get(){
        if(isOverflow()!=dépassement.VIDE) {
            byte element = buffer[readerIndex];
            readerIndex = (readerIndex + 1) % capacité;
            return element;
        }else{
            return -1;
        }
    }
    public byte[] getAll(){
        int taille = bytesToRead();
        byte[] buf = new byte[taille];
        for (int i=0; i<=taille-1;i++) {
            buf[i]=get();
        }
        return buf;
    }


    public int bytesToRead(){
        int taille = writeIndex - readerIndex;
        if(taille<0){
            taille = 5 +taille;
        }
        return taille;
    }
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
