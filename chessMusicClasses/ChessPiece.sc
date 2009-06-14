//Abstract Class
ChessPiece{	
	classvar <fileNumbers;	//Class variable storing the chess file letters
	var <>pieceType, <pieceName, <colour, <chessPosition, <>arrayPosition, <>attackingSquares, <>attackingSquaresChess, <>possibleMoves, <>possibleMoves2D, <>board;
	var <>lastMoved, <>isAttacking, <>underAttack, isActive, activate, <>state;
	var chessAsArray, arrayAsChess, fileAsInt, okAP;
	var resetAttackingSquares, updateAttackingSquares, updateAttackingSquaresChess, printBoard, updatePossibleMoves, updatePossibleMoves2D, updateUnderAttack;
	var <>pieceSynth;
	
	*new { arg argColour, argBoard, argPromo;
		fileNumbers = ["a", "b", "c", "d", "e", "f", "g", "h"]
		^super.new.initChessPiece(argColour, argBoard, argPromo);
	}
	
	/*Abstract initialisation method for ChessPiece*/
	initChessPiece{ arg argColour, argBoard, argPromo;
		
		board = argBoard;
		chessPosition = Array.newClear(2);
		arrayPosition = Array.newClear(2);
		colour = argColour;
		if(argPromo==true,
			{
				if(colour=="white", {chessPosition[1]=8},{chessPosition[1]=1})
			},
			{
				if(colour=="white", {chessPosition[1] = 1}, {chessPosition[1] = 8});
			}
		);
		lastMoved = false;
		underAttack = false;
		state =0;
		attackingSquares = Array2D.new(8,8);
		attackingSquaresChess = List.newClear();
		possibleMoves2D = Array2D.new(8,8);
		possibleMoves = List.newClear();
		this.resetAttackingSquares;
		
	}
	
	/*Method that returns a Chess Position as an Array Position*/
	chessAsArray{arg chessPos;
		var x;
		x=Array.newClear(2);
		x[0]=this.fileAsInt(chessPos[0]);
		x[1]=chessPos[1]-1;
		^x;
	}
	
	/*Method that returns an Array Position as a Chess Position*/
	arrayAsChess{arg argFile, argRank;
		var cFile, cRank, ret;
		cFile = fileNumbers[argFile];
		cRank = argRank+1;
		ret = ""++cFile++cRank;
		^ret;
	}
	
	/*Method to update array position*/
	updateArrayPosition{
		arrayPosition[0] = this.fileAsInt(chessPosition[0]);
		arrayPosition[1] = chessPosition[1]-1;
	}
	
	/*Method convert chess file (char) into array value (int) Redundant? chessAsArray[0]*/
	fileAsInt{arg argFile;		 
		 ^fileNumbers.find([argFile]);
	}
	
	/*Method to null all squares being attacked, used when updatingAttackingSquares*/
	resetAttackingSquares{
		for (0, 7, { arg i; 
			for(0, 7, {arg j; 
				attackingSquares.put(i,j,false)}
			)
		})
	}
	
	/*Method to update an array storing attacking squares as chess value symbols*/
	updateAttackingSquaresChess{
		
		//Reset attackingSquaresChess
		attackingSquaresChess = List.new();
		
		//Loop through board
		for (0, 7, { arg i; 
			for(0, 7, {arg j;
				//If this piece can attack a square 
				if(possibleMoves2D[i,j]=="take",
					{
					//Add the square to the ASChess array as a symbol
					attackingSquaresChess.add(this.arrayAsChess(i,j).asSymbol)
					}
				)
			})
		});
	}
	
	/*Abstract method for updating the squares being attacked*/
	updateAttackingSquares{
	}
	
	/*Method to update whether or not this piece is under attack*/
	updateUnderAttack{
		//Reset state
		this.underAttack_(false);
		//Loop through the board
		for(0,7,{arg i;
			for(0,7,{arg j;
					var piece;
					piece = this.board.piecesBoard[i,j];
					//If there is a piece
					if(piece != nil,
						{	//If the piece is on the enemy team, and it's attackingSquares include the location of this piece.
							if( (piece.colour !=this.colour) && (piece.attackingSquaresChess.includes( this.arrayAsChess(this.arrayPosition[0],this.arrayPosition[1]).asSymbol )),
								{this.underAttack_(true)}
							)
						}
					)
				}
			)
		})
	}
	
	/*Method to check if this piece is attacking another*/
	updateIsAttacking{
		if(attackingSquaresChess.isEmpty.not,
			{this.isAttacking_(true)}, 
			{this.isAttacking_(false)}
		)
	}
	
		
	/*Method to update the possible moves (as strings)*/
	updatePossibleMoves{
		possibleMoves=List.newClear();
		
		for(0,7,{arg i;
			for(0,7,{arg j;
				if( (this.possibleMoves2D[i,j]==true) || (this.possibleMoves2D[i,j] == "take"),
					{this.possibleMoves.add(this.arrayAsChess(i,j).asSymbol)}
				)
			})
		})
	}
	
	/*Abstract method for updating the possible moves 2DArray*/
	updatePossibleMoves2D{
	}
	
	/*Abstract method for activating the pieces Synth*/
	activate{
	}
	
	/*Method for printing board including position and squares being attacked*/
	printBoard{arg pieceName;
		
		for(7,0, {arg i;						//For each rank
			var ret= "|";						//Variable for printing
			var ret2;
			for(0,7,{"|-----------".post;});		//Print top line
			"|".post; "".postln;					//
			2.do{for(0,7,{"|           ".post;});
			"|".post; "".postln;
			};				
			for(	0,7, {arg j; 					//For each file
				if(arrayPosition == [j,i], 		//Check if printing piece or attacking boolean
								{ret = ret + pieceName +"|"}, //Print piece
								{ if(attackingSquares.at(j,i)==true, //Check if attacking square
									{ret2= "          |"}, //Print true if so
									{ret2= "        "+" |"}); //else print false
								  if(possibleMoves2D.at(j,i)==true,
								  	{ret2= "  Move    |"});
								  if(possibleMoves2D.at(j,i)=="take",
								  	{ret2= "  Take    |"});
								  if(possibleMoves2D.at(j,i)=="block",
								  	{ret2= "  Block   |"});
								  if(possibleMoves2D.at(j,i)=="team",
								  	{ret2= "  Team    |"});
								  		
								  	
								  ret = ret + ret2;
								}
					); 
				});
			ret.postln;
			2.do{for(0,7,{"|           ".post;});
			"|".post; "".postln;
			};	
			 });
			 for(0,7,{"|-----------".post;});		//Print top line
			"|".post; "".postln;					//
	
	}
	
	/*Method for updating the position of a chessPiece*/
	chessPosition_{ arg newPosition;
						
		chessPosition = newPosition;			//Updates the position variable
		this.updateArrayPosition;				//Updates the arrayPosition variable
		this.updateAttackingSquares;			//Updates the squares being attacked
		//this.updateAttackingSquaresChess;		//Updates the squares being attacked (as chess values)
		^chessPosition						//Returns the new position
	}
	
	/*Method to check if square being attacked is on the board*/
	okAP{arg ap; 
		var ret;
		if(ap.isInteger, {if((ap>=0) && (ap<=7),{ret =true},{ret=false})},{ret=false});
		^ret;
	}
	
	/*Method to check if a piece is active (under attack, attacking, or the last on the team to move)*/
	/*If the piece is in more than one state, one is selected at random*/
	isActive{
		var ret;
		
		if(this.underAttack==true || this.isAttacking==true || this.lastMoved==true,
			{ret = true; this.activate},
			{ret = false}
		);
		^ret
	}
	
	/*Abstract methods. For stopping synth routines*/
	stopRouts{
	}
	
		
}

