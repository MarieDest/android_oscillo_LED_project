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
     *
     * @param str   le Nom
     */
    @Override
    public void connect(String str) {
        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(str);
        disconnect();
        mConnectThread = new ConnectThread(device);
        setState(STATE_CONNECTING);
        mConnectThread.start();
    }

    @Override
    public void disconnect() {
       // BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().;
        //disconnect();
        //mConnectThread = new ConnectThread(device);
        setState(STATE_NOT_CONNECTED);
        mConnectThread=null;


    }

    @Override
    public void send(byte[] b) {
        this.getFrameProcessor().toFrame(b);
        mByteRingBuffer.put(this.getFrameProcessor().getTxFrame());
        mWritingThread.write();
    }


    private class ConnectThread extends Thread{
        public ConnectThread(BluetoothDevice device){
            try {
                mSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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
    private void startReadWriteThreads(){
        mWritingThread = new WritingThread(mSocket);
        Log.i("ConnectThread","Tread WritingThread lancé");
       // mWritingThread.start();
        setState(STATE_CONNECTED);
    }
    private class WritingThread extends Thread{
        private OutputStream mOutStream;

        public WritingThread(BluetoothSocket mSocket) {
            try {
                mOutStream = mSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        public void afficher(byte[] b ){
            System.out.print("[");
            for (int ii = 0 ; ii< b.length-1;ii++){

                System.out.print(b[ii]);
                System.out.print(" , ");
            }
            System.out.print(b[b.length-1]);
            System.out.print("]");

        }

        @Override
        public void run() {
            while (mSocket !=null){
            }
    }
    public void write(){
        byte[] b=mByteRingBuffer.getAll();
        try {
            mOutStream.write(b);
            Message writtenMsg = handler.obtainMessage(1, -1, -1,b);
            writtenMsg.sendToTarget();
            Log.d("Message","le message a été envoyé. Le message était : "+b);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}}
