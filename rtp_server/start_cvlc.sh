#!/bin/bash
#RTP rtsp://10.0.0.137:8091/test.sdp
cvlc -vvv alsa://sysdefault:CARD=External --no-video --sout '#transcode{acodec=a52,ab=128,samplerate=8000}:rtp{mux=ts,dst=239.255.12.42,port=8091,sdp=rtsp://10.0.0.137:8091/test.sdp,name="TestStream"}"}'

#multicast rtp://239.255.12.42:8091
cvlc -vvv alsa://sysdefault:CARD=External --no-video --sout '#transcode{acodec=a52,ab=128,samplerate=8000}:rtp{mux=ts,dst=239.255.12.42,port=8091,sdp=sap,name="TestStream"}"}'