ChessPawn : ChessPiece {
		
	var panPos, pr, pBuff, pBuffFrames;
	
	*new{arg argColour, argFile, argBoard;
		^super.new(argColour, argBoard, false).initChessPawn(argFile);
	}
	
	initChessPawn{arg argFile;
		var fileVal = super.fileAsInt(argFile);		//Convert the chess file letter into an int
		pieceType = "pawn";
		
		if( fileVal>=0 && fileVal<8, 				//Check the file int is on the board
			{	chessPosition[0] = argFile;		
				if(colour =="white", {	chessPosition[1] = 2; //Set starting rank based on colour
									panPos= -1;
									pieceName=" W.Pawn  ";
									},  
								  {if(colour=="black", 
								  {chessPosition[1] = 7;
								   pieceName=" B.Pawn  ";
								   panPos=1}, 
								   {"Bad Colour".postln})
								   });
				super.updateArrayPosition;			//Convert chess position into array values
				this.updateAttackingSquares;		//Update the squares being attacked
				super.updateAttackingSquaresChess;
				this.updatePossibleMoves2D;
				super.updateIsAttacking;
			},
			{"Bad File value".postln} //Check for legit file
		);
		
		if(colour=="white",
			{panPos= -1},
			{panPos= 1}
		);
			
	}
	
	updateAttackingSquares{		
		super.resetAttackingSquares;				//Set all squares being attacked to false
	
		if( (colour == "white") && (chessPosition[1]!=8), //If the piece is white and not on the last rank
			{ 
				if(super.okAP(arrayPosition[0]-1) && super.okAP(arrayPosition[1]+1),         //Check attacking position is on the board
					{attackingSquares.put( arrayPosition[0]-1, arrayPosition[1]+1, true)}); //Set the attacking position to true
				if(super.okAP(arrayPosition[0]+1) && super.okAP(arrayPosition[1]+1),         
					{attackingSquares.put( arrayPosition[0]+1, arrayPosition[1]+1, true)});
			},
			{	
				if((colour == "black") && (chessPosition[1]!=1),  //If the pice is black and not on the first rank
					{	
						if(super.okAP(arrayPosition[0]-1) && super.okAP(arrayPosition[1]-1), 
							{ attackingSquares.put( arrayPosition[0]-1, arrayPosition[1]-1, true)});
						if(super.okAP(arrayPosition[0]+1) && super.okAP(arrayPosition[1]-1), 							{attackingSquares.put( arrayPosition[0]+1, arrayPosition[1]-1, true)});
					};
				);
			};
		);
			
	}
	
	updatePossibleMoves2D{
		var pm1, pm2;
		
		//Reset possible moves		
		possibleMoves2D = Array2D.new(8,8);
		
		//Set relative possible move distances dependant on colour
		if(colour=="white",{pm1=1; pm2=2}, {pm1 = -1; pm2 = -2});
		
		//Set square 1 ahead as true
		possibleMoves2D.put(arrayPosition[0],arrayPosition[1]+pm1,true);
		
		//If piece is in the starting rank, set 2 squares ahead as true
		if( ((arrayPosition[1]==1) && (colour=="white")) || ((arrayPosition[1]==6) && (colour=="black")),{possibleMoves2D.put(arrayPosition[0],arrayPosition[1]+pm2,true)});
		
		//Set all enemy occupied squares that can attack as a possible move
		for(0,7,
			{arg i;
				for(0,7,
					{arg j;
						//If can attack this square
						if(attackingSquares[i,j]==true,
							//If there is a piece in the square
							{if(this.board.piecesBoard[i,j]!=nil,
								//If the piece is an enemy
								{if( (this.board.piecesBoard[i,j].colour) != (this.colour),
									{													//Set move as possible
										possibleMoves2D[i,j] = "take";
									}
								)}	
							)}
						)
					}
				)
			}
		);
		
		super.updatePossibleMoves;
	}
	
	/*Method for updating the position of a chessPiece*/
	chessPosition_{ arg newPosition;
		var ret;
		ret = super.chessPosition_(newPosition);
		^ret
	}
	
	printBoard{
		super.printBoard(pieceName);
	}
	

	/*Method to activate the piece's synth according to state*/
	activate{
		var poss, pan, sel;
		
		//Set appropriate panning
		if(colour=="white",
			{pan= -1},
			{pan = 1}
		);
		
		
		//List of possible states. If piece is attacking or under attack, the normal state is removed
		poss = List[];
		if(lastMoved, {poss.add(1)});
		if(underAttack, {poss.add(2); poss.remove(1)});
		if(isAttacking, {poss.add(3); poss.remove(1)});
		
		//Function to select a state to play from possibilities
		sel = {
			switch (poss.choose)
			{1}	{state = 1; pieceSynth = Synth(\pawnSynth, [\argPan, pan, \rate, 1, \endLoop, this.board.pawnBFrames, \amp, 1, \resFreq1, 150, \resFreq2, 700, \resAmp1, 0, \resAmp2, 0, \hpFreq, 500, \hpAmp, 0]) }
			{2}	{state = 2; pieceSynth = Synth(\pawnSynth, [\argPan, pan, \rate, 0.751, \endLoop, this.board.pawnBFrames, \amp, 0, \resFreq1, 150, \resFreq2, 700, \resAmp1, 0, \resAmp2, 0, \hpFreq, 500, \hpAmp, 1]) }
			{3}	{state = 3; pieceSynth = Synth(\pawnSynth, [\argPan, pan, \rate, 1.25, \endLoop, this.board.pawnBFrames/4, \amp, 1, \resFreq1, 150, \resFreq2, 700, \resAmp1, 2.5, \resAmp2, 02.5, \hpFreq, 500, \hpAmp, 0]) };
		};
		
		//free current synth if state has changed
		if(pieceSynth!=nil,
			{
				if(poss.includes(state),
					{},
					{
						pieceSynth.free;
						pieceSynth=nil;
						sel.();
					}
				)
			},
			{
			 sel.();
			}
		)
		
		
		
		
	}
		
}

