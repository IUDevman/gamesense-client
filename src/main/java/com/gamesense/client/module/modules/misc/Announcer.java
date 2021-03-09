package com.gamesense.client.module.modules.misc;

import com.gamesense.api.event.events.DestroyBlockEvent;
import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.event.events.PlayerJumpEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Module.Declaration(name = "Announcer", category = Category.Misc)
public class Announcer extends Module {

    public BooleanSetting clientSide = registerBoolean("Client Side", false);
    BooleanSetting walk = registerBoolean("Walk", true);
    BooleanSetting place = registerBoolean("Place", true);
    BooleanSetting jump = registerBoolean("Jump", true);
    BooleanSetting breaking = registerBoolean("Breaking", true);
    BooleanSetting attack = registerBoolean("Attack", true);
    BooleanSetting eat = registerBoolean("Eat", true);
    public BooleanSetting clickGui = registerBoolean("GUI", true);
    IntegerSetting delay = registerInteger("Delay", 1, 1, 20);

    public static int blockBrokeDelay = 0;
    static int blockPlacedDelay = 0;
    static int jumpDelay = 0;
    static int attackDelay = 0;
    static int eattingDelay = 0;
    static long lastPositionUpdate;
    static double lastPositionX;
    static double lastPositionY;
    static double lastPositionZ;
    private static double speed;
    String heldItem = "";
    int blocksPlaced = 0;
    int blocksBroken = 0;
    int eaten = 0;

    public static String walkMessage = "I just walked{blocks} meters thanks to GameSense!";
    public static String placeMessage = "I just inserted{amount}{name} into the muliverse thanks to GameSense!";
    public static String jumpMessage = "I just hovered in the air thanks to GameSense!";
    public static String breakMessage = "I just snapped{amount}{name} out of existance thanks to GameSense!";
    public static String attackMessage = "I just disembowed{name} with a{item} thanks to GameSense!";
    public static String eatMessage = "I just gobbled up{amount}{name} thanks to GameSense!";
    public static String guiMessage = "I just opened my advanced hacking console thanks to GameSense!";

