package com.example.tp_eb03;

/**
 * classe qui fabrique des trames standardisés
 */
public class FrameProcessor {
    public byte[] getTxFrame() {
        return txFrame;
    }

    private byte[] txFrame;
    private byte[] rxFrame;
    private final static byte HEADER = 0x05;
    private final static byte TAIL = 0x04;
    private final static byte onSetDutyCycle = 0x0A;

    public void fromFrame(){}

    /**
     * création de trames grâce aux données
     * @param ValueToSendAndID
     */
    public void toFrame(byte[] ValueToSendAndID){
        if(ValueToSendAndID[0]==0x0A){
        byte lengthTrame = 0x01;
        boolean needEscape=false;
        byte ctrl= (byte) -(lengthTrame + onSetDutyCycle +  ValueToSendAndID[1]);

            // si la valeur qu'on cherche à mettre est 0x04 ou 0x05 ou 0x06
        if(ValueToSendAndID[1] == 0x04 || ValueToSendAndID[1]==0x05 || ValueToSendAndID[1] == 0x06){
            byte escape=0x06;
            needEscape = true;
            ValueToSendAndID[1] = (byte) (ValueToSendAndID[1] + escape);
            lengthTrame = (byte) 0x02;
            ctrl= (byte) -(lengthTrame + onSetDutyCycle + 0x06 + ValueToSendAndID[1]);

        }


        if(needEscape) {

            if(ctrl == 0x04 || ctrl==0x05 || ctrl == 0x06){
                txFrame =new byte[] {HEADER, (byte) 0x00, lengthTrame,onSetDutyCycle,(byte) 0x06, ValueToSendAndID[1],(byte)0x06,0x38,TAIL};
            }else{

                txFrame = new byte[] {HEADER, (byte) 0x00, lengthTrame,onSetDutyCycle,(byte) 0x06, ValueToSendAndID[1],ctrl,0X37,TAIL};
                System.out.println(ctrl);
            }
        }
        else{
            ctrl = (byte) -(lengthTrame + onSetDutyCycle +  ValueToSendAndID[1]);
            if(ctrl == 0x04 || ctrl==0x05 || ctrl == 0x06){
                txFrame =new byte[] {HEADER, (byte) 0x00, lengthTrame,onSetDutyCycle, ValueToSendAndID[1],(byte)0x06,0x036,TAIL};
            }else {
                txFrame = new byte[]{HEADER, (byte) 0x00, lengthTrame, onSetDutyCycle, ValueToSendAndID[1], (byte) 0X35, TAIL};
            }
        }
        needEscape = false;
        }

    }
    public void abstractOperation(){}
}