ChessRook : ChessPiece{
	
	*new{arg argColour, argFile, argBoard, argPromo;
		^super.new(argColour, argBoard, argPromo).initChessRook(argFile);
	}
	
	initChessRook{arg argFile;
		var fileVal = super.fileAsInt(argFile);
		pieceType = "rook";
		if ((fileVal==0) || (fileVal==7),
			{	chessPosition[0]=argFile;
				super.updateArrayPosition;
				this.updateAttackingSquares;
				super.updateAttackingSquaresChess;
				this.updatePossibleMoves2D;
				super.updateIsAttacking;
				
			},
			{"Bad File value".postln};
		);
		if(colour=="white", {pieceName=" W.Rook  "},{if(colour=="black", {pieceName=" B.Rook  "},{"Bad Colour".postln})});
	}
				
	updateAttackingSquares{
		super.resetAttackingSquares;
		for(0,7, {arg i; 
					attackingSquares.put(arrayPosition[0],i,true); //Set all squares in rank and file to true
					attackingSquares.put(i,arrayPosition[1],true)
		});
		attackingSquares.put(arrayPosition[0],arrayPosition[1],false);
		
	}
	
	/*Method that updates the possible moves the rook may make*/
	updatePossibleMoves2D{
		
		var file, rank, setFunc, val;
		
		//2D Array representing the moves
		this.possibleMoves2D=Array2D.new(8,8);

		//File and Rank of this piece
		file = this.arrayPosition[0];
		rank = this.arrayPosition[1];
		
		//Set the possition of this piece to the name (importantly, not nil)
		this.possibleMoves2D[file,rank]=pieceName;
				
		setFunc ={arg rof, maxVal;
			var change, minVal, pmNil, pbNNil, pmSet, pbCol, pbIsPiece;
			
			if(rof=="file",{change = rank},{change=file});

			for(change, maxVal,
				{arg i;
					 
					 					 
					//Function that checks if a possibleMove2D square is Nil
					//Works for either situation (when checking a rank or file)
					pmNil = {if(rof=="file",
								{if(this.possibleMoves2D[file,i]==nil,{true},{false})},
								{if(this.possibleMoves2D[i,rank]==nil,{true},{false})}
							)
					};
					
					//Function that sets a possibleMove2D 
					//Works for either situation (when checking a rank or file)
					//arg pos Square in rank or file to set
					//arg val The value to set the square to
					pmSet = {arg pos, tof;
							if(rof=="file",
								{this.possibleMoves2D[file, pos]=tof},
								{this.possibleMoves2D[pos, rank]=tof}
							)
					};
					
					//Function that checks if a square on the board is Not Nil (if there is a piece there)
					//Works for either situation (when checking a rank or file)
					pbNNil = {if(rof=="file",
								{if(this.board.piecesBoard[file,i]!=nil,{true},{false})},
								{if(this.board.piecesBoard[i,rank]!=nil,{true},{false})}
							)
					};
					
					//Function that checks if the colour of a piece on the board is the same as the colour of this piece					//Works for either situation (when checking a rank or file)
					pbCol = {if(rof=="file",
								{if(this.board.piecesBoard[file,i].colour==this.colour,{true},{false})},
								{if(this.board.piecesBoard[i,rank].colour==this.colour,{true},{false})}
							)
					};
					
					//Function that checks if a square on the possible moves board has not yet been set.
					pbIsPiece = {arg j;
							
							if(rof=="file",
								{if(this.board.piecesBoard[file,j] ==nil,{true},{false})},
								{if(this.board.piecesBoard[j,rank] ==nil,{true},{false})}
							)
					};	
									
					//If not already set
					if(pmNil.value){
						
						//If there is a piece in the square
						if(pbNNil.value,
							{	
								//If the piece is on the same team
								if(pbCol.value,
									//Set move as false
									{pmSet.value(i, "team")},
									//Set move as true (take)
									{pmSet.value(i, "take" )}
								);
								
								//For all squares after that piece
								if(maxVal==7,
									{
										if( (i+1)>7,
											{minVal=7;
											 if(pbCol.value,
												{val= "team"},
												{val= "take"}
											)},
											{minVal=(i+1); 
											 val="block"
											 }
										);
										
										for(minVal,maxVal,
											//Set to false
											{arg j; 
												pmSet.value(j, val);
												
											}
										)
									},
									{	
										if( (i-1)<0,
											{minVal=0;
											 if(pbCol.value,
											 	{val="team"},
											 	{val="take"}
											 )},
											{minVal=(i-1);
											 val="block"}
										);
										
										for(minVal,maxVal,
											//Set to false
											{arg j; 
												pmSet.value(j, val);
											
											}
										)
									}	
								)
							},
							//If there is no piece, set to true
							{
							pmSet.value(i, true)}
						)
					}
				}
			);
		};
		
		//Set possible moves for each direction from the piece
		setFunc.value("file",7);
		setFunc.value("rank",7);
		setFunc.value("file",0);
		setFunc.value("rank",0);
		super.updatePossibleMoves;	
				
	}
	
	/*Method for updating the position of a chessPiece*/
	chessPosition_{ arg newPosition;
		var ret;
		ret = super.chessPosition_(newPosition);		
		^ret
	}
	
	printBoard{
		super.printBoard(pieceName);
	}	
		
	activate{
		var poss, pan, sel;
		
		if(colour=="white",
			{pan= -1},
			{pan = 1}
		);
				
		poss = List[];
		if(lastMoved, {poss.add(1)});
		if(underAttack, {poss.add(2); poss.remove(1)});
		if(isAttacking, {poss.add(3); poss.remove(1)});
		
		sel = {
			switch (poss.choose)
			{1}	{state = 1; pieceSynth = Synth(\rookSynth, [\amp, 1, \argPan, pan, \noiseFreq, 880, \oscRate, 0.15]);}
			{2}	{state = 2; pieceSynth = Synth(\rookSynth, [\amp, 1, \argPan, pan, \noiseFreq, 660, \oscRate, 0.1]);}
			{3}	{state = 3; pieceSynth = Synth(\rookSynth, [\amp, 1, \argPan, pan, \noiseFreq, 1320, \oscRate, 0.25]);}
		};
		
		//free current synth if state has changed
		if(pieceSynth!=nil,
			{
				if(poss.includes(state),
					{},
					{
						pieceSynth.free;
						pieceSynth=nil;
						sel.();
					}
				)
			},
			{
			 sel.();
			}
		)

	}
			
}

