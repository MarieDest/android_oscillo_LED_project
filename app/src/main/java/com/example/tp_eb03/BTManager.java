package com.example.tp_eb03;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * classe qui hérite de Transceiver et qui permet de gerer la communication Bluetooth
 */
public class BTManager extends Transceiver {
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothSocket mSocket = null;
    private ConnectThread mConnectThread = null;
    private WritingThread mWritingThread = null;

    public ByteRingBuffer getByteRingBuffer() {
        return mByteRingBuffer;
    }

    private ByteRingBuffer mByteRingBuffer = new ByteRingBuffer(255);
    private Handler handler;
    private boolean mWrite=false;


    /**
     * fonction de connexion au device
     * @param str   le Nom du device
     */
    @Override
    public void connect(String str) {
        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(str);
        disconnect();
        mConnectThread = new ConnectThread(device);
        setState(STATE_CONNECTING);
        mConnectThread.start();
    }

    /**
     * fonction qui ne fonctionne pas.
     * fonction de déconnexion du device
     */
    @Override
    public void disconnect() {
       // BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().;
        //disconnect();
        //mConnectThread = new ConnectThread(device);
        setState(STATE_NOT_CONNECTED);
        mConnectThread=null;


    }

    /**
     * fonction d'envoi de données sous le format standardisé dans le buffer circulaire  et d'écriture dans le thread d'écriture
     * @param b
     */
    @Override
    public void send(byte[] b) {
        this.getFrameProcessor().toFrame(b);
        mByteRingBuffer.put(this.getFrameProcessor().getTxFrame());
        mWritingThread.write();
    }

    /**
     * Thread de connexion au device ( c'est dans un thread parce que la fonction de connexion est bloquante
     */
    private class ConnectThread extends Thread{
        /**
         * constructeur du thread de connexion
         * @param device
         */
        public ConnectThread(BluetoothDevice device){
            try {
                mSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * fonction de connexion au device.
         */
        public void run(){
            mAdapter.cancelDiscovery();
            try {
                mSocket.connect();
            } catch (IOException e) {
                disconnect();
            }
            mConnectThread = null;
            startReadWriteThreads();

        }
    }

    /**
     * fonction de création d'un thread d'écriture et de lecture
     */
    private void startReadWriteThreads(){
        mWritingThread = new WritingThread(mSocket);
        Log.i("ConnectThread","Tread WritingThread lancé");
        mWritingThread.start();
        setState(STATE_CONNECTED);
    }

    /**
     * thread d'écriture sur le device
     */
    private class WritingThread extends Thread{
        private OutputStream mOutStream;

        /**
         * constructeur du thread
         * @param mSocket
         */
        public WritingThread(BluetoothSocket mSocket) {
            try {
                mOutStream = mSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        /**
         * méthode d'écriture dans le device
         */
    public void write() {
        byte[] b = mByteRingBuffer.getAll();
        try {
            mOutStream.write(b);
            Message writtenMsg = handler.obtainMessage(1, -1, -1, b);
            writtenMsg.sendToTarget();
            Log.d("Message", "le message a été envoyé. Le message était : " + b);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    }
}
