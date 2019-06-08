#0x21 = most sig. 4 tubes/digits
#0x20 = least sig. 3 tubes/digits

#configure (0x05 = config register)
#not actually needed as defaults are okay, else do this in general init only once
# bank = 0
# mirror = 0
# seqop = 0
# disslw = 0
# haen = 0
# odr = 0
# intpol = 0
#i2cset -y 1 0x21 0x05 0x00
#verify config reg.
#i2cget -y 1 0x21 0x05

# set(IODIR_A, all as outputs (0))
i2cset -y 1 0x21 0x00 0x00
i2cset -y 1 0x20 0x00 0x00

# set(IODIR_B, all as outputs (0))
i2cset -y 1 0x21 0x01 0x00
i2cset -y 1 0x20 0x01 0x00

#iocon.bank = 0 (seq mode)
# then, GPIO_A addr = 12h, GPIO_B=13h, IODIR_A = 00h, IODIR_B= 01h
#set "34" on 2 leftmost tubes
i2cset -y 1 0x21 0x12 0x34

#set "56" on next 2 tubes
i2cset -y 1 0x21 0x13 0x56

#set "78" on next 2 tubes
i2cset -y 1 0x20 0x12 0x78
