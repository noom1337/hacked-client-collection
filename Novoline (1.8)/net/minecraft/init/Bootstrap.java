package net.minecraft.init;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.projectile.*;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.LoggingPrintStream;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintStream;
import java.util.Random;

public final class Bootstrap {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final PrintStream SYSOUT = System.out;

    /**
     * Whether the blocks, items, etc have already been registered
     */
    private static boolean alreadyRegistered = false;

    /**
     * Is Bootstrap registration already done?
     */
    public static boolean isRegistered() {
        return alreadyRegistered;
    }

    static void registerDispenserBehaviors() {
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.arrow, new BehaviorProjectileDispense() {

            protected IProjectile getProjectileEntity(World worldIn, IPosition position) {
                final EntityArrow entityarrow = new EntityArrow(worldIn, position.getX(), position.getY(), position.getZ());
                entityarrow.canBePickedUp = 1;
                return entityarrow;
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.egg, new BehaviorProjectileDispense() {

            protected IProjectile getProjectileEntity(World worldIn, IPosition position) {
                return new EntityEgg(worldIn, position.getX(), position.getY(), position.getZ());
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.snowball, new BehaviorProjectileDispense() {

            protected IProjectile getProjectileEntity(World worldIn, IPosition position) {
                return new EntitySnowball(worldIn, position.getX(), position.getY(), position.getZ());
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.experience_bottle, new BehaviorProjectileDispense() {

            protected IProjectile getProjectileEntity(World worldIn, IPosition position) {
                return new EntityExpBottle(worldIn, position.getX(), position.getY(), position.getZ());
            }

            protected float func_82498_a() {
                return super.func_82498_a() * 0.5F;
            }

            protected float func_82500_b() {
                return super.func_82500_b() * 1.25F;
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.potionitem, new IBehaviorDispenseItem() {

            private final BehaviorDefaultDispenseItem field_150843_b = new BehaviorDefaultDispenseItem();

            public ItemStack dispense(IBlockSource source, final ItemStack stack) {
                return ItemPotion.isSplash(stack.getMetadata()) ? new BehaviorProjectileDispense() {

                    protected IProjectile getProjectileEntity(World worldIn, IPosition position) {
                        return new EntityPotion(worldIn, position.getX(), position.getY(), position.getZ(), stack.copy());
                    }

                    protected float func_82498_a() {
                        return super.func_82498_a() * 0.5F;
                    }

                    protected float func_82500_b() {
                        return super.func_82500_b() * 1.25F;
                    }
                }.dispense(source, stack) : this.field_150843_b.dispense(source, stack);
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.spawn_egg, new BehaviorDefaultDispenseItem() {

            public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                final EnumFacing enumfacing = BlockDispenser.getFacing(source.getBlockMetadata());
                final double d0 = source.getX() + (double) enumfacing.getFrontOffsetX();
                final double d1 = (float) source.getBlockPos().getY() + 0.2F;
                final double d2 = source.getZ() + (double) enumfacing.getFrontOffsetZ();
                final Entity entity = ItemMonsterPlacer.spawnCreature(source.getWorld(), stack.getMetadata(), d0, d1, d2);

                if (entity instanceof EntityLivingBase && stack.hasDisplayName()) {
                    entity.setCustomNameTag(stack.getDisplayName());
                }

                stack.splitStack(1);
                return stack;
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.fireworks, new BehaviorDefaultDispenseItem() {

            public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                final EnumFacing enumfacing = BlockDispenser.getFacing(source.getBlockMetadata());
                final double d0 = source.getX() + (double) enumfacing.getFrontOffsetX();
                final double d1 = (float) source.getBlockPos().getY() + 0.2F;
                final double d2 = source.getZ() + (double) enumfacing.getFrontOffsetZ();
                final EntityFireworkRocket entityfireworkrocket = new EntityFireworkRocket(source.getWorld(), d0, d1, d2, stack);
                source.getWorld().spawnEntityInWorld(entityfireworkrocket);
                stack.splitStack(1);
                return stack;
            }

            protected void playDispenseSound(IBlockSource source) {
                source.getWorld().playAuxSFX(1002, source.getBlockPos(), 0);
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.fire_charge, new BehaviorDefaultDispenseItem() {

            public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                final EnumFacing enumfacing = BlockDispenser.getFacing(source.getBlockMetadata());
                final IPosition iposition = BlockDispenser.getDispensePosition(source);
                final double d0 = iposition.getX() + (double) ((float) enumfacing.getFrontOffsetX() * 0.3F);
                final double d1 = iposition.getY() + (double) ((float) enumfacing.getFrontOffsetY() * 0.3F);
                final double d2 = iposition.getZ() + (double) ((float) enumfacing.getFrontOffsetZ() * 0.3F);
                final World world = source.getWorld();
                final Random random = world.rand;
                final double d3 = random.nextGaussian() * 0.05D + (double) enumfacing.getFrontOffsetX();
                final double d4 = random.nextGaussian() * 0.05D + (double) enumfacing.getFrontOffsetY();
                final double d5 = random.nextGaussian() * 0.05D + (double) enumfacing.getFrontOffsetZ();
                world.spawnEntityInWorld(new EntitySmallFireball(world, d0, d1, d2, d3, d4, d5));
                stack.splitStack(1);
                return stack;
            }

            protected void playDispenseSound(IBlockSource source) {
                source.getWorld().playAuxSFX(1009, source.getBlockPos(), 0);
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.boat, new BehaviorDefaultDispenseItem() {

            private final BehaviorDefaultDispenseItem field_150842_b = new BehaviorDefaultDispenseItem();

            public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                final EnumFacing enumfacing = BlockDispenser.getFacing(source.getBlockMetadata());
                final World world = source.getWorld();
                final double d0 = source.getX() + (double) ((float) enumfacing.getFrontOffsetX() * 1.125F);
                final double d1 = source.getY() + (double) ((float) enumfacing.getFrontOffsetY() * 1.125F);
                final double d2 = source.getZ() + (double) ((float) enumfacing.getFrontOffsetZ() * 1.125F);
                final BlockPos blockpos = source.getBlockPos().offset(enumfacing);
                final Material material = world.getBlockState(blockpos).getBlock().getMaterial();
                final double d3;

                if (Material.water.equals(material)) {
                    d3 = 1.0D;
                } else {
                    if (!Material.air.equals(material) || !Material.water.equals(world.getBlockState(blockpos.down()).getBlock().getMaterial())) {
                        return this.field_150842_b.dispense(source, stack);
                    }

                    d3 = 0.0D;
                }

                final EntityBoat entityboat = new EntityBoat(world, d0, d1 + d3, d2);
                world.spawnEntityInWorld(entityboat);
                stack.splitStack(1);
                return stack;
            }

            protected void playDispenseSound(IBlockSource source) {
                source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
            }
        });
        final IBehaviorDispenseItem ibehaviordispenseitem = new BehaviorDefaultDispenseItem() {

            private final BehaviorDefaultDispenseItem field_150841_b = new BehaviorDefaultDispenseItem();

            public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                final ItemBucket itembucket = (ItemBucket) stack.getItem();
                final BlockPos blockpos = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));

                if (itembucket.tryPlaceContainedLiquid(source.getWorld(), blockpos)) {
                    stack.setItem(Items.bucket);
                    stack.stackSize = 1;
                    return stack;
                } else {
                    return this.field_150841_b.dispense(source, stack);
                }
            }
        };
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.lava_bucket, ibehaviordispenseitem);
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.water_bucket, ibehaviordispenseitem);
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.bucket, new BehaviorDefaultDispenseItem() {

            private final BehaviorDefaultDispenseItem field_150840_b = new BehaviorDefaultDispenseItem();

            public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                final World world = source.getWorld();
                final BlockPos blockpos = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));
                final IBlockState iblockstate = world.getBlockState(blockpos);
                final Block block = iblockstate.getBlock();
                final Material material = block.getMaterial();
                final Item item;

                if (Material.water.equals(material) && block instanceof BlockLiquid && iblockstate.getValue(BlockLiquid.LEVEL) == 0) {
                    item = Items.water_bucket;
                } else {
                    if (!Material.lava.equals(material) || !(block instanceof BlockLiquid) || iblockstate.getValue(BlockLiquid.LEVEL) != 0) {
                        return super.dispenseStack(source, stack);
                    }

                    item = Items.lava_bucket;
                }

                world.setBlockToAir(blockpos);

                if (--stack.stackSize == 0) {
                    stack.setItem(item);
                    stack.stackSize = 1;
                } else if (((TileEntityDispenser) source.getBlockTileEntity()).addItemStack(new ItemStack(item)) < 0) {
                    this.field_150840_b.dispense(source, new ItemStack(item));
                }

                return stack;
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.flint_and_steel, new BehaviorDefaultDispenseItem() {

            private boolean field_150839_b = true;

            protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                final World world = source.getWorld();
                final BlockPos blockpos = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));

                if (world.isAirBlock(blockpos)) {
                    world.setBlockState(blockpos, Blocks.fire.getDefaultState());

                    if (stack.attemptDamageItem(1, world.rand)) {
                        stack.stackSize = 0;
                    }
                } else if (world.getBlockState(blockpos).getBlock() == Blocks.tnt) {
                    Blocks.tnt.onBlockDestroyedByPlayer(world, blockpos, Blocks.tnt.getDefaultState().withProperty(BlockTNT.EXPLODE, Boolean.TRUE));
                    world.setBlockToAir(blockpos);
                } else {
                    this.field_150839_b = false;
                }

                return stack;
            }

            protected void playDispenseSound(IBlockSource source) {
                if (this.field_150839_b) {
                    source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
                } else {
                    source.getWorld().playAuxSFX(1001, source.getBlockPos(), 0);
                }
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.dye, new BehaviorDefaultDispenseItem() {

            private boolean field_150838_b = true;

            protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                if (EnumDyeColor.WHITE == EnumDyeColor.byDyeDamage(stack.getMetadata())) {
                    final World world = source.getWorld();
                    final BlockPos blockpos = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));

                    if (ItemDye.applyBonemeal(stack, world, blockpos)) {
                        if (!world.isRemote) {
                            world.playAuxSFX(2005, blockpos, 0);
                        }
                    } else {
                        this.field_150838_b = false;
                    }

                    return stack;
                } else {
                    return super.dispenseStack(source, stack);
                }
            }

            protected void playDispenseSound(IBlockSource source) {
                if (this.field_150838_b) {
                    source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
                } else {
                    source.getWorld().playAuxSFX(1001, source.getBlockPos(), 0);
                }
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(Blocks.tnt), new BehaviorDefaultDispenseItem() {

            protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                final World world = source.getWorld();
                final BlockPos blockpos = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));
                final EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, (double) blockpos.getX() + 0.5D, blockpos.getY(), (double) blockpos.getZ() + 0.5D, null);
                world.spawnEntityInWorld(entitytntprimed);
                world.playSoundAtEntity(entitytntprimed, "game.tnt.primed", 1.0F, 1.0F);
                --stack.stackSize;
                return stack;
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.skull, new BehaviorDefaultDispenseItem() {

            private boolean field_179240_b = true;

            protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                final World world = source.getWorld();
                final EnumFacing enumfacing = BlockDispenser.getFacing(source.getBlockMetadata());
                final BlockPos blockpos = source.getBlockPos().offset(enumfacing);
                final BlockSkull blockskull = Blocks.skull;

                if (world.isAirBlock(blockpos) && blockskull.canDispenserPlace(world, blockpos, stack)) {
                    if (!world.isRemote) {
                        world.setBlockState(blockpos, blockskull.getDefaultState().withProperty(BlockSkull.FACING, EnumFacing.UP), 3);
                        final TileEntity tileentity = world.getTileEntity(blockpos);

                        if (tileentity instanceof TileEntitySkull) {
                            if (stack.getMetadata() == 3) {
                                GameProfile gameprofile = null;

                                if (stack.hasTagCompound()) {
                                    final NBTTagCompound nbttagcompound = stack.getTagCompound();

                                    if (nbttagcompound.hasKey("SkullOwner", 10)) {
                                        gameprofile = NBTUtil.readGameProfileFromNBT(nbttagcompound.getCompoundTag("SkullOwner"));
                                    } else if (nbttagcompound.hasKey("SkullOwner", 8)) {
                                        final String s = nbttagcompound.getString("SkullOwner");

                                        if (!StringUtils.isNullOrEmpty(s)) {
                                            gameprofile = new GameProfile(null, s);
                                        }
                                    }
                                }

                                ((TileEntitySkull) tileentity).setPlayerProfile(gameprofile);
                            } else {
                                ((TileEntitySkull) tileentity).setType(stack.getMetadata());
                            }

                            ((TileEntitySkull) tileentity).setSkullRotation(enumfacing.getOpposite().getHorizontalIndex() * 4);
                            Blocks.skull.checkWitherSpawn(world, blockpos, (TileEntitySkull) tileentity);
                        }

                        --stack.stackSize;
                    }
                } else {
                    this.field_179240_b = false;
                }

                return stack;
            }

            protected void playDispenseSound(IBlockSource source) {
                if (this.field_179240_b) {
                    source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
                } else {
                    source.getWorld().playAuxSFX(1001, source.getBlockPos(), 0);
                }
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(Blocks.pumpkin), new BehaviorDefaultDispenseItem() {

            private boolean field_179241_b = true;

            protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                final World world = source.getWorld();
                final BlockPos blockpos = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));
                final BlockPumpkin blockpumpkin = (BlockPumpkin) Blocks.pumpkin;

                if (world.isAirBlock(blockpos) && blockpumpkin.canDispenserPlace(world, blockpos)) {
                    if (!world.isRemote) {
                        world.setBlockState(blockpos, blockpumpkin.getDefaultState(), 3);
                    }

                    --stack.stackSize;
                } else {
                    this.field_179241_b = false;
                }

                return stack;
            }

            protected void playDispenseSound(IBlockSource source) {
                if (this.field_179241_b) {
                    source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
                } else {
                    source.getWorld().playAuxSFX(1001, source.getBlockPos(), 0);
                }
            }
        });
    }

    /**
     * Registers blocks, items, stats, etc.
     */
    public static void register() {
        if (!alreadyRegistered) {
            alreadyRegistered = true;

            if (LOGGER.isDebugEnabled()) {
                redirectOutputToLog();
            }

            Block.registerBlocks();
            BlockFire.init();
            Item.registerItems();
            StatList.init();
            registerDispenserBehaviors();
        }
    }

    /**
     * redirect standard streams to LOGGER
     */
    private static void redirectOutputToLog() {
        System.setErr(new LoggingPrintStream("STDERR", System.err));
        System.setOut(new LoggingPrintStream("STDOUT", SYSOUT));
    }

    public static void printToSYSOUT(String s) {
        SYSOUT.println(s);
    }

}
