package com.example.tp_eb03;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class BTManager extends Transceiver {
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter mAdapter;
    private BluetoothSocket mSocket = null;
    private ConnectThread mConnectThread = null;
    private WritingThread mWritingThread = null;
    private ByteRingBuffer mByteRingBuffer = new ByteRingBuffer(255);

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

    }

    @Override
    public void send(byte[] b) {

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
        mWritingThread.start();
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

        @Override
        public void run() {
            while (mSocket !=null){
                try {
                    mOutStream.write(mByteRingBuffer.get());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }
}}
