package com.gamesense.api.setting.values;

import java.util.function.Supplier;

import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.module.Module;

public class ColorSetting extends Setting<GSColor> {
    private boolean rainbow=false;
    private final boolean rainbowEnabled,alphaEnabled;

    public ColorSetting(String name, Module module, boolean rainbow, GSColor value) {
        super(value, name, module);
        this.rainbow = rainbow;
        this.rainbowEnabled = true;
        this.alphaEnabled = false;
    }
    
    public ColorSetting(String name, String configName, Module module, Supplier<Boolean> isVisible, boolean rainbow, boolean rainbowEnabled, boolean alphaEnabled, GSColor value) {
        super(value, name, configName, module, isVisible);
        this.rainbow = rainbow;
        this.rainbowEnabled = rainbowEnabled;
        this.alphaEnabled = alphaEnabled;
    }

    @Override
    public GSColor getValue() {
        if (rainbow) return new GSColor(GSColor.fromHSB((System.currentTimeMillis() % (360 * 32)) / (360f * 32), 1, 1),getColor().getAlpha());
        else return super.getValue();
    }
    
    public GSColor getColor() {
    	return super.getValue();
    }

    public long toLong() {
        long temp=getColor().getRGB() & 0xFFFFFF;
        if (rainbowEnabled) temp+=((rainbow ? 1 : 0)<<24);
        if (alphaEnabled) temp+=getColor().getAlpha()<<32;
        return temp;
    }

    public void fromLong(long number) {
        if (rainbowEnabled) rainbow = ((number & 0x1000000) != 0);
        else rainbow = false;
        setValue(new GSColor((int)(number & 0xFFFFFF)));
        if (alphaEnabled) setValue(new GSColor(getColor(),(int)((number&0xFF00000000l)>>32)));
    }
    
    public boolean getRainbow() {
    	return rainbow;
    }
    
    public void setRainbow (boolean rainbow) {
    	this.rainbow=rainbow;
    }
    
    public boolean rainbowEnabled() {
    	return rainbowEnabled;
    }
    
    public boolean alphaEnabled() {
    	return alphaEnabled;
    }
}