
(
t=FlowView(); t.decorator.margin_(8@8).gap_(8@8);
a=PlayLine(t, 200@50);
a.win.background_(Color.rand);
 //a.timeline.pattern_(Pbind())
)

// TODO bug when setting time BEFORE play
//????
a.timeline.time_(76);
a.timeline.play


