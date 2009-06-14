ChessBoard{
	classvar <fileNumbers; //Look up table for file values as numbers
	var <>piecesBoard, <>turn, <>temp; // Variables
	var <>whitePieces, <>blackPieces;
	var <>whitePawns, <>blackPawns, <>whiteRooks, <>blackRooks, <>whiteKnights, <>blackKnights, <>whiteBishops, <>blackBishops, <>whiteQueen, <>blackQueen, <>whiteKing, <>blackKing; //Arrays to keep track of pieces of each type
	var move, printBoard, createPieces, setPieces, fileAsInt, chessAsArray;
	var <>whiteLastMoved, <>blackLastMoved;
	var <pawnB, <pawnBFrames;
	var <knightB, <knightBFrames;
	var <queenB, <queenBFrames;
	var <bishopB, <bishopBFrames;
	var <deathB, <winnerB, <loserB;
		
	*new {
		fileNumbers = ["a", "b", "c", "d", "e", "f", "g", "h"];
		^super.new.initChessBoard;
	}
	
	initChessBoard{
	
			
		piecesBoard = Array2D.new(8,8);		//Array representing the board
		turn = "white";						//Initialise turn to white
		temp = Array.newClear(16);			//Array for temporarily storing new pieces
		
		whitePieces = Array.newClear(16);
		whitePawns = Array.newClear(8);		//Arrays for accessing piece types
		whiteRooks = Array.newClear(10);		//Large array to account for piece upgrades (2 + 8 pawns)
		whiteKnights = Array.newClear(10);	
		whiteBishops = Array.newClear(10);
		whiteQueen = Array.newClear(9);		
		whiteKing = Array.newClear(1);
		
		blackPieces = Array.newClear(16);
		blackPawns = Array.newClear(8);
		blackRooks = Array.newClear(10);
		blackKnights = Array.newClear(10);
		blackBishops = Array.newClear(10);
		blackQueen = Array.newClear(9);
		blackKing = Array.newClear(1);
		
		
		//Add Pawns to board
		this.createPawns;
				
		//Add Rooks to board
		this.createPieces(ChessRook, "a", "h");
		
		//Add Knights to board
		this.createPieces(ChessKnight,"b","g");
		
		//Add Bishops to board
		this.createPieces(ChessBishop,"c","f");
		
		//Add Queens and Kings to board
		this.createKQ;
		
		//Update the possible moves for all the pieces
		for(0,7,{arg i;
			for(0,7,{arg j;
				if(piecesBoard[i,j]!=nil, {piecesBoard[i,j].updatePossibleMoves2D})
			})
		});
		
		this.setSynthDefs;
	}
	
	/*Method to create pawns*/
	createPawns{
	
		for(0,7, {arg i;	
						//Create a new white pawn, store it in the temp array
						temp[i] = ChessPawn.new("white", fileNumbers[i], this);
						//Add the pawn to the white pawn array for lookups
						whitePawns[i] = temp[i];
						whitePieces[whitePieces.indexOf(nil)] = temp[i];
						//Create a new black pawn, store it in the temp array
						temp[i+8] = ChessPawn.new("black", fileNumbers[i], this);
						//Add the pawn to the black pawn array for lookups
						blackPawns[i] = temp[i+8];
						blackPieces[blackPieces.indexOf(nil)] = temp[i+8];
		});
		//Add the pieces to the board
		this.setPieces;
	}
	
	/*Method to create kings and queens*/
	createKQ{
		var tArray;
		//Loop twice, once for each colour
		2.do({arg i;
				var col;
				//Check which colour loop
				if(i==0,{col="white"; tArray = whitePieces},{col="black"; tArray = blackPieces}); 
				//Create a new queen (in a temp array)
				temp[i] = ChessQueen.new(col, "d", this, false);
				//Add it to the team array
				tArray[tArray.indexOf(nil)] = temp[i];
				//Check colour, add to correct piece array
				if(i==0,{whiteQueen[0] = temp[i]},{blackQueen[0]=temp[i]});
				//Create a new King
				temp[i+8]= ChessKing.new(col, this);
				//Add it to the team Array
				tArray[tArray.indexOf(nil)] = temp[i+8];
				//Add king to the correct piece array
				if(i==0,{whiteKing[0] = temp[i+8]},{blackKing[0]=temp[i+8]});
		});
		
		//Call method to add the pieces to the board
		this.setPieces;
	}
	
	/*Method to create pieces of type Rook, Knight, Bishop*/
	//argPiece: 		Type of piece to be created
	//argFile: 	First file the new pieces belong to
	//argFile2:	Second file the new pieces belong to
	
	createPieces{arg argPiece, argFile1, argFile2;
	
		var col, pArray, pArrayFunc;
		
		//Function to case check to set the right array to store the piece in
		pArrayFunc={
		case
			{col=="white" && argPiece==ChessRook}		{pArray=whiteRooks}
			{col=="white" && argPiece==ChessKnight}	{pArray=whiteKnights}			{col=="white" && argPiece==ChessBishop}	{pArray=whiteBishops}

			{col=="black" && argPiece==ChessRook}		{pArray=blackRooks}
			{col=="black" && argPiece==ChessKnight}	{pArray=blackKnights}			{col=="black" && argPiece==ChessBishop}	{pArray=blackBishops}

		};			
		
		for(0,1, {arg i;			//Perform twice, once for each colour
					var file;			//File of individual piece
					var add;			//Used to ensure black pieces are put in correct position in temp
					var tArray;		//Array of that team
					
					if(i==0,{col="white"; add=0; tArray =whitePieces},{col="black";add=1;tArray=blackPieces});          //Check colour
					pArrayFunc.value;			//Select correct array
					for(0,1,{arg j; 			//Generate two pieces
						if(j==0,
							{file=argFile1},
							{file=argFile2}
						);   
						temp[i+j+add] = argPiece.new(col,file,this,false); //Placing into temp array
						pArray[j] = temp[i+j+add];	//Add to pieces array
						tArray[tArray.indexOf(nil)] = temp[i+j+add]; //Add to team array
					}); 			
		});
		this.setPieces;
	}
	
	/*Method to put newly created pieces onto the board*/
	setPieces{
		//Run through temporary array
		for(0,temp.size-1, 
			{arg i;
				//If piece exists 		
				if(temp[i]!=nil,		
					//Place it on the board
					{
						piecesBoard.put(										temp[i].arrayPosition[0],
							temp[i].arrayPosition[1], 
							temp[i])
					}
				);
								
			}		
		);
		temp.fill(nil)							//Reset temporary array
		
		
	}
	
	
	/*Method for moving pieces*/
	/*Takes a an algebraic chess move as an arguement, e.g. e4 or Bxf7+*/	/*Determines which piece is to be moved, and moves it*/		  
	move{arg input;
		var pieces, files, ranks;
		var target, target2, targetAsArray, targetAsArray2, targetAsSymbol, piecesArray;
		var capPiece, capType, capArray;
		var colour, type, movingPiece, movingPiece2, movingFrom, movingFrom2;
		var fromFile, fromRank;
		var castleFunc, findTargetFunc, setTypeFunc, checkSpecified, findMovingFunc;
		var postMoveFunc, captureFunc, movePieceFunc, promotionFunc, updateLastMoved, checkActiveFunc;
		var type2, pieceTemp, ugArray, tArray;
		var pan;
		
                //////////////////////////////// Variable Initialisation /////////////////// 
		
		pieces = ['R','N','B','Q','K'];				//Array of symbols to check if moving piece is a piece (ie Not a pawn)
		files = ['a','b','c','d','e','f','g','h']; 	//Array of files as symbols to check if piece is a pawn
		ranks = ['1','2','3','4','5','6','7','8']; 	//Array of rank numbers as symbols for finding the target
		
               /////////////////////////////////////// Sub-methods //////////////////////////////////// 
		
		/*Searches through the input for the target square, always found as being a-h followed by a Number*/
		findTargetFunc = {
			for(0,input.size-1,{arg i;
				if(files.any{|j| j==input[i].asSymbol} && ranks.any{|k| k==input[(i+1)].asSymbol},
					//Set target stored in Array
					{target = [input[i].asString, input[i+1]]; 
					//Set target stored as String
					 targetAsSymbol = (input[i].asString++input[i+1]).asSymbol; 
					}
				)
			});
			
			//Set targetAsArray (Target as array values)
			targetAsArray = this.chessAsArray([target[0],ranks.indexOf(target[1].asSymbol)+1]);
			target[1] = targetAsArray[1]+1; //Change target rank from String to Int 
		};
		
		/*Sub-method to perform castling actions*/
		castleFunc={
		if(turn=="white",
					{	
						movingPiece=this.whiteKing[0];
						
						case
						{input=="O-O"}	{	
											target=["g",1];
											targetAsArray=[6,0]; 
											target2=["f",1];
											targetAsArray2=[5,0];
											movingPiece2=this.whiteRooks[1];
										}
						{input=="O-O-O"}	{	
											target=["c",1];
											targetAsArray=[2,0]; 
											target2=["d",1];
											targetAsArray2=[3,0];
											movingPiece2=this.whiteRooks[0];
										};
						
						
					},
					{	
						movingPiece=blackKing[0];
						
						case
						{input=="O-O"}	{	
											target=["g",8];
											targetAsArray=[6,7]; 
											target2=["f",8];
											targetAsArray2=[5,7];
											movingPiece2=this.blackRooks[1];
										}
										
						{input=="O-O-O"}	{	
											target=["c",8];
											targetAsArray=[2,7]; 
											target2=["d",8];
											targetAsArray2=[3,7];
											movingPiece2=this.blackRooks[0];
										};
						
					}	
				)
		};
		
		/*Sub-method to set the type of the moving piece and the array it belongs to*/
		setTypeFunc = {
			//Convert first char of input into a symbol for comparison
			type=input[0].asSymbol;
				
			//Set piece type, 		
			if(pieces.any{|x| x==type},		//If first char is a 'piece' (non-pawn)
							{	switch (type,	//Switch to determine which piece
									'R',   	{type="rook"},
									'N', 	{type="knight" },
									'B', 	{type="bishop"},
									'Q', 	{type="queen"},
									'K', 	{type="king"}
								)
							},
							{if(files.any{|x| x==type},{type="pawn"})});  //Else if piece is a pawn, set appropriately

			//Case test to set array to search for correct piece
			case
				{(colour=="white")&&(type=="pawn")}	{piecesArray=whitePawns}
				{(colour=="white")&&(type=="rook")}	{piecesArray=whiteRooks}
				{(colour=="white")&&(type=="knight")}	{piecesArray=whiteKnights}
				{(colour=="white")&&(type=="bishop")}	{piecesArray=whiteBishops}
				{(colour=="white")&&(type=="queen")}	{piecesArray=whiteQueen}
				{(colour=="white")&&(type=="king")}	{piecesArray=whiteKing}
				
				{(colour=="black")&&(type=="pawn")}	{piecesArray=blackPawns}
				{(colour=="black")&&(type=="rook")}	{piecesArray=blackRooks}
				{(colour=="black")&&(type=="knight")}	{piecesArray=blackKnights}
				{(colour=="black")&&(type=="bishop")}	{piecesArray=blackBishops}
				{(colour=="black")&&(type=="queen")}	{piecesArray=blackQueen}
				{(colour=="black")&&(type=="king")}	{piecesArray=blackKing};
		};
		
		/*Sub-method to check if the piece is specified in notation (by rank)*/
		checkSpecified = {	
			
			///If piece is a pawn
			if(type=="pawn",
				
				{	
					//If the second symbol is x (capture)
					if(input[1].asSymbol == 'x',
						//Set the file of piece as input[0]
						{
							fromFile=input[0].asSymbol;
							if(turn=="white", {pan=1},{pan= -1});
							Synth(\eventSynth, [\pan, pan, \bufnum, deathB.bufnum])
						}
					)},
				{	
					//If the second char is a a-h
					if(files.any{|k| k==input[1].asSymbol},
							{	
								//if the third char is a-h or x
								if( (files.any{|k| k==input[2].asSymbol}) || (input[2].asSymbol == 'x'),
									//the second char is the 'from' file
									{fromFile=input[1].asSymbol})
							},
							{
								//If the second char is a number (1-8)
								if(ranks.any{|l| l==input[1].asSymbol},
									{fromRank=input[1].asSymbol}
								)
							}
						)
				}
				
			);
		};
		
		/*Sub-method to find the piece to move*/
		findMovingFunc = {
		
		//If piece has been specified by file or rank, set that as moving piece
			if( (fromFile !=nil) || (fromRank !=nil),
				{	
					//Search through all the pieces of that type
					for(0, piecesArray.size-1, 
					{arg i;
						//If there is a piece in the array
						if(piecesArray[i]!=nil,
							{	
								//If the specified rank or file matches that of the piece
								if( ((piecesArray[i].chessPosition[0].asSymbol)==fromFile) || ((piecesArray[i].chessPosition[1].asSymbol)==fromRank),
									{	
										//If the piece can legally move to the target square
										if(piecesArray[i].possibleMoves.includes(targetAsSymbol),
											{ 
												//Set piece as that to move
												movingPiece=piecesArray[i]
											}
										)
									}	
								)
							}
						);
					}); 
				},
				{			
					//If the piece has not been specified
					
					//Loop through the array for pieces of the type
					for(0, piecesArray.size-1, 
						{arg i;
						//If there is a piece in the array
						if(piecesArray[i]!=nil,{
							//If the piece can legally move to the target square
							if(piecesArray[i].possibleMoves.includes(targetAsSymbol),
								//Set that piece as the one to move
								{movingPiece=piecesArray[i]}
							)}
						);
					});
	
					}
			);
		};
		
		/*Sub-method to post information regarding the moving piece. E.g. "Moving Piece White Rook ["a", 1] to a4*/
		postMoveFunc = {
		
			"Moving Piece ".post; 
			movingPiece.colour.post; 
			movingPiece.pieceType.post; 
			movingPiece.chessPosition.post;
			" to ".post;
			targetAsSymbol.postln;
		};
		
		/*Sub-method to check if a move is a capture. In the case that it is, the caputred piece is removed*/
		captureFunc = {
		
			if(input.contains("x"),
				{
					capPiece = piecesBoard[targetAsArray[0], targetAsArray[1]];
					capType = capPiece.pieceType;
					if(turn=="white", {tArray=blackPieces},{tArray=whitePieces});
					
					case
							
						{turn=="black" && capType=="pawn"}   {capArray = whitePawns}
						{turn=="black" && capType=="rook"}   {capArray = whiteRooks}
						{turn=="black" && capType=="knight"} {capArray = whiteKnights}
						{turn=="black" && capType=="bishop"} {capArray = whiteBishops}
						{turn=="black" && capType=="queen"}  {capArray = whiteQueen}
						
						{turn=="white" && capType=="pawn"}   {capArray = blackPawns}
						{turn=="white" && capType=="rook"}   {capArray = blackRooks}
						{turn=="white" && capType=="knight"} {capArray = blackKnights}
						{turn=="white" && capType=="bishop"} {capArray = blackBishops}
						{turn=="white" && capType=="queen"}  {capArray = blackQueen};
					
					//Release and nil the captured piece's synth
					if(capPiece.pieceSynth!=nil,
						{
							capPiece.pieceSynth.free;
							capPiece.pieceSynth = nil;
						}
					);					
					capArray[capArray.indexOf(capPiece)] =nil;
					piecesBoard[targetAsArray[0], targetAsArray[1]]=nil;
					tArray[tArray.indexOf(capPiece)] = nil;
					
				
				}
			);
			
		};
		
		/*Sub-method to actually move the piece*/
		movePieceFunc = {
			
			//Store the square being moved from
			movingFrom = movingPiece.arrayPosition;
			//Move the piece to new position
			piecesBoard[targetAsArray[0],targetAsArray[1]] = movingPiece;
			//Remove the piece from the old position
			piecesBoard[movingFrom[0],movingFrom[1]] = nil; 
			//Tell the piece it has moved
			movingPiece.chessPosition_(target); 
			
			//Repeats above if there is a second piece moving (Castling)
			if(movingPiece2!=nil,
				{	movingFrom2 = movingPiece2.arrayPosition;
					piecesBoard[targetAsArray2[0],targetAsArray2[1]] = movingPiece2;
					piecesBoard[movingFrom2[0],movingFrom2[1]] = nil;
					movingPiece2.chessPosition_(target2);
				}
			);
			
		};

		/*Sub-method to check if a move is a promotion, and perform it.*/
		promotionFunc = {
			if(input.contains("="),	
				{
				
				type2 = (input[input.indexOf($=)+1]).asSymbol;
				
				if(turn=="white", {tArray=whitePieces},{tArray=blackPieces});
				
				case
				{type2=='Q'}	{	
								//Set piece type array
								if(turn=="white", {ugArray = whiteQueen}, {ugArray = blackQueen});
								//create a new queen
								temp =ChessQueen.new(turn, target[0], this, true);
								}
								
				{type2=='R'}	{
								if(turn=="white", {ugArray = whiteRooks}, {ugArray = blackRooks});
								temp = ChessRook.new(turn, target[0], this, true);
								}
				{type2=='N'}	{
								if(turn=="white", {ugArray = whiteKnights}, {ugArray = blackKnights});
								temp = ChessKnight.new(turn, target[0], this, true);
								}
				{type2=='B'}	{
								if(turn=="white", {ugArray = whiteBishops}, {ugArray = blackBishops});
								temp = ChessBishop.new(turn, target[0], this, true);
								};				
				
				//Remove pawn from board
				piecesBoard[movingPiece.arrayPosition[0],movingPiece.arrayPosition[1]]=nil;
				//Remove pawn from pawn array
				piecesArray[piecesArray.indexOf(movingPiece)]=nil;
				//Remove pawn from team array
				tArray[tArray.indexOf(movingPiece)] = nil;
				//Put promotion piece into empty slot in promotion array
				ugArray[ugArray.indexOf(nil)]=temp;
				//Add promoted piece to the board
				piecesBoard[targetAsArray[0],targetAsArray[1]] = temp;
				//Promoted piece set as the piece that moved
				movingPiece = temp; 
				}
			);
		};
		
		/*Sub-method to update the last moved piece*/
		updateLastMoved = {
			
			var tArray;
			//Reset the state of all pieces

			if(turn=="white", {tArray=whitePieces},{tArray=blackPieces});
			for(0, tArray.size-1,
				{arg i;
					if(tArray[i]!=nil,
						{
							tArray[i].lastMoved_(false)
						}
					)
				}
			);
			
			//Set the last moved piece as active
			movingPiece.lastMoved_(true);
			//If the move was a castle, set the second piece as active
			if(movingPiece2!=nil,
				{	movingPiece2.lastMoved_(true)}
			);
		};	
		
		/*Sub-method to check if pieces are active*/
		checkActiveFunc ={
			for(0, 7,{arg i;
					for(0,7,{arg j;
							if(piecesBoard[i,j]!=nil,
								{piecesBoard[i,j].isActive}
							)
					})
			})
		};
		
		
			

			
///////////////////////////////////////////////////////// Method //////////////////////////////////////////////////////////
				
		if( (input == "O-O") || (input == "O-O-O"),
			{	
				//Perform castling
				castleFunc.value;
			},
		
			{	
				//Find the target square
				findTargetFunc.value;

				//set the colour of the piece to move
				colour=turn;
			
				//Set the type of piece, and the pieceArray containing it.
				setTypeFunc.value;				
				
				//Checks if the starting rank or file of the piece has been specified (in cases of ambiguity)
				checkSpecified.value;
				
				//Find the piece that is to move
				findMovingFunc.value;
				
				//Checks if the move is a capture, removes the captured piece.
				captureFunc.value;
				
				//Print out the move details
				postMoveFunc.value;
				
			}
		);
		
		//Move the piece
		movePieceFunc.value;	
		
		//Update the last moved piece
		updateLastMoved.value;
		
		//Check if the move results in a piece promotion, perform promotion
		promotionFunc.value;
		
		//Update the possible moves for all pieces
		for(0,7,{arg i;
			for(0,7,{arg j;
				if(piecesBoard[i,j]!=nil,{
					piecesBoard[i,j].updatePossibleMoves2D;
					piecesBoard[i,j].updateAttackingSquares;
					piecesBoard[i,j].updateAttackingSquaresChess;
					piecesBoard[i,j].updateIsAttacking;
					}
				)
			})
		});
		
		//Update the under attack status of all pieces
		for(0,7, {arg i;
			for(0,7, {arg j;
				if(piecesBoard[i,j]!=nil,{
					piecesBoard[i,j].updateUnderAttack;
				})
			
			})}
		);
		
		//Check if pieces are active
		checkActiveFunc.value;
		
		//Update the turn
		if(turn=="white",
			{turn="black"},
			{turn="white"}
		);
		
		
/////////////////////////////////////////////////// End of Move Method //////////////////////////////////////////////
	}
	

	/*Method that returns a Chess Position as an Array Position*/
	chessAsArray{arg chessPos;
		var x;
		x=Array.newClear(2);
		x[0]=this.fileAsInt(chessPos[0]);
		x[1]=chessPos[1]-1;
		^x;
	}
	
	/*Method convert chess file (char) into array value (int) Redundant? chessAsArray[0]*/
	fileAsInt{arg argFile;		 
		 ^fileNumbers.find([argFile]);
	}
	
	
	/*Method for printing board including position and squares being attacked*/
	printBoard{

		for(7,0, {arg i;						//For each rank
			var ret= "|";						//Variable for printing
			"   ".post;
			for(0,7,{"|-----------".post;});		//Print top line
			"|".post;
			
			"".postln;
			"   ".post;
			8.do{"|           ".post};"|".post;
			
			"".postln;		
			(i+1+" ").post; 				//Print rank number
		
			8.do{"|           ".post};
			"|".post;
			"".postln;
			
				
			/*for(0,7, {arg j;
				
				if(piecesBoard[j,i]!=nil,{ret = ret +piecesBoard[j,i].pieceName+"|"}, {ret = ret +"          |"})};
			);
			ret.post;*/
			
			"   ".post;
			for(0,7,{arg j; 
				if(piecesBoard[j,i]!=nil,	{ret=ret+piecesBoard[j,i].pieceName+"|"},
										{ret=ret+"          |"});
										
										});ret.post;			"".postln;
					
			"   ".post;
			for(0,7,{"|           ".post;});
			"|".post; "".postln;
			});
			
			"   ".post;
			for(0,7,{"|-----------".post;});		//Print top line
			"|".post;
			"".postln;
		
		"      ".post;		
		for(0,7,{arg i;"   ".post;fileNumbers[i].post; "        ".post;});
		"".postln;
	
	}
	
	setSynthDefs{
		
		///////////////////////////////////////////////////// Pawn Synth ///////////////////////////////////////////////////////
		
		{
			pawnB=Buffer.read(Server.default,"sounds/chessSounds/pawnMarch.aiff");
			Server.default.sync;
			pawnBFrames = pawnB.numFrames;
			
			SynthDef(\pawnSynth,{ 
			arg out=0, rate=1, gate=1, loopRel=0, startLoop=0, endLoop=pawnBFrames, loop=1, ipol=2,
			amp=1, argPan= 0, resFreq1=150, resFreq2=700, resAmp1=0.01, resAmp2=0.01, hpFreq=150, hpAmp=0;
			
			var sig, reson1, reson2, highP;
			sig = LoopBuf.ar(1, pawnB.bufnum, BufRateScale.kr(pawnB.bufnum)*rate, gate+loopRel, startLoop, endLoop, ipol);
			reson1 = Resonz.ar(sig, resFreq1, resAmp1);
			reson2 = Resonz.ar(sig, resFreq2, resAmp2);
			highP = HPF.ar(sig, hpFreq, hpAmp);
				Out.ar(out,
					Pan2.ar(Mix.new([sig*amp, reson1, reson2, highP]), argPan))
				
			}).send(Server.default); 
			
		}.fork;
		
		///////////////////////////////////////////////////// Rook Synth ///////////////////////////////////////////////////////
		SynthDef(\rookSynth,{ arg amp=1, argPan, noiseFreq=400, oscRate=0.15, ezVal=100, carrfreq=440, modfreq=1, moddepth=0.01; 
		
		var sig, sigMod, noise, noise2, rate, out=0;
		
			rate = {arg rAmp=0.5; SinOsc.kr(oscRate, 0, rAmp, 0.1)};
			sigMod = SinOsc.ar(110);
			sig = SinOsc.ar(110 + (sigMod*250), 0, rate);
			
			noise = LFNoise1.ar(noiseFreq, rate.value(0.3),0.1);
			
			Out.ar(out,
				Pan2.ar(Mix.new([sig, noise]*amp), argPan))
			
		}).send(Server.default); 
		
		///////////////////////////////////////////////////// Knight Synth ///////////////////////////////////////////////////////
		{
			knightB=Buffer.read(Server.default,"sounds/chessSounds/knight.wav");
			Server.default.sync;
			knightBFrames = knightB.numFrames;
			
			SynthDef(\knightSynth,{ 
			arg out=0, rate=1, gate=1, loopRel=0, startLoop=0, endLoop=knightBFrames, loop=1, ipol=2,
			amp=1, argPan= 0, resFreq1=150, resFreq2=700, resAmp1=0.01, resAmp2=0.01, hpFreq=150, hpAmp=0;
			
			var sig, reson1, reson2, highP;
			sig = LoopBuf.ar(1, knightB.bufnum, BufRateScale.kr(knightB.bufnum)*rate, gate+loopRel, startLoop, endLoop, ipol);
			reson1 = Resonz.ar(sig, resFreq1, resAmp1);
			reson2 = Resonz.ar(sig, resFreq2, resAmp2);
			highP = HPF.ar(sig, hpFreq, hpAmp);
				Out.ar(out,
					Pan2.ar(Mix.new([sig*amp, reson1, reson2, highP]), argPan))
				
			}).send(Server.default); 
			
		}.fork;
		
		////////////////////////////////////////////////// Bishop Synth ////////////////////////////////////////////////////////////
		{
			bishopB = Array.newClear(9);
			bishopBFrames = Array.newClear(9);
			
			for(0,bishopB.size-1,{arg i;
				var name;
				name = "RootA";
					switch (i,
						0,		{name = "LowA"},
						1,		{name = "LowE"},
						2,		{name = "RootA"},
						3,		{name = "C"},
						4,		{name = "C#"},
						5,		{name = "D"},
						6,		{name = "E"},
						7,		{name = "F"},
						8,		{name = "HighA"}
						);
					bishopB[i]= Buffer.read(Server.default,"sounds/chessSounds/Bishop"++name++".aif");
					Server.default.sync;
					bishopBFrames[i] = bishopB[i].numFrames;
			});
			
			Server.default.sync;		
					
			SynthDef(\bishopSynth, {arg amp=0, pan, lev0=0, lev1=0, lev2=0, lev3=0, lev4=0, lev5=0, lev6=0, lev7=0, lev8=0;
				
				var sig, levs, rate, bframes;
				
				sig = Array.newClear(9);
				levs = Array.newClear(9);
				levs[0]=lev0;
				levs[1]=lev1;
				levs[2]=lev2;
				levs[3]=lev3;
				levs[4]=lev4;
				levs[5]=lev5;
				levs[6]=lev6;
				levs[7]=lev7;
				levs[8]=lev8;
				
				rate = SinOsc.kr(0.25, 0, 1, 1);
				bframes=0;
				for(0,bishopB.size-1,{arg i;
					bframes = bishopBFrames[i];
					sig[i] = LoopBuf.ar(1, bishopB[i].bufnum, BufRateScale.kr(bishopB[i].bufnum), startLoop:0, endLoop:bframes)*levs[i]*EnvGen.ar(Env.new([0,1],[0.5]));
				});
				
				Out.ar(0, Pan2.ar((amp*Mix.new(sig)),pan));
			}).send(Server.default)
		}.fork;

		/////////////////////////////////////////////////////// Queen Synth////////////////////////////////////////////////////////
		{
			queenB=Buffer.read(Server.default,"sounds/chessSounds/queen.wav");
			Server.default.sync;
			queenBFrames = queenB.numFrames;
			
			SynthDef(\queenSynth,{ 
			arg out=0, rate=1, gate=1, loopRel=0, startLoop=0, endLoop=pawnBFrames, loop=1, ipol=2,
			amp=1, argPan= 0, resFreq1=150, resFreq2=700, resAmp1=0.01, resAmp2=0.01, hpFreq=150, hpAmp=0;
			
			var sig, reson1, reson2, highP;
			sig = LoopBuf.ar(1, queenB.bufnum, BufRateScale.kr(queenB.bufnum)*rate, gate+loopRel, startLoop, endLoop, ipol);
			reson1 = Resonz.ar(sig, resFreq1, resAmp1);
			reson2 = Resonz.ar(sig, resFreq2, resAmp2);
			highP = HPF.ar(sig, hpFreq, hpAmp);
				Out.ar(out,
					Pan2.ar(Mix.new([sig*amp, reson1, reson2, highP]), argPan))
				
			}).send(Server.default); 
					
		}.fork;
		
		/////////////////////////////////////////////////// King Synth ////////////////////////////////////////////////////////////
		
		//Slightly modified version of a Bell representation by Nick Collins (from the Computational Music 1 course)
		SynthDef(\kingSynth,{arg freqMul=300, vRate1= 1, vRate2= 5, tRate1= 0.1, tRate2= 3, pan;
		
		var numpartials, spectrum, amplitudes, modfreqs1, modfreqs2, decaytimes; 
		var sig, env; 
		
		spectrum =  [ 0.5,  1, 1.19, 1.56, 2,   2.51, 2.66, 3.01, 4.1 ];
		amplitudes= [ 0.125, 0.5, 0.4, 0.25, 0.45, 0.2, 0.15, 0.3, 0.05 ];
		numpartials = spectrum.size;
		modfreqs1 = Array.rand(numpartials, vRate1, vRate2); //vibrato rates from 1 to 5 Hz 
		modfreqs2 = Array.rand(numpartials, tRate1, tRate2); //tremolo rates from 0.1 to 3 Hz 
		decaytimes = Array.rand(numpartials, 2.5, 7.5); //decay from 2.5 to 7.5 seconds
		
		sig = Mix.fill(spectrum.size, {arg i;  
					var amp, freq, freq2; 
					
					freq= (spectrum[i]+(SinOsc.kr(modfreqs1[i],0,0.005)))*freqMul; 
					freq2= (spectrum[i]+(SinOsc.kr(modfreqs1[i],0,0.005)))*600; 
					amp= 0.1* Line.kr(1,0,decaytimes[i])*(SinOsc.ar(modfreqs2[i],0,0.1,0.9)* amplitudes[i]);
					
					Pan2.ar(Mix.new([SinOsc.ar(freq, 0, amp), SinOsc.ar(freq2, 0, amp)]),1.0.rand2)}
					);
					
		env = EnvGen.ar(Env.new([1,1],[7.5]), doneAction:2);
		
		Out.ar(0, Pan2.ar(sig*env), pan); 
		}).send(Server.default);
	
	
	////////////////////////////////////////////////////////// Death/End Sound ////////////////////////////////////////////////////////////////
		{
			deathB = Buffer.read(Server.default,"sounds/chessSounds/death.wav");
			winnerB = Buffer.read(Server.default,"sounds/chessSounds/winner.wav");
			loserB = Buffer.read(Server.default, "sounds/chessSounds/loser.wav");
			Server.default.sync;
			SynthDef(\eventSynth, {|pan, bufnum=0|
				var sig;
				sig = PlayBuf.ar(1, bufnum, BufRateScale.kr(bufnum), loop:0);
				FreeSelfWhenDone.kr(sig);
				Out.ar(0, Pan2.ar(sig, pan));
			}).send(Server.default);
			Server.default.sync;
		}.fork
	
	}
	
	
		

}