ChessKnight : ChessPiece{
	
	*new{arg argColour, argFile, argBoard, argPromo;
		^super.new(argColour, argBoard, argPromo).initChessKnight(argFile);
	}
	
	initChessKnight{arg argFile;
		var fileVal = super.fileAsInt(argFile);
		pieceType = "knight";
		if((fileVal==1) || (fileVal==6),
			{	chessPosition[0]=argFile;
				super.updateArrayPosition;
				this.updateAttackingSquares;
				super.updateAttackingSquaresChess;
				super.updatePossibleMoves2D;
				super.updateIsAttacking;
			},
			{"Bad File value".postln}
		);
		if(colour=="white", {pieceName="W.Knight "},{if(colour=="black", {pieceName="B.Knight "},{"Bad Colour".postln})});
	}
	
	updateAttackingSquares{
		var places=Array2D.new(8,2); //2D array to store the squares that are under attack
		
		//1 o'clock
		if(super.okAP(arrayPosition[0]+1),{places.put(0,0,arrayPosition[0]+1)});   //If attacking squares is on the board, set as true
		if(super.okAP(arrayPosition[1]+2),{places.put(0,1,arrayPosition[1]+2)});
		//2 o'clock
		if(super.okAP((arrayPosition[0]+2)),{places.put(1,0,arrayPosition[0]+2)});
		if(super.okAP((arrayPosition[1]+1)),{places.put(1,1,arrayPosition[1]+1)});
		//4 o'clock
		if(super.okAP((arrayPosition[0]+2)),{places.put(2,0,arrayPosition[0]+2)});
		if(super.okAP((arrayPosition[1]-1)),{places.put(2,1,arrayPosition[1]-1)});
		//5 o'clock
		if(super.okAP((arrayPosition[0]+1)),{places.put(3,0,arrayPosition[0]+1)});
		if(super.okAP((arrayPosition[1]-2)),{places.put(3,1,arrayPosition[1]-2)});
		//7 o'clock
		if(super.okAP((arrayPosition[0]-1)),{places.put(4,0,arrayPosition[0]-1)});
		if(super.okAP((arrayPosition[1]-2)),{places.put(4,1,arrayPosition[1]-2)});
		//8 o'clock
		if(super.okAP((arrayPosition[0]-2)),{places.put(5,0,arrayPosition[0]-2)});
		if(super.okAP((arrayPosition[1]-1)),{places.put(5,1,arrayPosition[1]-1)});
		//10 o'clock
		if(super.okAP((arrayPosition[0]-2)),{places.put(6,0,arrayPosition[0]-2)});
		if(super.okAP((arrayPosition[1]+1)),{places.put(6,1,arrayPosition[1]+1)});
		//11 o'clock
		if(super.okAP((arrayPosition[0]-1)),{places.put(7,0,arrayPosition[0]-1)});
		if(super.okAP((arrayPosition[1]+2)),{places.put(7,1,arrayPosition[1]+2)});
		
		super.resetAttackingSquares;
		
		for(0,places.size, {arg i;					//Iterate through the attacking squares that are on the board and set to true
			if(super.okAP(places.at(i,0)) && super.okAP(places.at(i,1)),
				{attackingSquares.put(places.at(i,0),places.at(i,1),true)};
			);
		});
		
				
	}
	
	updatePossibleMoves2D{
	
		//Reset possibleMoves2D
		possibleMoves2D = Array2D.new(8,8);
		
		//Scan through entire board
		for(0,7,{arg i;
			for(0,7,{arg j;
				if(attackingSquares[i,j]==true,
					{if(this.board.piecesBoard[i,j]!=nil,
						{
						if(this.board.piecesBoard[i,j].colour==this.colour,
							{possibleMoves2D[i,j]=false},
							{possibleMoves2D[i,j]="take"}
						)
						},
						{possibleMoves2D[i,j]=true}
					)}	
				)
			})
		});
		super.updatePossibleMoves;
	}
	
	/*Method for updating the position of a chessPiece*/
	chessPosition_{ arg newPosition;
		var ret;
		ret = super.chessPosition_(newPosition);
		^ret
	}
	
	activate{
		var poss, pan, sel;
		
		if(colour=="white",
			{pan= -1},
			{pan = 1}
		);
		
		poss = List[];
		if(lastMoved, {poss.add(1)});
		if(underAttack, {poss.add(2); poss.remove(1)});
		if(isAttacking, {poss.add(3); poss.remove(1)});
		
		sel = {
			switch (poss.choose)
			{1}	{state =1; pieceSynth = Synth(\knightSynth, [\argPan, pan, \rate, 1, \endLoop, this.board.pawnBFrames, \amp, 1, \resFreq1, 150, \resFreq2, 700, \resAmp1, 0, \resAmp2, 0, \hpFreq, 500, \hpAmp, 0]) }
			{2}	{state =2; pieceSynth = Synth(\knightSynth, [\argPan, pan, \rate, 0.751, \endLoop, this.board.pawnBFrames, \amp, 0, \resFreq1, 150, \resFreq2, 700, \resAmp1, 0, \resAmp2, 0, \hpFreq, 500, \hpAmp, 1]) }
			{3}	{state =3; pieceSynth = Synth(\knightSynth, [\argPan, pan, \rate, 1.25, \endLoop, this.board.pawnBFrames/4, \amp, 1, \resFreq1, 150, \resFreq2, 700, \resAmp1, 2.5, \resAmp2, 02.5, \hpFreq, 500, \hpAmp, 0]) };
		};
		
		//free current synth if state has changed
		if(pieceSynth!=nil,
			{
				if(poss.includes(state),
					{},
					{
						pieceSynth.free;
						pieceSynth=nil;
						sel.();
					}
				)
			},
			{
			 sel.();
			}
		)

	}

	printBoard{
		super.printBoard(pieceName);
	}
	
}
	
