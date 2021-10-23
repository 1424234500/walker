package com.walker.design.commond;

/**
 * 遥控器
 */
public class ControlRemoteSimple {

    Command slot;

    public void buttonWasPressed() {
        slot.executeOn();
    }

    public Command getSlot() {
        return slot;
    }

    public ControlRemoteSimple setSlot(Command slot) {
        this.slot = slot;
        return this;
    }
}



