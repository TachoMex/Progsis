	ORG 0
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
	fcc	"taller de programación de sistemas;jojojo"
	fcc	"Esta es una cadena con \"comillas\" y diagonal \\"
EN3		EQU	3
 DW	2
 DB   	2
 DC.W 	2
 DC.B 	2
e1		FCB  	2
 FDB  	2222
 DS   	34
 DS.B 	34
 DS.W 	34  
 RMB  	34  
 RMW  	34 
 FCC  	"HOLA MUNDO"
	rmw		@500  
e3		swi
e4		adca	$3
EN5		EQU		2
EN4		END
