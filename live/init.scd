(
{
	arg w=Window("io", 200@60).front;
	var  z, f;
	var a="params.scd".loadRelative[0];

	a.setEsp; // init equivalent
	a.guigui(w);

	// rajout de quelques trucs
	z=NumberBox(a.win.parent, Rect(0, 40, 100, 20 ))
	.action_{arg s;var c=s.value;
			a.newTime(1.max(c)); 
	}
	.scroll_step_(0.5);
	f=StaticText(a.win.parent, Rect(100, 40, 100, 20 ));
	TaskProxy({
		while{a.routines.notNil}{
			defer{
				var tt=a.time;
				var t=a.win.value*a.time;
				f.string_(t); z.value_(tt)
			};
			0.2.wait}
	}).play;

	// finalement on renvoie l'objet
	a
}.value

)