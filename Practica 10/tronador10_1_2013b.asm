	ORG  $17
UNO    EQU  $30
DOS    EQU  $50
	BRA  UNO
	LBRA DOS
	BCS  UNO
	LBCS TRES
TRES   BCS  DOS
CUATRO SWI
	STS	[125,PC]
	STS	[D,SP]
	STS	,Y
	STS	0,PC
	STS	2,SP
	STS	-2,X
	STS	41,X
	STS	-41,Y
	STS	65500,PC
	STS	3,-X
	STS	4,Y-
	STS	5,+SP
	STS	6,X+
	STS	D,PC
	SUBD	#4096
	SUBD	#$00FF
	SUBD	#0
	SBCA	#$0
	SBCA	#255
	SBCA	#@75
	SUBA	4096
	SUBA	180
	IDIV
A	EQU	4
EN2	EQU	8000
EN3	EQU	3
	FDB	8000
	FCC	35
	FCC	"fiu"
	FCC	"Hi"
	FCC	"Esta es una cadena muy larga que no debe indicar error"
	RMB	8000
	RMW	8000
EN4	EQU	2
	DEX
 addd 5

 ABA
E INCA 10
 CLRA
 CLR 10
 JMP 20
 LDAA 255
 LDAA 256
 LDAA #10
E1 LDAA
 LDAA $100
 BLO 1000
 LBLO 0
E2 STAA #100
E3 ANDA #100000
E4 LDAA [100,X
E5 LDAA 100,Y]
 LDAA $A;KSHAKSJHDSAJKDKDJSALDKSAJDLKSAJDLKASJDLSA
E6 LDAA 100,X+
E7 LDAA ,
E8 LDAA 3,-X+
E9 LDAA -3,-X
 LDAA [-1000,PC]
E10 LDAA @100,PC+
 LDAA %11111111,X
 LDAA %111111111,Y
 LDAA %1111111111,SP
E11 LDAA %11111111111111111111111111111111111111111111111111,PC
E12 JMP D,D
HOLA

 HOLA
E13 E E
E14 LDAA E,X
E15 LDAA ,X
 LDAA %11111111
 LDAA %111111111
	inca
E16 INC $-136
E17 bita #@-100
E18 LDAA 0,+y
 jmp 0,PC
 jmp ,PC
 JMP [0,Y]
 JMP [,Y]
E19 jmp [10,Y,]
E20 jmp 10,y,0
 bitb 256
 cpd juan56789

E21 LDAA 100,SP+
 LDAA 11,X
 LDAA 111,Y
 LDAA 0,SP

 tbeq 65000
	STS	,Y
	STS	0,PC
	STS	2,SP
	STS	-2,X
	STS	41,X
	STS	-41,Y
	STS	65500,PC
	STS	3,-X
	STS	4,Y-
	STS	5,+SP
	STS	6,X+
	STS	D,PC
E01	ABY
	andb	,pc
	bitb	5,-y
	clr	1,+x
	cps	1,x+
	cpx	a,sp
	inc	16,x
	ldaa	8,y
	ldaa	256,pc
	STS	[125,PC]
a1	STS	[D,SP]
	LDAA	[10,X]
a3	LDAA	[31483,X]
	JMP	[D,PC]
	ADCA	[D,X]
	ADCB	[D,SP]
a4	ADDA	[D,Y]
	addA	[d,pc]
	adda	[65535,x]
	anda	[0,y]
	asr	[255,sp]
	bitb	[4,pc]
a5	eora	[d,x]
	neg	[d,y]
uno    EQU  $30
dos    EQU  $50
e1	BRA  uno
	LBRA dos
	BCS  uno
	LBCS tres
tres   BCS  dos
cuatro SWI
cinco	DS	10
	BRA	CINCO
seis	DS	4096
	bcc	$17
	lbcc	$17
	bcc	UNO
	lbcc	UNO
	lblo	SEIS
	lblo	e1
V1	EQU	15
	LDAA	V1
	DW	4096
	DB	15
	DW	65535
	DB	255
	FCC	"esta cadena es un poco larga"
Uno	equ	$22
Dos	equ	$2222
	Ldaa	Uno
	Ldaa	Dos
	dc.b	$22
	dc.w	$2222
	fcc	"taller de programación de sistemas"
	END
Aqui	ya	"no leer"
Dos	equ	$2222
