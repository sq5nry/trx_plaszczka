#both channels
i2cset -y 2 0x19 0xc3

# l/r inputs
i2cset -y 2 0x19 0xa3

# volume -3dB
i2cset -y 2 0x19 0x03

#phone amp only
i2cset -y 2 0x25 0x08

