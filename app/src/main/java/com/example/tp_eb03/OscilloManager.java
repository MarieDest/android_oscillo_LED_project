package com.example.tp_eb03;

import androidx.appcompat.widget.AppCompatSeekBar;

import com.google.android.material.badge.BadgeDrawable;

/**
 * classe qui implémente la classe transceiver listener seul la méthode setCalibrationDutyCircle est implémentée
 */
public class OscilloManager implements Transceiver.TransceiverListener {

    private Transceiver mTransceiver;//= new BTManager(); //ici le transceiver peut etre que BTManager puisque c'est la seule classe qui a ce type.



    public void getInstance(){

    }

    /**
     * méthode qui crée un objet FrameProcessor et l'attache au transceiver et crée une série de byte pour l'envoyer
     * @param value
     */
    public void setCalibrationDutyCycle(int value){
        FrameProcessor mFrameProcessor = new FrameProcessor();
        byte[] b=new byte[]{0x0A,(byte) value};

        mTransceiver.attachFrameProcessor(mFrameProcessor);
        mTransceiver.send(b);

    }
    public void getStatus(){

    }
    public void attachTransceiver(String adresse){
        mTransceiver.connect(adresse);
    }
    public void detachTransceiver(){
        mTransceiver.disconnect();
    }


    @Override
    public void onTransceiverDataReceiver() {

    }

    @Override
    public void onTransceiverStateChanged(int state) {

    }

    @Override
    public void onTransceiverConnectionLost() {

    }

    @Override
    public void onTransceiverUnableToConnect() {

    }

    public interface OscilloEventListener{

        void onOscilloStateChanged(int state);
        void onOscilloConnectionLost();
        void onOscilloUnableToConnect();
    }

}