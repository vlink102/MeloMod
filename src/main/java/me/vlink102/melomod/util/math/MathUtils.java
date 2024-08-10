package me.vlink102.melomod.util.math;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class MathUtils {
    // yes  != for not equal to, == for equal to.

    public static double clamp(double t, double a, double b) {
        if (t <= a) return a;
        if (a < t && b > t) return t;
        return Math.min(b, t);
    }

    public static double[] getNearest(double xQ, double yQ, double zQ, double x0, double y0, double z0, double x1, double y1, double z1) {
        return new double[]{clamp(xQ, x0, x1), clamp(yQ, y0, y1), clamp(zQ, z0, z1)};
    }

    public static Vec3 nearest(double xQ, double yQ, double zQ, double x0, double y0, double z0, double x1, double y1, double z1) {
        double[] nearest = getNearest(xQ, yQ, zQ, x0, y0, z0, x1, y1, z1);
        return new Vec3(nearest[0], nearest[1], nearest[2]);
    }

    public static double[] getNearest(EntityPlayer player, BlockPos pos) {
        return getNearest(player.posX, player.posY, player.posZ, pos.getX() - 0.5, pos.getY() - 0.5, pos.getZ() - 0.5, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    public static Vec3 nearest(EntityPlayer player, BlockPos pos) {
        double[] nearest = getNearest(player, pos);
        return new Vec3(nearest[0], nearest[1], nearest[2]);
    }

    public static double getMinimalDistance(EntityPlayer player, BlockPos pos) {
        return Math.sqrt(Math.pow(Math.max(0, Math.abs(player.posX) - 0.5f), 2) + Math.pow(Math.max(0, Math.abs(player.posY) - 0.5f), 2) + Math.pow(Math.max(0, Math.abs(player.posZ) - 0.5f), 2));
    }

    public static double getMinimalDistance(double a1, double a2, double b1, double b2, double c1, double c2, double u, double v, double w) {
        double x = 0, y = 0, z = 0;
        if (u < a1) {
            x = a1;
        }
        if (a1 <= u && u <= a2) {
            x = u;
        }
        if (u > a2) {
            x = a2;
        }

        if (v < b1) {
            y = b1;
        }
        if (b1 <= v && v <= b2) {
            y = v;
        }
        if (v > b2) {
            y = b2;
        }

        if (w < c1) {
            z = c1;
        }
        if (c1 <= w && w <= c2) {
            z = w;
        }
        if (w > c2) {
            z = c2;
        }
        return Math.sqrt(Math.pow((x - u), 2) + Math.pow((y - v), 2) + Math.pow((z - w), 2));
    }

    public static double getMinimalDistance(Vec3 eyes, Block block, BlockPos pos) {
        AxisAlignedBB boundingBox = block.getSelectedBoundingBox(Minecraft.getMinecraft().theWorld, pos);
        return getMinimalDistance(boundingBox.minX, boundingBox.maxX, boundingBox.minY, boundingBox.maxY, boundingBox.minZ, boundingBox.maxZ, eyes.xCoord, eyes.yCoord, eyes.zCoord);
    }

    public static class MathEvaluator {

        public double calculate(String expression) {
            return eval(new Expression(expression));
        }

        public double eval(Expression expression) {
            return expression.parseFully(expression.getExpression());
        }

        private static class Expression {
            @Setter
            @Getter
            private String expression;
            private int pos = -1;
            private int character = -1;

            public Expression(String string) {
                this.expression = string;
            }

            private void next(String string) {
                character = (++pos < string.length() ? string.charAt(pos) : -1);
            }

            public boolean find(String string, int find) {
                while (character == ' ') next(string);
                if (character == find) {
                    next(string);
                    return true;
                }
                return false;
            }

            public double parseFully(String string) {
                next(string);
                return parseExpression(string);
            }

            double parseExpression(String string) {
                double x = parseTerm(string);
                while (true) {
                    if (find(string, '+')) {
                        x += parseTerm(string);
                    } else if (find(string, '-')) {
                        x -= parseTerm(string);
                    } else {
                        return x;
                    }
                }
            }

            double parseTerm(String string) {
                double x = parseFactor(string);
                while (true) {
                    if (find(string, '*')) {
                        x *= parseFactor(string);
                    } else if (find(string, '/')) {
                        x /= parseFactor(string);
                    } else {
                        return x;
                    }
                }
            }

            double parseFactor(String string) {
                if (find(string, '+')) {
                    return +parseFactor(string);
                }
                if (find(string, '-')) {
                    return -parseFactor(string);
                }

                double x = 0;
                int startPos = this.pos;
                if (find(string, '(')) {
                    x = parseExpression(string);
                    find(string, ')');
                } else if ((character >= '0' && character <= '9') || character == '.') {
                    while ((character >= '0' && character <= '9') || character == '.') {
                        next(string);
                    }
                    x = Double.parseDouble(string.substring(startPos, this.pos));
                } else if (character >= 'a' && character <= 'z') {
                    while (character >= 'a' && character <= 'z') {
                        next(string);
                    }
                    String func = string.substring(startPos, this.pos);
                    if (find(string, '(')) {
                        x = parseExpression(string);
                        find(string, ')');
                    } else {
                        x = parseFactor(string);
                    }
                    switch (func) {
                        case "sqrt":
                            x = Math.sqrt(x);
                            break;
                        case "sin":
                            x = Math.sin(Math.toRadians(x));
                            break;
                        case "cos":
                            x = Math.cos(Math.toRadians(x));
                            break;
                        case "tan":
                            x = Math.tan(Math.toRadians(x));
                            break;
                        default:
                            x = 0;
                    }
                }

                if (find(string, '^')) {
                    x = Math.pow(x, parseFactor(string));
                }

                return x;
            }
        }
    }
}