    //English, Arabic, Spanish, French, Hebrew, German, Japanese, Dutch, Greek, Turkish, Polish, Russian, Danish, Albanian, Chinese and Finnish.
    public static String[] walkMessages = {"I just walked{blocks} meters thanks to GameSense!", "!\u0644\u0642\u062f \u0645\u0634\u064a\u062a \u0644\u0644\u062a\u0648 \u0639\u0644\u0649 \u0628\u0639\u062f{blocks} \u0645\u062a\u0631 \u0645\u0646 \u0627\u0644\u0623\u0645\u062a\u0627\u0631 \u0628\u0641\u0636\u0644 GameSense!", "\u00a1Acabo de caminar{blocks} metros gracias a GameSense!", "Je viens de marcher{blocks} m\u00e8tres gr\u00e2ce \u00e0 GameSense!", "\u05e4\u05e9\u05d5\u05d8 \u05d4\u05dc\u05db\u05ea\u05d9{blocks} \u05de\u05d8\u05e8\u05d9\u05dd \u05d1\u05d6\u05db\u05d5\u05ea GameSense!", "Ich bin gerade{blocks} Meter dank GameSense gelaufen!\n", "GameSense\u306e\u304a\u304b\u3052\u3067{blocks}\u30e1\u30fc\u30c8\u30eb\u6b69\u3044\u305f\u3068\u3053\u308d\u3067\u3059!", "Ik heb net{blocks} gelopen met dank aan GameSense!", "\u039c\u03ce\u03bb\u03b9\u03c2 \u03c0\u03b5\u03c1\u03c0\u03ac\u03c4\u03b7\u03c3\u03b1{blocks} \u03bc\u03ad\u03c4\u03c1\u03b1 \u03c7\u03ac\u03c1\u03b7 \u03c4\u03bf GameSense!", "GameSense sayesinde{blocks} metre y\u00fcr\u00fcd\u00fcm!", "W\u0142a\u015bnie przeszed\u0142em{blocks} metry dzi\u0119ki GameSense!", "\u042f \u043f\u0440\u043e\u0441\u0442\u043e \u043f\u0440\u043e\u0448\u0435\u043b{blocks} \u043c\u0435\u0442\u0440\u043e\u0432 \u0431\u043b\u0430\u0433\u043e\u0434\u0430\u0440\u044f GameSense!", "Jeg gik lige{blocks} meter takket v\u00e6re GameSense!", "Un\u00eb vet\u00ebm eca{blocks} metra fal\u00eb GameSense!", "\u591a\u4e8f\u4e86GameSense\uff0c\u6211\u624d\u8d70\u4e86{blocks}\u7c73\uff01", "K\u00e4velin juuri{blocks} metri\u00e4 GameSensen ansiosta!"};
    public static String[] placeMessages = {"I just inserted{amount}{name} into the muliverse thanks to GameSense!", "\u0644\u0642\u062f \u0623\u062f\u0631\u062c\u062a \u0644\u0644\u062a\u0648{amount}{name} \u0641\u064a muliverse \u0628\u0641\u0636\u0644 GameSense!", "\u00a1Acabo de insertar{amount}{name} en el universo gracias a GameSense!", "Je viens d'ins\u00e9rer{amount}{name} dans le mulivers gr\u00e2ce \u00e0 GameSense!", "\u05d4\u05e8\u05d2\u05e2 \u05d4\u05db\u05e0\u05e1\u05ea\u05d9 \u05d0\u05ea{amount}{name} \u05dc\u05de\u05d5\u05dc\u05d9\u05d1\u05e8\u05e1 \u05d1\u05d6\u05db\u05d5\u05ea GameSense!", "Ich habe gerade dank GameSense{amount}{name} in das Multiversum eingefügt! \n", "GameSense\u306e\u304a\u304b\u3052\u3067\u3001{amount}{name}\u3092\u30de\u30eb\u30c1\u30d0\u30fc\u30b9\u306b\u633f\u5165\u3057\u307e\u3057\u305f\uff01", "Ik heb zojuist{amount}{name} in het muliversum ingevoegd dankzij GameSense!", "\u039c\u03ce\u03bb\u03b9\u03c2 \u03c7\u03c1\u03b7\u03c3\u03b9\u03bc\u03bf\u03c0\u03bf\u03b9\u03ae\u03c3\u03b1{amount}{name} \u03c7\u03ac\u03c1\u03b7 \u03c4\u03bf GameSense", "GameSense sayesinde birden fazla ki\u015fiye{amount}{name} ekledim!", "W\u0142a\u015bnie wstawi\u0142em{amount}{name} do wielu dzi\u0119ki GameSense!", "\u042f \u0442\u043e\u043b\u044c\u043a\u043e \u0447\u0442\u043e \u0432\u0441\u0442\u0430\u0432\u0438\u043b{amount}{name} \u0432\u043e \u0432\u0441\u0435\u043b\u0435\u043d\u043d\u0443\u044e \u0431\u043b\u0430\u0433\u043e\u0434\u0430\u0440\u044f GameSense!", "Jeg har lige indsat{amount}{name} i muliversen takket v\u00e6re GameSense!", "\u591a\u4e8f\u4e86GameSense\uff0c\u6211\u521a\u521a\u5c06{amount}{name}\u63d2\u5165\u4e86\u591a\u4eba\u6e38\u620f\uff01", "Un\u00eb vet\u00ebm futa{amount}{name} n\u00eb muliverse fal\u00eb GameSense!"};
    public static String[] jumpMessages = {"I just hovered in the air thanks to GameSense!", "\u0644\u0642\u062f \u062d\u0648\u0645\u062a \u0644\u0644\u062a\u0648 \u0641\u064a \u0627\u0644\u0647\u0648\u0627\u0621 \u0628\u0641\u0636\u0644 GameSense!", "\u00a1Acabo de volar en el aire gracias a GameSense!", "Je viens de planer dans les airs gr\u00e2ce \u00e0 GameSense!", "\u05e4\u05e9\u05d5\u05d8 \u05e8\u05d9\u05d7\u05e4\u05ea\u05d9 \u05d1\u05d0\u05d5\u05d5\u05d9\u05e8 \u05d1\u05d6\u05db\u05d5\u05ea GameSense!", "Ich habe gerade dank GameSense in der Luft geschwebt!\n", "GameSense\u306e\u304a\u304b\u3052\u3067\u5b99\u306b\u6d6e\u3044\u305f\u3060\u3051\u3067\u3059\uff01", "Dankzij GameSense zweefde ik gewoon in de lucht!", "\u039c\u03cc\u03bb\u03b9\u03c2 \u03b1\u03b9\u03c9\u03c1\u03ae\u03b8\u03b7\u03ba\u03b1 \u03c3\u03c4\u03bf\u03bd \u03b1\u03ad\u03c1\u03b1 \u03c7\u03ac\u03c1\u03b7\u03c2 \u03c4\u03bf GameSense!", "GameSense sayesinde havada as\u0131l\u0131 kald\u0131m!", "Po prostu unosi\u0142em si\u0119 w powietrzu dzi\u0119ki GameSense!", "\u042f \u043f\u0440\u043e\u0441\u0442\u043e \u0437\u0430\u0432\u0438\u0441 \u0432 \u0432\u043e\u0437\u0434\u0443\u0445\u0435 \u0431\u043b\u0430\u0433\u043e\u0434\u0430\u0440\u044f GameSense!", "Un\u00eb thjesht u fiksova n\u00eb aj\u00ebr fal\u00eb GameSense!", "\u591a\u4e8f\u4e86GameSense\uff0c\u6211\u624d\u5f98\u5f8a\u5728\u7a7a\u4e2d\uff01", "Min\u00e4 vain leijuin ilmassa GameSensen ansiosta!"};
    public static String[] breakMessages = {"I just snapped{amount}{name} out of existance thanks to GameSense!", "\u0644\u0642\u062f \u0642\u0637\u0639\u062a \u0644\u0644\u062a\u0648{amount}{name} \u0645\u0646 \u062e\u0627\u0631\u062c \u0628\u0641\u0636\u0644 GameSense!", "\u00a1Acabo de sacar{amount}{name} de la existencia gracias a GameSense!", "Je viens de casser{amount}{name} hors de l'existence gr\u00e2ce \u00e0 GameSense!", "\u05e4\u05e9\u05d5\u05d8 \u05d7\u05d8\u05e4\u05ea\u05d9 \u05d0\u05ea{amount}{name} \u05de\u05d4\u05d4\u05ea\u05e7\u05d9\u05d9\u05dd \u05d1\u05d6\u05db\u05d5\u05ea GameSense!", "Ich habe gerade{amount}{name} dank GameSense aus der Existenz gerissen!", "GameSense\u306e\u304a\u304b\u3052\u3067\u3001{amount}{name}\u304c\u5b58\u5728\u3057\u306a\u304f\u306a\u308a\u307e\u3057\u305f\u3002", "Ik heb zojuist{amount}{name} uit het bestaan \u200B\u200Bgehaald dankzij GameSense!", "\u039c\u03ce\u03bb\u03b9\u03c2 \u03ad\u03c3\u03c0\u03b1\u03c3\u03b1 \u03c4\u03bf{amount}{name} \u03b1\u03c0\u03cc \u03c4\u03b7\u03bd \u03cd\u03c0\u03b1\u03c1\u03be\u03b7 \u03c7\u03ac\u03c1\u03b7 \u03c3\u03c4\u03bf GameSense!", "GameSense sayesinde{amount}{name} varl\u0131\u011f\u0131n\u0131 yeni \u00e7\u0131kard\u0131m!", "W\u0142a\u015bnie wyskoczy\u0142em z gry dzi\u0119ki{amount}{name} dzi\u0119ki GameSense!", "\u042f \u0442\u043e\u043b\u044c\u043a\u043e \u0447\u0442\u043e \u043e\u0442\u043a\u043b\u044e\u0447\u0438\u043b{amount}{name} \u0438\u0437 \u0441\u0443\u0449\u0435\u0441\u0442\u0432\u043e\u0432\u0430\u043d\u0438\u044f \u0431\u043b\u0430\u0433\u043e\u0434\u0430\u0440\u044f GameSense!", "Jeg har lige sl\u00e5et{amount}{name} ud af eksistens takket v\u00e6re GameSense!", "\u591a\u4e8f\u4e86GameSense\uff0c\u6211\u624d\u5c06{amount}{name}\u6dd8\u6c70\u4e86\uff01", "Napsautin juuri{amount}{name} olemassaolosta GameSensen ansiosta!"};
    public static String[] eatMessages = {"I just ate{amount}{name} thanks to GameSense!", "\u0644\u0642\u062f \u0623\u0643\u0644\u062a \u0644\u0644\u062a\u0648{amount}{name} \u0628\u0641\u0636\u0644 GameSense!", "\u00a1Acabo de comer{amount}{name} gracias a GameSense!", "Je viens de manger{amount}{name} gr\u00e2ce \u00e0 GameSense!", "\u05e4\u05e9\u05d5\u05d8 \u05d0\u05db\u05dc\u05ea\u05d9{amount}{name} \u05d1\u05d6\u05db\u05d5\u05ea GameSense!", "Ich habe gerade dank GameSense{amount}{name} gegessen!", "GameSense\u306e\u304a\u304b\u3052\u3067{amount}{name}\u3092\u98df\u3079\u307e\u3057\u305f\u3002", "Ik heb zojuist{amount}{name} gegeten dankzij GameSense!", "\u039c\u03cc\u03bb\u03b9\u03c2 \u03ad\u03c6\u03b1\u03b3\u03b1{amount}{name} \u03c7\u03ac\u03c1\u03b7 \u03c3\u03c4\u03bf GameSense!", "GameSense sayesinde sadece{amount}{name} yedim!", "W\u0142a\u015bnie zjad\u0142em{amount}{name} dzi\u0119ki GameSense!", "Jeg spiste lige{amount}{name} takket v\u00e6re GameSense!", "\u042f \u0442\u043e\u043b\u044c\u043a\u043e \u0447\u0442\u043e \u0441\u044a\u0435\u043b{amount}{name} \u0431\u043b\u0430\u0433\u043e\u0434\u0430\u0440\u044f GameSense!", "Un\u00eb thjesht h\u00ebngra{amount}{name} fal\u00eb GameSense!", "\u611f\u8c22GameSense\uff0c\u6211\u521a\u5403\u4e86{amount}{name}\uff01", "S\u00f6in juuri{amount}{name} Gamessenin ansiosta!"};

