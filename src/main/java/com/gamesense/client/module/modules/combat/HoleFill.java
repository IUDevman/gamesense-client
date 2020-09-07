package com.gamesense.client.module.modules.combat;

import com.gamesense.api.settings.Setting;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HoleFill extends Module{
	public HoleFill(){
		super("HoleFill", Category.Combat);
	}

	private ArrayList<BlockPos> holes = new ArrayList();

	private final List<Block> obbyonly = Arrays.asList(Blocks.OBSIDIAN);

	private final List<Block> bothonly = Arrays.asList(Blocks.OBSIDIAN,
			Blocks.ENDER_CHEST);

	private final List<Block> echestonly = Arrays.asList(Blocks.ENDER_CHEST);

	private final List<Block> webonly = Arrays.asList(Blocks.WEB);

	private List<Block> list = Arrays.asList(new Block[]{
	});

	Setting.Double range;
	Setting.Integer yRange;
	Setting.Integer waitTick;
	Setting.Boolean chat;
	Setting.Boolean rotate;
	Setting.Mode type;

	BlockPos pos;
	private int waitCounter;

	public void setup(){
		ArrayList<String> blockmode = new ArrayList<>();
		blockmode.add("Obby");
		blockmode.add("EChest");
		blockmode.add("Both");
		blockmode.add("Web");
		type = registerMode("Block", "BlockMode", blockmode, "Obby");
		range = registerDouble("Place Range", "PlaceRange", 5, 0, 10);
		yRange = registerInteger("Y Range", "YRange", 2 , 0 ,10);
		waitTick = registerInteger("Tick Delay", "TickDelay", 1 , 0, 20);
		rotate = registerBoolean("Rotate", "Rotate", false);
		chat = registerBoolean("Toggle Msg", "ToggleMsg", false);
	}

	public void onUpdate(){
		holes = new ArrayList();
		if (type.getValue().equalsIgnoreCase("Obby")){
			list = obbyonly;
		}
		if (type.getValue().equalsIgnoreCase("EChest")){
			list = echestonly;
		}
		if (type.getValue().equalsIgnoreCase("Both")){
			list = bothonly;
		}
		if (type.getValue().equalsIgnoreCase("Web")){
			list = webonly;
		}
		Iterable<BlockPos> blocks = BlockPos.getAllInBox(mc.player.getPosition().add(-range.getValue(), -yRange.getValue(), -range.getValue()), mc.player.getPosition().add(range.getValue(), yRange.getValue(), range.getValue()));
		for (BlockPos pos : blocks){
			if (!mc.world.getBlockState(pos).getMaterial().blocksMovement() && !mc.world.getBlockState(pos.add(0, 1, 0)).getMaterial().blocksMovement()){
				boolean solidNeighbours = (
						mc.world.getBlockState(pos.add(1, 0, 0)).getBlock() == Blocks.BEDROCK | mc.world.getBlockState(pos.add(1, 0, 0)).getBlock() == Blocks.OBSIDIAN
								&& mc.world.getBlockState(pos.add(0, 0, 1)).getBlock() == Blocks.BEDROCK | mc.world.getBlockState(pos.add(0, 0, 1)).getBlock() == Blocks.OBSIDIAN
								&& mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock() == Blocks.BEDROCK | mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock() == Blocks.OBSIDIAN
								&& mc.world.getBlockState(pos.add(0, 0, -1)).getBlock() == Blocks.BEDROCK | mc.world.getBlockState(pos.add(0, 0, -1)).getBlock() == Blocks.OBSIDIAN
								&& mc.world.getBlockState(pos.add(0, 0, 0)).getMaterial() == Material.AIR
								&& mc.world.getBlockState(pos.add(0, 1, 0)).getMaterial() == Material.AIR
								&& mc.world.getBlockState(pos.add(0, 2, 0)).getMaterial() == Material.AIR);
				if (solidNeighbours){
					this.holes.add(pos);
				}
			}
		}

		// search blocks in hotbar
		int newSlot = -1;
		for (int i = 0; i < 9; i++){
			// filter out non-block items
			ItemStack stack =
					mc.player.inventory.getStackInSlot(i);

			if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)){
				continue;
			}
			// only use whitelisted blocks
			Block block = ((ItemBlock) stack.getItem()).getBlock();
			if (!list.contains(block)){
				continue;
			}

			newSlot = i;
			break;
		}

		// check if any blocks were found
		if (newSlot == -1)
			return;

		// set slot
		int oldSlot = mc.player.inventory.currentItem;
		//	Wrapper.getPlayer().inventory.currentItem = newSlot;

		if (waitTick.getValue() > 0){
			if (waitCounter < waitTick.getValue()){
				//  waitCounter++;
				mc.player.inventory.currentItem = newSlot;
				holes.forEach(this::place);
				mc.player.inventory.currentItem = oldSlot;
				return;
			} else{
				waitCounter = 0;
			}
		}
	}

	public void onEnable(){
		if (mc.player != null && chat.getValue()) Command.sendRawMessage("\u00A7aHolefill turned ON!");
	}

	public void onDisable(){
		if (mc.player != null && chat.getValue()) Command.sendRawMessage("\u00A7cHolefill turned OFF!");
	}

	private void place(BlockPos blockPos){
		//if(mc.player.getDistanceSq(blockPos) <= minRange.getValue()) return;
		for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(blockPos))){
			if (entity instanceof EntityLivingBase){
				return;
			}
		}// entity on block
		placeBlockScaffold(blockPos, rotate.getValue());
		waitCounter++;
	}

	public static boolean placeBlockScaffold(BlockPos pos, boolean rotate){
		Vec3d eyesPos = new Vec3d(mc.player.posX,
				mc.player.posY + mc.player.getEyeHeight(),
				mc.player.posZ);

		for(EnumFacing side : EnumFacing.values())
		{
			BlockPos neighbor = pos.offset(side);
			EnumFacing side2 = side.getOpposite();

			if(!canBeClicked(neighbor))
				continue;

			Vec3d hitVec = new Vec3d(neighbor).add(0.5, 0.5, 0.5)
					.add(new Vec3d(side2.getDirectionVec()).scale(0.5));

			if(rotate)
				faceVectorPacketInstant(hitVec);
			mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
			processRightClickBlock(neighbor, side2, hitVec);
			mc.player.swingArm(EnumHand.MAIN_HAND);
			mc.rightClickDelayTimer = 0;
			mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

			return true;
		}

		return false;
	}

	public static boolean canBeClicked(BlockPos pos)
	{
		return getBlock(pos).canCollideCheck(getState(pos), false);
	}

	public static IBlockState getState(BlockPos pos)
	{
		return mc.world.getBlockState(pos);
	}

	public static Block getBlock(BlockPos pos)
	{
		return getState(pos).getBlock();
	}

	public static void faceVectorPacketInstant(Vec3d vec)
	{
		float[] rotations = getNeededRotations2(vec);

		mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotations[0],
				rotations[1], mc.player.onGround));
	}

	private static float[] getNeededRotations2(Vec3d vec)
	{
		Vec3d eyesPos = getEyesPos();

		double diffX = vec.x - eyesPos.x;
		double diffY = vec.y - eyesPos.y;
		double diffZ = vec.z - eyesPos.z;

		double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

		float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
		float pitch = (float)-Math.toDegrees(Math.atan2(diffY, diffXZ));

		return new float[]{
				mc.player.rotationYaw
						+ MathHelper.wrapDegrees(yaw - mc.player.rotationYaw),
				mc.player.rotationPitch + MathHelper
						.wrapDegrees(pitch - mc.player.rotationPitch)};
	}

	public static Vec3d getEyesPos()
	{
		return new Vec3d(mc.player.posX,
				mc.player.posY + mc.player.getEyeHeight(),
				mc.player.posZ);
	}

	public static void processRightClickBlock(BlockPos pos, EnumFacing side,
											  Vec3d hitVec)
	{
		getPlayerController().processRightClickBlock(mc.player,
				mc.world, pos, side, hitVec, EnumHand.MAIN_HAND);
	}

	private static PlayerControllerMP getPlayerController()
	{
		return mc.playerController;
	}
}