ChessBishop : ChessPiece{
		
	var <storedRouts, <routs;
	 
	*new{arg argColour, argFile, argBoard, argPromo;
		^super.new(argColour, argBoard, argPromo).initChessBishop(argFile);
	}
	
	initChessBishop{arg argFile;
		var fileVal = super.fileAsInt(argFile);
		pieceType = "bishop";
		storedRouts = Array.newClear(3);
		routs = Array.newClear(3);		
		this.setRouts;
		if((fileVal==2) || (fileVal==5),
			{	chessPosition[0]=argFile;
				super.updateArrayPosition;
				this.updateAttackingSquares;
				super.updateAttackingSquaresChess;
				this.updatePossibleMoves2D;
				super.updateIsAttacking;
			},
			{"Bad File value".postln}
		);
		if(colour=="white", {pieceName="W.Bishop "},{if(colour=="black", {pieceName="B.Bishop "},{"Bad Colour".postln})});
	}
	
	updateAttackingSquares{
		super.resetAttackingSquares;
		for(0,7, {arg i; 							  //For all diagonal directions
			//2 o'clock
			if(super.okAP(arrayPosition[0]+i) && super.okAP(arrayPosition[1]+i),     //Check if square is on the board
				{attackingSquares.put(arrayPosition[0]+i,arrayPosition[1]+i, true)} //If on the board, set to true
			);
			//4 o'clock
			if(super.okAP(arrayPosition[0]+i) && super.okAP(arrayPosition[1]-i), 				{attackingSquares.put(arrayPosition[0]+i,arrayPosition[1]-i, true)}
			);
			//7 o'clock
			if(super.okAP(arrayPosition[0]-i) && super.okAP(arrayPosition[1]-i), 				{attackingSquares.put(arrayPosition[0]-i,arrayPosition[1]-i, true)}
			);
			//10 o'clock
			if(super.okAP(arrayPosition[0]-i) && super.okAP(arrayPosition[1]+i), 				{attackingSquares.put(arrayPosition[0]-i,arrayPosition[1]+i, true)}
			);
			
		});
		
		
	}
	
	updatePossibleMoves2D{
		
		var file, rank;			//File and rank of this piece
		var blockFile, blockRank;	//File and rank of piece blocking this piece
		var dir;					//Direction of attack
		var setFunc;				//Function for blocking squares behind a piece
		
		//2D Array representing the moves
		possibleMoves2D=Array2D.new(8,8);
		//File and Rank of this piece
		file = this.arrayPosition[0];
		rank = this.arrayPosition[1];
		
		//Set the possition of this piece to the name (importantly, not nil)
		this.possibleMoves2D[file,rank]=pieceName;
		
		//Loop through all squares on the board
		for(0,7,{arg i;
			for(0,7,{arg j;
			
				//If the piece has not been previously set
				if(possibleMoves2D[i,j]==nil,
					{
					
					//If could potentially move to this square
					if(attackingSquares[i,j]==true,
						{
						
						 //If there is a piece on this square
						 if(this.board.piecesBoard[i,j]!=nil,
						 	{	
						 		/////////////////////////////////////////////////////////////////////
						 						/*Set up variables and method*/
						 		/////////////////////////////////////////////////////////////////////
						 		
						 		//Store the position of the blocking piece
						 		blockFile = this.board.piecesBoard[i,j].arrayPosition[0];
								blockRank = this.board.piecesBoard[i,j].arrayPosition[1];
							 	
							 	//Function that sets all squares blocked by the blocking piece to false
								setFunc={arg dir;
									//File and rank of first blocked square
									var bf1, br1;	
									//Distances of that first piece from the edges
									var c1, c2;
									//Maximum board values (0,7)
									var m1, m2;
									//Direction to move in each axis (+ or -)
									var pm1, pm2;
									//Co-ordinates of square to mark as blocked
									var l1, l2;
									//Start and End values for looping
									var minVal, maxVal;
		
									case
										{dir==1} {																								bf1 = blockFile+1;
												br1 = blockRank+1;
												c1 =(7-blockFile);
												c2 =(7-blockRank);
												m1=7;
												m2=7;
												pm1="+";
												pm2="+";
												
											
											}
											
									{dir==4}	{
												bf1 = blockFile+1;
												br1 = blockRank-1;
												c1 =(7-blockFile);
												c2 =blockRank;
												m1=7;
												m2=0;
												pm1="+";
												pm2="-";
											}
									
									{dir==7}	{
												bf1 = blockFile-1;
												br1 = blockRank-1;
												c1 = blockFile;
												c2 = blockRank;
												m1 = 0;
												m2 = 0;
												pm1 = "-";
												pm2 = "-";
											}	
									
									{dir==10}	{
												bf1 = blockFile-1;
												br1 = blockRank+1;
												c1 = blockFile;
												c2 = (7-blockRank);
												m1 = 0;
												m2 = 7;
												pm1 = "-";
												pm2 = "+";
											};										
									//If the first blocked square would be off the board,
									//Set the first blocked square values to the board maximum
									if(bf1>7,{bf1=7});
									if(bf1<0,{bf1=0});
								 	if(br1>7,{br1=7});
								 	if(br1<0,{br1=0});
								 	
								 	//Check if the blocking piece is closer to the edge of the x or y axis.
								 	//Use the direction that is closer for looping
								 	if(c1<=c2, 
									{minVal=bf1; maxVal=m1},
									{minVal=br1; maxVal=m2}
									);
									
									//Loop through all squares between the blocking piece and the edge of the board
									//Setting values to blocked
									for(minVal, maxVal,
										{arg k;
										
										if(pm1=="+", {l1=(bf1+((k-minVal).abs))});
										if(pm1=="-", {l1=(bf1-((k-minVal).abs))});
										if(pm2=="+", {l2=(br1+((k-minVal).abs))});
										if(pm2=="-", {l2=(br1-((k-minVal).abs))});
										
										possibleMoves2D[l1, l2]=false};
										
									)
															 		
								};
								
								/////////////////////////////////////////////////////////////////////
						 						/*Setup blocked pieces*/
						 		/////////////////////////////////////////////////////////////////////


							 	//Check direction of the blocking piece, relative to this piece
							 	//dir numbers refer to analog clock hour direction
							 	case
							 	{(i>=file) && (j>=rank)} {dir = 1}
							 	{(i>=file) && (j<=rank)} {dir = 4}
							 	{(i<=file) && (j<=rank)} {dir = 7} 
							 	{(i<=file) && (j>=rank)} {dir = 10};
								
								//If the piece is on the same team, can't move there,
								//If on enemy team, can take;
							 	if(this.board.piecesBoard[i,j].colour==this.colour,
							 		{possibleMoves2D[i,j]=false},
							 		{possibleMoves2D[i,j]="take"}
							 	);
							 	
																
								//Call the setFunction for the direction of the blocking square
								setFunc.value(dir);
											
						 	},
						 	
						 	{
						 	//If can attack the square, and no piece is in it, set square as a possble move
						 	possibleMoves2D[i,j]=true
						 	}
						 )
						})
					}
				);		
			})
	});	
			
			
	//Update the possibleMoves array (moves stored in chess values)
	super.updatePossibleMoves;	
		
		
	}
	
	/*Method for updating the position of a chessPiece*/
	chessPosition_{ arg newPosition;
		var ret;
		ret = super.chessPosition_(newPosition);
		^ret
	}
	
	printBoard{
		super.printBoard(pieceName);
	}
	
	setRouts{
		//Normal
		storedRouts[0] = {
			Routine.new({
				inf.do({
					pieceSynth.set(\lev0, 0, \lev1, 0, \lev2, (1/3), \lev3, 0, \lev4, 0, \lev5, (1/3), \lev6, (1/3), \lev7, 0, \lev8, 0 );
					3.wait;
					pieceSynth.set(\lev5, 0, \lev4, (1/3));
					3.wait;
				})
			})
		};
		
		//Under Attack
		storedRouts[1] = {
			Routine.new({	
				inf.do({
					pieceSynth.set(\lev0, (1/3), \lev1, 0, \lev2, 0, \lev3, 0, \lev4, 0, \lev5, (1/3), \lev6, 0, \lev7, (1/3), \lev8, 0 );
					3.wait;
					pieceSynth.set(\lev3, (1/3), \lev5, 0, \lev6, (1/3), \lev7, 0);
					3.wait;
				})
			})
		};
		
		//Attacking	
		storedRouts[2] = {
			Routine.new({
				inf.do({
					pieceSynth.set(\lev0, (1.5/6), \lev1, (1.5/6), \lev2, (1/6), \lev3, 0, \lev4, 0, \lev5, (1/6), \lev6, (1.5/6), \lev7, 0, \lev8, (1.5/6) );
					3.wait;
					pieceSynth.set(\lev5, 0, \lev4, 0); //(1/3));
					3.wait;
				})
			})
		};
		
	}
	
	stopRouts{
		routs.do({|item| item.stop});
	}
	
	activate{
		var poss, pan, sel;
		
		if(colour=="white",
			{pan= -1},
			{pan = 1}
		);
				
		poss = List[];
		if(lastMoved, {poss.add(1)});
		if(underAttack, {poss.add(2); poss.remove(1)});
		if(isAttacking, {poss.add(3); poss.remove(1)});
		
		sel = {
			pieceSynth = Synth(\bishopSynth, [\amp, 1, \pan, pan]);
			switch (poss.choose)
			{1}	{state =1; routs[0]=storedRouts[0].().play}
			{2}	{state =2; routs[1]=storedRouts[1].().play}
			{3}	{state =3; routs[2]=storedRouts[2].().play};
		};
		
		//free current synth if state has changed
		if(pieceSynth!=nil,
			{
				if(poss.includes(state),
					{},
					{
						pieceSynth.free;
						pieceSynth=nil;
						this.stopRouts;
						sel.();
					}
				)
			},
			{
			 sel.();
			}
		)
	}
	
}

