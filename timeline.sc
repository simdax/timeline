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
		.orientation_(\horizontal)
		.onClose_{routine.stop.clear; esp.stop.clear}
		// stop always
		.keyDownAction_{
			arg self, c;
			switch(c, $ , {
				this.play;
			})
		}
		//retriggers
		.mouseUpAction_{
			arg self;
			if(time==inf){} // do nothing
			{
				offset=(self.value*time);
				if(playing.postln){esp.stop.play}
				{if(paused.postln){esp.stop.play.pause}};
				//	keyDownAction.value
			}
		};
		
	}
	// interface
	play{ arg quant;
		if(playing){
			//"pause".postln;
			this.routines.do(_.pause);
			playing=false; paused=true
		}
		{
			if( paused)
			{//"resume".postln;
				this.routines.do(_.resume(quant:quant));
				playing=true; }
			{//"play".postln;
				this.routines.do(_.play(quant:quant));
				playing=true};
		}

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
			this.recommence;
		}).quant_(0);

		this.setEsp;
		
	}
	// time manip
	recommence{
		defer
		{
			offset=0;
			esp.stop.play;
			routine.stop.play;
			win.value_(0);
		}
	}
	time_{arg new, post=false;
		var pourcentage;
		var niou;
		var now;
		defer{
			now=time.copy*win.value;
			if(new<now){this.recommence}
			{
				pourcentage=time/new; 
				//action
				offset=win.value=(win.value*pourcentage);
			};
			this.changed(\time);
		};
		time=new
	}
	// var

	pattern_{ arg niou ;  
		pattern=niou;
		this.setEsp
	}
	setEsp{ esp.source=Plazy{arg s;PFF(offset,pattern)}}
	routines{ ^[routine, esp]}
	
}
// just a timeline with infos

PlayLine {
	
	*size{^310@80}
	*new{
		arg win=Window("io").front, b=this.size;
		var timeBox,f, tp;
		
		var a,w, quant=0, vue1,vue2;
		w=FlowView(win,b);
		w.onClose_{tp.stop.clear};

		
		a=TimeLine.new(w, (b.x-4)@(b.y/2));
		w.decorator.nextLine;
		vue2=View(w, b.x@b.y/2).layout_(HLayout());

		
		// rajout de quelques trucs
		timeBox=NumberBox(vue2)
		.action_{arg s;var c=s.value;
			a.time=(1.max(c)); 
		}
		.scroll_step_(0.5);
		f=StaticText(vue2,"2:22n".bounds);
		tp=TaskProxy({
			while{a.routines.notNil}{
				defer{
					var tt=a.time;
					var t=a.win.value*a.time;
					f.string_(t); timeBox.value_(tt)
				};
				0.2.wait}
		}).play;
		NumberBox(vue2).value_(quant).action_{arg self; quant=self.value};
		Button(vue2).action_{a.play(quant)}
		//.onClose_{Pdef(\a).stop};
		^(win:w,timeline:a,timeBox:timeBox);
	}
}