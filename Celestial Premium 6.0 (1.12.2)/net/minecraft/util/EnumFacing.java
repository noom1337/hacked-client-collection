/*
 * Decompiled with CFR 0.150.
 */
package net.minecraft.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

public enum EnumFacing implements IStringSerializable
{
    DOWN(0, 1, -1, "down", AxisDirection.NEGATIVE, Axis.Y, new Vec3i(0, -1, 0)),
    UP(1, 0, -1, "up", AxisDirection.POSITIVE, Axis.Y, new Vec3i(0, 1, 0)),
    NORTH(2, 3, 2, "north", AxisDirection.NEGATIVE, Axis.Z, new Vec3i(0, 0, -1)),
    SOUTH(3, 2, 0, "south", AxisDirection.POSITIVE, Axis.Z, new Vec3i(0, 0, 1)),
    WEST(4, 5, 1, "west", AxisDirection.NEGATIVE, Axis.X, new Vec3i(-1, 0, 0)),
    EAST(5, 4, 3, "east", AxisDirection.POSITIVE, Axis.X, new Vec3i(1, 0, 0));

    private final int index;
    private final int opposite;
    private final int horizontalIndex;
    private final String name;
    private final Axis axis;
    private final AxisDirection axisDirection;
    private final Vec3i directionVec;
    public static final EnumFacing[] VALUES;
    private static final EnumFacing[] HORIZONTALS;
    private static final Map<String, EnumFacing> NAME_LOOKUP;

    private EnumFacing(int indexIn, int oppositeIn, int horizontalIndexIn, String nameIn, AxisDirection axisDirectionIn, Axis axisIn, Vec3i directionVecIn) {
        this.index = indexIn;
        this.horizontalIndex = horizontalIndexIn;
        this.opposite = oppositeIn;
        this.name = nameIn;
        this.axis = axisIn;
        this.axisDirection = axisDirectionIn;
        this.directionVec = directionVecIn;
    }

    public int getIndex() {
        return this.index;
    }

    public int getHorizontalIndex() {
        return this.horizontalIndex;
    }

    public AxisDirection getAxisDirection() {
        return this.axisDirection;
    }

    public EnumFacing getOpposite() {
        return VALUES[this.opposite];
    }

    public EnumFacing rotateAround(Axis axis) {
        switch (axis) {
            case X: {
                if (this != WEST && this != EAST) {
                    return this.rotateX();
                }
                return this;
            }
            case Y: {
                if (this != UP && this != DOWN) {
                    return this.rotateY();
                }
                return this;
            }
            case Z: {
                if (this != NORTH && this != SOUTH) {
                    return this.rotateZ();
                }
                return this;
            }
        }
        throw new IllegalStateException("Unable to get CW facing for axis " + axis);
    }

    public EnumFacing rotateY() {
        switch (this) {
            case NORTH: {
                return EAST;
            }
            case EAST: {
                return SOUTH;
            }
            case SOUTH: {
                return WEST;
            }
            case WEST: {
                return NORTH;
            }
        }
        throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
    }

    private EnumFacing rotateX() {
        switch (this) {
            case NORTH: {
                return DOWN;
            }
            default: {
                throw new IllegalStateException("Unable to get X-rotated facing of " + this);
            }
            case SOUTH: {
                return UP;
            }
            case UP: {
                return NORTH;
            }
            case DOWN: 
        }
        return SOUTH;
    }

    private EnumFacing rotateZ() {
        switch (this) {
            case EAST: {
                return DOWN;
            }
            default: {
                throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
            }
            case WEST: {
                return UP;
            }
            case UP: {
                return EAST;
            }
            case DOWN: 
        }
        return WEST;
    }

    public EnumFacing rotateYCCW() {
        switch (this) {
            case NORTH: {
                return WEST;
            }
            case EAST: {
                return NORTH;
            }
            case SOUTH: {
                return EAST;
            }
            case WEST: {
                return SOUTH;
            }
        }
        throw new IllegalStateException("Unable to get CCW facing of " + this);
    }

    public int getXOffset() {
        return this.axis == Axis.X ? this.axisDirection.getOffset() : 0;
    }

    public int getYOffset() {
        return this.axis == Axis.Y ? this.axisDirection.getOffset() : 0;
    }

    public int getZOffset() {
        return this.axis == Axis.Z ? this.axisDirection.getOffset() : 0;
    }

    public String getName2() {
        return this.name;
    }

    public Axis getAxis() {
        return this.axis;
    }

    @Nullable
    public static EnumFacing byName(String name) {
        return name == null ? null : NAME_LOOKUP.get(name.toLowerCase(Locale.ROOT));
    }

    public static EnumFacing getFront(int index) {
        return VALUES[MathHelper.abs(index % VALUES.length)];
    }

    public static EnumFacing byHorizontalIndex(int horizontalIndexIn) {
        return HORIZONTALS[MathHelper.abs(horizontalIndexIn % HORIZONTALS.length)];
    }

    public static EnumFacing fromAngle(double angle) {
        return EnumFacing.byHorizontalIndex(MathHelper.floor(angle / 90.0 + 0.5) & 3);
    }

    public float getHorizontalAngle() {
        return (this.horizontalIndex & 3) * 90;
    }

    public static EnumFacing random(Random rand) {
        return EnumFacing.values()[rand.nextInt(EnumFacing.values().length)];
    }

