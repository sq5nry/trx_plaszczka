#!/bin/bash
#check input device
alsamixer --view=capture
arecord -L
# SB Live USB
cvlc -vvv alsa://sysdefault:CARD=External --delay=100 --sout '#transcode{acodec=a52,ab=128,samplerate=8000}:rtp{dst=sq9nry.no-ip.org,port=8092,sdp=rtsp://10.0.0.139:8092/test.sdp}"}'
# USB dongle audio card
cvlc -vvv alsa://sysdefault:CARD=AUDIO --delay=100 --sout '#transcode{acodec=a52,ab=128,samplerate=8000}:rtp{dst=sq9nry.no-ip.org,port=8092,sdp=rtsp://10.0.0.139:8092/test.sdp}"}'



#for ext rtsp://10.0.0.137:8092/test.sdp   rtsp://sq9nry.no-ip.org:8092/test.sdp
cvlc -vvv alsa://sysdefault:CARD=External --delay=100 --sout '#transcode{acodec=a52,ab=128,samplerate=8000}:rtp{dst=sq9nry.no-ip.org,port=8092,sdp=rtsp://10.0.0.139:8092/test.sdp}"}'

#wmp mmsh://sq9nry.no-ip.org:8091
#cvlc -vvv alsa://sysdefault:CARD=External --sout '#transcode{vcodec=DIV3,vb=128,scale=1,acodec=mp3,ab=16,channels=1}:std{access=mmsh,mux=asfh,dst=:8091}'

#for ext rtsp://10.0.0.137:8091/test.sdp   rtsp://sq9nry.no-ip.org:8091/test.sdp
#cvlc -vvv alsa://sysdefault:CARD=External --delay=100 --sout '#transcode{acodec=a52,ab=128,samplerate=8000}:rtp{dst=10.0.0.137,port=8091,sdp=rtsp://10.0.0.137:8091/test.sdp}"}'

#RTP rtsp://sq9nry.no-ip.org:8091/test.sdp
#cvlc -vvv alsa://sysdefault:CARD=External --no-video --sout '#transcode{acodec=a52,ab=128,samplerate=8000}:rtp{mux=ts,dst=239.255.12.42,port=8091,sdp=rtsp://10.0.0.137:8091/test.sdp,name="TestStream"}"}'

#multicast rtp://239.255.12.42:8091
#cvlc -vvv alsa://sysdefault:CARD=External --no-video --sout '#transcode{acodec=a52,ab=128,samplerate=8000}:rtp{mux=ts,dst=239.255.12.42,port=8091,sdp=sap,name="TestStream"}"}'







