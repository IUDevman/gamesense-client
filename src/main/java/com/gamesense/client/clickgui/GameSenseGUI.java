package com.gamesense.client.clickgui;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.gamesense.api.setting.Setting;
import com.gamesense.api.setting.SettingsManager;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.font.FontUtil;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.HUDModule;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ClickGuiModule;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.lukflug.panelstudio.base.Animation;
import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.base.SettingsAnimation;
import com.lukflug.panelstudio.base.SimpleToggleable;
import com.lukflug.panelstudio.component.IFixedComponent;
import com.lukflug.panelstudio.component.IFixedComponentProxy;
import com.lukflug.panelstudio.component.IResizable;
import com.lukflug.panelstudio.component.IScrollSize;
import com.lukflug.panelstudio.container.IContainer;
import com.lukflug.panelstudio.hud.HUDGUI;
import com.lukflug.panelstudio.layout.CSGOLayout;
import com.lukflug.panelstudio.layout.ChildUtil.ChildMode;
import com.lukflug.panelstudio.layout.ComponentGenerator;
import com.lukflug.panelstudio.layout.IComponentAdder;
import com.lukflug.panelstudio.layout.IComponentGenerator;
import com.lukflug.panelstudio.layout.ILayout;
import com.lukflug.panelstudio.layout.PanelAdder;
import com.lukflug.panelstudio.layout.PanelLayout;
import com.lukflug.panelstudio.mc12.MinecraftHUDGUI;
import com.lukflug.panelstudio.popup.CenteredPositioner;
import com.lukflug.panelstudio.popup.MousePositioner;
import com.lukflug.panelstudio.popup.PanelPositioner;
import com.lukflug.panelstudio.popup.PopupTuple;
import com.lukflug.panelstudio.setting.IBooleanSetting;
import com.lukflug.panelstudio.setting.ICategory;
import com.lukflug.panelstudio.setting.IClient;
import com.lukflug.panelstudio.setting.IColorSetting;
import com.lukflug.panelstudio.setting.IEnumSetting;
import com.lukflug.panelstudio.setting.IKeybindSetting;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.setting.IModule;
import com.lukflug.panelstudio.setting.INumberSetting;
import com.lukflug.panelstudio.setting.ISetting;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.ClearTheme;
import com.lukflug.panelstudio.theme.GameSenseTheme;
import com.lukflug.panelstudio.theme.IColorScheme;
import com.lukflug.panelstudio.theme.ITheme;
import com.lukflug.panelstudio.theme.IThemeMultiplexer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class GameSenseGUI extends MinecraftHUDGUI {
    public static final int WIDTH = 100, HEIGHT = 12, FONT_HEIGHT = 9, DISTANCE = 10, HUD_BORDER = 2;
    public static IClient client;
    public static GUIInterface guiInterface;
    public static HUDGUI gui;
    private final ITheme theme, gameSenseTheme, clearTheme;

    public GameSenseGUI() {
    	// Get some module instances ...
        ClickGuiModule clickGuiModule = ModuleManager.getModule(ClickGuiModule.class);
        ColorMain colorMain = ModuleManager.getModule(ColorMain.class);

        // Define interface and themes ..
        guiInterface = new GUIInterface(true) {
        	@Override
			public void drawString(Point pos, int height, String s, Color c) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(pos.x,pos.y,0);
				double scale=height/(double)(FontUtil.getFontHeight(colorMain.customFont.getValue())+(colorMain.customFont.getValue()?1:0));
				end(false);
				FontUtil.drawStringWithShadow(colorMain.customFont.getValue(),s,0,0,new GSColor(c));
				begin(false);
				GlStateManager.scale(scale,scale,1);
				GlStateManager.popMatrix();
			}

			@Override
			public int getFontWidth(int height, String s) {
				double scale=height/(double)(FontUtil.getFontHeight(colorMain.customFont.getValue())+(colorMain.customFont.getValue()?1:0));
				return (int)Math.round(FontUtil.getStringWidth(colorMain.customFont.getValue(),s)*scale);
			}
			
			@Override
			public double getScreenWidth() {
				return super.getScreenWidth();
			}
			
			@Override
			public double getScreenHeight() {
				return super.getScreenHeight();
			}

            @Override
            public String getResourcePrefix() {
                return "gamesense:gui/";
            }
        };
        Supplier<Boolean> themePredicate=()->clickGuiModule.theme.getValue().equals("2.0") || clickGuiModule.theme.getValue().equals("2.1.2");
        gameSenseTheme = new GameSenseTheme(new GSColorScheme("gamesense",()->!themePredicate.get()),FONT_HEIGHT,3,5,": "+TextFormatting.GRAY);
        clearTheme = new ClearTheme(new GSColorScheme("clear",themePredicate),()->clickGuiModule.theme.getValue().equals("2.1.2"),FONT_HEIGHT,3,1,": "+TextFormatting.GRAY);
        theme = new IThemeMultiplexer() {
            @Override
            public ITheme getTheme() {
                if (themePredicate.get()) return clearTheme;
                else return gameSenseTheme;
            }
        };
        
        // Define client structure
        client=()->Arrays.stream(Category.values()).sorted((a,b)->a.toString().compareTo(b.toString())).map(category->new ICategory() {
			@Override
			public String getDisplayName() {
				return category.toString();
			}

			@Override
			public Stream<IModule> getModules() {
				return ModuleManager.getModulesInCategory(category).stream().sorted((a,b)->a.getName().compareTo(b.getName())).map(module->new IModule() {
					@Override
					public String getDisplayName() {
						return module.getName();
					}

					@Override
					public IToggleable isEnabled() {
						return new IToggleable() {
							@Override
							public boolean isOn() {
								return module.isEnabled();
							}

							@Override
							public void toggle() {
								module.toggle();
							}
						};
					}

					@Override
					public Stream<ISetting<?>> getSettings() {
						Stream<ISetting<?>> temp=SettingsManager.getSettingsForModule(module).stream().map(setting->createSetting(setting));
						return Stream.concat(temp,Stream.concat(Stream.of(new IBooleanSetting() {
							@Override
							public String getDisplayName() {
								return "Toggle Msgs";
							}

							@Override
							public void toggle() {
								module.setToggleMsg(!module.isToggleMsg());
							}

							@Override
							public boolean isOn() {
								return module.isToggleMsg();
							}
						}),Stream.of(new IKeybindSetting() {
							@Override
							public String getDisplayName() {
								return "Keybind";
							}

							@Override
							public int getKey() {
								return module.getBind();
							}

							@Override
							public void setKey(int key) {
								module.setBind(key);
							}

							@Override
							public String getKeyName() {
								return Keyboard.getKeyName(module.getBind());
							}
						})));
					}
				});
			}
        	
        });

        // Define GUI object
        IToggleable guiToggle=new SimpleToggleable(false);
        IToggleable hudToggle=new SimpleToggleable(false) {
        	@Override
        	public boolean isOn() {
        		if (guiToggle.isOn()&&super.isOn()) return clickGuiModule.showHUD.getValue();
        		return super.isOn();
        	}
        };
        gui = new HUDGUI(guiInterface,theme.getDescriptionRenderer(),new MousePositioner(new Point(10,10)),guiToggle,hudToggle);
        BiFunction<Context,Integer,Integer> scrollHeight=(context,componentHeight)->{
        	if (clickGuiModule.scrolling.getValue().equals("Screen")) return componentHeight;
        	else return Math.min(componentHeight,Math.max(HEIGHT*4,GameSenseGUI.this.height-context.getPos().y-HEIGHT));
        };
        Supplier<Animation> animation=()->new SettingsAnimation(()->clickGuiModule.animationSpeed.getValue(),()->guiInterface.getTime());
		PopupTuple popupType=new PopupTuple(new PanelPositioner(new Point(0,0)),false,new IScrollSize() {
			@Override
			public int getScrollHeight (Context context, int componentHeight) {
				return scrollHeight.apply(context,componentHeight);
			}
		});
		// Populate HUD
        for (Module module : ModuleManager.getModules()) {
            if (module instanceof HUDModule) {
                ((HUDModule)module).populate(theme);
                gui.addHUDComponent(((HUDModule)module).getComponent(),new IToggleable() {
					@Override
					public boolean isOn() {
						return module.isEnabled();
					}

					@Override
					public void toggle() {
						module.toggle();
					}
                },animation.get(),theme,HUD_BORDER);
            }
        }
        // Populate GUI
        IComponentAdder classicPanelAdder=new PanelAdder(new IContainer<IFixedComponent>() {
			@Override
			public boolean addComponent(IFixedComponent component) {
				return gui.addComponent(new IFixedComponentProxy<IFixedComponent>() {
					@Override
		            public void handleScroll (Context context, int diff) {
			            IFixedComponentProxy.super.handleScroll(context,diff);
			            if (clickGuiModule.scrolling.getValue().equals("Screen")) {
	                        Point p = getPosition(guiInterface);
	                        p.translate(0, -diff);
	                        setPosition(guiInterface, p);
			            }
			        }

					@Override
					public IFixedComponent getComponent() {
						return component;
					}
				});
			}

			@Override
			public boolean addComponent(IFixedComponent component, IBoolean visible) {
				return gui.addComponent(new IFixedComponentProxy<IFixedComponent>() {
					@Override
		            public void handleScroll (Context context, int diff) {
			            IFixedComponentProxy.super.handleScroll(context,diff);
			            if (clickGuiModule.scrolling.getValue().equals("Screen")) {
	                        Point p = getPosition(guiInterface);
	                        p.translate(0, -diff);
	                        setPosition(guiInterface, p);
			            }
			        }

					@Override
					public IFixedComponent getComponent() {
						return component;
					}
				},visible);
			}

			@Override
			public boolean removeComponent(IFixedComponent component) {
				return gui.removeComponent(component);
			}
        },false,()->!clickGuiModule.csgoLayout.getValue(),title->title) {
        	@Override
        	protected IScrollSize getScrollSize (IResizable size) {
        		return new IScrollSize() {
        			@Override
        			public int getScrollHeight (Context context, int componentHeight) {
        				return scrollHeight.apply(context,componentHeight);
        			}
        		};
        	}
		};
		IComponentGenerator generator=new ComponentGenerator(scancode->scancode==Keyboard.KEY_DELETE);
		ILayout classicPanelLayout=new PanelLayout(WIDTH,new Point(DISTANCE,DISTANCE),(WIDTH+DISTANCE)/2,HEIGHT+DISTANCE,animation,level->ChildMode.DOWN,level->ChildMode.DOWN,popupType);
		classicPanelLayout.populateGUI(classicPanelAdder,generator,client,theme);
		// CSGO Layout!
		PopupTuple colorPopup=new PopupTuple(new CenteredPositioner(()->new Rectangle(new Point(0,0),guiInterface.getWindowSize())),true,new IScrollSize() {});
		IComponentAdder horizontalCSGOAdder=new PanelAdder(gui,true,()->clickGuiModule.csgoLayout.getValue(),title->title);
		ILayout horizontalCSGOLayout=new CSGOLayout(new Labeled("GameSense",null,()->true),new Point(100,100),480,WIDTH,animation,"Enabled",true,true,2,ChildMode.DOWN,colorPopup) {
			@Override
			public int getScrollHeight (Context context, int componentHeight) {
				return 320;
			}
			
			@Override
			protected boolean isUpKey (int key) {
				return key==Keyboard.KEY_UP;
			}
			
			@Override
			protected boolean isDownKey (int key) {
				return key==Keyboard.KEY_DOWN;
			}
			
			@Override
			protected boolean isLeftKey (int key) {
				return key==Keyboard.KEY_LEFT;
			}
			
			@Override
			protected boolean isRightKey (int key) {
				return key==Keyboard.KEY_RIGHT;
			}
		};
		horizontalCSGOLayout.populateGUI(horizontalCSGOAdder,generator,client,theme);
    }
    
    private ISetting<?> createSetting (Setting<?> setting) {
    	if (setting instanceof BooleanSetting) {
    		return new IBooleanSetting() {
				@Override
				public String getDisplayName() {
					return setting.getName();
				}
				
				@Override
				public IBoolean isVisible() {
					return ()->setting.isVisible();
				}

				@Override
				public void toggle() {
					((BooleanSetting)setting).setValue(!((BooleanSetting)setting).getValue());
				}

				@Override
				public boolean isOn() {
					return ((BooleanSetting)setting).getValue();
				}
				
				@Override
				public Stream<ISetting<?>> getSubSettings() {
					return null;
					/*if (setting.getSubSettings().count()==0) return null;
					return setting.getSubSettings().map(subSetting->createSetting(subSetting));*/
				}
    		};
    	} else if (setting instanceof IntegerSetting) {
    		return new INumberSetting() {
				@Override
				public String getDisplayName() {
					return setting.getName();
				}
				
				@Override
				public IBoolean isVisible() {
					return ()->setting.isVisible();
				}

				@Override
				public double getNumber() {
					return ((IntegerSetting)setting).getValue();
				}

				@Override
				public void setNumber(double value) {
					((IntegerSetting)setting).setValue((int)Math.round(value));
				}

				@Override
				public double getMaximumValue() {
					return ((IntegerSetting)setting).getMax();
				}

				@Override
				public double getMinimumValue() {
					return ((IntegerSetting)setting).getMin();
				}

				@Override
				public int getPrecision() {
					return 0;
				}
				
				@Override
				public Stream<ISetting<?>> getSubSettings() {
					return null;
					/*if (setting.getSubSettings().count()==0) return null;
					return setting.getSubSettings().map(subSetting->createSetting(subSetting));*/
				}
    		};
    	} else if (setting instanceof DoubleSetting) {
    		return new INumberSetting() {
				@Override
				public String getDisplayName() {
					return setting.getName();
				}
				
				@Override
				public IBoolean isVisible() {
					return ()->setting.isVisible();
				}

				@Override
				public double getNumber() {
					return ((DoubleSetting)setting).getValue();
				}

				@Override
				public void setNumber(double value) {
					((DoubleSetting)setting).setValue(value);
				}

				@Override
				public double getMaximumValue() {
					return ((DoubleSetting)setting).getMax();
				}

				@Override
				public double getMinimumValue() {
					return ((DoubleSetting)setting).getMin();
				}

				@Override
				public int getPrecision() {
					return 2;
				}
				
				@Override
				public Stream<ISetting<?>> getSubSettings() {
					return null;
					/*if (setting.getSubSettings().count()==0) return null;
					return setting.getSubSettings().map(subSetting->createSetting(subSetting));*/
				}
    		};
    	} else if (setting instanceof ModeSetting) {
    		return new IEnumSetting() {
    			private final ILabeled[] states=((ModeSetting)setting).getModes().stream().map(mode->new Labeled(mode,null,()->true)).toArray(ILabeled[]::new);
    			
				@Override
				public String getDisplayName() {
					return setting.getName();
				}
				
				@Override
				public IBoolean isVisible() {
					return ()->setting.isVisible();
				}

				@Override
				public void increment() {
					((ModeSetting)setting).increment();
				}
				
				@Override
				public void decrement() {
					((ModeSetting)setting).decrement();
				}

				@Override
				public String getValueName() {
					return ((ModeSetting)setting).getValue();
				}
				
				@Override
				public int getValueIndex() {
					return ((ModeSetting)setting).getModes().indexOf(getValueName());
				}

				@Override
				public void setValueIndex(int index) {
					((ModeSetting)setting).setValue(((ModeSetting)setting).getModes().get(index));
				}

				@Override
				public ILabeled[] getAllowedValues() {
					return states;
				}
				
				@Override
				public Stream<ISetting<?>> getSubSettings() {
					return null;
					/*if (setting.getSubSettings().count()==0) return null;
					return setting.getSubSettings().map(subSetting->createSetting(subSetting));*/
				}
    		};
    	} else if (setting instanceof ColorSetting) {
    		return new IColorSetting() {
				@Override
				public String getDisplayName() {
					return TextFormatting.BOLD+setting.getName();
				}
				
				@Override
				public IBoolean isVisible() {
					return ()->setting.isVisible();
				}

				@Override
				public Color getValue() {
					return ((ColorSetting)setting).getValue();
				}

				@Override
				public void setValue(Color value) {
					((ColorSetting)setting).setValue(new GSColor(value));
				}

				@Override
				public Color getColor() {
					return ((ColorSetting)setting).getColor();
				}

				@Override
				public boolean getRainbow() {
					return ((ColorSetting)setting).getRainbow();
				}

				@Override
				public void setRainbow(boolean rainbow) {
					((ColorSetting)setting).setRainbow(rainbow);
				}
				
				@Override
				public boolean hasAlpha() {
					return ((ColorSetting)setting).alphaEnabled();
				}
				
				@Override
				public boolean allowsRainbow() {
					return ((ColorSetting)setting).rainbowEnabled();
				}

				@Override
				public boolean hasHSBModel() {
					return ModuleManager.getModule(ColorMain.class).colorModel.getValue().equalsIgnoreCase("HSB");
				}
				
				@Override
				public Stream<ISetting<?>> getSubSettings() {
					//Stream<ISetting<?>> temp=setting.getSubSettings().map(subSetting->createSetting(subSetting));
					return /*Stream.concat(temp,*/Stream.of(new IBooleanSetting() {
						@Override
						public String getDisplayName() {
							return "Sync Color";
						}
						
						@Override
						public IBoolean isVisible() {
							return ()->setting!=ModuleManager.getModule(ColorMain.class).enabledColor;
						}

						@Override
						public void toggle() {
							((ColorSetting)setting).setValue(ModuleManager.getModule(ColorMain.class).enabledColor.getColor());
							((ColorSetting)setting).setRainbow(ModuleManager.getModule(ColorMain.class).enabledColor.getRainbow());
						}

						@Override
						public boolean isOn() {
							return ModuleManager.getModule(ColorMain.class).enabledColor.getColor().equals(((ColorSetting)setting).getColor());
						}
					})/*)*/;
				}
    		};
    	}
    	return new ISetting<Void>() {
			@Override
			public String getDisplayName() {
				return setting.getName();
			}
			
			@Override
			public IBoolean isVisible() {
				return ()->setting.isVisible();
			}

			@Override
			public Void getSettingState() {
				return null;
			}

			@Override
			public Class<Void> getSettingClass() {
				return Void.class;
			}
			
			@Override
			public Stream<ISetting<?>> getSubSettings() {
				return null;
				/*if (setting.getSubSettings().count()==0) return null;
				return setting.getSubSettings().map(subSetting->createSetting(subSetting));*/
			}
    	};
    }

    public static void renderItem(ItemStack item, Point pos) {
    	GameSense.INSTANCE.gameSenseGUI.getInterface().end(false);
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glPopAttrib();
        GlStateManager.enableDepth();
        GlStateManager.disableAlpha();
        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().getRenderItem().zLevel = -150.0f;
        RenderHelper.enableGUIStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(item, pos.x, pos.y);
        Minecraft.getMinecraft().getRenderItem().renderItemOverlays(Minecraft.getMinecraft().fontRenderer, item, pos.x, pos.y);
        RenderHelper.disableStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().zLevel = 0.0F;
        GlStateManager.popMatrix();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GameSense.INSTANCE.gameSenseGUI.getInterface().begin(false);
    }

    public static void renderEntity(EntityLivingBase entity, Point pos, int scale) {
    	GameSense.INSTANCE.gameSenseGUI.getInterface().end(false);
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glPopAttrib();
        GlStateManager.enableDepth();
        GlStateManager.disableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.color(1, 1, 1, 1);
        GuiInventory.drawEntityOnScreen(pos.x, pos.y, scale, 28, 60, entity);
        GlStateManager.popMatrix();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GameSense.INSTANCE.gameSenseGUI.getInterface().begin(false);
    }

    @Override
    protected HUDGUI getHUDGUI() {
        return gui;
    }

    @Override
    protected GUIInterface getInterface() {
        return guiInterface;
    }

    @Override
    protected int getScrollSpeed() {
        return ModuleManager.getModule(ClickGuiModule.class).scrollSpeed.getValue();
    }
    
    
    private final class GSColorScheme implements IColorScheme {
    	private final String configName;
    	private final Supplier<Boolean> isVisible;
    	
    	public GSColorScheme (String configName, Supplier<Boolean> isVisible) {
    		this.configName=configName;
    		this.isVisible=isVisible;
    	}
    	
		@Override
		public void createSetting(ITheme theme, String name, String description, boolean hasAlpha, boolean allowsRainbow, Color color, boolean rainbow) {
			ModuleManager.getModule(ClickGuiModule.class).registerColor(name,configName+"_"+name.replace(" ",""),isVisible,rainbow,allowsRainbow,hasAlpha,new GSColor(color));
		}

		@Override
		public Color getColor(String name) {
			return ((ColorSetting)SettingsManager.getSettingsForModule(ModuleManager.getModule(ClickGuiModule.class)).stream().filter(setting->setting.getConfigName().equals(configName+"_"+name.replace(" ",""))).findFirst().orElse(null)).getValue();
		}
    }
}