    public static EnumFacing getFacingFromVector(float x, float y, float z) {
        EnumFacing enumfacing = NORTH;
        float f = Float.MIN_VALUE;
        for (EnumFacing enumfacing1 : EnumFacing.values()) {
            float f1 = x * (float)enumfacing1.directionVec.getX() + y * (float)enumfacing1.directionVec.getY() + z * (float)enumfacing1.directionVec.getZ();
            if (!(f1 > f)) continue;
            f = f1;
            enumfacing = enumfacing1;
        }
        return enumfacing;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public static EnumFacing getFacingFromAxis(AxisDirection axisDirectionIn, Axis axisIn) {
        for (EnumFacing enumfacing : EnumFacing.values()) {
            if (enumfacing.getAxisDirection() != axisDirectionIn || enumfacing.getAxis() != axisIn) continue;
            return enumfacing;
        }
        throw new IllegalArgumentException("No such direction: " + (Object)((Object)axisDirectionIn) + " " + axisIn);
    }

    public static EnumFacing func_190914_a(BlockPos p_190914_0_, EntityLivingBase p_190914_1_) {
        if (Math.abs(p_190914_1_.posX - (double)((float)p_190914_0_.getX() + 0.5f)) < 2.0 && Math.abs(p_190914_1_.posZ - (double)((float)p_190914_0_.getZ() + 0.5f)) < 2.0) {
            double d0 = p_190914_1_.posY + (double)p_190914_1_.getEyeHeight();
            if (d0 - (double)p_190914_0_.getY() > 2.0) {
                return UP;
            }
            if ((double)p_190914_0_.getY() - d0 > 0.0) {
                return DOWN;
            }
        }
        return p_190914_1_.getHorizontalFacing().getOpposite();
    }

    public Vec3i getDirectionVec() {
        return this.directionVec;
    }

    static {
        VALUES = new EnumFacing[6];
        HORIZONTALS = new EnumFacing[4];
        NAME_LOOKUP = Maps.newHashMap();
        EnumFacing[] arrenumFacing = EnumFacing.values();
        int n = arrenumFacing.length;
        for (int i = 0; i < n; ++i) {
            EnumFacing enumfacing;
            EnumFacing.VALUES[enumfacing.index] = enumfacing = arrenumFacing[i];
            if (enumfacing.getAxis().isHorizontal()) {
                EnumFacing.HORIZONTALS[enumfacing.horizontalIndex] = enumfacing;
            }
            NAME_LOOKUP.put(enumfacing.getName2().toLowerCase(Locale.ROOT), enumfacing);
        }
    }

    public static enum Plane implements Predicate<EnumFacing>,
    Iterable<EnumFacing>
    {
        HORIZONTAL,
        VERTICAL;


        public EnumFacing[] facings() {
            switch (this) {
                case HORIZONTAL: {
                    return new EnumFacing[]{NORTH, EAST, SOUTH, WEST};
                }
                case VERTICAL: {
                    return new EnumFacing[]{UP, DOWN};
                }
            }
            throw new Error("Someone's been tampering with the universe!");
        }

        public EnumFacing random(Random rand) {
            EnumFacing[] aenumfacing = this.facings();
            return aenumfacing[rand.nextInt(aenumfacing.length)];
        }

        @Override
        public boolean apply(@Nullable EnumFacing p_apply_1_) {
            return p_apply_1_ != null && p_apply_1_.getAxis().getPlane() == this;
        }

        @Override
        public Iterator<EnumFacing> iterator() {
            return Iterators.forArray(this.facings());
        }
    }

    public static enum AxisDirection {
        POSITIVE(1, "Towards positive"),
        NEGATIVE(-1, "Towards negative");

        private final int offset;
        private final String description;

        private AxisDirection(int offset, String description) {
            this.offset = offset;
            this.description = description;
        }

        public int getOffset() {
            return this.offset;
        }

        public String toString() {
            return this.description;
        }
    }

    public static enum Axis implements Predicate<EnumFacing>,
    IStringSerializable
    {
        X("x", Plane.HORIZONTAL),
        Y("y", Plane.VERTICAL),
        Z("z", Plane.HORIZONTAL);

        private static final Map<String, Axis> NAME_LOOKUP;
        private final String name;
        private final Plane plane;

        private Axis(String name, Plane plane) {
            this.name = name;
            this.plane = plane;
        }

        @Nullable
        public static Axis byName(String name) {
            return name == null ? null : NAME_LOOKUP.get(name.toLowerCase(Locale.ROOT));
        }

        public String getName2() {
            return this.name;
        }

        public boolean isVertical() {
            return this.plane == Plane.VERTICAL;
        }

        public boolean isHorizontal() {
            return this.plane == Plane.HORIZONTAL;
        }

        public String toString() {
            return this.name;
        }

        @Override
        public boolean apply(@Nullable EnumFacing p_apply_1_) {
            return p_apply_1_ != null && p_apply_1_.getAxis() == this;
        }

        public Plane getPlane() {
            return this.plane;
        }

        @Override
        public String getName() {
            return this.name;
        }

        static {
            NAME_LOOKUP = Maps.newHashMap();
            for (Axis enumfacing$axis : Axis.values()) {
                NAME_LOOKUP.put(enumfacing$axis.getName2().toLowerCase(Locale.ROOT), enumfacing$axis);
            }
        }
    }
}

