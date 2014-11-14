	ORG $17
UNO EQU $30
DOS EQU $50
	BRA UNO
	LBRA DOS
	BCS UNO
	LBCS TRES
TRES BCS DOS
CUATRO SWI 
	ldaa 	,X
	ldaa	0,X 
	ldaa	1,X
	ldaa	15,X
	ldaa 	-1,X
	ldaa 	-16,X
	stab 	-8,Y
	ldaa 	255,X 
	ldaa 	34,X 
	ldaa 	-18,X 
	ldaa 	-256,X 
	ldaa 	-20,y
	LDAA 	31483,X 
	staa 	1,-SP 
	STX 	2,SP+ 
	STX 	2,+SP 
	LDAA 	B,X 
	LDAA 	A,X 
 	LDAA 	D,X
 	jmp 	[d,pc]
	ADCA 	[D,X] 
	ADCB 	[D,SP]
	ADDA 	[D,Y] 
	ldaa	#$59	;A<=59H
	adda	#$78	;A<=78H
	LDAA 	[10,X]
	LDAA 	[31483,X]
	daa
	stab	$0802
	clra
	ldaa	16
	adda	17
ini	staa	@10
	ldx	$800
	bra uno
	DB 15 
	FCC "hola mundo"
	jmp	ini
	END