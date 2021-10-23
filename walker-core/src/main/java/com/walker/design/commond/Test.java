package com.walker.design.commond;


public class Test {

    public static void main(String[] argv) {
        Item itemLight = new ItemLight();
        Command commandLight = new CommandLight().setItem(itemLight);

        Item itemDoor = new ItemDoor();
        Command commandDoor = new CommandDoor().setItem(itemDoor);

//        ControlRemoteSimple controlRemoteSimple = new ControlRemoteSimple();
//
//        controlRemoteSimple.setSlot(commandLight);
//        controlRemoteSimple.buttonWasPressed();
//
//        controlRemoteSimple.setSlot(commandDoor);
//        controlRemoteSimple.buttonWasPressed();

        ControlRemote controlRemote = new ControlRemote(8, 10);
        controlRemote.setSlot(0, commandLight);
        controlRemote.setSlot(1, commandDoor);

        controlRemote.buttonWasPressed(0);
        controlRemote.buttonWasPushed(0);
        controlRemote.buttonWasPressed(1);

        CommandMacro commandMacro = new CommandMacro().setItems(itemDoor, itemLight);
        controlRemote.setSlot(5, commandMacro);


        controlRemote.buttonWasPressed(5);


        controlRemote.buttonWasBack();
        controlRemote.buttonWasBackAll(1);
        controlRemote.buttonWasBackAll(998);

//
//        for(int i = 0; i < 10; i++){
//            controlRemote.setSlot(i, commandDoor);
//            controlRemote.buttonWasPressed(i);
//        }
//        controlRemote.buttonWasBackAll(998);


    }


}



