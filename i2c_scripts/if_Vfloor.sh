#0x04 = channel C
#0x01 = channel A
#
# 0xf03f = max val
# 0x{lower_nibble}{const_0}0x{const_3}{higher_nibble}
# 3 = power down disabled, vref buffer disabled, normal gain, clr normal
# const_0 = actually could be used  (8 / 10 /12) bit chips
#
# def recommended = 32 (0x...2) 0010 0000
i2cset -y 2 0x0f 0x08 0x9037 w
