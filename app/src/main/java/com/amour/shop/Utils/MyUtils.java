package com.amour.shop.Utils;

class MyUtils {

    private static Object[] ch;

//    private static final int zdoHivtcH(int var0, int var1) {
//        boolean var5 = false;
//        int var2 = var0;
//        int var3 = var1;
//        int var4 = var1 + var0 >> 24;
//        var5 = false;
//        Object var10000 = null;
//
//        while (true) {
//            try {
//                if (!var5) {
//                    var5 = true;
//                    var3 = var0 >>> var1 | var0 << -var1;
//                    return var3;
//                }
//            } catch (Exception var7) {
//                continue;
//            }
//
//            byte var8 = 0;
//            var10000 = null;
//
//            while (true) {
//                try {
//                    if (var8 == 0) {
//                        int var9 = var8 + 1;
//                        var3 = var2 + var4;
//                    }
//
//                    return var3;
//                } catch (Exception var6) {
//                }
//            }
//        }
//    }
//
//    private static final int bvxwJ(byte[] var0, int var1) {
//        boolean var4 = false;
//        int var2 = var0[14] << 16;
//        var4 = false;
//        Object var10000 = null;
//
//        while (true) {
//            try {
//                if (!var4) {
//                    var4 = true;
//                    var2 = var0[var1 & 255] & 255 | (var0[var1 >> 8 & 255] & 255) << 8 | (var0[var1 >> 16 & 255] & 255) << 16 | var0[var1 >> 24 & 255] << 24;
//                    return var2;
//                }
//            } catch (Exception var6) {
//                continue;
//            }
//
//            var4 = false;
//            var10000 = null;
//
//            while (true) {
//                try {
//                    if (!var4) {
//                        var4 = true;
//                        var2 = var0[var1 & 127] >> 8;
//                    }
//
//                    return var2;
//                } catch (Exception var5) {
//                }
//            }
//        }
//    }
//
//    private static final void jxhaJHAGJ28382() {
//        boolean var15 = false;
//        boolean var16 = false;
//        int[] var0 = new int[256];
//        byte[] var1 = new byte[256];
//        int[] var2 = new int[256];
//        int[] var3 = new int[256];
//        int[] var4 = new int[256];
//        int[] var5 = new int[256];
//        int[] var6 = new int[30];
//        long var7 = 9223372036854775807L;
//        int var9 = 0;
//
//        int var10;
//        for (var10 = 1; var9 < 256; ++var9) {
//            var0[var9] = var10;
//            var10 ^= var10 << 1 ^ (var10 >>> 7) * 283;
//        }
//
//        var1[0] = 99;
//        var15 = false;
//        Throwable var10000 = null;
//
//        while (true) {
//            try {
//                if (!var15) {
//                    var15 = true;
//
//                    for (var10 = 0; var10 < 255; ++var10) {
//                        var9 = var0[255 - var10] | var0[255 - var10] << 8;
//                        var1[var0[var10]] = (byte) (var9 ^ var9 >> 4 ^ var9 >> 5 ^ var9 >> 6 ^ var9 >> 7 ^ 99);
//                    }
//
//                    int var11;
//                    for (var9 = 0; var9 < 256; ++var9) {
//                        var11 = var1[var9] & 255;
//                        var10 = var11 << 1 ^ (var11 >>> 7) * 283;
//                        var11 = ((var11 ^ var10) << 24 ^ var11 << 16 ^ var11 << 8 ^ var10) & -1;
//                        var2[var9] = var11;
//                        var3[var9] = var11 << 8 | var11 >>> -8;
//                        var4[var9] = var11 << 16 | var11 >>> -16;
//                        var5[var9] = var11 << 24 | var11 >>> -24;
//                    }
//
//                    var11 = 0;
//
//                    for (var10 = 1; var11 < 30; ++var11) {
//                        var6[var11] = var10;
//                        var10 = var10 << 1 ^ (var10 >>> 7) * 283;
//                    }
//                }
//                break;
//            } catch (Exception var23) {
//            }
//        }
//
//        byte[] var26 = new byte[16];
//        byte var30 = 0;
//        var10000 = null;
//
//        int var31;
//        label113:
//        {
//            label150:
//            {
//                boolean var10001;
//                while (true) {
//                    try {
//                        try {
//                            if (var30 == 0) {
//                                var31 = var30 + 1;
//                                var26[0] = 45;
//                                var26[1] = -5;
//                                var26[2] = 115;
//                                var26[3] = -90;
//                                var26[4] = -21;
//                                var26[5] = 34;
//                                var26[6] = -81;
//                                var26[7] = -97;
//                                break label150;
//                            }
//                        } catch (Exception var21) {
//                            continue;
//                        }
//                    } catch (Throwable var22) {
//                        var10000 = var22;
//                        var10001 = false;
//                        break;
//                    }
//
//                    try {
//                        ;
//                    } catch (Throwable var20) {
//                        var10000 = var20;
//                        var10001 = false;
//                        break;
//                    }
//
//                    var26[8] = 35;
//                    var26[9] = 67;
//                    var26[10] = 42;
//                    var26[11] = 40;
//                    var26[12] = -114;
//                    var26[13] = -69;
//                    var26[14] = -89;
//                    var26[15] = -51;
//                    break label113;
//                }
//
//                Throwable var27;
//                while (true) {
//                    var27 = var10000;
//
//                    try {
//                        var27 = var27;
//                        break;
//                    } catch (Throwable var17) {
//                        var10000 = var17;
//                        var10001 = false;
//                    }
//                }
//
//                var26[8] = 35;
//                var26[9] = 67;
//                var26[10] = 42;
//                var26[11] = 40;
//                var26[12] = -114;
//                var26[13] = -69;
//                var26[14] = -89;
//                var26[15] = -51;
//                throw var27;
//            }
//
//            var26[8] = 35;
//            var26[9] = 67;
//            var26[10] = 42;
//            var26[11] = 40;
//            var26[12] = -114;
//            var26[13] = -69;
//            var26[14] = -89;
//            var26[15] = -51;
//        }
//
//        byte var25 = 4;
//        int var24 = var25 + 6;
//        int[] var12 = new int[(var24 + 1) * 4];
//        int var32 = 0;
//        var10000 = null;
//
//        label95:
//        while (true) {
//            if (var32 != 0) {
//                break;
//            }
//
//            var32 += 3;
//            int var13 = 0;
//            var10 = 0;
//
//            label93:
//            while (true) {
//                var30 = 0;
//                var10000 = null;
//
//                try {
//                    while (true) {
//                        try {
//                            if (var30 == 0) {
//                                var31 = var30 + 3;
//                                if (var10 < 16) {
//                                    var12[(var13 >> 2) * 4 + var13 & 3] = var26[var10] & 255 | (var26[var10 + 1] & 255) << 8 | (var26[var10 + 2] & 255) << 16 | var26[var10 + 3] << 24;
//                                    var10 += 4;
//                                    ++var13;
//                                    continue label93;
//                                }
//                            }
//                            break;
//                        } catch (Exception var18) {
//                        }
//                    }
//
//                    var13 = var24 + 1 << 2;
//                    int var14 = var25;
//
//                    while (true) {
//                        if (var14 >= var13) {
//                            break label95;
//                        }
//
//                        var10 = var12[(var14 - 1 >> 2) * 4 + (var14 - 1 & 3)];
//                        if (var14 % var25 == 0) {
//                            var10 = bvxwJ(var1, zdoHivtcH(var10, 8)) ^ var6[var14 / var25 - 1];
//                        } else if (var25 > 6 && var14 % var25 == 4) {
//                            var10 = bvxwJ(var1, var10);
//                        }
//
//                        var12[(var14 >> 2) * 4 + (var14 & 3)] = var12[(var14 - var25 >> 2) * 4 + (var14 - var25 & 3)] ^ var10;
//                        ++var14;
//                    }
//                } catch (Exception var19) {
//                    break;
//                }
//            }
//        }
//
//        int[] var29 = new int[]{906471306, 579498873, 1309303415, -425526398};
//        Object[] var28 = new Object[]{var1, var2, var3, var4, var5, var12, var29, null};
//        ch = var28;
//        BhAljA();
//    }
//
//    private static final void BhAljA() {
//        boolean var6 = false;
//        boolean var7 = false;
//        byte var13 = 0;
//        Throwable var10000 = null;
//
//        label72:
//        while (var13 == 0) {
//            var13 = 2;
//            StackTraceElement[] var0 = (new Throwable()).getStackTrace();
//            int var1 = var0.length;
//            int var2 = 393009724;
//            int var3 = 1;
//
//            while (var3 < var1) {
//                StringBuilder var4 = new StringBuilder();
//                var6 = false;
//                var10000 = null;
//
//                try {
//                    label94:
//                    {
//                        boolean var10001;
//                        label79:
//                        {
//                            while (true) {
//                                try {
//                                    try {
//                                        if (var6) {
//                                            break;
//                                        }
//
//                                        var6 = true;
//                                        var4 = var4.append(var0[var3].getClassName()).append(var0[var3].getMethodName());
//                                    } catch (Exception var10) {
//                                        continue;
//                                    }
//                                } catch (Throwable var11) {
//                                    var10000 = var11;
//                                    var10001 = false;
//                                    break label79;
//                                }
//
//                                if (var4.toString().hashCode() == var2) {
//                                    ch[7] = var3;
//                                    return;
//                                }
//                                break label94;
//                            }
//
//                            try {
//                                ch[7] = 4;
//                            } catch (Throwable var9) {
//                                var10000 = var9;
//                                var10001 = false;
//                                break label79;
//                            }
//
//                            if (var4.toString().hashCode() == var2) {
//                                ch[7] = var3;
//                                return;
//                            }
//                            break label94;
//                        }
//
//                        Throwable var5;
//                        while (true) {
//                            var5 = var10000;
//
//                            try {
//                                var5 = var5;
//                                break;
//                            } catch (Throwable var8) {
//                                var10000 = var8;
//                                var10001 = false;
//                            }
//                        }
//
//                        if (var4.toString().hashCode() != var2) {
//                            throw var5;
//                        }
//
//                        ch[7] = var3;
//                        return;
//                    }
//
//                    ++var3;
//                } catch (Exception var12) {
//                    continue label72;
//                }
//            }
//
//            return;
//        }
//
//        ch[7] = 3;
//    }
//
//    public static final String ujco(String var0) {
//        boolean var21 = false;
//        boolean var22 = false;
//        if (ch == null) {
//            jxhaJHAGJ28382();
//        }
//
//        StackTraceElement[] var10000 = (new Throwable()).getStackTrace();
//        StringBuilder var13 = new StringBuilder();
//        String var12 = var10000[(Integer) ch[7]].getClassName();
//        var13 = var13.append(var12);
//        var12 = var10000[(Integer) ch[7]].getMethodName();
//        int var1 = var13.append(var12).toString().hashCode();
//        int[] var2 = (int[]) ((int[]) ch[6]);
//        int var3 = var1 ^ var2[0];
//        int var4 = var1 ^ var2[1];
//        int var5 = var1 ^ var2[2];
//        int var27 = var1 ^ var2[3];
//        int[] var26 = (int[]) ((int[]) ch[5]);
//        int[] var6 = (int[]) ((int[]) ch[1]);
//        int[] var7 = (int[]) ((int[]) ch[2]);
//        int[] var8 = (int[]) ((int[]) ch[3]);
//        int[] var9 = (int[]) ((int[]) ch[4]);
//        byte[] var10 = (byte[]) ((byte[]) ch[0]);
//        char[] var25 = var0.toCharArray();
//        var22 = false;
//        var10000 = null;
//
//        label69:
//        while (!var22) {
//            var22 = true;
//            int var11 = var25.length;
//            int var28 = 0;
//
//            while (var28 < var11) {
//                if (var28 % 8 == 0) {
//                    boolean var29 = false;
//                    var29 = false;
//                    var29 = false;
//                    var29 = false;
//                    int var14 = var3 ^ var26[0];
//                    int var15 = var4 ^ var26[1];
//                    int var16 = var5 ^ var26[2];
//                    int var17 = var27 ^ var26[3];
//
//                    int var18;
//                    int var19;
//                    int var20;
//                    int var30;
//                    for (var30 = 4; var30 < 36; var30 += 4) {
//                        var18 = var6[var14 & 255] ^ var7[var15 >> 8 & 255] ^ var8[var16 >> 16 & 255] ^ var9[var17 >>> 24] ^ var26[var30];
//                        var19 = var6[var15 & 255] ^ var7[var16 >> 8 & 255] ^ var8[var17 >> 16 & 255] ^ var9[var14 >>> 24] ^ var26[var30 + 1];
//                        var20 = var6[var16 & 255] ^ var7[var17 >> 8 & 255] ^ var8[var14 >> 16 & 255] ^ var9[var15 >>> 24] ^ var26[var30 + 2];
//                        var17 = var6[var17 & 255] ^ var7[var14 >> 8 & 255] ^ var8[var15 >> 16 & 255] ^ var9[var16 >>> 24] ^ var26[var30 + 3];
//                        var30 += 4;
//                        var14 = var6[var18 & 255] ^ var7[var19 >> 8 & 255] ^ var8[var20 >> 16 & 255] ^ var9[var17 >>> 24] ^ var26[var30];
//                        var15 = var6[var19 & 255] ^ var7[var20 >> 8 & 255] ^ var8[var17 >> 16 & 255] ^ var9[var18 >>> 24] ^ var26[var30 + 1];
//                        var16 = var6[var20 & 255] ^ var7[var17 >> 8 & 255] ^ var8[var18 >> 16 & 255] ^ var9[var19 >>> 24] ^ var26[var30 + 2];
//                        var17 = var6[var17 & 255] ^ var7[var18 >> 8 & 255] ^ var8[var19 >> 16 & 255] ^ var9[var20 >>> 24] ^ var26[var30 + 3];
//                    }
//
//                    var20 = var6[var14 & 255] ^ var7[var15 >> 8 & 255] ^ var8[var16 >> 16 & 255] ^ var9[var17 >>> 24] ^ var26[var30];
//                    var19 = var6[var15 & 255] ^ var7[var16 >> 8 & 255] ^ var8[var17 >> 16 & 255] ^ var9[var14 >>> 24] ^ var26[var30 + 1];
//                    var18 = var6[var16 & 255] ^ var7[var17 >> 8 & 255] ^ var8[var14 >> 16 & 255] ^ var9[var15 >>> 24] ^ var26[var30 + 2];
//                    var17 = var6[var17 & 255] ^ var7[var14 >> 8 & 255] ^ var8[var15 >> 16 & 255] ^ var9[var16 >>> 24] ^ var26[var30 + 3];
//                    var16 = var30 + 4;
//                    var3 = var10[var20 & 255] & 255 ^ (var10[var19 >> 8 & 255] & 255) << 8 ^ (var10[var18 >> 16 & 255] & 255) << 16 ^ var10[var17 >>> 24] << 24 ^ var26[var16 + 0];
//                    var4 = var10[var19 & 255] & 255 ^ (var10[var18 >> 8 & 255] & 255) << 8 ^ (var10[var17 >> 16 & 255] & 255) << 16 ^ var10[var20 >>> 24] << 24 ^ var26[var16 + 1];
//                    var5 = var10[var18 & 255] & 255 ^ (var10[var17 >> 8 & 255] & 255) << 8 ^ (var10[var20 >> 16 & 255] & 255) << 16 ^ var10[var19 >>> 24] << 24 ^ var26[var16 + 2];
//                    var27 = var10[var17 & 255] & 255 ^ (var10[var20 >> 8 & 255] & 255) << 8 ^ (var10[var19 >> 16 & 255] & 255) << 16 ^ var10[var18 >>> 24] << 24 ^ var26[var16 + 3];
//                }
//
//                var21 = false;
//                var10000 = null;
//
//                try {
//                    label63:
//                    while (true) {
//                        try {
//                            if (!var21) {
//                                var21 = true;
//                                switch (var28 % 8) {
//                                    case 0:
//                                        var25[var28] = (char) (var3 >> 16 ^ var25[var28]);
//                                        break label63;
//                                    case 1:
//                                        var25[var28] = (char) (var3 ^ var25[var28]);
//                                        break label63;
//                                    case 2:
//                                        var25[var28] = (char) (var4 >> 16 ^ var25[var28]);
//                                        break label63;
//                                    case 3:
//                                        var25[var28] = (char) (var4 ^ var25[var28]);
//                                        break label63;
//                                    case 4:
//                                        var25[var28] = (char) (var5 >> 16 ^ var25[var28]);
//                                        break label63;
//                                    case 5:
//                                        var25[var28] = (char) (var5 ^ var25[var28]);
//                                        break label63;
//                                    case 6:
//                                        var25[var28] = (char) (var27 >> 16 ^ var25[var28]);
//                                        break label63;
//                                    case 7:
//                                        var25[var28] = (char) (var27 ^ var25[var28]);
//                                }
//                            }
//                            break;
//                        } catch (Exception var23) {
//                        }
//                    }
//
//                    ++var28;
//                } catch (Exception var24) {
//                    continue label69;
//                }
//            }
//
//            return new String(var25);
//        }
//
//        return new String(var25);
//    }
}
