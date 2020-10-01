package com.gamesense.client.macro;

import java.util.ArrayList;
import java.util.List;

public class MacroManager{
	List<Macro> macros;

	public MacroManager(){
		macros = new ArrayList<>();
	}

	public List<Macro> getMacros(){
		return macros;
	}

	public Macro getMacroByKey(int key){
		Macro m = getMacros().stream().filter(mm -> mm.getKey() == key).findFirst().orElse(null);
		return m;
	}

	public void addMacro(Macro macro){
		macros.add(macro);
	}

	public void delMacro(Macro macro){
		macros.remove(macro);
	}
}