ChessQueen : ChessPiece{
	var freq, r;
	
	*new{arg argColour, argFile, argBoard, argPromo;
		^super.new(argColour, argBoard, argPromo).initChessQueen(argFile);
	}
	
	initChessQueen{arg argFile;
		pieceType = "queen";
		chessPosition[0] = argFile;			//Queens always start on the same file
		super.updateArrayPosition;			//But may be created on any file when upgrading
		this.updateAttackingSquares;
		super.updateAttackingSquaresChess;
		if(colour=="white", 
			{pieceName=" W.Queen "},
			{pieceName=" B.Queen "}
		);	
	}
	
	updateAttackingSquares{
		super.resetAttackingSquares;
		
		//12,3,6,9 o'clock
		for(0,7, {arg i; 				//Set rank/file squares as under attack (as with rook)
					attackingSquares.put(arrayPosition[0],i,true);
					attackingSquares.put(i,arrayPosition[1],true)
		});


		for(0,7, {arg i; 				//Set diagonal squares as under attack (as with bishop)
		
			//2 o'clock
			if(super.okAP(arrayPosition[0]+i) && super.okAP(arrayPosition[1]+i), 				{attackingSquares.put(arrayPosition[0]+i,arrayPosition[1]+i, true)}
			);
			//4 o'clock
			if(super.okAP(arrayPosition[0]+i) && super.okAP(arrayPosition[1]-i), 				{attackingSquares.put(arrayPosition[0]+i,arrayPosition[1]-i, true)}
			);
			//7 o'clock
			if(super.okAP(arrayPosition[0]-i) && super.okAP(arrayPosition[1]-i), 				{attackingSquares.put(arrayPosition[0]-i,arrayPosition[1]-i, true)}
			);
			//10 o'clock
			if(super.okAP(arrayPosition[0]-i) && super.okAP(arrayPosition[1]+i), 				{attackingSquares.put(arrayPosition[0]-i,arrayPosition[1]+i, true)}
			);
			
		});
			
		
	}
	
	updatePossibleMoves2D{
		
		var file, rank;			//File and rank of this piece
		var blockFile, blockRank;	//File and rank of piece blocking this piece
		var dir;					//Direction of attack
		var setFuncR, setFuncB;		//Function for blocking squares behind a piece
		
		//2D Array representing the moves
		possibleMoves2D=Array2D.new(8,8);
		//File and Rank of this piece
		file = this.arrayPosition[0];
		rank = this.arrayPosition[1];
		
		//Set the possition of this piece to the name (importantly, not nil)
		this.possibleMoves2D[file,rank]=pieceName;
		
		setFuncR ={arg rof, maxVal;

			var change, minVal, pmNil, pbNNil, pmSet, pbCol;
			
			if(rof==file,{change = rank},{change=file});
			
			for(change, maxVal,
				{arg i;
					
					//Function that checks if a possibleMove2D square is Nil
					//Works for either situation (when checking a rank or file)
					pmNil = {if(rof==file,
								{if(this.possibleMoves2D[file,i]==nil,{true},{false})},
								{if(this.possibleMoves2D[i,rank]==nil,{true},{false})}
							)
					};
					
					//Function that sets a possibleMove2D 
					//Works for either situation (when checking a rank or file)
					//arg pos Square in rank or file to set
					//arg val The value to set the square to
					pmSet = {arg pos, tof;
							if(rof==file,
								{this.possibleMoves2D[file, pos]=tof},
								{this.possibleMoves2D[pos, rank]=tof}
							)
					};
					
					//Function that checks if a square on the board is Not Nil (if there is a piece there)
					//Works for either situation (when checking a rank or file)
					pbNNil = {if(rof==file,
								{if(this.board.piecesBoard[file,i]!=nil,{true},{false})},
								{if(this.board.piecesBoard[i,rank]!=nil,{true},{false})}
							)
					};
					
					//Function that checks if the colour of a piece on the board is the same as the colour of this piece					//Works for either situation (when checking a rank or file)
					pbCol = {if(rof==file,
								{if(this.board.piecesBoard[file,i].colour==this.colour,{true},{false})},
								{if(this.board.piecesBoard[i,rank].colour==this.colour,{true},{false})}
							)
					};
					
					//If not already set
					if(pmNil.value){
					
						//If there is a piece in the square
						if(pbNNil.value,
							{
								//If the piece is on the same team
								if(pbCol.value,
									//Set move as false
									{pmSet.value(i, false)},
									//Set move as true (take)
									{pmSet.value(i, "take" )}
								);
								//For all squares after that piece
								if(maxVal==7,
									{
										if( (i+1)>7,{minVal=7},{minVal=(i+1)} );
										for(minVal,maxVal,
											//Set to false
											{arg j; pmSet.value(j, false)}
										)
									},
									{	
										if( (i-1)<0,{minVal=0},{minVal=(i-1)} );
										for(minVal,maxVal,
											//Set to false
											{arg j; pmSet.value(j, false)}
										)
									}	
								)
							},
							//If there is no piece, set to true
							{pmSet.value(i, true)}
						)
					}
				}
			);
		};
		
		//Set possible moves for each direction from the piece
		setFuncR.value(rank,0);
		setFuncR.value(rank,7);
		setFuncR.value(file,0);
		setFuncR.value(file,7);



		//Loop through all squares on the board
		for(0,7,{arg i;
			for(0,7,{arg j;
			
				//If the piece has not been previously set
				if(possibleMoves2D[i,j]==nil,
					{
					
					//If could potentially move to this square
					if(attackingSquares[i,j]==true,
						{
						
						 //If there is a piece on this square
						 if(this.board.piecesBoard[i,j]!=nil,
						 	{	
						 		/////////////////////////////////////////////////////////////////////
						 						/*Set up variables and method*/
						 		/////////////////////////////////////////////////////////////////////
						 		
						 		//Store the position of the blocking piece
						 		blockFile = this.board.piecesBoard[i,j].arrayPosition[0];
								blockRank = this.board.piecesBoard[i,j].arrayPosition[1];
							 	
							 	//Function that sets all squares blocked by the blocking piece to false
								setFuncB={arg dir;
									//File and rank of first blocked square
									var bf1, br1;	
									//Distances of that first piece from the edges
									var c1, c2;
									//Maximum board values (0,7)
									var m1, m2;
									//Direction to move in each axis (+ or -)
									var pm1, pm2;
									//Co-ordinates of square to mark as blocked
									var l1, l2;
									//Start and End values for looping
									var minVal, maxVal;
		
									case
										{dir==1} {																								bf1 = blockFile+1;
												br1 = blockRank+1;
												c1 =(7-blockFile);
												c2 =(7-blockRank);
												m1=7;
												m2=7;
												pm1="+";
												pm2="+";
												
											
											}
											
									{dir==4}	{
												bf1 = blockFile+1;
												br1 = blockRank-1;
												c1 =(7-blockFile);
												c2 =blockRank;
												m1=7;
												m2=0;
												pm1="+";
												pm2="-";
											}
									
									{dir==7}	{
												bf1 = blockFile-1;
												br1 = blockRank-1;
												c1 = blockFile;
												c2 = blockRank;
												m1 = 0;
												m2 = 0;
												pm1 = "-";
												pm2 = "-";
											}	
									
									{dir==10}	{
												bf1 = blockFile-1;
												br1 = blockRank+1;
												c1 = blockFile;
												c2 = (7-blockRank);
												m1 = 0;
												m2 = 7;
												pm1 = "-";
												pm2 = "+";
											};										
									//If the first blocked square would be off the board,
									//Set the first blocked square values to the board maximum
									if(bf1>7,{bf1=7});
									if(bf1<0,{bf1=0});
								 	if(br1>7,{br1=7});
								 	if(br1<0,{br1=0});
								 	
								 	//Check if the blocking piece is closer to the edge of the x or y axis.
								 	//Use the direction that is closer for looping
								 	if(c1<=c2, 
									{minVal=bf1; maxVal=m1},
									{minVal=br1; maxVal=m2}
									);
									
									//Loop through all squares between the blocking piece and the edge of the board
									//Setting values to blocked
									for(minVal, maxVal,
										{arg k;
										
										if(pm1=="+", {l1=(bf1+((k-minVal).abs))});
										if(pm1=="-", {l1=(bf1-((k-minVal).abs))});
										if(pm2=="+", {l2=(br1+((k-minVal).abs))});
										if(pm2=="-", {l2=(br1-((k-minVal).abs))});
										
										possibleMoves2D[l1, l2]=false};
										
									)
															 		
								};
								
								/////////////////////////////////////////////////////////////////////
						 						/*Setup blocked pieces*/
						 		/////////////////////////////////////////////////////////////////////


							 	//Check direction of the blocking piece, relative to this piece
							 	//dir numbers refer to analog clock hour direction
							 	case
							 	{(i>=file) && (j>=rank)} {dir = 1}
							 	{(i>=file) && (j<=rank)} {dir = 4}
							 	{(i<=file) && (j<=rank)} {dir = 7} 
							 	{(i<=file) && (j>=rank)} {dir = 10};
								
								//If the piece is on the same team, can't move there,
								//If on enemy team, can take;
							 	if(this.board.piecesBoard[i,j].colour==this.colour,
							 		{possibleMoves2D[i,j]=false},
							 		{possibleMoves2D[i,j]="take"}
							 	);
							 	
																
								//Call the setFunction for the direction of the blocking square
								setFuncB.value(dir);
											
						 	},
						 	
						 	{
						 	//If can attack the square, and no piece is in it, set square as a possble move
						 	possibleMoves2D[i,j]=true
						 	}
						 )
						})
					}
				);		
			})
	});	
			
			
	//Update the possibleMoves array (moves stored in chess values)
	super.updatePossibleMoves;	
		
		
	}
	
	/*Method for updating the position of a chessPiece*/
	chessPosition_{ arg newPosition;
		var ret;
		ret = super.chessPosition_(newPosition);
		^ret
	}
	
	activate{
		var poss, pan, sel;
		
		if(colour=="white",
			{pan= -1},
			{pan = 1}
		);
		
		poss = List[];
		if(lastMoved, {poss.add(1)});
		if(underAttack, {poss.add(2); poss.remove(1)});
		if(isAttacking, {poss.add(3); poss.remove(1)});
		
		sel = {
			switch (poss.choose)
			{1}	{state =1; pieceSynth = Synth(\queenSynth, [\argPan, pan, \rate, 1, \endLoop, this.board.pawnBFrames, \amp, 1, \resFreq1, 150, \resFreq2, 700, \resAmp1, 0, \resAmp2, 0, \hpFreq, 500, \hpAmp, 0]) }
			{2}	{state =2; pieceSynth = Synth(\queenSynth, [\argPan, pan, \rate, 0.751, \endLoop, this.board.pawnBFrames, \amp, 0, \resFreq1, 150, \resFreq2, 700, \resAmp1, 0, \resAmp2, 0, \hpFreq, 500, \hpAmp, 1]) }
			{3}	{state =3; pieceSynth = Synth(\queenSynth, [\argPan, pan, \rate, 1.25, \endLoop, this.board.pawnBFrames/4, \amp, 1, \resFreq1, 150, \resFreq2, 700, \resAmp1, 2.5, \resAmp2, 02.5, \hpFreq, 500, \hpAmp, 0]) };
		};
		
		//free current synth if state has changed
		if(pieceSynth!=nil,
			{
				if(poss.includes(state),
					{},
					{
						pieceSynth.free;
						pieceSynth=nil;
						sel.();
					}
				)
			},
			{
			 sel.();
			}
		)
	}
		
	printBoard{
		super.printBoard(pieceName);
	}
	
}

