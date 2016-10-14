TimeLine  {

	var <time,<offset,<fps,
	<paused,<playing,
	<pattern,<esp,<routine,
	<win;

	*new{ arg p,b;
		^super.new.gui(p,b)
	}

	// UI
	gui{ arg parent, bounds;
		parent??
		{parent=Window("timeline", bounds).front};
		// var init;
		this.init;

		win=Slider(parent, bounds)
		.onClose_{routine.stop.clear; esp.stop.clear}
		// stop always
		.keyDownAction_{
			arg self, c;
			switch(c, $ , { 
				if(playing){
					"pause".postln;
					this.routines.do(_.pause);
					playing=false; paused=true
				}
				{
					if( paused)
					{"resume".postln; this.routines.do(_.resume); playing=true; }
					{"play".postln;this.routines.do(_.play); playing=true};
				}
			})
		}
		//retriggers
		.mouseUpAction_{
			arg self;
			if(time==inf){} // do nothing
			{
				offset=(self.value.postln*time);
				if(playing.postln){esp.stop.play}
				{if(paused.postln){esp.stop.play.pause}};
				//	keyDownAction.value
			}
		};
		
	}
	// interface
	play{
		win.keyDownAction.value
	}
	
	//pr
	init{

		//routine

		time=4; offset=0; // en time (slider.val *time)
		fps=(1/25);
		paused=false; playing=false;

		pattern=Pbind(\octave, 5, \degree,Pseries(0,1), \dur, 0.5);
		esp=EventPatternProxy().quant_(0);
		routine=TaskProxy({ 
			var b=0; 
			while{b<1}{
				var t=fps/time;
				defer{
					b=win.value;
					offset=b+t;
					win.value_(b+t);
				};
				fps.wait;
			};
			this.recommence
		}).quant_(0);

		this.setEsp;
		
	}
	// time manip
	recommence{ 
		offset=0;
		esp.stop.play;
		defer{win.value_(0)};
		routine.stop.play;
	}
	time_{arg new, post=false;
		var pourcentage;
		var niou;
		var now=time*win.value;
		time=new; //routine.envir_(s);
		if(new<now){this.recommence}
		{
			pourcentage=time/new; pourcentage;
			//action
			offset=win.value=(win.value*pourcentage);
		};
		this.changed(\time);
	}
	// var

	pattern_{ arg niou ;  
		pattern=niou;
		this.setEsp
	}
	setEsp{ esp.source=Plazy{arg s;PFF(offset,pattern)}}
	routines{ ^[routine, esp]}
	
}

PlayLine{

	*size{^200@50}
	*new{
		arg win=Window("io").front, b=this.size;
		var  z, f, tp;
		
		var a,w;
		w=FlowView(win,b);		
		a=TimeLine(w, (b.x-4)@(b.y/2));

		w.onClose_{tp.stop.clear};
		
		// rajout de quelques trucs
		z=NumberBox(w)
		.action_{arg s;var c=s.value;
			a.time=(1.max(c)); 
		}
		.scroll_step_(0.5);
		f=StaticText(w,"ufisqf".bounds);
		tp=TaskProxy({
			while{a.routines.notNil}{
				defer{
					var tt=a.time;
					var t=a.win.value*a.time;
					f.string_(t); z.value_(tt)
				};
				0.2.wait}
		}).play;
		^w;
	}
}