    public void onUpdate() {
        blockBrokeDelay++;
        blockPlacedDelay++;
        jumpDelay++;
        attackDelay++;
        eattingDelay++;
        heldItem = mc.player.getHeldItemMainhand().getDisplayName();

        if (walk.getValue()) {
            if (lastPositionUpdate + (5000L * delay.getValue()) < System.currentTimeMillis()) {

                double d0 = lastPositionX - mc.player.lastTickPosX;
                double d2 = lastPositionY - mc.player.lastTickPosY;
                double d3 = lastPositionZ - mc.player.lastTickPosZ;


                speed = Math.sqrt(d0 * d0 + d2 * d2 + d3 * d3);

                if (!(speed <= 1) && !(speed > 5000)) {
                    String walkAmount = new DecimalFormat("0.00").format(speed);

                    Random random = new Random();
                    if (clientSide.getValue()) {
                        MessageBus.sendClientPrefixMessage(walkMessage.replace("{blocks}", " " + walkAmount));
                    } else {
                        MessageBus.sendServerMessage(walkMessages[random.nextInt(walkMessages.length)].replace("{blocks}", " " + walkAmount));
                    }
                    lastPositionUpdate = System.currentTimeMillis();
                    lastPositionX = mc.player.lastTickPosX;
                    lastPositionY = mc.player.lastTickPosY;
                    lastPositionZ = mc.player.lastTickPosZ;
                }
            }
        }

    }