ChessKing : ChessPiece{

	var <storedRouts, <routs, stopRouts;
	
	*new{arg argColour, argBoard;
		^super.new(argColour, argBoard, false).initChessKing;
	}
	
	initChessKing{
		pieceType = "king";
		chessPosition[0] = "e";
		storedRouts = Array.newClear(3);
		routs = Array.newClear(3);
		super.updateArrayPosition;
		this.updateAttackingSquares;
		super.updateAttackingSquaresChess;
		if(colour=="white", {pieceName="  W.King "},{if(colour=="black", {pieceName="  B.King "},{"Bad Colour".postln})});
		this.setRoutines;
	}
	
	updateAttackingSquares{
		var posSquares = Array2D.new(8,2);		//2D array that stores the relative location of the attacking squares and whether or not
											//they are on the board
		super.resetAttackingSquares;			//Set all squares currently under attack to false									
		posSquares.put(0,0, [1,1]);				//Set relative co-ordinates for squares under attack
		posSquares.put(1,0, [1,0]);
		posSquares.put(2,0, [1,-1]);
		posSquares.put(3,0, [0,-1]);
		posSquares.put(4,0, [-1,-1]);
		posSquares.put(5,0, [-1,0]);
		posSquares.put(6,0, [-1,1]);
		posSquares.put(7,0, [0,1]);									
		for(0, posSquares.colAt(1).size-1, {arg i; posSquares.put(i,1, true)}); //Set all possible squares to true
		
		
										//If any attacking squares are off the board, set to false
		if(arrayPosition[0] == 0,{ posSquares.put(4,1,false); posSquares.put(5,1,false); posSquares.put(6,1,false) });
		if(arrayPosition[1] == 0,{ posSquares.put(2,1,false); posSquares.put(3,1,false); posSquares.put(4,1,false) });
		if(arrayPosition[0] == 7,{ posSquares.put(0,1,false); posSquares.put(1,1,false); posSquares.put(2,1,false) });
		if(arrayPosition[1] == 7,{ posSquares.put(0,1,false); posSquares.put(6,1,false); posSquares.put(7,1,false) });
		
		for(0, 7, {arg i; 	 					//Set attacking squares that are on the board to true
				if(posSquares.at(i,1)==true, 
					{attackingSquares.put(arrayPosition[0]+posSquares[i,0][0], arrayPosition[1]+posSquares[i,0][1], true)}
				);			
		});
		
		
	}
	
	updatePossibleMoves2D{
	
		//Loop through all squares
		for(0,7,{arg i;
			for(0,7,{arg j;
				
				//If can attack square
				if(attackingSquares[i,j]==true,
					//If there is a piece in square
					{if(this.board.piecesBoard[i,j] !=nil,
						//If the piece is on same team
						{if(this.board.piecesBoard[i,j].colour==this.colour,
							//Set true
							{this.possibleMoves2D[i,j]=false},
							//If enemy, set false
							{this.possibleMoves2D[i,j]="take"}
						)},
						{this.possibleMoves2D[i,j]=true}
					)}
				)
			})
		});
		
		super.updatePossibleMoves;
	
	}
	
	//Method to set up synth routines for various states
	//0 Normal
	//1 Under Attack
	//2 Attacking
	
	setRoutines{
		storedRouts[0] = {
			Routine.new({
				inf.do({	
					 Synth(\kingSynth, [\freqMul, 300]);
					2.wait;
					 Synth(\kingSynth, [\freqMul, 400]);
				});	
			});
		};
					
		storedRouts[1] = {
			Routine.new({
				inf.do({
					2.do({
						 Synth(\kingSynth, [\freqMul, 475, \vRate1, 0.5, \vRate2, 2, \tRate1, 0.05, \tRate2, 1.5]);
						2.wait;
					});
					2.do({
						 Synth(\kingSynth, [\freqMul, 450, \vRate1, 0.5, \vRate2, 2, \tRate1, 0.05, \tRate2, 1.5]);
						1.wait;
					});
				});
			});
		};
		
		storedRouts[2] = {
		
			Routine.new({
				inf.do({	
					2.do({
						 Synth(\kingSynth, [\freqMul, 475, \vRate1, 0.5, \vRate2, 2, \tRate1, 0.05, \tRate2, 1.5]);
						1.5.wait;
					});
					
					2.do({
						 Synth(\kingSynth, [\freqMul, 450, \vRate1, 0.5, \vRate2, 2, \tRate1, 0.05, \tRate2, 1.5]);					1.5.wait
					});
				});
			});
		};
	}
	
	/*Method for updating the position of a chessPiece*/
	chessPosition_{ arg newPosition;
		var ret;
		ret = super.chessPosition_(newPosition);
		^ret
	}
	
	stopRouts{
		routs.do({|item| item.stop});
	}
	
	activate{
		var poss, pan, sel;
		
		if(colour=="white",
			{pan= -1},
			{pan = 1}
		);
			
		poss = List[];
		if(lastMoved, {poss.add(1)});
		if(underAttack, {poss.add(2); poss.remove(1)});
		if(isAttacking, {poss.add(3); poss.remove(1)});
		
		sel = {
			this.stopRouts;
			switch (poss.choose)
			{1}	{state =1; routs[0]=storedRouts[0].().play}
			{2}	{state =2; routs[1]=storedRouts[1].().play}
			{3}	{state =3; routs[2]=storedRouts[2].().play};
		
		};
		
		//free current synth if state has changed
		if(pieceSynth!=nil,
			{
				if(poss.includes(state),
					{},
					{
						pieceSynth=nil;
						sel.();
					}
				)
			},
			{
			 sel.();
			}
		)
	}
		
	printBoard{
		super.printBoard(pieceName);
	}
	
	
}
