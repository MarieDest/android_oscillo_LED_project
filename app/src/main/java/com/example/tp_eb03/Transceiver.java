package com.example.tp_eb03;

/**
 * classe abstraite de traitement de données d'une connexion
 */
public abstract class Transceiver {
    public static final int STATE_NOT_CONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;

    private int mState;
    private TransceiverListener mTransceiverListener;

    public FrameProcessor getFrameProcessor() {
        return mFrameProcessor;
    }

    private FrameProcessor mFrameProcessor;


    public void setTransceiverListener(TransceiverListener mTransceiverListener) {
        this.mTransceiverListener = mTransceiverListener;
    }

    public void attachFrameProcessor(FrameProcessor frameProcessor){
        mFrameProcessor = frameProcessor;
    }

    public void detachFrameProcessor(){
        mFrameProcessor = null;
    }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        mState = state;
        if(mTransceiverListener != null){
            mTransceiverListener.onTransceiverStateChanged(state);
        }
    }

    /***********************************************************************************************
     *                              INTERFACES
     **********************************************************************************************/

    public interface TransceiverListener{
        void onTransceiverDataReceiver();
        void onTransceiverStateChanged(int state);
        void onTransceiverConnectionLost();
        void onTransceiverUnableToConnect();
    }

    /***********************************************************************************************
     *                              METHODES ABSTRAITES
     **********************************************************************************************/

    public abstract void connect(String str);
    public abstract void disconnect();
    public abstract void send(byte[] b);

}