    @EventHandler
    private final Listener<LivingEntityUseItemEvent.Finish> eatListener = new Listener<>(event -> {
        int randomNum = ThreadLocalRandom.current().nextInt(1, 10 + 1);
        if (event.getEntity() == mc.player) {
            if (event.getItem().getItem() instanceof ItemFood || event.getItem().getItem() instanceof ItemAppleGold) {
                eaten++;
                if (eattingDelay >= 300 * delay.getValue()) {
                    if (eat.getValue() && eaten > randomNum) {
                        Random random = new Random();
                        if (clientSide.getValue()) {
                            MessageBus.sendClientPrefixMessage(eatMessages[random.nextInt(eatMessages.length)].replace("{amount}", " " + eaten).replace("{name}", " " + mc.player.getHeldItemMainhand().getDisplayName()));
                        } else {
                            MessageBus.sendServerMessage(eatMessages[random.nextInt(eatMessages.length)].replace("{amount}", " " + eaten).replace("{name}", " " + mc.player.getHeldItemMainhand().getDisplayName()));
                        }
                        eaten = 0;
                        eattingDelay = 0;
                    }
                }
            }
        }
    });

    @EventHandler
    private final Listener<PacketEvent.Send> sendListener = new Listener<>(event -> {
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBlock) {
            blocksPlaced++;
            int randomNum = ThreadLocalRandom.current().nextInt(1, 10 + 1);
            if (blockPlacedDelay >= 150 * delay.getValue()) {
                if (place.getValue() && blocksPlaced > randomNum) {
                    Random random = new Random();
                    String msg = placeMessages[random.nextInt(placeMessages.length)].replace("{amount}", " " + blocksPlaced).replace("{name}", " " + mc.player.getHeldItemMainhand().getDisplayName());
                    if (clientSide.getValue()) {
                        MessageBus.sendClientPrefixMessage(msg);
                    } else {
                        MessageBus.sendServerMessage(msg);
                    }
                    blocksPlaced = 0;
                    blockPlacedDelay = 0;
                }
            }
        }
    });

    @EventHandler
    private final Listener<DestroyBlockEvent> destroyListener = new Listener<>(event -> {
        blocksBroken++;
        int randomNum = ThreadLocalRandom.current().nextInt(1, 10 + 1);
        if (blockBrokeDelay >= 300 * delay.getValue()) {
            if (breaking.getValue() && blocksBroken > randomNum) {
                Random random = new Random();
                String msg = breakMessages[random.nextInt(breakMessages.length)]
                        .replace("{amount}", " " + blocksBroken)
                        .replace("{name}", " " + mc.world.getBlockState(event.getBlockPos()).getBlock().getLocalizedName());
                if (clientSide.getValue()) {
                    MessageBus.sendClientPrefixMessage(msg);
                } else {
                    MessageBus.sendServerMessage(msg);
                }
                blocksBroken = 0;
                blockBrokeDelay = 0;
            }
        }
    });

    @EventHandler
    private final Listener<AttackEntityEvent> attackListener = new Listener<>(event -> {
        if (attack.getValue() && !(event.getTarget() instanceof EntityEnderCrystal)) {
            if (attackDelay >= 300 * delay.getValue()) {
                String msg = attackMessage.replace("{name}", " " + event.getTarget().getName()).replace("{item}", " " + mc.player.getHeldItemMainhand().getDisplayName());
                if (clientSide.getValue()) {
                    MessageBus.sendClientPrefixMessage(msg);
                } else {
                    MessageBus.sendServerMessage(msg);
                }
                attackDelay = 0;
            }
        }
    });

    @EventHandler
    private final Listener<PlayerJumpEvent> jumpListener = new Listener<>(event -> {
        if (jump.getValue()) {
            if (jumpDelay >= 300 * delay.getValue()) {
                if (clientSide.getValue()) {
                    Random random = new Random();
                    MessageBus.sendClientPrefixMessage(jumpMessages[random.nextInt(jumpMessages.length)]);
                } else {
                    Random random = new Random();
                    MessageBus.sendServerMessage(jumpMessages[random.nextInt(jumpMessages.length)]);
                }
                jumpDelay = 0;
            }
        }
    });

    public void onEnable() {
        blocksPlaced = 0;
        blocksBroken = 0;
        eaten = 0;
        speed = 0;
        blockBrokeDelay = 0;
        blockPlacedDelay = 0;
        jumpDelay = 0;
        attackDelay = 0;
        eattingDelay = 0;
    }
}