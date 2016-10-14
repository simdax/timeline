(
(
// time manip
recommence:{ arg s;
	s.offset=0;
	s.esp.stop.play;
	defer{s.win.value_(0)};
	s.routine.stop.play;
},
newTime:{arg s, new, post=false;
	var pourcentage;
	var niou;
	var now=s.time*s.win.value;
	s.time=new; s.routine.envir_(s);
	if(new<now){s.recommence}
	{
		pourcentage=s.time/new; pourcentage;
		//action
		s.offset=s.win.value=(s.win.value*pourcentage);
	};
},
// var
time:4, offset:0, // en time (slider.val *time)
fps:1/25,
//routine
paused:false, playing:false,
pattern:Pbind(\octave, 5, \degree,Pseries(0,1), \dur, 0.5),
setPattern:{arg s, niou ;  
	s.pattern=niou;
	s.setEsp
},
setEsp:{arg s; s.esp.source=PFF(s.offset,s.pattern)},
esp:EventPatternProxy().quant_(0),
	routine:TaskProxy({ arg s;
		var b=0;
	while{b<1}{
		var time={(s.time/s.fps).reciprocal}.value;
		defer{
			b=s.win.value;
			s.offset=b+time;
			s.win.value_(b+time);
		};
		s.fps.wait;
	};
	s.recommence
}).quant_(0),
routines:{arg s; [s.routine, s.esp]},
// UI
guigui:{ arg s, parent=Window("timeline", 200@40).front;
	s.setEsp;
	s.routine.envir_(s); //s.esp.envir_(s);
	s.win=Slider(parent, 200@30)
	.onClose_{s.routine.clear; s.esp.clear; s.routines=nil}
	// stop always
	.keyDownAction_{
		arg self, c;
		switch(c, $ , { 
			if(s.playing){
				"pause".postln;
				s.routines.do(_.pause);
				s.playing=false; s.paused=true
			}
			{
				if( s.paused)
				{"resume".postln; s.routines.do(_.resume); s.playing=true; }
				{"play".postln;s.routines.do(_.play); s.playing=true};
			}
		})
	}
	//retriggers
	.mouseUpAction_{
		arg self;
		if(s.time==inf){} // do nothing
		{
			s[\offset]=(self.value.postln*s.time).postln;
			if(s.playing.postln){s.esp.stop.play}
			{if(s.paused.postln){s.esp.stop.play.pause}};
			//	s.keyDownAction.value
		}
	};
}
)
)





